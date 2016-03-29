package com.guess.myutils;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChat;
import com.guess.message.EaseLogin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

@SuppressLint("HandlerLeak")
public class LoginUtil {
	
	private ApplicationUtil application;
	
	private final int LOGIN_SUCCESS = 0;
	private Context context;
	private LoginListener mLoginListener;
	
	private ShareData share;
	
	private String loginQqUrl = "http://api.caisichuan.com/user/qqLogin"; // QQ登录地址
	private String loginGuestUrl = "http://api.caisichuan.com/user/guestLogin";// 游客登录地址
	private String loginPhoneUrl = "http://api.caisichuan.com/user/phoneLogin";// 电话登录地址
	private String loginWeiboUrl = "http://api.caisichuan.com/user/weiboLogin";// 微博登录

	//加载用户信息的url
	private String infoUrl = "http://api.caisichuan.com/user/info";
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == LOGIN_SUCCESS){
				mLoginListener.onLoginAfter();
				
			}
		};
	};
	/**
	 * 构造方法
	 * @param context
	 */
	public LoginUtil(Context context){
		this.context = context;
		share = ShareData.getInstance(context);
		application = new ApplicationUtil(context);
	}
	
	/**
	 * 登录
	 * @param listener 登录成功之后执行的操作
	 */
	public void login(LoginListener listener){
		mLoginListener = listener;
		String serverUrl = "";
		HashMap<String, String> map = new HashMap<>();
		int type = share.getType();
		if(type == 0){
			//游客账号
			serverUrl = loginGuestUrl;
			map.put("guestAccount", share.getGuestAccount());
			map.put("password", share.getGuestPassword());
		}else if(type == 2){
			//电话账号
			serverUrl = loginPhoneUrl;
			map.put("phone", share.getPhone());
			map.put("password", share.getPhonePassword());
		}else if(type == 3){
			//QQ账号
			serverUrl = loginQqUrl;
			map.put("openId", share.getOpenId());
			map.put("accessToken", share.getAccessTokenQQ());
		}else if(type == 4){
			//微博账号
			serverUrl = loginWeiboUrl;
			map.put("weiboUid", share.getWeiboUid());
			map.put("accessToken", share.getAccessTokenWeibo());
		}
		loginServer(serverUrl, map);
		
		//登录环信
		if(!EMChat.getInstance().isLoggedIn()){
			new EaseLogin(context).init();
		}
	}
	
	/**
	 * 登录函数
	 * @param url 服务器地址
	 * @param map 参数
	 */
	private void loginServer(final String url, final HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						//登录成功
						handler.sendEmptyMessage(LOGIN_SUCCESS);
					}else{
						//登录失败 再次登录
						loginServer(url, map);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				//再次执行登录操作
				loginServer(url, map);
			}
		}, map);
		new ApplicationUtil(context).getRequestQueue().add(json);
	}
	
	/**
	 * 接口
	 * @author YXC
	 *
	 */
	public interface LoginListener{
		/**
		 * 登录成功之后执行此方法
		 * 调用本类的login方法必须实现此方法
		 */
		public void onLoginAfter();
	}
	
	/**
	 * 获取用户信息
	 * @param url
	 */
	public void getData(){
		HashMap<String, String> map = new HashMap<>();//不需要传参数
		MyJsonRequest jsonRequest = new MyJsonRequest(infoUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						String level = String.valueOf(response.getInt("level"));
						String avatar = response.getString("avatar");
						String fan = response.getString("fanCount");
						String gold = String.valueOf(response.getInt("gold"));
						String nickName = response.getString("nickname");
						int type = response.getInt("type");
						String address = response.getString("address");
						String phoen = response.getString("phone");
						String account = response.getString("guestAccount");
						
						share.setAccount(account);
						share.setIntegration(level);
						share.setAddress(address);
						share.setCoin(gold);
						share.setHead(avatar);
						share.setNickName(nickName);
						share.setFan(fan);	
						share.setPhone(phoen);
						share.setType(type);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				login(new LoginListener() {
					
					@Override
					public void onLoginAfter() {
						// TODO Auto-generated method stub
						getData();
					}
				});
			}
		}, map);
		
		application.getRequestQueue().add(jsonRequest);
	}
}
