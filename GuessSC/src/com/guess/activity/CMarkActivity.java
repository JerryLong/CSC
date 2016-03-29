package com.guess.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.guess.adapter.CMarkAdapter;
import com.guess.bean.CMarkBean;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myview.XListView;
import com.guess.myview.XListView.IXListViewListener;
import com.guess.utils.UrlContants;
import com.guess.utils.VolleyErrorHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class CMarkActivity extends Activity implements IXListViewListener {
	
	private TextView tvBack, tvTitle;
	
	private XListView mListView;
	private CMarkAdapter mAdapter;
	private ApplicationUtil mApplicationUtil;
	private ArrayList<CMarkBean> mList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mark_layout);
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		tvBack.setText(R.string.back);
		tvTitle.setText("猜商城");
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mListView = (XListView) findViewById(R.id.cmark_listview);
		mAdapter = new CMarkAdapter(this);
		mListView.setAdapter(mAdapter);
		mApplicationUtil = new ApplicationUtil(this);
		mListView.setXListViewListener(this);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setLoading(CMarkActivity.this, "商城里面空空的!");
		getData();
	}

	private void getData() {
		String url = UrlContants.LOCATION + UrlContants.CMARK_LIST;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("index", "0");
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						JSONArray array = response.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							Gson gson = new Gson();
							CMarkBean bean = gson.fromJson(array.get(i).toString(), CMarkBean.class);
							mList.add(bean);
						}
						mAdapter.setData(mList);
						mListView.setVisibility(View.VISIBLE);
						onLoad();
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(CMarkActivity.this);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									getData();
								}
							});
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(CMarkActivity.this, VolleyErrorHelper.getMessage(error, CMarkActivity.this),
						Toast.LENGTH_SHORT).show();
			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);
		
	}

	@Override
	public void onRefresh() {
		mListView.setLoading(CMarkActivity.this, "商城里面空空的!");
		mList.clear();
		getData();
		onLoad() ;
	}

	@Override
	public void onLoadMore() {
		/*getData();
		onLoad() ;*/
	}

	private void onLoad() {

		mListView.stopRefresh();

		mListView.stopLoadMore();

		mListView.setRefreshTime(CMarkActivity.this, "cmark");

	}
}
