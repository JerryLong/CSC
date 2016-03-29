package com.guess.message.fragment;

import com.cdrongyao.caisichuan.R;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.ui.EaseConversationListFragment;
import com.easemob.util.NetUtils;
import com.guess.activity.MainActivity;
import com.guess.database.EaseUserInfo;
import com.guess.message.ChatActivity;
import com.guess.myutils.Constant;
import com.guess.myutils.ShareData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class FragmentConversion extends EaseConversationListFragment {

	public static FragmentConversion instance;
	
	private ShareData share;
	private EaseUserInfo userInfo;

	@Override
	protected void setUpView() {
		instance = this;
		share = ShareData.getInstance(getActivity());
		userInfo = new EaseUserInfo(getActivity());
		super.setUpView();
		
		saveUserInfo();
		
		// 注册上下文菜单
		registerForContextMenu(conversationListView);
		conversationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = conversationListView.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(EMChatManager.getInstance().getCurrentUser()))
					Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, 0).show();
				else {
					// 进入聊天页面
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					if (conversation.isGroup()) {
						if (conversation.getType() == EMConversationType.ChatRoom) {
							// it's group chat
							intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
						} else {
							intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
						}

					}
					// it's single chat
					intent.putExtra(Constant.EXTRA_USER_ID, username);
					startActivity(intent);
				}

			}
		});
	}

	@Override
	protected void onConnectionDisconnected() {
		super.onConnectionDisconnected();
		if (NetUtils.hasNetwork(getActivity())) {

		} else {

		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;
		boolean deleteMessage = false;
		if (item.getItemId() == R.id.delete_conversation) {
			deleteMessage = true;
			handled = true;
		}
		EMConversation tobeDeleteCons = conversationListView
				.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
		// 删除此会话
		EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(),
				deleteMessage);
		// InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
		// inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
		refresh();

		// 更新消息未读数
		MainActivity.instance.updateUnreadLabel();

		return handled ? true : super.onContextItemSelected(item);
	}
	
	/**
	 * 保存用户信息
	 */
	private void saveUserInfo(){
		for(int i = 0; i < conversationList.size(); i++){
			EMConversation conversation = conversationList.get(i);
			EMMessage message = conversation.getLastMessage();
			String Nickname = "";
			String Avatar = "";
			
			//必须是带缺省值的参数 不然无法收到IOS消息的拓展属性
			Avatar = message.getStringAttribute("Avatar", null);
			Nickname = message.getStringAttribute("Nickname", null);
			//保存用户信息
			String username = message.getFrom();
			System.out.println("message==="+message);
			System.out.println("username=="+username);
			System.out.println("id========="+share.getId());
			if ((!share.getId().equals(username))) {
				userInfo.addUserInfo(username, Nickname, Avatar);
			}
		}
	}

}
