package com.guess.myutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * the net manager
 * @author YXC
 *
 */
public class MyNetManager {
	//上下文对象
	private Context myContext;
	//网络管理对象
	private ConnectivityManager connManager;
	
	//构造方法
	public MyNetManager(Context context){
		myContext = context;
	}
	/**
	 * state of current net
	 * @return 
	 * 	return true if net is available otherwise false
	 */
	public boolean netIsAvailable(){
		connManager = (ConnectivityManager)myContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
		// NetworkInfo对象
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();   
		if(networkInfo != null){  
			return networkInfo.isAvailable();  
		}else{  
			return false;  
		}  			 
	}
	
	/**
	 * 判断当前网络是不是WiFi
	 */
	public boolean isWifi(){
		NetworkInfo[] networkInfos = connManager.getAllNetworkInfo();
		for(int i = 0; i < networkInfos.length; i++){
			if(networkInfos[i].getState() == NetworkInfo.State.CONNECTED){
				if(networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI){
					return true;
				}
			}
		}
		return false;
	}
}
