package com.guess.user;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.myadapter.MyAttentionAdapter;
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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 我的关注列表
 * @author YXC
 *
 */
@SuppressLint("InflateParams")
public class MyAttention extends Activity implements OnClickListener, IXListViewListener{
	private MyNetManager myNet;
	
	private int type = 2;// 1 粉丝列表 2关注列表
	
	private TextView tvBack, tvTitle;
	
	private XListView mListView;
	private MyAttentionAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mList;
	
	private String attentionUrl = "http://api.caisichuan.com/api/v2/user/getAttentionUserList";
	private String fanUrl = "http://api.caisichuan.com/api/v2/user/getMyFansList";
	private String url = attentionUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myattention);
		
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		myNet = new MyNetManager(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);		
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		
		type = getIntent().getIntExtra("type", 2);
		int title = 0;
		int des = 0;
		if(type == 1){
			//粉丝列表
			title = R.string.title_fan_list;
			des = R.string.emptyview_tv_fan;
			url = fanUrl;
		}else{
			//关注列表
			title = R.string.title_attention_list;
			des = R.string.emptyview_tv_attention;
			url = attentionUrl;
		}
		
		mListView = (XListView) findViewById(R.id.myattention_lv_contect);
		mList = new ArrayList<>();
		mAdapter = new MyAttentionAdapter(MyAttention.this, mList);
		mListView.setAdapter(mAdapter);
		mListView.setPullLoadEnable(false);
		mListView.setXListViewListener(this);
		
		mListView.setLoading(this, des);

		if(myNet.netIsAvailable()){
			HashMap<String, String> map = new HashMap<>();
			getData(url, map);
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
			.show();
		}
		
		tvBack.setText(getString(R.string.title_myself));
		tvTitle.setText(title);
		
		tvBack.setOnClickListener(this);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				HashMap<String, Object> map = mList.get(arg2-1);
				Intent userDetail = new Intent(MyAttention.this, UserDetail.class);
				userDetail.putExtra("id", map.get("uid").toString());
				startActivity(userDetail);
			}
		});
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
	 * @param url
	 * @param map
	 */
	private void getData(final String url, final HashMap<String, String> map){
		MyJsonRequest jsonRequest = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
			//	System.out.println("==response=="+response);
				mListView.stopRefresh();
				mListView.setRefreshTime(getApplicationContext(), "attention");
				try {
					if(response.getInt("ret") == 0){
						HashMap<String, Object> dataMap = null;
						JSONArray careArray = response.getJSONArray("list");
						for(int i = 0; i < careArray.length(); i++){
							dataMap = new HashMap<>();
							JSONObject careJson = careArray.getJSONObject(i);
							dataMap.put("nickName", careJson.get("nickName"));
							dataMap.put("avatar", careJson.getString("avatar"));
							dataMap.put("uid", careJson.getString("uid"));
						
							mList.add(dataMap);
							mAdapter.notifyDataSetChanged();
						}
						
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(MyAttention.this);
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
				mListView.stopRefresh();
			//	Toast.makeText(getApplicationContext(), "fail to request data", Toast.LENGTH_SHORT).show();
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(jsonRequest);
	}

	@Override
	public void onRefresh() {
		// refresh data
		if(myNet.netIsAvailable()){
			//mList = new ArrayList<>();
			mList.clear();
			HashMap<String, String> map = new HashMap<>();
			getData(url, map);
			mListView.setLoading(this, R.string.emptyview_tv_attention);
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
