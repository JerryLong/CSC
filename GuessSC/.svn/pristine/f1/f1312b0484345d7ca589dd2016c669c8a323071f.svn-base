package com.guess.message;

import java.util.Iterator;
import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.guess.myutils.ShareData;
import com.guess.utils.Md5;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

/**
 * 本地账号登录之后登录环信账号
 * new EaseLogin(getApplicationContext(), username, passwrod).login();
 * 即可
 * @author YXC
 *
 */
public class EaseLogin {
	
	private ShareData share;
	
	private Context context;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				//EaseHelper.getInstance().initEase(context);
				EMChatManager.getInstance().loadAllConversations();
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						//设置ios推送昵称  必须在子线程中执行
						EMChatManager.getInstance().updateCurrentUserNick(share.getNickName());
					}
				}).start();
				
			}
		};
	};
	
	/**
	 * 构造方法
	 * @param context  上下文对象
	 */
	public EaseLogin(Context context){
		share = ShareData.getInstance(context);
		this.context = context;
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		// 初始化
		
		//EaseUI.getInstance().init(context);
		set();
		
		//EMChat.getInstance().init(context);
		//EMChat.getInstance().setAppInited();
		
		
		login();
	}
	
	/**
	 * 应用有第三方服务(如百度地图) 
	 * 需要在初始化EMChat前调用这个方法
	 */
	private void set(){
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果app启用了远程的service，此application:onCreate会被调用2次
		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

		if (processAppName == null ||!processAppName.equalsIgnoreCase("com.rongyao.guess")) {
		    //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
		    
		    // 则此application::onCreate 是被service 调用的，直接返回
		    return;
		}
	}
	/**
	 * 获取应用名
	 * @param pID
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = context.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					@SuppressWarnings("unused")
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

	/**.
	 * 登录
	 */
	public void login() {
		ShareData share = ShareData.getInstance(context);
		int type = share.getType();
		String userName = share.getId();
		String password = "";
		if(type == 0){
			password = Md5.MD5Encode(share.getGuestPassword());
		}else if(type == 2){
			password = Md5.MD5Encode(share.getPhonePassword());
		}else if(type == 3){
			password = Md5.MD5Encode(Md5.MD5Encode(share.getId()));
		}else if(type == 4){
			password = Md5.MD5Encode(Md5.MD5Encode(share.getId()));
		}
		EMChatManager.getInstance().login(userName, password, new EMCallBack() {// 回调
			@Override
			public void onSuccess() {
				new Thread(new Runnable() {
					public void run() {
						handler.sendEmptyMessage(0);
					}
				}).start();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(int code, String message) {
				login();
				//Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
			}
		});
	}
	

}
