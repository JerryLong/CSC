package com.guess.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myutils.MyJsonRequest;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

public class PublicTools {
	static String url = null;

	public static String getImageUrl(int userId, boolean isVideo) {
		StringBuffer str2 = new StringBuffer();
		String path = "http://caichengdu.qiniudn.com/";
		str2.append(path);
		str2.append(url);
		return str2.toString();
	}

	@SuppressLint("SimpleDateFormat")
	public static String setUrl(int userId, boolean isVideo) {
		StringBuffer str1 = new StringBuffer();
		StringBuffer str = new StringBuffer();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		str1.append(date);
		str1.append(" ");
		str1.append(userId);
		date = str1.toString();
		byte[] bt = date.getBytes();
		String ss = Base64.encodeToString(bt, Base64.NO_WRAP);
		if (isVideo) {
			str.append("video/");
			str.append(ss += "==");
			str.append(".mp4");
		} else {
			str.append("normal/");
			str.append(ss += "==");
			str.append(".png");
		}

		return str.toString();
	}

	public static void upLoadFile(final Context context, final int userId, String path, String uploadTtoken,
			final boolean isVideo, final UploadFinishListener afterUpload) {

		// 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
		UploadManager uploadManager = new UploadManager();
		File data = new File(path);
		String key = setUrl(userId, isVideo);
		String token = uploadTtoken;
		uploadManager.put(data, key, token, new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject res) {
				Logs.i("String key  ==  " + key + "  ==info==  " + info + "  ==res==  " + res);

				if (info.statusCode == 200) {
					url = key;
					System.out.println("String key  ==  " + key + "  ==info==  " + info + "  ==res==  " + res
							+ "   ===  " + getImageUrl(userId, isVideo));
					afterUpload.onAfterUpload();
				} else {
					Toast.makeText(context, "上传失败 请稍后重试", Toast.LENGTH_SHORT).show();
				}

			}
		}, null);
	}

	public static void upload(final Context context, final int userId, final String path, final boolean isVideo,
			final UploadFinishListener afterUpload) {

		HashMap<String, String> map = new HashMap<String, String>();
		String url = "http://api.caisichuan.com/app/uploadToken";
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						if (response.getBoolean("success")) {
							String upLoadToken = response.getString("uploadToken");
							System.out.println("upLoadToken  ===" + upLoadToken);
							upLoadFile(context, userId, path, upLoadToken, isVideo, afterUpload);
						}
					} else if (response.getInt("ret") == Constant.NOT_LOGIN) {
						LoginUtil login = new LoginUtil(context);
						login.login(new LoginListener() {

							@Override
							public void onLoginAfter() {
								// TODO Auto-generated method stub
								upload(context, userId, path, isVideo, afterUpload);
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}, map);
		new ApplicationUtil(context).getRequestQueue().add(request);

	}

	public static void setPraise(ApplicationUtil mApplicationUtil, int quizId, int evaluation) {

		String url = UrlContants.LOCATION + UrlContants.NCWC_PRAISE;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("quizId", String.valueOf(quizId));
		if (evaluation == 0) {
			map.put("evaluation", "1");
		} else {
			map.put("evaluation", "0");
		}
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Toast.makeText(getBaseContext(),
				// VolleyErrorHelper.getMessage(error, AnswerActivity.this),
				// Toast.LENGTH_LONG).show();
			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);
	}

	public interface UploadFinishListener {
		public void onAfterUpload();
	}

}
