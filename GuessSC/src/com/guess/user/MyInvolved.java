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
import com.guess.activity.SquareDetail;
import com.guess.myadapter.MyQuestionAdapter;
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
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我参与的
 * 
 * @author YXC
 *
 */
@SuppressLint("InflateParams")
public class MyInvolved extends Activity implements OnClickListener, IXListViewListener {
	private MyNetManager myNet;
	
	private TextView tvBack, tvTitle;

	private XListView mListView;
	private MyQuestionAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mList;

	private String questionUrl = "http://api.caisichuan.com/user/participateQuiz";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinvolve);

		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		myNet = new MyNetManager(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);

		mListView = (XListView) findViewById(R.id.myinvolved_lv_content);
		mList = new ArrayList<>();
		mAdapter = new MyQuestionAdapter(MyInvolved.this, mList);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		mListView.setPullLoadEnable(false);
		mListView.setLoading(this, R.string.emptyview_tv_involved);

		if(myNet.netIsAvailable()){
			HashMap<String, String> map = new HashMap<>();
			getData(questionUrl, map);
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
			.show();
		}

		tvBack.setText(getString(R.string.title_myself));
		tvTitle.setText(getString(R.string.title_involved));
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				HashMap<String, Object> dataMap = mList.get(arg2-1);
				Intent squareDetail = new Intent(MyInvolved.this, SquareDetail.class);
				squareDetail.putExtra("createTime", dataMap.get("createTime").toString());
				squareDetail.putExtra("totalBet", dataMap.get("totalBet").toString());
				squareDetail.putExtra("imageUrl", dataMap.get("imageUrl").toString());
				squareDetail.putExtra("evaluation", dataMap.get("evaluation").toString());
				squareDetail.putExtra("love", dataMap.get("love").toString());
				squareDetail.putExtra("attention", dataMap.get("attention").toString());
				squareDetail.putExtra("deadline", dataMap.get("deadline").toString());
				squareDetail.putExtra("description", dataMap.get("title").toString());
				squareDetail.putExtra("commentNumber", dataMap.get("commentNumber").toString());
				squareDetail.putExtra("id", dataMap.get("id").toString());
				squareDetail.putParcelableArrayListExtra("answers", (ArrayList<? extends Parcelable>) dataMap.get("answers"));
				squareDetail.putExtra("myInfo", dataMap.get("myInfo").toString());
				squareDetail.putExtra("confirmAnswer", dataMap.get("confirmAnswer").toString());
				squareDetail.putExtra("finished", (Boolean)dataMap.get("finished"));
				startActivity(squareDetail);	
			}
		});

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
			//	System.out.println("==response==" + response);
				mListView.stopRefresh();
				mListView.setRefreshTime(getApplicationContext(), "involved");
				try {
					if (response.getInt("ret") == 0) {
						HashMap<String, Object> dataMap;
						JSONArray recordArray = response.getJSONArray("list");
						JSONArray answer;
						HashMap<String, Object> answerMap;
						ArrayList<HashMap<String, Object>> answerList;
						for (int i = 0; i < recordArray.length(); i++) {
							answerList = new ArrayList<>();
							dataMap = new HashMap<>();
							//基本信息
							JSONObject recordJson = recordArray.getJSONObject(i);
							dataMap.put("createTime", recordJson.get("createTime") + "000");
							dataMap.put("imageUrl", recordJson.get("imageUrl"));
							dataMap.put("title", recordJson.get("description"));
							dataMap.put("deadline", recordJson.get("deadline"));
							dataMap.put("attention", recordJson.get("attention"));
							dataMap.put("totalBet", recordJson.get("totalBet"));							
							dataMap.put("commentNumber", recordJson.get("commentNumber"));							
							dataMap.put("love", recordJson.get("love"));
							dataMap.put("id", recordJson.get("id"));
							dataMap.put("finished", recordJson.getBoolean("finished"));
							String myInfo = recordJson.getString("myInfo");
							//我的选择信息
							if(myInfo != null){
								dataMap.put("myInfo", myInfo);
								dataMap.put("evaluation", new JSONObject(myInfo).get("evaluation"));
							}else{
								dataMap.put("myInfo", "");
								dataMap.put("evaluation", "0");
							}
							//答案确定信息
							String confirmAnswer = recordJson.getString("confirmAnswer");
							if(confirmAnswer != null){
								JSONObject confirm = new JSONObject(confirmAnswer);
								int index = confirm.getInt("answerIndex");
								String strAnswer = ((char)(65+index)) +  "." + confirm.get("content");
								dataMap.put("answer", strAnswer);
								dataMap.put("confirmAnswer", confirmAnswer);
							}else{
								dataMap.put("answer", "未公布");
								dataMap.put("confirmAnswer", "");
							}
							//是否结束
							if(recordJson.getBoolean("finished")){
								dataMap.put("finish", "1");
							}else{
								dataMap.put("finish", "0");
							}
							//答案列表信息
							answer = recordJson.getJSONArray("answers");
							for(int j = 0; j < answer.length(); j++){
								answerMap = new HashMap<>();
								JSONObject json = answer.getJSONObject(j);
								answerMap.put("content", json.get("content"));
								answerMap.put("totalBet", json.get("totalBet"));
								answerMap.put("answerIndex", json.get("answerIndex"));
								answerList.add(answerMap);
							}
							dataMap.put("answers", answerList);
							
							mList.add(dataMap);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(MyInvolved.this);
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
				Toast.makeText(getApplicationContext(), R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(jsonRequest);
	}

	@Override
	public void onRefresh() {
		// refresh data
		if(myNet.netIsAvailable()){
			mListView.setLoading(this, R.string.emptyview_tv_involved);
			mList.clear();
			HashMap<String, String> map = new HashMap<>();
			getData(questionUrl, map);
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
