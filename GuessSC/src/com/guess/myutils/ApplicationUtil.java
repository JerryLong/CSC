package com.guess.myutils;

import java.io.File;
import java.math.BigDecimal;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.cdrongyao.caisichuan.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

/**
 * 系统工具类
 * 
 * @author YXC
 *
 */
@SuppressWarnings("deprecation")
public class ApplicationUtil {
	private Context context;
	public static RequestQueue mRequestQueue;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public ApplicationUtil(Context context) {
		this.context = context;
	}
	
	public RequestQueue getRequestQueue(){
		if(mRequestQueue == null){
			DefaultHttpClient client = new DefaultHttpClient();
			CookieStore store = new CookieShare(context);
			client.setCookieStore(store);
			HttpStack httpStak = new HttpClientStack(client);
			mRequestQueue = Volley.newRequestQueue(context, httpStak);
		}
		return mRequestQueue;
	}

	/**
	 * 获取应用名称
	 * 
	 * @return
	 */
	public String getApplicationName() {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
			return packageManager.getApplicationLabel(applicationInfo).toString();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取应用图标
	 * 
	 * @return
	 */
	public Drawable getApplicationIcon() {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
			return packageManager.getApplicationIcon(applicationInfo);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return context.getDrawable(R.drawable.ic_launcher);
	}

	/**
	 * 获取应用版本名称
	 * 
	 * @return
	 */
	public String getVersionName() {
		PackageManager packageManager = context.getPackageManager();
		try {
			// applicationInfo =
			// packageManager.getApplicationInfo(context.getPackageName(), 0);
			return packageManager.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "1.0";
	}

	/**
	 * 获取应用版本号
	 * 
	 * @return
	 */
	public int getVersionCode() {
		PackageManager packageManager = context.getPackageManager();
		try {
			// applicationInfo =
			// packageManager.getApplicationInfo(context.getPackageName(), 0);
			return packageManager.getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 获取应用缓存大小
	 * 
	 * @return
	 */
	public String getTotalCacheSize() {
		long cacheSize = 0;
		try {
			cacheSize = getFolderSize(context.getCacheDir());
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				cacheSize += getFolderSize(context.getExternalCacheDir());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getFormatSize(cacheSize);
	}

	/**
	 * 缓存格式化
	 * 
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			// return size + "Byte";
			return "0MB";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}

	/**
	 * 获取文件缓存大小
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 清除缓存
	 */
	public void clearAllCache() {
		deleteDir(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteDir(context.getExternalCacheDir());
		}
	}

	private boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * 加载单张图片
	 * 
	 * @param imgView
	 *            要显示所加载图片的Imageview
	 * @param url
	 *            图片的url
	 */
	public void getBitmap1(final ImageView imgView, String url) {

		ImageRequest imageRequest = new ImageRequest(url, new Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {
				// TODO Auto-generated method stub
				imgView.setImageBitmap(response);
			}
		}, 120, 120, Config.RGB_565, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
			}
		});
		mRequestQueue.add(imageRequest);
	}

}
