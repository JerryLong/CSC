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
import com.guess.fragment.SquareQuestionFragment;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class OfficialGuessDetail extends FragmentActivity implements OnClickListener{

	private ApplicationUtil application;
	
	private SquareQuestionFragment questionFragment;

	
	private TextView tvBack, tvReport;
	
	private ImageView imgPicture;
	
	private int type, activityId;
	//详细信息
	private String confirmAnswer, myInfo, deadLine, imageUrl, attention, totalBet, description;
	private int authorBet;
	private ArrayList<HashMap<String, Object>> answerList;
	private boolean finish;
	
	private String detailUrl = "http://api.caisichuan.com/officalActivity/activityDetail";

	private String guessUrl = "http://api.caisichuan.com/officalActivity/submitGambleActivityAnswer";
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case 0:	
					Picasso.with(OfficialGuessDetail.this).load(imageUrl).resize(480, 300).placeholder(R.drawable.default_picture2x).into(imgPicture);
					Long deadTime = Long.parseLong(deadLine);
					if(System.currentTimeMillis() > deadTime){
						finish = true;
					}else{
						finish = false;
					}
					
					questionFragment = new SquareQuestionFragment();
					questionFragment.setData(guessUrl, authorBet, confirmAnswer, myInfo, deadLine, description, totalBet, attention, answerList, finish);
					getSupportFragmentManager().beginTransaction().add(R.id.official_guess_detail_fragment, questionFragment).commit();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.official_guess_detail);
		
		initView();
		initData();
	}
	
	private void initData() {
		application = new ApplicationUtil(getApplicationContext());
		Intent intent = getIntent();

		activityId = intent.getIntExtra("id", -1);
		type = intent.getIntExtra("type", -1);

		answerList = new ArrayList<>();
		getData();
	}

	/**
	 * 初始化
	 */
	private void initView() {

		tvBack = (TextView) findViewById(R.id.official_guess_detail_back);
		tvReport = (TextView) findViewById(R.id.official_guess_detail_report);
		
		imgPicture = (ImageView) findViewById(R.id.official_guess_detail_image);


		tvBack.setOnClickListener(this);
		tvReport.setOnClickListener(this);
	}
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.official_guess_detail_back:
			finish();
			break;

		case R.id.official_guess_detail_report:
			Intent report = new Intent(OfficialGuessDetail.this, ActivityReport.class);
			report.putExtra("id", activityId);
			startActivity(report);
			break;
		}
		
	}
	
	private void getData(){
		HashMap<String, String> map = new HashMap<>();
		map.put("type", String.valueOf(type));
		map.put("activityId", String.valueOf(activityId));
		MyJsonRequest json = new MyJsonRequest(detailUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						HashMap<String, Object> map;
						JSONObject activity = response.getJSONObject("activity");
						imageUrl = activity.getString("imageUrl");
						description = activity.getString("description");
						authorBet = activity.getInt("authorBet");
						totalBet = activity.getString("totalBet");
						attention = activity.getString("attention");
						deadLine = activity.getString("deadline") + "000";
						confirmAnswer = activity.getString("confirmAnswer");
						if(confirmAnswer == null){
							confirmAnswer = "";
						}
						myInfo = response.getString("myInfo");
						if(myInfo == null){
							myInfo = "";
						}
						JSONArray answer = activity.getJSONArray("answers");
						JSONObject object;
						for(int i = 0; i < answer.length(); i++){
							object = answer.getJSONObject(i);
							map = new HashMap<>();
							map.put("content", object.getString("content"));
							map.put("totalBet", object.getString("totalBet"));
							map.put("answerIndex", object.getString("index"));
							answerList.add(map);
						}
						handler.sendEmptyMessage(0);
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(OfficialGuessDetail.this);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									getData();
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
		}, map);
		application.getRequestQueue().add(json);
	}


}
