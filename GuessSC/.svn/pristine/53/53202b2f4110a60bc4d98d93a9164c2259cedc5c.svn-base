package com.guess.user;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.myadapter.MyRecordAdapter;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.MyNetManager;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myview.XListView;
import com.guess.myview.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的兑换记录
 * 
 * @author YXC
 *
 */
@SuppressLint("InflateParams")
public class MyRecord extends Activity implements OnClickListener, IXListViewListener {
	private MyNetManager myNet;
	
	private TextView tvBack, tvTitle;

	private XListView mListView;
	private MyRecordAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mList;

	private String recordUrl = "http://api.caisichuan.com/user/myStoreOrders";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myrecord);

		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		myNet = new MyNetManager(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);

		mListView = (XListView) findViewById(R.id.myrecord_lv_content);
		mList = new ArrayList<>();
		mAdapter = new MyRecordAdapter(MyRecord.this, mList);
		mListView.setAdapter(mAdapter);
		mListView.setPullLoadEnable(false);
		mListView.setXListViewListener(this);

		mListView.setLoading(this, R.string.emptyview_tv_exchange);

		if(myNet.netIsAvailable()){
			HashMap<String, String> map = new HashMap<>();
			getData(recordUrl, map);
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
			.show();
		}

		tvBack.setText(getString(R.string.title_myself));
		tvTitle.setText(getString(R.string.title_change_record));

		tvBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * get data
	 * 
	 * @param url
	 * @param map
	 */
	private void getData(final String url, final HashMap<String, String> map) {
		MyJsonRequest jsonRequest = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				mListView.stopRefresh();
				mListView.setRefreshTime(getApplicationContext(), "record");
				try {
					if (response.getInt("ret") == 0) {
						HashMap<String, Object> dataMap;
						JSONArray recordArray = response.getJSONArray("list");
						for (int i = 0; i < recordArray.length(); i++) {
							dataMap = new HashMap<>();
							JSONObject recordJson = recordArray.getJSONObject(i);
							dataMap.put("name", recordJson.get("name"));
							dataMap.put("time", recordJson.get("time") + "000");
							dataMap.put("imageUrl", recordJson.get("imageUrl"));
							dataMap.put("marketPrice", recordJson.get("marketPrice"));
							dataMap.put("goldPrice", recordJson.get("goldPrice"));
							mList.add(dataMap);
							mAdapter.notifyDataSetChanged();
						}

					} else {
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(MyRecord.this);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									getData(url, map);
								}
							});
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				mListView.stopRefresh();
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(jsonRequest);
	}

	@Override
	public void onRefresh() {
		// refresh data
		if(myNet.netIsAvailable()){
			mListView.setLoading(this, R.string.emptyview_tv_exchange);
			mList.clear();
			HashMap<String, String> map = new HashMap<>();
			getData(recordUrl, map);
		}else{
			mListView.stopRefresh();
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
			.show();
		}
	}

	@Override
	public void onLoadMore() {
		// load more

	}
}
