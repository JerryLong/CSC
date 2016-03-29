package com.guess.myutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class UpdateUtil extends Thread{
	
	private ApplicationUtil application;
	
	public String updateUrl = "http://api.caisichuan.com/app/latestVersion";
	private Activity context;

	private String mSavePath;// 下载文件存储路径
	private HashMap<String, Object> mHashMap;
	
	private SharedPreferences spf;
	
	private float versionCode = 0;//服务器获取的版本号
	private final String NAME = "guess.apk";
	private int flag = 0;//如果用户自己执行检查更新操作 当没有检测到新版本时 提示用户没有新版本 反之  如果进入程序时程序会判断有没有新版本 如果没有的话则不提示

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				//下载完成 
				installApk();
			}else if(msg.what == 1){
				versionCode = Float.parseFloat(mHashMap.get("version").toString());
				if(versionCode > application.getVersionCode()){
					showUpdateDialog();
				}else{
					if(flag == 1){
						MyAlertDialog.setDialog(context, "当前已是最新版本", false, true);
					}
				}
			}
		};
	};
	/**
	 * 构造方法
	 * @param context
	 * @param flag
	 * flag = 1如果用户自己执行检查更新操作 当没有检测到新版本时 提示用户没有新版本 
	 * flag != 1反之  如果进入程序时程序会判断有没有新版本 如果没有的话则不提示
	 */
	public UpdateUtil(Activity context, int flag) {
		this.context = context;
		application = new ApplicationUtil(context);
		spf = context.getSharedPreferences("update", Context.MODE_PRIVATE);
		this.flag = flag;
	}

	/**
	 * 下载新版本
	 * 
	 * @author YXC
	 * 
	 */
	public void run() {
		try {
			// 判断SD卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 获得存储卡的路径
				String sdpath = Environment.getExternalStorageDirectory() + "/";
				mSavePath = sdpath + "download";
				URL url = new URL(mHashMap.get("url").toString());
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				// 创建输入流
				InputStream is = conn.getInputStream();

				File file = new File(mSavePath);
				// 判断文件目录是否存在
				if (!file.exists()) {
					file.mkdir();
				}
				File apkFile = new File(mSavePath, NAME);
				FileOutputStream fos = new FileOutputStream(apkFile);
				// 缓存
				byte buf[] = new byte[1024];
				// 写入到文件中
				do {
					int numread = is.read(buf);

					if (numread <= 0) {
						// 下载完成
						handler.sendEmptyMessage(0);
						break;
					}
					// 写入文件
					fos.write(buf, 0, numread);
				} while (true);
				fos.close();
				is.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 安装文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, NAME);
		if (!apkfile.exists()) {
			return;
		}
		Intent install = new Intent(Intent.ACTION_VIEW);
		install.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(install);
	}
	
	/**
	 * 检查是否有版本更新
	 * @return
	 */
	public void checkUpdate(){
		MyJsonRequest json = new MyJsonRequest(updateUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						mHashMap = new HashMap<>();
						String strJson = response.getString("version");
						if((!"null".equals(strJson)) && (strJson != null)){
							JSONObject json = new JSONObject(strJson);
							if(json.has("url")){
								mHashMap.put("url", json.getString("url"));
							}else{
								mHashMap.put("url", "");
							}
							if(json.has("version")){
								mHashMap.put("version", json.get("version"));
							}else{
								mHashMap.put("version", "");
							}
							if(json.has("time")){
								mHashMap.put("time", json.getString("time"));
							}else{
								mHashMap.put("time", "");
							}
							if(json.has("remark")){
								mHashMap.put("remark", json.getString("remark"));
							}else{
								mHashMap.put("remark", "有新版本 立即更新?");
							}
							handler.sendEmptyMessage(1);
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
				versionCode = 0;
			}
		});
		application.getRequestQueue().add(json);
	}
	
	/**
	 * 判断用户是否点击过不再提示更新
	 * @return
	 */
	public boolean isRemind(){
		boolean remind = spf.getBoolean("remind", true);
		return remind;
	}
	
	/**
	 * 显示是否更新对话框
	 */
	private void showUpdateDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("版本更新").setMessage(mHashMap.get("remark").toString());
		dialog.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				start();
			}
		});
		dialog.setNegativeButton("不再提醒", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = spf.edit();
				editor.putBoolean("update", true);
				editor.commit();
				
				dialog.dismiss();
			}
		});
	}
}
