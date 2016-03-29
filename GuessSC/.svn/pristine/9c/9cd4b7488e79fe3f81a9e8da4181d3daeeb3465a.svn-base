package com.guess.guide;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChat;
import com.guess.activity.MainActivity;
import com.guess.message.EaseHelper;
import com.guess.message.EaseLogin;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.ShareData;
import com.guess.user.ActivityLogin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

/**
 * @author YXC
 * 
 */
@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {
	
	private ImageView img_logo;

	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	private static final int GET_INFO = 1002;

	private static final long SPLASH_DELAY_MILLIS = 3000;
	
	private String loginQqUrl = "http://api.caisichuan.com/user/qqLogin"; // QQ登录地址
	private String loginGuestUrl = "http://api.caisichuan.com/user/guestLogin";// 游客登录地址
	private String loginPhoneUrl = "http://api.caisichuan.com/user/phoneLogin";// 电话登录地址
	private String loginWeiboUrl = "http://api.caisichuan.com/user/weiboLogin";// 微博登录

	private ShareData share;
	private ApplicationUtil application;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			case GET_INFO:
				new LoginUtil(SplashActivity.this).getData();//获取用户信息
				
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		EaseHelper.getInstance().initEase(getApplicationContext());//初始化环信UI
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		init();
		initView();
	}

	private void initView() {
		
		img_logo = (ImageView) findViewById(R.id.splash_img_logo);
		
		img_logo.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(SplashActivity.this,
				R.anim.anim01);
		img_logo.startAnimation(anim);
		anim.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				img_logo.setVisibility(View.GONE);
			}
		});

	}

	private void init() {
		share = ShareData.getInstance(getApplicationContext());
		application = new ApplicationUtil(getApplicationContext());
		isFirstIn = share.isFirstLogin();
		if (!isFirstIn) {
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome() {
		Intent intent;
		if(!share.getLogin()){
			intent = new Intent(SplashActivity.this, ActivityLogin.class);
		}else{
			System.out.println("kaishidenglu");
			intent = new Intent(SplashActivity.this, MainActivity.class);
			int type = share.getType();
			HashMap<String, String> map = new HashMap<>();
			String url = loginGuestUrl;
			if(type == 0){
				url = loginGuestUrl;
				map.put("password", share.getGuestPassword());
				map.put("guestAccount", share.getGuestAccount());
			}else if(type == 2){
				url = loginPhoneUrl;
				map.put("phone", share.getPhone());
				map.put("password", share.getPhonePassword());
			}else if(type == 3){
				url = loginQqUrl;
				map.put("openId", share.getOpenId());
				map.put("accessToken", share.getAccessTokenQQ());
			}else if(type == 4){
				url = loginWeiboUrl;
				map.put("weiboUid", share.getWeiboUid());
				map.put("accessToken", share.getAccessTokenWeibo());
			}
			login(url, map);
			
			//登录环信
			if(!EMChat.getInstance().isLoggedIn()){
				new EaseLogin(getApplicationContext()).init();
			}
		}
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, MyGuide.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}
	
	private void login(String url, HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						mHandler.sendEmptyMessage(GET_INFO);
						
					}else{
						Toast.makeText(getApplicationContext(), R.string.login_defeat, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
			//	Toast.makeText(getApplicationContext(), R.string.service_connect_defeat, Toast.LENGTH_SHORT).show();
			}
		}, map);
		application.getRequestQueue().add(json);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
}
