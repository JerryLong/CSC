package com.guess.user;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.MyNetManager;
import com.guess.myutils.PDialogUtil;
import com.guess.myutils.RandomName;
import com.guess.myutils.ShareData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

@SuppressLint("HandlerLeak")
public class ActivityPassword extends Activity implements OnClickListener {
	private ShareData share;
	private RequestQueue requestQueue;
	private InputMethodManager imm;

	private TextView tvBack, tvTitle;

	private TextView tvTimeOut;

	private EditText etPsd, etPhone, etCode;

	private Button btnGet, btnDone;

	private String title;
	private int operation = 0;// 当前所做的操作 1注册 2 修改密码 3找回密码 4绑定账号
	private String phone = "", password, code;

	private final String country = "86";
	private final String rule = "^1(3|5|7|8|4)\\d{9}";// 检验手机号码是否正确的正则表达式

	private String registerUrl = "http://192.168.2.222:8080/cscapi/api/v1/registerPhoneAccount";// 注册
//	private String registerUrl = "http://api.caisichuan.com/api/v1/registerPhoneAccount";// 注册
	private String findPasswordUrl = "http://api.caisichuan.com/api/v1/retrievePassword";// 找回密码
	private String bindUrl = "http://api.caisichuan.com/api/v1/guestAccountBind";// 绑定电话

	// 短信回调
	Handler handlerSMS = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
					 Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					Toast.makeText(getApplicationContext(), getString(R.string.code_send), Toast.LENGTH_SHORT).show();
					handler.sendEmptyMessage(1);
				} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表

				}
			} else {
				((Throwable)msg.obj).printStackTrace();
				Toast.makeText(ActivityPassword.this, getString(R.string.code_get_defeat), Toast.LENGTH_SHORT).show();
			}

		}

	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				int time = msg.arg2;
				if(time > 0){
					String timeOut = getString(R.string.send_message_time, time);
					tvTimeOut.setText(Html.fromHtml(timeOut));
					tvTimeOut.setVisibility(View.VISIBLE);
				}else{
					tvTimeOut.setVisibility(View.GONE);
					btnGet.setClickable(true);
					//btnGet.setBackground(background);
				}
				break;

			case 1:
				setTimeOut();
				btnGet.setClickable(false);
				//btnGet.setBackground(background);//设置为不可点击背景
				break;		
			}
		};
	};
	
	EventHandler eh = new EventHandler() {

		@Override
		public void afterEvent(int event, int result, Object data) {
			System.out.println("data=="+data);
			Message msg = new Message();
			msg.arg1 = event;
			msg.arg2 = result;
			msg.obj = data;
			handlerSMS.sendMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		initSMSSDK();
		initView();	
	}

	/**
	 * 初始化短信接口
	 */
	private void initSMSSDK() {
		SMSSDK.initSDK(this, Constant.APP_KEY_SHARE, Constant.APP_SECRET_SHARE);
		// 注册短信回调
		
		SMSSDK.registerEventHandler(eh);
	}

	/**
	 * 初始化
	 */
	private void initView() {
		share = ShareData.getInstance(getApplicationContext());
		requestQueue = new ApplicationUtil(getApplicationContext()).getRequestQueue();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		title = getIntent().getStringExtra("title");
		operation = getIntent().getIntExtra("operation", 0);

		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		tvTimeOut = (TextView) findViewById(R.id.psd_time_out);

		etCode = (EditText) findViewById(R.id.psd_code);
		etPhone = (EditText) findViewById(R.id.psd_phone);
		etPsd = (EditText) findViewById(R.id.psd_password);

		btnDone = (Button) findViewById(R.id.psd_done);
		btnGet = (Button) findViewById(R.id.psd_get_code);

		tvTitle.setText(title);

		tvBack.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		btnGet.setOnClickListener(this);

	}
	
	@Override
	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(etCode.getWindowToken(), 0);
		}
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			finish();
			break;

		case R.id.psd_get_code:
			// 获取验证码
			phone = etPhone.getText().toString();
			Pattern p = Pattern.compile(rule);
			Matcher m = p.matcher(phone);
			if (m.matches()) {
				SMSSDK.getVerificationCode(country, phone);
				etCode.setText("");
				etPsd.setText("");
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.phone_not_right), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.psd_done:
			//设置密码
			//网络可用
			code = etCode.getText().toString();
			phone = etPhone.getText().toString();
			if(code != null&& (!"".equals(code))){
//				SMSSDK.submitVerificationCode(country, phone, code);//提交验证码
				if(new MyNetManager(getApplicationContext()).netIsAvailable()){
					PDialogUtil.showDialog(ActivityPassword.this, getString(R.string.myinfo_password_setting), true);
					password = etPsd.getText().toString().trim();
					// 密码检验
					boolean flag = true;
					char[] ch = password.toCharArray();
					if ((ch.length >= 6) && (ch.length <= 16)) {
						for (int i = 0; i < ch.length; i++) {
							if (((ch[i] >= 48) && (ch[i] <= 57))
									|| ((ch[i] >= 65) && (ch[i] <= 90))
									|| ((ch[i] >= 97) && ch[i] <= 122)) {
	
							} else {
								flag = false;
								Toast.makeText(getApplicationContext(),
										getString(R.string.myinfo_password_rule),
										Toast.LENGTH_SHORT).show();
								break;
							}
						}
					} else {
						flag = false;
						Toast.makeText(getApplicationContext(),
								getString(R.string.myinfo_password_rule), Toast.LENGTH_SHORT)
								.show();
					}
					if(flag){//密码格式正确
						String url = "";
						password = etPsd.getText().toString();
						HashMap<String, String> psdMap = new HashMap<String, String>();
						if(operation == 1){
							//注册
							String nickname = new RandomName(getApplicationContext()).getRandomName();
							psdMap.put("phone", phone);
							psdMap.put("code", code);
							psdMap.put("zone", country);
							psdMap.put("password", password);
							psdMap.put("nickname", nickname);
							psdMap.put("type", "1");
							System.out.println("phone="+phone);
							System.out.println("code="+code);
							System.out.println("country="+country);
							System.out.println("password="+password);
							System.out.println("nickname="+nickname);
							url = registerUrl;
						}else if(operation == 3){
							//找回密码
							psdMap.put("phone", phone);
							psdMap.put("code", code);
							psdMap.put("zone", country);
							psdMap.put("newPassword", password);
							psdMap.put("type", "1");
							url = findPasswordUrl;
						}else if(operation == 4){
							//绑定用户
							url = bindUrl;
							psdMap.put("phone", phone);
							psdMap.put("code", code);
							psdMap.put("zone", country);
							psdMap.put("password", password);
							psdMap.put("type", "1");
						}
						getData(url, psdMap, operation);
					}
				}else{
					Toast.makeText(getApplicationContext(),
							getString(R.string.network_not_available),
							Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), getString(R.string.code_not_right), Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	/**
	 * 设置发送验证码超时
	 */
	private void setTimeOut(){
		new Thread(new Runnable() {
			int time = 60;
			@Override
			public void run() {
				try {
					while(time >= 0){
						Message msg = handler.obtainMessage();
						msg.what = 0;
						msg.arg2 = time;
						handler.sendMessage(msg);
						Thread.sleep(1000);
						time--;
					}
					//btnGet.setBackground(background);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 联网获取数据
	 * 
	 * @param url
	 *            服务器地址
	 * @param map
	 *            传递的参数
	 * @param flag
	 *            当前所要做的操作 flag = 1:注册 flag = 2:修改密码 flag = 3:找回密码
	 */
	public void getData(final String url, final HashMap<String, String> map, final int flag) {
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// 成功
				PDialogUtil.cancelDialog();
				try {
					int ret = response.getInt("ret");
					if (ret == 0) {
						if (flag == 1) {
							// 注册成功
							share.setPhone(phone);
							share.setPhonePassword(password);
							share.setType(2);

							Toast.makeText(getApplicationContext(), getString(R.string.myinfo_register_success),
									Toast.LENGTH_SHORT).show();
							
						}else if (flag == 3) {
							// 找回密码成功
							share.setPhonePassword(password);

							Toast.makeText(getApplicationContext(), getString(R.string.myinfo_password_success),
									Toast.LENGTH_SHORT).show();
						} else if (flag == 4) {
							// 手机号码绑定成功
							share.setPhone(phone);
							share.setPhonePassword(password);
							share.setType(2);//把用户设置为手机号码账户

							Toast.makeText(getApplicationContext(), getString(R.string.myinfo_bind_success),
									Toast.LENGTH_SHORT).show();
							
						}
						finish();
					} else if (ret == 1) {
						if (flag == 1 || flag == 4) {
							// rule of phone or password not right
							Toast.makeText(getApplicationContext(), getString(R.string.rule_not_right),
									Toast.LENGTH_SHORT).show();
						}

					} else if (ret == 2) {

						// code not right
						Toast.makeText(getApplicationContext(), getString(R.string.code_not_right), Toast.LENGTH_SHORT)
								.show();

					} else if (ret == 3) {
						if (flag == 1) {
							// phone has registered
							Toast.makeText(getApplicationContext(), getString(R.string.phone_has_register),
									Toast.LENGTH_SHORT).show();
						} else if (flag == 4) {
							// phone has binded
							Toast.makeText(getApplicationContext(), getString(R.string.phone_has_binded),
									Toast.LENGTH_SHORT).show();
						}
					} else if (ret == 4) {
						if (flag == 4) {
							// account has binded
							Toast.makeText(getApplicationContext(), getString(R.string.account_has_bind),
									Toast.LENGTH_SHORT).show();
						}
					} else {
						// request defeat
						Toast.makeText(getApplicationContext(), getString(R.string.request_defeat), Toast.LENGTH_SHORT)
								.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// 失败
				Toast.makeText(getApplicationContext(), R.string.login_defeat, Toast.LENGTH_SHORT).show();
				PDialogUtil.cancelDialog();
			}
		}, map);
		requestQueue.add(request);
	}

}
