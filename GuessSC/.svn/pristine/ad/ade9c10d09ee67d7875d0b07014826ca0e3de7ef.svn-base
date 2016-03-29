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
import com.guess.adapter.OfficialAdapter;
import com.guess.bean.OfficialBean;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myutils.MyJsonRequest;
import com.guess.myview.XListView;
import com.guess.myview.XListView.IXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

public class OfficialActivity extends Activity implements IXListViewListener{
	private TextView tvTitle, tvBack;
	
	private XListView mListView;
	private OfficialAdapter mAdapter;
	private ApplicationUtil mApplicationUtil;
	private ArrayList<OfficialBean> mList = new ArrayList<OfficialBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_official_layout);
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		tvBack.setText(R.string.back);
		tvTitle.setText("官方活动");
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mAdapter = new OfficialAdapter(this);
		mListView = (XListView) findViewById(R.id.activity_official_listview);
		mListView.setPullLoadEnable(false);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		mApplicationUtil = new ApplicationUtil(getApplicationContext());

		mListView.setLoading(OfficialActivity.this, "暂时没有活动 敬请期待!");
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = null;
				OfficialBean bean = mList.get(arg2 - 1);
				int type = bean.getAcType();
				if(type == 0){//普通活动
					intent = new Intent(OfficialActivity.this, OfficialCommonDetail.class);
					
				}else if(type == 1){//竞猜活动
					intent = new Intent(OfficialActivity.this, OfficialGuessDetail.class);
					
				}else if(type == 2){//二维码活动
					intent = new Intent(OfficialActivity.this, OfficialCodeDetail.class);
				}
				intent.putExtra("response", bean.getResponse());
				intent.putExtra("type", type);
				intent.putExtra("id", bean.getId());
				startActivity(intent);
			}
		});
		getData();

	}

	public void getData() {
		HashMap<String, String> map = new HashMap<String, String>();
		String url = "http://api.caisichuan.com/officalActivity/activityList";
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				mListView.setRefreshTime(getApplicationContext(), "officialActivity");
				mListView.stopRefresh();
				try {
					if(response.getInt("ret") == 0){
						JSONArray array = response.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							Gson gson = new Gson();
							OfficialBean bean = gson.fromJson(array.get(i).toString(), OfficialBean.class);
							mList.add(bean);
						}
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(OfficialActivity.this);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									getData();
								}
							});
						}
					}

					mAdapter.setData(mList);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}, map);
		mApplicationUtil.getRequestQueue().add(request);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mListView.setLoading(OfficialActivity.this, "暂时没有活动 敬请期待!");
		mList.clear();
		getData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
}
