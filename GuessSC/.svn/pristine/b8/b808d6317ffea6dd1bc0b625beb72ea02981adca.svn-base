package com.guess.message;

import java.util.List;

import com.cdrongyao.caisichuan.R;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseSettingsProvider;
import com.easemob.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.guess.database.EaseUserInfo;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.ShareData;

import android.content.Context;
import android.content.Intent;

public class EaseHelper{
	private EaseUserInfo userInfo;
	private ShareData share;
	private static EaseHelper instance;
	private Context context;
	private ApplicationUtil application;
	EaseUI easeUI;
	private EMConnectionListener connectionListener;
	protected EMEventListener eventListener = null;

	/*public final String AVATAR = "http://img1.touxiang.cn/uploads/allimg/111029/2330264224-36.png";
	public final String AVATAR1 = "http://img1.touxiang.cn/uploads/20130608/08-054059_703.jpg";
	public final String AVATAR2 = "http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg";*/

	public synchronized static EaseHelper getInstance() {
		if (instance == null) {
			instance = new EaseHelper();
		}
		return instance;
	}

	public void initEase(Context context) {
		if (EaseUI.getInstance().init(context)) {
			this.context = context;
			userInfo = new EaseUserInfo(context);
			share = ShareData.getInstance(context);
			application = new ApplicationUtil(context);
			// 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
			EMChat.getInstance().setDebugMode(false);
			// get easeui instance
			easeUI = EaseUI.getInstance();
			// 调用easeui的api设置providers
			setEaseUIProviders();
			registerEventListener();
			setReConnect();
		}
	}

	private void setEaseUIProviders() {
		easeUI.setSettingsProvider(new EaseSettingsProvider() {
			
			@Override
			public boolean isSpeakerOpened() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isMsgVibrateAllowed(EMMessage message) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isMsgSoundAllowed(EMMessage message) {
				return share.isVoice();
			}
			
			@Override
			public boolean isMsgNotifyAllowed(EMMessage message) {
				//允许通知栏提示消息
				return true;
			}
		});
		// 需要easeui库显示用户头像和昵称设置此provider
		easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

			@Override
			public EaseUser getUser(String username) {
				EaseUser user = new EaseUser(EMChatManager.getInstance().getCurrentUser());
				// 在此设置用户需要显示的用户昵称和头像
				if (username.equals(share.getId())) {
					String avatar = share.getHead();
					if(!"".equals(avatar)){
						user.setAvatar(avatar);
					}
					user.setNick(share.getNickName());
				} else {
					user = userInfo.getUserInfo(username);
				}
				return user;
			}
		});
		// 不设置，则使用easeui默认的
		easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				// 修改标题,这里使用默认
				return application.getApplicationName();
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				// 设置小图标，这里为默认
				return R.drawable.icon30;
			}

			@Override
			public String getDisplayedText(EMMessage message) {
				// 设置状态栏的消息提示，可以根据message的类型做相应提示
				String ticker = EaseCommonUtils.getMessageDigest(message, context);
				if (message.getType() == Type.TXT) {
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				}
				EaseUser user = new EaseUser(message.getFrom());
				// 此处应该通过username来获取昵称然后显示
				return user.getNick() + ": " + ticker;

			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
				return "接收到" + messageNum + "条消息";
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// 设置点击通知栏跳转事件
				Intent intent = new Intent(context, ChatActivity.class);
				// 有电话时优先跳转到通话页面

				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { // 单聊信息
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
				} else { // 群聊信息
					// message.getTo()为群聊id
					intent.putExtra("userId", message.getTo());
					if (chatType == ChatType.GroupChat) {
						intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
					} else {
						intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
					}

				}
				return intent;
			}
		});
	}

	/**
	 * 设置重连监听
	 */
	private void setReConnect() {
		connectionListener = new EMConnectionListener() {
			@Override
			public void onDisconnected(int error) {

			}

			@Override
			public void onConnected() {

				EMChat.getInstance().setAppInited();
			}
		};
		// 注册连接监听
		EMChatManager.getInstance().addConnectionListener(connectionListener);
	}

	/**
	 * 全局事件监听 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理 activityList.size()
	 * <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
	 */
	private void registerEventListener() {
		eventListener = new EMEventListener() {

			@Override
			public void onEvent(EMNotifierEvent event) {
				EMMessage message = (EMMessage) event.getData();
				switch (event.getEvent()) {
				case EventNewMessage:
					// 应用在后台，不需要刷新UI,通知栏提示新消息
					if (!easeUI.hasForegroundActivies()) {
						easeUI.getNotifier().onNewMsg(message);
					}
					
					break;
				case EventOfflineMessage:
					if (!easeUI.hasForegroundActivies()) {
						@SuppressWarnings("unchecked")
						List<EMMessage> messages = (List<EMMessage>) event.getData();
						easeUI.getNotifier().onNewMesg(messages);
					}
					break;
				case EventNewCMDMessage:
					break;

				case EventDeliveryAck:
					message.setDelivered(true);
					break;
				case EventReadAck:
					message.setAcked(true);
					break;
				// add other events in case you are interested in
				default:
					break;
				}

			}
		};

		EMChatManager.getInstance().registerEventListener(eventListener);
	}
}
