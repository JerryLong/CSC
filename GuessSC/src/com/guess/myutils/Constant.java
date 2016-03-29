package com.guess.myutils;

import com.easemob.easeui.EaseConstant;

/**
 * 存储常量
 * 
 * @author YXC
 *
 */
public class Constant extends EaseConstant {

	public static final int NOT_LOGIN = -1004;//未登录代码
	
	// 腾讯平台的APPID和APPKEY
	public static final String APP_ID_TENCENT = "1103513934";
	// public static final String APP_ID_TENCENT = "222222";
	public static final String APP_KEY_TENCENT = "jMoD3ozS7Y1CihbG";
	
	public static final String APP_ID_WEIXIN = "wxb21e7dc4b06ede85";
	public static final String APP_SECRET_WEIXIN = "0cfafa0e350a3f6c0c09fa6856eb5403";
	
	//ShareSDK平台的APP_KEY 和 APP_SECRET

	// ShareSDK平台的APP_KEY 和 APP_SECRET
	public static final String APP_KEY_SHARE = "457ff918aa9c";
	public static final String APP_SECRET_SHARE = "e74bc9b5d1eb5246d97cb10f1e9f3384";

	// 环信的AppKey
	public static final String APP_KEY_EASA = "cdrongyao#caisichuan";

	// 新浪平台的APPKEY
	public static final String APP_KEY_SINA = "3830290540";

	/**
	 * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
	 * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
	 */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	/**
	 * WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。 详情请查看 Demo 中对应的注释。
	 */
	public static final String SCOPE = null;

	public static final String AVATAR = "http://img1.touxiang.cn/uploads/20121224/24-054837_708.jpg";
    
    //官网
    public static final String WEB_URL = "http://www.caisichuan.com/";
}
