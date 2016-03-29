package com.guess.shareutil;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cdrongyao.caisichuan.R;
import com.guess.myutils.AccessTokenKeeper;
import com.guess.myutils.Constant;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class OneKeyShare extends Activity {

	/*
	 * 
	 * 调用分享的Activity需要重些onNewIntent()方法 并在此方法里加入下面这句代码
	 * mWeiboShareApi.handleWeiboResponse(intent, this); //当前应用唤起微博分享后，返回当前应用
	 * 
	 * 
	 * 微博第三方账号登录 第一步: 应用签名 将demo中lib目录下的对应的全部 libweibosdkcore.so文件目录 拷贝到你的目标工程中
	 * 第二步: 导入weiboSdkCore.jar 工程右键->properties->java build path->libraries->add
	 * external jar 第三步: 添加权限 <uses-permission
	 * android:name="android.permission.INTERNET" /> <uses-permission
	 * android:name="android.permission.ACCESS_WIFI_STATE" /> <uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE" />
	 * <uses-permission
	 * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> 第四步: 开始授权
	 * 
	 * @author YXC
	 */

	private String shareTitle;
	private String shareText;
	private Bitmap shareBmp;

	/** 微博分享 */

	public IWeiboShareAPI mWeiboShareApi;
	AuthInfo mAuthInfo;
	Oauth2AccessToken mAccessToken;

	/** 微信分享 */
	public static IWXAPI wxApi;

	// 微信APPID
	public final String WEIXIN_APP_ID = Constant.APP_ID_WEIXIN;

	// 微博相关信息
	private final String WEIBO_APP_KEY = Constant.APP_KEY_SINA; // 应用的APP_KEY
	private final String REDIRECT_URL = Constant.REDIRECT_URL;// 应用的回调页
	private final String SCOPE = Constant.SCOPE;// 应用申请的高级权限

	private LinearLayout lltWeFriens, lltWechat, lltSina;
	private Button btnCancel;
	private AlertDialog dialog;

	private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/shareImg/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 注册微信
		wxApi = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, false);
		wxApi.registerApp(WEIXIN_APP_ID);

		// 注册微博
		mAuthInfo = new AuthInfo(getApplicationContext(), WEIBO_APP_KEY, REDIRECT_URL, SCOPE);
		mWeiboShareApi = WeiboShareSDK.createWeiboAPI(this, WEIBO_APP_KEY);
		mWeiboShareApi.registerApp();
		super.onCreate(savedInstanceState);
	}

	/**
	 * 微博分享初始化
	 * 
	 * @param text
	 *            文字
	 * @param bmp
	 *            图片
	 */
	private void onWeiboShare(String text, Bitmap bmp) {

		sendMultiMessage(text, bmp);
	}

	/**
	 * 微博分享
	 * 
	 * @param text
	 *            文字
	 * @param bmp
	 *            图片
	 */
	private void sendMultiMessage(String text, Bitmap bmp) {
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();// 初始化微博的分享消息

		TextObject object = new TextObject();
		object.text = text;
		weiboMessage.textObject = object;

		ImageObject object1 = new ImageObject();
		object1.setImageObject(bmp);
		weiboMessage.imageObject = object1;

		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		// mWeiboShareApi.sendRequest(this, request); // 发送请求消息到微博，唤起微博分享界面
		mWeiboShareApi.sendRequest(this, request, mAuthInfo, "", new AuthListener());
	}

	/**
	 * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
	 * 
	 * @param flag
	 *            (0:分享到微信好友，1：分享到微信朋友圈)
	 */
	private void wechatShare(int flag, String title, String text, Bitmap bmp) {
		if (!wxApi.isWXAppInstalled()) {
			Toast.makeText(this, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		WXMediaMessage msg;
		if(flag == 1){
			//分享到微信朋友圈  只分享图片
			msg = new WXMediaMessage();
			WXImageObject imgObj = new WXImageObject();
			imgObj.imagePath = setPicToView(bmp);
			msg.mediaObject = imgObj;
		}else{
			//分享给微信好友  链接加文字
			WXWebpageObject webObj = new WXWebpageObject();
			webObj.webpageUrl = Constant.WEB_URL;
			msg = new WXMediaMessage(webObj);
			msg.description = text;
			msg.title = title;
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis() + "");
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

	/**
	 * 弹出分享框
	 */
	protected void onShare(String title, String text, Bitmap bmp) {
		this.shareTitle = title;
		this.shareText = text;
		this.shareBmp = bmp;
		dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.share_dialog);
		btnCancel = (Button) window.findViewById(R.id.share_cancel);
		lltSina = (LinearLayout) window.findViewById(R.id.share_sina);
		lltWechat = (LinearLayout) window.findViewById(R.id.share_we_chat);
		lltWeFriens = (LinearLayout) window.findViewById(R.id.share_we_friends);

		btnCancel.setOnClickListener(listener);
		lltSina.setOnClickListener(listener);
		lltWechat.setOnClickListener(listener);
		lltWeFriens.setOnClickListener(listener);
	}

	/**
	 * 点击监听函数
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.share_cancel:

				break;

			case R.id.share_we_chat:
				// 分享到微信好友
				wechatShare(0, shareTitle, shareText, shareBmp);
				break;
			case R.id.share_sina:
				// 分享到新浪微博
				// Toast.makeText(getApplicationContext(), "分享到新浪微博",
				// Toast.LENGTH_SHORT).show();
				onWeiboShare(shareText, shareBmp);
				break;
			case R.id.share_we_friends:
				// 分享到微信朋友圈
				wechatShare(1, shareTitle, shareText, shareBmp);
				break;
			}
			dialog.dismiss();
		}
	};

	/**
	 * 微博授权
	 * 
	 * @author YXC
	 *
	 */
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {

			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken != null && mAccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(OneKeyShare.this, mAccessToken); // 保存Token
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

	/**
	 * 保存图片到手机
	 * @param mBitmap
	 * @return
	 */
	private String setPicToView(Bitmap mBitmap) {
		
		String fileName = path + System.currentTimeMillis() + ".jpg";
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 内存不可用
			return "";
		}
		BufferedOutputStream bos = null;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();// 创建文件夹
		}
		
		File file1 = new File(fileName);
		try {
			if (!file1.exists()) {
				file1.createNewFile();

			} else {
				file1.delete();
				file1.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file1));
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			try {
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return fileName;
	}
}