package com.guess.user;

import java.util.HashMap;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChatManager;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.activity.MainActivity;
import com.guess.database.DarenData;
import com.guess.database.DarenLable;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.MyAlertDialog;
import com.guess.myutils.MyAlertDialog.DialogListener;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.ShareData;
import com.guess.myutils.UpdateUtil;
import com.guess.myview.MyCheckSwitchButton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * 设置界面
 * 
 * @author YXC
 *
 */
@SuppressLint("HandlerLeak")
public class ActivitySetting extends Activity implements OnClickListener {
	
	private ApplicationUtil applicationUtil;
	private RequestQueue requestQueue;
	private ShareData share;
	
	private TextView tvBack;
	private TextView tvTitle;
	private TextView tvCache;
	private TextView tvVersion;

	private Button btnLogout;

	private RelativeLayout rltHelp, rltAbout, rltClearCache, rltTicking, rltUpdate;

	private MyCheckSwitchButton checkVoice;
	
	private String logoutUrl = "http://api.caisichuan.com/user/logout";
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				//发送注销请求
				HashMap<String, String> map = new HashMap<>();
				MyJsonRequest jsonRequest = new MyJsonRequest(logoutUrl, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						//注销成功
						logout();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//失败
						btnLogout.setClickable(true);
					}
				}, map);
				requestQueue.add(jsonRequest);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initView();
	}

	/**
	 * 初始化
	 */
	public void initView() {
		
		share = ShareData.getInstance(getApplicationContext());
		applicationUtil = new ApplicationUtil(getApplicationContext());
		requestQueue = applicationUtil.getRequestQueue();
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		tvVersion = (TextView) findViewById(R.id.setting_tv_version);
		tvCache = (TextView) findViewById(R.id.setting_tv_cache);

		btnLogout = (Button) findViewById(R.id.setting_btn_logout);

		rltAbout = (RelativeLayout) findViewById(R.id.setting_rlt_about);
		rltClearCache = (RelativeLayout) findViewById(R.id.setting_rlt_clearcache);
		rltHelp = (RelativeLayout) findViewById(R.id.setting_rlt_help);
		rltTicking = (RelativeLayout) findViewById(R.id.setting_rlt_ticking);
		rltUpdate = (RelativeLayout) findViewById(R.id.setting_rlt_update);

		checkVoice = (MyCheckSwitchButton) findViewById(R.id.setting_open_voice);
		checkVoice.setChecked(share.isVoice());

		tvBack.setText(getString(R.string.title_myself));
		tvTitle.setText(getString(R.string.title_setting));

		tvVersion.setText(applicationUtil.getApplicationName() + applicationUtil.getVersionName());
		
		tvCache.setText(applicationUtil.getTotalCacheSize());

		// 开启或关闭声音
		checkVoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				share.setVoice(isChecked);
			}
		});

		tvBack.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		rltAbout.setOnClickListener(this);
		rltClearCache.setOnClickListener(this);
		rltHelp.setOnClickListener(this);
		rltTicking.setOnClickListener(this);
		rltUpdate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			// 返回
			finish();
			break;

		case R.id.setting_btn_logout:
			// 注销
			MyAlertDialog.showAlertDialog(ActivitySetting.this, "返回", "注销", "确定要退出当前账号?", new DialogListener() {
				
				@Override
				public void onOkDialog() {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(0);
					btnLogout.setClickable(false);
					share.setLogin(false);
					//清除数据库
					new DarenData(getApplicationContext()).clearAll();
					new DarenLable(getApplicationContext()).clearAll();
					
					Intent login = new Intent(ActivitySetting.this, ActivityLogin.class);
					startActivity(login);
					finish();//返回
					MainActivity.instance.finish();//结束ctivityMyself
				}
				
				@Override
				public void onCancelDialog() {
					// TODO Auto-generated method stub
					
				}
			});
			
			break;

		case R.id.setting_rlt_about:
			// 关于
			Intent about = new Intent(ActivitySetting.this, ActivityAbout.class);
			startActivity(about);
			break;

		case R.id.setting_rlt_clearcache:
			// 清除缓存
			if(!"0M".equals(applicationUtil.getTotalCacheSize())){
				MyAlertDialog.showAlertDialog(ActivitySetting.this, "返回", "清除", "清除所有缓存?", new DialogListener() {
					
					@Override
					public void onOkDialog() {
						//测试代码
						//new EaseUserInfo(getApplicationContext()).clearAll();
						applicationUtil.clearAllCache();
						Toast.makeText(getApplicationContext(), getString(R.string.setting_cache_clear_ok), Toast.LENGTH_SHORT).show();
						tvCache.setText("0M");
					}
					
					@Override
					public void onCancelDialog() {
					}
				});
			}
			break;

		case R.id.setting_rlt_help:
			// 使用帮助
			Intent help = new Intent(ActivitySetting.this, ActivityHelp.class);
			startActivity(help);
			break;
		case R.id.setting_rlt_ticking:
			// 意见反馈
			Intent ticking = new Intent(ActivitySetting.this, ActivityTicking.class);
			startActivity(ticking);
			break;
		case R.id.setting_rlt_update:
			// 检查更新
			UpdateUtil update = new UpdateUtil(ActivitySetting.this, 1);
			update.checkUpdate();
			break;
		}
	}
	
	/**
	 * 注销环信登录
	 */
	private void logout(){
		EMChatManager.getInstance().logout();
	}

}
