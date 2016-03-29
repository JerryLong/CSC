package com.guess.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.cdrongyao.caisichuan.R;
import com.dralong.slidingmenu.lib.SlidingMenu;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.controller.EaseUI;
import com.guess.fragment.GamesFragment;
import com.guess.fragment.SessionFragment;
import com.guess.message.fragment.FragmentConversion;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.UpdateUtil;
import com.guess.tools.MenuDrawer;
import com.guess.user.FragmentMyself;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements EMEventListener {
	private TextView tvUnreadLable;
	public static MainActivity instance;
	private long exitTime = 0;
	public SlidingMenu side_drawer;
	public static TabFragmentHost mTabHost;
	// 标签
	private String[] TabTag = { "tab1", "tab2", "tab3" };
	// 自定义tab布局显示文本和顶部的图片
	private Integer[] ImgTab = { R.layout.tab_main_layout, R.layout.tab_session_layout, R.layout.tab_mine_layout };

	// tab 选中的activity
	@SuppressWarnings("rawtypes")
	private Class[] ClassTab = { GamesFragment.class, SessionFragment.class, FragmentMyself.class };

	// tab选中背景 drawable 样式图片 背景都是同一个
	private Integer[] StyleTab = { R.color.drak_lower, R.color.drak_lower, R.color.drak_lower, R.color.drak_lower };
	
	/**
	 * 接收自定义消息
	 */
	NotificationManager nManager;
	public static boolean isForeground = false;
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		instance = this;
		setContentView(R.layout.maintabs);

		nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		new UpdateUtil(MainActivity.this, 0).checkUpdate();
		setupView();
		initValue();
		setLinstener();
		fillData();
		initSlidingMenu();
	}

	private void setupView() {

		// 实例化framentTabHost
		mTabHost = (TabFragmentHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

	}

	private void initValue() {

		// 初始化tab选项卡
		InitTabView();
	}

	private void setLinstener() {
		// imv_back.setOnClickListener(this);

	}

	private void fillData() {
		// TODO Auto-generated method stub

	}

	// 初始化 tab 自定义的选项卡 ///////////////
	private void InitTabView() {

		// 可以传递参数 b;传递公共的userid,version,sid
		Bundle b = new Bundle();
		// 循环加入自定义的tab
		for (int i = 0; i < TabTag.length; i++) {
			// 封装的自定义的tab的样式
			View indicator = getIndicatorView(i);
			mTabHost.addTab(mTabHost.newTabSpec(TabTag[i]).setIndicator(indicator), ClassTab[i], b);// 传递公共参数
			if (i == 1) {
				tvUnreadLable = (TextView) indicator.findViewById(R.id.tab_un_read);
			}

		}
		mTabHost.getTabWidget().setDividerDrawable(R.color.drak_lower);
	}

	// 设置tab自定义样式:注意 每一个tab xml子布局的linearlayout 的id必须一样
	public View getIndicatorView(int i) {
		// 找到tabhost的子tab的布局视图
		View v = getLayoutInflater().inflate(ImgTab[i], null);
		LinearLayout tv_lay = (LinearLayout) v.findViewById(R.id.main_tab);
		tv_lay.setBackgroundResource(StyleTab[i]);

		return v;
	}

	protected void initSlidingMenu() {
		side_drawer = new MenuDrawer(this).initSlidingMenu();
		side_drawer.toggle();
	}

	/**
	 * 按两次返回键推出程序
	 *
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (side_drawer.isMenuShowing()) {
				side_drawer.toggle();
			} else {
				if ((System.currentTimeMillis() - exitTime) > 2000) {

					Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();

				} else {
					finish();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		
		registerMessageReceiver();
		isForeground = true;
		if(EaseUI.getInstance().init(getApplicationContext())){
			EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
		}
		EaseUI.getInstance().pushActivity(this);
		updateUnreadLabel();
		super.onResume();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mMessageReceiver);
		EMChatManager.getInstance().unregisterEventListener(this);
		EaseUI.getInstance().popActivity(this);
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
			// 刷新UI
			FragmentConversion.instance.refresh();

			updateUnreadLabel();
			break;
		case EventOfflineMessage:
			if (!EaseUI.getInstance().hasForegroundActivies()) {
				@SuppressWarnings("unchecked")
				List<EMMessage> messages = (List<EMMessage>) event.getData();
				EaseUI.getInstance().getNotifier().onNewMesg(messages);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int count = EMChatManager.getInstance().getUnreadMsgsCount();
				if (count > 0) {
					tvUnreadLable.setText(String.valueOf(count));
					tvUnreadLable.setVisibility(View.VISIBLE);

				} else {
					tvUnreadLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}
	
	/**
	 * 注册广播接收器
	 */
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
              String message = intent.getStringExtra(KEY_MESSAGE);
              String extras = intent.getStringExtra(KEY_EXTRAS);
              try {
				JSONObject json = new JSONObject(extras);
				String title = json.getString("title");
				showNotification(title, message);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void showNotification(String title, String content){
		String strTitle = "".equals(title)?new ApplicationUtil(getApplicationContext()).getApplicationName():title;
		Notification notification = new Notification(
				R.drawable.icon30, strTitle,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击之后取消
		notification.defaults = Notification.DEFAULT_SOUND;// 默认提示音
		Intent intent1 = new Intent(MainActivity.this,
				MainActivity.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pintent = PendingIntent.getActivity(
				MainActivity.this, 0, intent1, 0);
		notification.setLatestEventInfo(MainActivity.this, strTitle,
				content, pintent);
		nManager.notify(100, notification);
	}
}
