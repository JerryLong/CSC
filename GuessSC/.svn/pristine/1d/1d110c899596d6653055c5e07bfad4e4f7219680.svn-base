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
import com.guess.activity.AnswerActivity;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我发布的问题
 * 
 * @author YXC
 *
 */
@SuppressLint("InflateParams")
public class MyQuestions extends Activity implements OnClickListener, IXListViewListener {
	private MyNetManager myNet;
	
	private TextView tvBack, tvTitle;

	private XListView mListView;
	private MyQuestionAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mList;
	
	private String questionUrl = "http://api.caisichuan.com/user/myUploadQuiz";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myquestion);

		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		myNet = new MyNetManager(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		
		mListView = (XListView) findViewById(R.id.myquestion_lv_content);
		mList = new ArrayList<>();
		mAdapter = new MyQuestionAdapter(MyQuestions.this, mList);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		mListView.setPullLoadEnable(false);
		mListView.setLoading(this, R.string.emptyview_tv_question);

		if(myNet.netIsAvailable()){
			mListView.refreshDrawableState();
			HashMap<String, String> map = new HashMap<>();
			getData(questionUrl, map);
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
			.show();
		}

		tvBack.setText(getString(R.string.title_myself));
		tvTitle.setText(getString(R.string.title_questions));
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyQuestions.this, AnswerActivity.class);
				intent.putExtra("id", (int) mList.get(arg2-1).get("id"));
				startActivity(intent);
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
				//System.out.println("==response==" + response);
				mListView.stopRefresh();
				mListView.setRefreshTime(getApplicationContext(), "question");
				try {
					if (response.getInt("ret") == 0) {
						HashMap<String, Object> dataMap;
						JSONArray recordArray = response.getJSONArray("normalQuizList");
						
						for (int i = 0; i < recordArray.length(); i++) {
							dataMap = new HashMap<>();
							JSONObject recordJson = recordArray.getJSONObject(i);
							dataMap.put("answer", recordJson.get("answer"));
							dataMap.put("createTime", recordJson.get("createTime") + "000");
							dataMap.put("title", recordJson.get("title"));
							dataMap.put("id", recordJson.get("id"));
							dataMap.put("audit", recordJson.getInt("audit"));
							dataMap.put("imageUrl", recordJson.get("imageUrl"));
							dataMap.put("finish", "2");
							mList.add(dataMap);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(MyQuestions.this);
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
		// refresh
		if(myNet.netIsAvailable()){
			mListView.setLoading(MyQuestions.this, R.string.emptyview_tv_question);
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
