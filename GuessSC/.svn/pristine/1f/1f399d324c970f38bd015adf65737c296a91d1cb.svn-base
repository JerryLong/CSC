package com.guess.user;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChat;
import com.guess.activity.ActivityShowPicture;
import com.guess.activity.AnswerActivity;
import com.guess.database.EaseUserInfo;
import com.guess.message.ChatActivity;
import com.guess.message.EaseLogin;
import com.guess.myadapter.UserQuestionAdapter;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.jerry.pulltorefresh.library.PullToRefreshGridView;
import com.jerry.pulltorefresh.library.PullToRefreshBase;
import com.jerry.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class UserDetail extends Activity implements OnClickListener, OnRefreshListener2<GridView>{
	private ApplicationUtil application;
	
	private TextView tvBack, tvTitle;
	
	private ImageView imgAvatar;
	
	private TextView tvCare, tvMessage, tvFan;
	
	private String userId, userName, avatar;
	private boolean attention = true;
	private int attentionCount = 0;
	
	private PullToRefreshGridView mGridView;
	private UserQuestionAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mList;
	
	private View emptyView;
	private TextView emptyDes;
	private ProgressBar pb;
	
	private String infoUrl = "http://api.caisichuan.com/api/v2/user/getUserDetailInfo";
	private String attentionUrl = "http://api.caisichuan.com/api/v2/user/attentionToUser";
	
	private Handler handler = new Handler(){
		@SuppressWarnings("deprecation")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Drawable drawable;
				tvTitle.setText(userName);
				tvFan.setText(String.valueOf(attentionCount));
				if((avatar != null) && (!"null".equals(avatar))){
					Picasso.with(UserDetail.this).load(avatar).resize(150, 150).placeholder(R.drawable.default_picture2x).into(imgAvatar);
				}
				if(attention){
					drawable = getResources().getDrawable(R.drawable.ic_user_yiguanzhu);
					tvCare.setText("已关注");
				}else{
					drawable = getResources().getDrawable(R.drawable.ic_user_add);
					tvCare.setText("关注");
				}
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				tvCare.setCompoundDrawables(null, drawable, null, null);
				
				tvCare.setOnClickListener(UserDetail.this);
				tvMessage.setOnClickListener(UserDetail.this);
				imgAvatar.setOnClickListener(UserDetail.this);
				break;
			case 1:
				mGridView.onRefreshComplete();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_detail);
		
		initView();
		initData();
	}

	/**
	 * 初始化
	 */
	@SuppressLint("InflateParams")
	private void initView() {
		mGridView = (PullToRefreshGridView)findViewById(R.id.user_detail_gd);
		mList = new ArrayList<>();
		mAdapter = new UserQuestionAdapter(UserDetail.this, mList);
		mGridView.setAdapter(mAdapter);
	//	mGridView.setPullToRefreshEnabled(false);
		mGridView.setOnRefreshListener(this);
		
		emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
		// emptyImage = (ImageView) emptyView.findViewById(R.id.empty_image);
		emptyDes = (TextView) emptyView.findViewById(R.id.empty_tv);
		pb = (ProgressBar) emptyView.findViewById(R.id.empty_pb);
		emptyDes.setVisibility(View.GONE);
		emptyDes.setText(R.string.emptyview_user_detail);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.bottomMargin = 120;
		addContentView(emptyView, lp);
		mGridView.setEmptyView(emptyView);
		
		tvBack = (TextView) findViewById(R.id.user_detail_tv_back);
		tvTitle = (TextView) findViewById(R.id.user_detail_tv_title);
		
		tvCare = (TextView) findViewById(R.id.user_detail_tv_care);
		tvMessage = (TextView) findViewById(R.id.user_detail_tv_message);
		tvFan = (TextView) findViewById(R.id.user_detail_tv_fan);
		
		imgAvatar = (ImageView) findViewById(R.id.user_detail_avatar);

		tvBack.setOnClickListener(this);
		
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent answer = new Intent(UserDetail.this, AnswerActivity.class);
				answer.putExtra("id", (int)mList.get(arg2).get("id"));
				startActivity(answer);
			}
		});
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		application = new ApplicationUtil(getApplicationContext());
		Intent intent = getIntent();
		userId = intent.getStringExtra("id");

		tvBack.setText(R.string.back);

		HashMap<String, String> map = new HashMap<>();
		map.put("uid", userId);
		getData(infoUrl, map);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_detail_tv_back:
			finish();
			break;

		case R.id.user_detail_tv_care:
			//关注或取消关注
			if((userId != null) && (!"".equals(userId))){
				HashMap<String, String> map = new HashMap<>();
				map.put("attentionId", userId);
				if(attention){
					map.put("attentionStatus", "false");
				}else{
					map.put("attentionStatus", "true");
				}
				getData(attentionUrl, map);
			}
			break;
		case R.id.user_detail_tv_message:
			//私信
			if(EMChat.getInstance().isLoggedIn()){
				//保存环信用户信息
				EaseUserInfo userInfo = new EaseUserInfo(getApplicationContext());
				userInfo.addUserInfo(userId, userName, avatar);
				
				Intent chat = new Intent(UserDetail.this, ChatActivity.class);
				chat.putExtra("userId", userId);
				//chat.putExtra("nickname", userName);
				startActivity(chat);
			}else{
				new EaseLogin(getApplicationContext()).init();
			}
			break;
		case R.id.user_detail_avatar:
			//点击头像查看图片
			
			Intent show = new Intent(UserDetail.this, ActivityShowPicture.class);
			show.putExtra("imageUrl", avatar);
			startActivity(show);
			break;
		}
		
	}
	
	private void getData(final String url, final HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				pb.setVisibility(View.GONE);
				mGridView.onRefreshComplete();
				try {
					if(response.getInt("ret") == 0){
						if(url.equals(infoUrl)){
							//获取用户信息
							JSONObject info = response.getJSONObject("info");
							userName = info.getString("nickName");
							avatar = info.getString("avatar");
							attention = info.getBoolean("attention");
							attentionCount = info.getInt("attentionCount");
							handler.sendEmptyMessage(0);
							
							JSONArray infoArray = info.getJSONArray("list");
							JSONObject object = null;
							HashMap<String, Object> map = null;
							for(int i = 0; i < infoArray.length(); i++){
								object = infoArray.getJSONObject(i);
								map = new HashMap<>();
								map.put("imageUrl", object.get("imageUrl"));
								map.put("avatar", object.get("avatar"));
								map.put("nickname", object.get("nickname"));
								map.put("title", object.get("title"));
								map.put("love", object.get("love"));
								map.put("id", object.getInt("id"));
								mList.add(map);
								mAdapter.notifyDataSetChanged();
							}
							
							if(infoArray.length() <= 0){
								emptyDes.setVisibility(View.VISIBLE);
							}
						}else if(url.equals(attentionUrl)){
							//关注或取消关注别人
							Drawable drawable;
							if(attention){
								attentionCount -= 1;
								tvFan.setText(String.valueOf(attentionCount));
								drawable = getResources().getDrawable(R.drawable.ic_user_add);
								tvCare.setText("关注");
								Toast.makeText(getApplicationContext(), "取消关注成功", Toast.LENGTH_SHORT).show();
							}else{
								attentionCount += 1;
								tvFan.setText(String.valueOf(attentionCount));
								drawable = getResources().getDrawable(R.drawable.ic_user_yiguanzhu);
								tvCare.setText("已关注");
								Toast.makeText(getApplicationContext(), "关注成功", Toast.LENGTH_SHORT).show();
							}
							drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
							tvCare.setCompoundDrawables(null, drawable, null, null);
							attention = !attention;
						}
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(UserDetail.this);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									getData(url, map);
								}
							});
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
				pb.setVisibility(View.GONE);
				emptyDes.setVisibility(View.VISIBLE);
				mGridView.onRefreshComplete();
			}
		}, map);
		application.getRequestQueue().add(json);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		mList = new ArrayList<>();
		HashMap<String, String> map = new HashMap<>();
		map.put("uid", userId);
		getData(infoUrl, map);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		handler.sendEmptyMessageDelayed(1, 600);
	}

}
