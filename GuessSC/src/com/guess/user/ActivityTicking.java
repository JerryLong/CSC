package com.guess.user;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户意见反馈
 * @author YXC
 *
 */
@SuppressLint("HandlerLeak")
public class ActivityTicking extends Activity{
	private TextView tvBack, tvTitle;
	private EditText etContent;
	private Button btnSend;
	private RequestQueue requestQueue;
	
	private String tickingUrl = "http://api.caisichuan.com/app/feedback";//服务器地址
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0://发送意见反馈
				String content = (String) msg.obj;
				HashMap<String, String> map = new HashMap<>();
				map.put("content", content);
				getData(tickingUrl, map);
				btnSend.setClickable(false);
				break;

			case 1://发送意见成功
				etContent.setText("");
				Toast.makeText(getApplicationContext(), "谢谢你的意见!", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 2://发送意见失败
				btnSend.setClickable(false);
				Toast.makeText(getApplicationContext(), getString(R.string.request_defeat), Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticking);
		
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		
		requestQueue = new ApplicationUtil(getApplicationContext()).getRequestQueue();
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		etContent = (EditText) findViewById(R.id.ticking_content);
		btnSend = (Button) findViewById(R.id.ticking_send);
		
		tvBack.setText(getString(R.string.title_setting));
		tvTitle.setText(getString(R.string.title_ticking));
		
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//发送意见反馈
				String content = etContent.getText().toString();
				if(!"".equals(content)){
					Message msg = handler.obtainMessage();
					msg.obj = content;
					msg.what = 0;
					handler.sendMessage(msg);
				}
			}
		});
	}
	
	/**
	 * 发送意见反馈
	 * @param url
	 * @param map
	 */
	private void getData(final String url, final HashMap<String, String> map){
		MyJsonRequest jsonRequest = new MyJsonRequest(tickingUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						//发送成功
						handler.sendEmptyMessage(1);
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(ActivityTicking.this);
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
				//失败
				handler.sendEmptyMessage(2);
			}
		}, map);
		requestQueue.add(jsonRequest);
	}

}
