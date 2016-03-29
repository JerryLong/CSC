package com.easemob.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

@SuppressLint("HandlerLeak")
public class MyBDLocation {
	private static MyBDLocation bdLocation;
	public static BDLocation lastLocation;
	private Context context;

	private LocationClient mLocationClient;
	private BDLocationListener myListener;

	private LocationFinishListener listener;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				listener.onLocationFinish();
			}
		};
	};

	private MyBDLocation(Context context) {
		this.context = context;
		init();
	}

	public static MyBDLocation getInstance(Context context) {
		if (bdLocation == null) {
			bdLocation = new MyBDLocation(context);
			return bdLocation;
		}
		return bdLocation;
	}

	public void registerLocation(LocationFinishListener listener) {
		mLocationClient.requestLocation();
		this.listener = listener;
	}

	public void init() {
		myListener = new MyLocationListener();
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.start();

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");

		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
	}

	public void unRegisterLocation() {
		if(mLocationClient != null){
			mLocationClient.unRegisterLocationListener(myListener);
			mLocationClient.stop();
		}
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(com.baidu.location.BDLocation location) {

			if (location == null) {
				return;
			}
			lastLocation = location;
			handler.sendEmptyMessage(0);
		}

	}

	public interface LocationFinishListener {
		public void onLocationFinish();
	}

}
