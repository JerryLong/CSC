package com.guess.user;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.activity.MainActivity;
import com.guess.message.EaseLogin;
import com.guess.myutils.AccessTokenKeeper;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.MyNetManager;
import com.guess.myutils.PDialogUtil;
import com.guess.myutils.RandomName;
import com.guess.myutils.ShareData;
import com.android.volley.VolleyError;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 登录页面
 * 
 * @author YXC
 * 
 */
@SuppressLint("HandlerLeak")
public class ActivityLogin extends Activity implements OnClickListener{
	private InputMethodManager imm;
	// forget password , register
	private TextView tvForget, tvRegister;
	private TextView tvUserTerms, tvPrivacyPolicy;
	// the edit box of phone and password
	private EditText etPhone, etPassword;
	// the button of login
	private Button btnLogin, btnTourism;
	// third account
	private ImageView imgQQ, imgWeibo;
	private ShareData share;

	private UserInfo mInfo;

	private String account = "1234567890", phone, password;
	private String openId, nickName, accessTokenQQ;// openID of QQ nickname
	private String weiboUid, accessTokenWeibo;

//	public static Tencent mTencent;
	public static Tencent mTencent;
	private AuthInfo mAuthInfo;
	private SsoHandler mSsoHandler;
	private Oauth2AccessToken mAccessToken;

	private String loginQqUrl = "http://api.caisichuan.com/user/qqLogin"; // QQ登录地址
	private String loginGuestUrl = "http://api.caisichuan.com/user/guestLogin";// 游客登录地址
	private String loginPhoneUrl = "http://api.caisichuan.com/user/phoneLogin";// 电话登录地址
	private String loginWeiboUrl = "http://api.caisichuan.com/user/weiboLogin";// 微博登录
	private String registerQqUrl = "http://api.caisichuan.com/user/registerQqAccount";// QQ注册
	private String registerGuestUrl = "http://api.caisichuan.com/user/registerGuestAccount";// 游客注册地址
	private String registerWeiboUrl = "http://api.caisichuan.com/user/registerWeiboAccount";// 游客注册地址

	private final int PHONE_LOGIN = 0;// 手机号码登录
	private final int QQ_LOGIN = 1;// QQ登录
	private final int QQ_REGISTER = 2;// QQ注册
	private final int GUEST_LOGIN = 3;// 游客登录
	private final int GUEST_REGISTER = 4;// 游客注册
	private final int WEIBO_LOGIN = 7;// 微博登录
	private final int WEIBO_REGISTER = 6;// 微博注册

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			JSONObject json = null;
			switch (msg.what) {
			case PHONE_LOGIN:// 处理手机号码登录结果
				PDialogUtil.cancelDialog();
				json = (JSONObject) msg.obj;
				try {
					if (json.getInt("ret") == 0) {// 登录成功
						share.setPhone(phone);
						share.setPhonePassword(password);
						loginSuccess(json.getString("nickname"), json.getString("avatar"), json.getString("alias"), 2, String.valueOf(json.getInt("id")));
					} else {// 登录失败
						showToast(json);
					}
				} catch (JSONException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				break;

			case QQ_LOGIN:// 处理QQ本地登录结果
				json = (JSONObject) msg.obj;
				try {
					if (json.getInt("ret") == 0) {
						// QQ登录成功
						share.setOpenId(openId);
						share.setAccessTokenQQ(accessTokenQQ);
						loginSuccess(json.getString("nickname"), json.getString("avatar"), json.getString("alias"), 3, String.valueOf(json.getInt("id")));
					} else {
						showToast(json);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case QQ_REGISTER:// 处理QQ注册结果
				/*
				 * json = (JSONObject) msg.obj; try { if(json.getInt("ret") ==
				 * 0){ //注册成功之后发送本地登录请求 HashMap<String, String> qqLoginMap = new
				 * HashMap<String, String>(); qqLoginMap.put("openId", openId);
				 * qqLoginMap.put("accessToken", accessToken);
				 * loginAndRegister(loginQqUrl, qqLoginMap, QQ_LOGIN); }else{
				 * Toast.makeText(getApplicationContext(), "登录失败",
				 * Toast.LENGTH_SHORT).show(); } } catch (JSONException e) { //
				 * TODO Auto-generated catch block e.printStackTrace(); }
				 */
				// 注册QQ登录账号之后直接发送登录请求
				HashMap<String, String> qqLoginMap = new HashMap<String, String>();
				qqLoginMap.put("openId", openId);
				qqLoginMap.put("accessToken", accessTokenQQ);
				loginAndRegister(loginQqUrl, qqLoginMap, QQ_LOGIN);
				break;
			case GUEST_LOGIN:// 处理游客登录结果
				
				PDialogUtil.cancelDialog();
				json = (JSONObject) msg.obj;
				try {
					if (json.getInt("ret") == 0) {
						loginSuccess(json.getString("nickname"), json.getString("avatar"), json.getString("alias"), 0, String.valueOf(json.getInt("id")));
					} else {
						showToast(json);
					}
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
				break;
			case GUEST_REGISTER:// 处理游客注册的结果
				json = (JSONObject) msg.obj;
				try {
					if (json.getInt("ret") == 0) {
						if (json.has("guestAccount")) {
							account = json.getString("guestAccount");
						}
						share.setAccount(account);
						share.setGuestPassword(password);
						// 游客注册成功之后发送登录请求
						HashMap<String, String> guestLogin = new HashMap<String, String>();
						// guestLogin.put("guestAccount", account);
						guestLogin.put("password", password);
						guestLogin.put("guestAccount", account);

						loginAndRegister(loginGuestUrl, guestLogin, GUEST_LOGIN);
					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.login_defeat), Toast.LENGTH_SHORT)
								.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 5:
				// 腾讯QQ第三方登录成功
				HashMap<String, String> info = (HashMap<String, String>) msg.obj;
				// 保存数据
				nickName = info.get("name");
				share.setNickName(nickName);
				share.setHead(info.get("head"));
				// 为用户注册账号
				HashMap<String, String> qqRegisterMap = new HashMap<String, String>();
				qqRegisterMap.put("openId", openId);
				qqRegisterMap.put("nickname", nickName);
				loginAndRegister(registerQqUrl, qqRegisterMap, QQ_REGISTER);// 为qq登录用户注册账号
				break;
			case WEIBO_REGISTER:
				// 处理微博注册结果
				//直接登录
				System.out.println("weiboUid="+weiboUid);
				System.out.println("accessToken=="+accessTokenWeibo);
				HashMap<String, String> weiboLoginMap = new HashMap<String, String>();
				weiboLoginMap.put("weiboUid", weiboUid);
				weiboLoginMap.put("accessToken", accessTokenWeibo);
				loginAndRegister(loginWeiboUrl, weiboLoginMap, WEIBO_LOGIN);
				break;
			case WEIBO_LOGIN:
				// 处理微博账号登录结果
				json = (JSONObject) msg.obj;
				try {
					if(json.getInt("ret") == 0){
						loginSuccess(json.getString("nickname"), json.getString("avatar"), json.getString("alias"), 4, String.valueOf(json.getInt("id")));
					}else{
						//登录失败
						showToast(json);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 8:
				//处理第三方微博登录结果
				Bundle value = (Bundle) msg.obj;
				weiboUid = value.getString("uid");
				accessTokenWeibo = value.getString("access_token");
				share.setAccessTokenWeibo(accessTokenWeibo);
				share.setWeiboUid(weiboUid);
				
				//微博账号本地注册
				HashMap<String, String> weiboRegisterMap = new HashMap<String, String>();
				weiboRegisterMap.put("weiboUid", weiboUid);
				weiboRegisterMap.put("nickname", new RandomName(getApplicationContext()).getRandomName());
				loginAndRegister(registerWeiboUrl, weiboRegisterMap, WEIBO_REGISTER);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mTencent = Tencent.createInstance(Constant.APP_ID_TENCENT, getApplicationContext());
		mAuthInfo = new AuthInfo(getApplicationContext(), Constant.APP_KEY_SINA, Constant.REDIRECT_URL,
				Constant.SCOPE);
		initView();

	}

	/**
	 * 初始化
	 */
	private void initView() {
		share = ShareData.getInstance(getApplicationContext());
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		tvForget = (TextView) findViewById(R.id.login_tv_forget);
		tvRegister = (TextView) findViewById(R.id.login_tv_register);
		tvUserTerms = (TextView) findViewById(R.id.login_tv_user_terms);
		tvPrivacyPolicy = (TextView) findViewById(R.id.login_tv_privacy_policy);
		etPassword = (EditText) findViewById(R.id.login_et_password);
		etPhone = (EditText) findViewById(R.id.login_et_phone);
		btnLogin = (Button) findViewById(R.id.login_btn_login);
		btnTourism = (Button) findViewById(R.id.login_btn_tourism);
		imgQQ = (ImageView) findViewById(R.id.login_img_qq);
		imgWeibo = (ImageView) findViewById(R.id.login_img_weibo);
		
		etPhone.setText(share.getPhone());

		tvForget.setOnClickListener(this);
		tvRegister.setOnClickListener(this);
		tvUserTerms.setOnClickListener(this);
		tvPrivacyPolicy.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		btnTourism.setOnClickListener(this);
		imgQQ.setOnClickListener(this);
		imgWeibo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
		}
		if (new MyNetManager(getApplicationContext()).netIsAvailable()) {
			HashMap<String, String> map = null;
			switch (v.getId()) {
			case R.id.login_tv_user_terms:
				//使用条款
				Intent terms = new Intent(ActivityLogin.this, TermsAndPolicy.class);
				terms.putExtra("flag", 1);
				startActivity(terms);
				break;
				
			case R.id.login_tv_privacy_policy:
				//隐私政策
				Intent policy = new Intent(ActivityLogin.this, TermsAndPolicy.class);
				policy.putExtra("flag", 2);
				startActivity(policy);
				break;
				
			case R.id.login_tv_forget:
				// 忘记密码
				Intent forget = new Intent(ActivityLogin.this, ActivityPassword.class);
				forget.putExtra("title", getString(R.string.login_forget_passwrod));
				forget.putExtra("operation", 3);
				startActivity(forget);
				break;

			case R.id.login_tv_register:
				// 注册用户
				Intent register = new Intent(ActivityLogin.this, ActivityPassword.class);
				register.putExtra("title", getString(R.string.login_user_register));
				register.putExtra("operation", 1);
				startActivity(register);
				// 打开注册页面
				/*
				 * RegisterPage registerPage1 = new RegisterPage();
				 * registerPage1.setRegisterCallback(new EventHandler() { public
				 * void afterEvent(int event, int result, Object data) { //
				 * 解析注册结果 if (result == SMSSDK.RESULT_COMPLETE) {
				 * 
				 * @SuppressWarnings("unchecked") HashMap<String,Object>
				 * phoneMap = (HashMap<String, Object>) data; //验证完成之后 Intent
				 * password = new Intent(ActivityLogin.this,
				 * ActivitySetPassword.class); String phone =
				 * phoneMap.get("phone").toString(); String country =
				 * phoneMap.get("country").toString();
				 * password.putExtra("phone", phone);
				 * password.putExtra("country", country);
				 * password.putExtra("flag", 1); startActivity(password);
				 * SMSSDK.submitUserInfo("", "", "", country, phone); } } });
				 * registerPage1.show(ActivityLogin.this);
				 */
				break;

			case R.id.login_btn_tourism:
				// 游客注册
				PDialogUtil.showDialog(ActivityLogin.this, getString(R.string.login_ing), false);
				if (!share.hasGuest()) {
					// 如果当前手机上没有注册过游客账户 进行注册操作
					map = new HashMap<String, String>();
					String currentTime = String.valueOf(System.currentTimeMillis());
					password = currentTime;
					map.put("nickname", new RandomName(getApplicationContext()).getRandomName());
					map.put("password", password);
					loginAndRegister(registerGuestUrl, map, GUEST_REGISTER);
				} else {
					// 直接登录
					map = new HashMap<String, String>();
					account = share.getGuestAccount();
					password = share.getGuestPassword();
					map.put("guestAccount", account);
					map.put("password", password);
					loginAndRegister(loginGuestUrl, map, GUEST_LOGIN);
				}
				break;

			case R.id.login_btn_login:
				// 登录
				
				phone = etPhone.getText().toString().trim();
				if (phone.length() == 11) {
					password = etPassword.getText().toString().trim();
					if (password.length() >= 6) {
						// 发送手机号码登录请求
						PDialogUtil.showDialog(ActivityLogin.this, getString(R.string.login_ing), false);
						map = new HashMap<String, String>();
						map.put("phone", phone);
						map.put("password", password);
						loginAndRegister(loginPhoneUrl, map, PHONE_LOGIN);
					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.login_password_incorrect),
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.login_pphone_incorrect),
							Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.login_img_qq:
				// QQ登录
				if (!mTencent.isSessionValid()) {
					mTencent.login(ActivityLogin.this, "all", loginListener);
				} else {
					// mTencent.releaseResource();
					mTencent.logout(ActivityLogin.this);
					mTencent.login(ActivityLogin.this, "all", loginListener);
				}
				break;

			case R.id.login_img_weibo:
				// 微博登录
				mSsoHandler = new SsoHandler(ActivityLogin.this, mAuthInfo);
				/*if (mSsoHandler == null && mAuthInfo != null) {
					mSsoHandler = new SsoHandler(ActivityLogin.this, mAuthInfo);
				} else {
					new LogoutAPI(ActivityLogin.this, MyConstants.APP_KEY_SINA,
							AccessTokenKeeper.readAccessToken(ActivityLogin.this)).logout(new LogOutRequestListener());
					mSsoHandler = new SsoHandler(ActivityLogin.this, mAuthInfo);
				}*/
				
				//All in one授权
				mSsoHandler.authorize(new AuthListener());
				//网页授权
			//	mSsoHandler.authorizeWeb(new AuthListener());
				//客户端授权
			//	mSsoHandler.authorizeClientSso(new AuthListener());

				break;
			}
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
					.show();
		}
	}

	IUiListener loginListener = new BaseUiListener() {

		@Override
		protected void doComplete(JSONObject values) {
			initOpenidAndToken(values);
			updateUserInfo();
		}
	};

	/**
	 * 实现IUiListener
	 * 
	 * @author YXC
	 * 
	 */
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			JSONObject json = (JSONObject) response;
			try {
				openId = json.getString("openid");
				accessTokenQQ = json.getString("access_token");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null == response) {
				// 返回空 登录失败
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				// 返回为空 登录失败
				return;
			}
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
		}

		@Override
		public void onCancel() {
		}
	}

	public static void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (Exception e) {
		}
	}

	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {
				}

				@Override
				public void onComplete(final Object response) {
					JSONObject json = (JSONObject) response;

					HashMap<String, String> info = new HashMap<String, String>();
					Message msg = new Message();
					msg.what = 5;
					if (json.has("nickname")) {
						try {
							info.put("name", json.getString("nickname"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (json.has("figureurl")) {
						try {
							info.put("head", json.getString("figureurl_qq_2"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					msg.obj = info;
					handler.sendMessage(msg);
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} else {

		}
	}

	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken != null && mAccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(ActivityLogin.this, mAccessToken); // 保存Token
				//登录成功
				Message msg = handler.obtainMessage();
				msg.obj = values;
				msg.what = 8;
				handler.sendMessage(msg);
			} else {
				// 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
				// String code = values.getString("code", "");
			}
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(data, loginListener);
			}
		} else if (requestCode == Constants.REQUEST_APPBAR) {
			if (resultCode == Constants.RESULT_LOGIN) {
				updateUserInfo();
			}
		}
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 手机号码登录 游客注册 游客登录 QQ注册 QQ登录的时候调用此方法
	 * 
	 * @param url
	 *            服务器地址
	 * @param map
	 *            传给服务器的参数
	 * @param flag
	 *            当前所执行的操作 flag = 0:手机号码登录操作 flag = 4:游客注册操作 flag = 3:游客登录操作
	 *            flag = 2:QQ注册 flag = 1:QQ登录
	 */
	private void loginAndRegister(String url, final HashMap<String, String> map, final int flag) {
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// 成功
				Message msg = handler.obtainMessage();
				msg.what = flag;
				msg.obj = response;
				handler.sendMessage(msg);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// 失败
				Toast.makeText(getApplicationContext(), getString(R.string.login_defeat), Toast.LENGTH_SHORT).show();
				PDialogUtil.cancelDialog();
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(request);
	}
	
	/**
	 * 登录成功
	 * @param nickName 昵称
	 * @param avatar  头像
	 * @param alias   别名
	 * @param type    登录方式
	 * @param id      id
	 */
	private void loginSuccess(String nickName, String avatar, String alias, int type, String id){
		new LoginUtil(ActivityLogin.this).getData();
		
		share.setNickName(nickName);
		share.setHead(avatar);
		share.setId(id);
		share.setAlias(alias);
		share.setType(type);
		share.setLogin(true);
		
		//登录环信
		new EaseLogin(getApplicationContext()).init();
		
		
		Intent login = new Intent(ActivityLogin.this, MainActivity.class);
		startActivity(login);
		finish();
	}
	
	/**
	 * 登录失败之后根据服务器返回的ret至提示错误原因
	 * @param json
	 */
	private void showToast(JSONObject json) {
		try {
			int ret = json.getInt("ret");
			if(ret == 300){
				Toast.makeText(getApplicationContext(), R.string.invalid_accout_or_password, Toast.LENGTH_SHORT)
				.show();
			}else if(ret == 301){
				Toast.makeText(getApplicationContext(), R.string.account_locking, Toast.LENGTH_SHORT)
				.show();
			}else if(ret == 302){
				Toast.makeText(getApplicationContext(), R.string.no_account, Toast.LENGTH_SHORT)
				.show();
			}else{
				Toast.makeText(getApplicationContext(), getString(R.string.login_defeat), Toast.LENGTH_SHORT)
				.show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
