package com.guess.myutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 声明共享数据存储类
 * 
 * @author YXC
 * 
 */
public class ShareData {
	// 用户id
	private String id;

	// 我的收获地址
	private String myAddress;
	// 昵称
	private String nickName;
	// 手机号码
	private String myPhone;
	// 当前是否登录
	private boolean login;
	// 用户积分
	private String integration;
	// 用户粉丝量
	private String fan;
	// 用户金币
	private String coin;
	// 头像
	private String head;
	// 是否开启声音
	private boolean voice;
	// 用于极光推送的别名?
	private String alias = "";
	// 是否是第一次登录
	private boolean firstLogin;
	// 用于微博登录
	private String weiboUid, accessTokenWeibo;
	private String openId, accessTokenQQ;

	// 账号类型0:游客账号 1:邮箱账号 2:电话账号 3:QQ账号 4:微博账号
	private int type;

	private static ShareData share;
	private String account, guestPassword, phonePassword;// 账号和密码
	
	private String rank, up;//四川达人的排名和超越多少人

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
		Editor editor = spf.edit();
		editor.putString("rank", rank);
		editor.commit();
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
		Editor editor = spf.edit();
		editor.putString("up", up);
		editor.commit();
	}

	// 数据存储对象
	private SharedPreferences spf;

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static ShareData getInstance(Context context) {
		if (share == null) {
			synchronized (ShareData.class) {
				if (share == null) {
					share = new ShareData(context);
				}
			}
		}
		return share;
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 */
	private ShareData(Context context) {
		// 初始化成员变量
		spf = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
		myAddress = spf.getString("address", "");
		nickName = spf.getString("name", "");
		myPhone = spf.getString("phone", "");
		integration = spf.getString("integration", "0");
		login = spf.getBoolean("login", false);
		head = spf.getString("head", "");
		fan = spf.getString("fan", "0");
		coin = spf.getString("coin", "0");
		type = spf.getInt("type", 2);
		voice = spf.getBoolean("voice", false);
		id = spf.getString("id", "-1");
		account = spf.getString("account", "");
		alias = spf.getString("alias", "");
		guestPassword = spf.getString("guestPassword", "");
		phonePassword = spf.getString("phonePassword", "");
		weiboUid = spf.getString("uid", "");
		accessTokenWeibo = spf.getString("accesstokenweibo", "");
		openId = spf.getString("openid", "");
		accessTokenQQ = spf.getString("accesstokenqq", "");
		firstLogin = spf.getBoolean("firstLogin", true);
		rank = spf.getString("rank", "0");
		up = spf.getString("up", "0");
	}

	/**
	 * 判断当前手机上是否注册过游客账号
	 * 
	 * @return
	 */
	public boolean hasGuest() {
		if (!"".equals(account)) {
			return true;
		}
		return false;
	}

	/**
	 * 返回本地账号
	 * 
	 * @return
	 */
	public String getGuestAccount() {
		return account.equals("null") ? "" : account;
	}

	/**
	 * 存储本地账号
	 * 
	 * @param account
	 */
	public void setAccount(String account) {
		if ((!"null".equals(account)) && (account != null)) {
			this.account = account;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("account", account);
			editor.commit();
		}
	}

	/**
	 * 返回游客登录密码
	 * 
	 * @return
	 */
	public String getGuestPassword() {
		return guestPassword.equals("null") ? "" : guestPassword;
	}

	/**
	 * 存储游客登录密码
	 * 
	 * @param password
	 */
	public void setGuestPassword(String guestPassword) {
		this.guestPassword = guestPassword;
		Editor editor = spf.edit();
		editor.putString("guestPassword", guestPassword);
		editor.commit();
	}

	/**
	 * 返回手机号码登录密码
	 * 
	 * @return
	 */
	public String getPhonePassword() {
		return phonePassword.equals("null") ? "" : phonePassword;
	}

	/**
	 * 存储手机号码登录密码
	 * 
	 * @param password
	 */
	public void setPhonePassword(String phonePassword) {
		this.phonePassword = phonePassword;
		Editor editor = spf.edit();
		editor.putString("phonePassword", phonePassword);
		editor.commit();
	}

	public boolean isVoice() {
		return voice;
	}

	public void setVoice(boolean voice) {
		this.voice = voice;
		Editor editor = spf.edit();
		editor.putBoolean("voice", voice);
		editor.commit();
	}

	/**
	 * 返回用于极光推送的别名
	 * 
	 * @return
	 */
	public String getAlias() {
		return alias.equals("null") ? "" : alias;
	}

	public void setAlias(String alias) {
		if ((alias != null) && (!"".equals(alias))) {
			this.alias = alias;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("alias", alias);
			editor.commit();
		}
	}

	/**
	 * 返回用户id
	 * 
	 * @return
	 */
	public String getId() {
		return id.equals("null") ? "-1" : id;
	}

	/**
	 * 设置用户id
	 * 
	 * @param id
	 */
	public void setId(String id) {
		if ((id != null) && (!"".equals(id))) {
			this.id = id;
			Editor editor = spf.edit();
			editor.putString("id", id);
			editor.commit();
		}
	}

	/**
	 * 返回微博登录的Uid
	 * 
	 * @return
	 */
	public String getWeiboUid() {
		return weiboUid;
	}

	/**
	 * 设置微博登录的Uid
	 * 
	 * @param weiboUid
	 */
	public void setWeiboUid(String weiboUid) {
		if ((weiboUid != null) && (!"".equals(weiboUid))) {
			this.weiboUid = weiboUid;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("uid", weiboUid);
			editor.commit();
		}
	}

	/**
	 * 返回微博登录的accessToken
	 * 
	 * @return
	 */
	public String getAccessTokenWeibo() {
		return accessTokenWeibo;
	}

	/**
	 * 设置微博登录的accessToken
	 * 
	 * @param accessTokenWeibo
	 */
	public void setAccessTokenWeibo(String accessTokenWeibo) {
		if ((accessTokenWeibo != null) && (!"".equals(accessTokenWeibo))) {
			this.accessTokenWeibo = accessTokenWeibo;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("accesstokenweibo", accessTokenWeibo);
			editor.commit();
		}
	}

	/**
	 * 返回QQ登录的openId
	 * 
	 * @return
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * 设置QQ登录的openId
	 * 
	 * @param weiboUid
	 */
	public void setOpenId(String openId) {
		if ((openId != null) && (!"".equals(openId))) {
			this.openId = openId;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("openid", openId);
			editor.commit();
		}
	}

	/**
	 * 返回QQ登录的accessToken
	 * 
	 * @return
	 */
	public String getAccessTokenQQ() {
		return accessTokenQQ;
	}

	/**
	 * 设置微博登录的accessToken
	 * 
	 * @param accessTokenQQ
	 */
	public void setAccessTokenQQ(String accessTokenQQ) {
		if ((accessTokenQQ != null) && (!"".equals(accessTokenQQ))) {
			this.accessTokenQQ = accessTokenQQ;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("accesstokenqq", accessTokenQQ);
			editor.commit();
		}
	}

	/**
	 * 是否是第一次登录
	 * 
	 * @return
	 */
	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
		// 存储数据
		Editor editor = spf.edit();
		editor.putBoolean("firstLogin", firstLogin);
		editor.commit();
	}

	/**
	 * 返回当前登录方式 0:游客账号 1:邮箱账号 2:电话账号 3:QQ账号 4:微博账号
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置当前登录方式 0:游客账号 1:邮箱账号 2:电话账号 3:QQ账号 4:微博账号
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
		// 存储数据
		Editor editor = spf.edit();
		editor.putInt("type", type);
		editor.commit();
	}

	/**
	 * 返回用户积分
	 * 
	 * @return
	 */
	public String getIntegration() {
		return integration.equals("null") ? "0" : integration;
	}

	/**
	 * 存储用户积分
	 * 
	 * @param jifen
	 */
	public void setIntegration(String integration) {
		this.integration = integration;
		// 存储数据
		Editor editor = spf.edit();
		editor.putString("integration", integration);
		editor.commit();
	}

	/**
	 * 返回用户金币
	 * 
	 * @return
	 */
	public String getCoin() {
		
		return coin.equals("null") ? "0" : coin;
	}

	/**
	 * 存储用户金币
	 * 
	 * @param coin
	 */
	public void setCoin(String coin) {
		this.coin = coin;
		// 存储数据
		Editor editor = spf.edit();
		editor.putString("coin", coin);
		editor.commit();
	}

	/**
	 * 返回用户粉丝
	 * 
	 * @return
	 */
	public String getFan() {
		return fan.equals("null") ? "0" : fan;
	}

	/**
	 * 存储用户积分
	 * 
	 * @param fan
	 */
	public void setFan(String fan) {
		this.fan = fan;
		// 存储数据
		Editor editor = spf.edit();
		editor.putString("fan", fan);
		editor.commit();
	}

	public String getHead() {
		return head.equals("null") ? "" : head;
	}

	public void setHead(String head) {
		if ((head != null) && (!"".equals(head))) {
			this.head = head;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("head", head);
			editor.commit();
		}
	}

	/**
	 * 获得地址myAddress
	 * 
	 * @return myAddress
	 */
	public String getAddress() {
		return myAddress.equals("null") ? "" : myAddress;
	}

	/**
	 * 设置地址 myAddress
	 * 
	 * @param myAddress
	 *            地址
	 */
	public void setAddress(String myAddress) {
		this.myAddress = myAddress;
		// 存储数据
		Editor editor = spf.edit();
		editor.putString("address", myAddress);
		editor.commit();
	}

	/**
	 * 获得用户名userName
	 * 
	 * @return userName
	 */
	public String getNickName() {
		return nickName.equals("null") ? "" : nickName;
	}

	/**
	 * 设置用户名 userName
	 * 
	 * @param userName
	 *            用户名
	 */
	public void setNickName(String nickName) {
		if ((nickName != null) && (!"".equals(nickName))) {
			this.nickName = nickName;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("name", nickName);
			editor.commit();
		}
	}

	/**
	 * 获得账户号码
	 * 
	 * @return myPhone
	 */
	public String getPhone() {
		return myPhone;
	}

	/**
	 * 设置账户号码
	 * 
	 * @param myPhone
	 *            电话号码
	 */
	public void setPhone(String myPhone) {
		if ((myPhone != null) && (!"".equals(myPhone)) && (!"null".equals(myPhone))) {
			this.myPhone = myPhone;
			// 存储数据
			Editor editor = spf.edit();
			editor.putString("phone", myPhone);
			editor.commit();
		}
	}

	/**
	 * 获得用户登录状态
	 * 
	 * @return login
	 */
	public boolean getLogin() {
		return login;
	}

	/**
	 * 设置用户登录状态
	 * 
	 * @param state
	 *            登录状态
	 */
	public void setLogin(boolean state) {
		this.login = state;
		// 存储数据
		Editor editor = spf.edit();
		editor.putBoolean("login", login);
		editor.commit();
	}
}
