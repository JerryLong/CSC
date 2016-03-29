package com.guess.message.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.activity.AnswerActivity;
import com.guess.myadapter.MyMessageAdapter;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myview.XListView;
import com.guess.myview.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

@SuppressLint("ShowToast")
public class FragmentMessage extends Fragment implements IXListViewListener{
	
	private ApplicationUtil application;

	private ArrayList<HashMap<String, Object>> messageList;
	private MyMessageAdapter messageAdapter;
	private XListView listView;
	private Activity activity;
	
	private String messageUrl = "http://api.caisichuan.com/user/getUserNotification";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_message_message, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();
		getData(messageUrl);
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	
	private void initView() {
		
		application = new ApplicationUtil(activity);
		
		messageList = new ArrayList<HashMap<String, Object>>();
		messageAdapter = new MyMessageAdapter(activity, messageList);
		listView = (XListView) activity.findViewById(R.id.message_list);
		listView.setAdapter(messageAdapter);
		listView.setXListViewListener(this);
		listView.setPullLoadEnable(false);
		listView.setLoading(activity, "");
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int type = (int) messageList.get(position - 1).get("type");
				if((type == 0) || (type == 1)){
					Intent answer = new Intent(activity, AnswerActivity.class);
					answer.putExtra("id", (int) messageList.get(position-1).get("questionId"));
					startActivity(answer);
				}
			}
		});
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	private void getData(final String url){
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				//System.out.println("response=="+response);
				listView.stopRefresh();
				try {
					if(response.getInt("ret") == 0){
						listView.setRefreshTime(activity, "commentMessage");
						JSONArray array = response.getJSONArray("notificationInfos");
						JSONObject json;
						JSONObject questionJson;
						HashMap<String, Object> map;
						for(int i = 0; i < array.length(); i++){
							json = array.getJSONObject(i);
						//	System.out.println("json===="+i+"==="+json);
							map = new HashMap<>();
							map.put("title", json.get("title"));
							map.put("time", json.getString("createAt") + "000");
							map.put("id", json.getInt("id"));
							map.put("type", json.getInt("type"));
							
							String content = json.getString("content");
							JSONObject json1 = new JSONObject(content);
							String question = json1.getString("questionInfo");
							questionJson = new JSONObject(question);

							map.put("questionId", questionJson.getInt("id"));
							map.put("imageUrl", questionJson.getString("imageUrl"));
							messageList.add(map);
							messageAdapter.notifyDataSetChanged();
						}
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(activity);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									getData(url);
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
				
			}
		});
		application.getRequestQueue().add(json);
	}
	@Override
	public void onRefresh() {
		listView.setLoading(activity, "");
		messageList.clear();
		getData(messageUrl);
		messageAdapter.notifyDataSetChanged();
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
}
