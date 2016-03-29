package com.guess.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityReport extends Activity implements OnClickListener{
	private TextView tvBack, tvTitle;
	
	private EditText etReason;
	
	private Button btnSend;
	
	private String questionId;
	
	private String reportUrl = "http://api.caisichuan.com/inform/ncwc";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		initView();
	}
	private void initView() {
		questionId = String.valueOf(getIntent().getIntExtra("id", 0));
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		
		etReason = (EditText) findViewById(R.id.report_reason);
		
		btnSend = (Button) findViewById(R.id.report_send);
		
		tvBack.setText(R.string.back);
		tvTitle.setText(R.string.title_report);
		
		tvBack.setOnClickListener(this);
		btnSend.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			finish();
			break;

		case R.id.report_send:
			String reason = etReason.getText().toString();
			if(reason.length() > 0){
				if(reason.length() < 200){
					HashMap<String, String> map = new HashMap<>();
					System.out.println();
					map.put("questionId", questionId);
					map.put("reason", reason);
					report(map);
					btnSend.setClickable(false);
				}else{
					Toast.makeText(getApplicationContext(), R.string.report_reason_long, Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
		
	}
	
	/**
	 * 举报
	 * @param map
	 */
	private void report(final HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(reportUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						Toast.makeText(getApplicationContext(), R.string.report_success, Toast.LENGTH_SHORT).show();
						etReason.setText("");
						finish();
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(ActivityReport.this);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									report(map);
								}
							});
						}
					}
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), R.string.report_defeat, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				btnSend.setClickable(true);
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(json);
	}

}
