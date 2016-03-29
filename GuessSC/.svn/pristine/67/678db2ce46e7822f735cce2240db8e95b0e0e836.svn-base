package com.guess.user;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.ShareData;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * "我的"界面
 * 
 * @author YXC
 *
 */
public class ActivityMyself extends Activity implements OnClickListener {
	public static ActivityMyself myselfInstance = null;
	
	//显示头像
	private ImageView imgIcon;
	// 个人信息
	private TextView tvName, tvCoin, tvIntegration, tvFan;
	private RelativeLayout rltMyQuestion, rltMyInvolved, rltMyAttention, rltSetting, rltMyRecord, rltRanking;
	private LinearLayout lltGold, lltLevel, lltFan;
	
	private ShareData share;
	private ApplicationUtil applicationUtil;
	
	//加载用户信息的url
	private String infoUrl = "http://cscapi.srulos.com/user/info";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myself);
		myselfInstance = this;
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		share = ShareData.getInstance(getApplicationContext());
		applicationUtil = new ApplicationUtil(getApplicationContext());
		
		imgIcon = (ImageView) findViewById(R.id.myself_user_icon);
		tvName = (TextView) findViewById(R.id.myself_user_name);
		tvIntegration = (TextView) findViewById(R.id.myself_user_integration);
		tvCoin = (TextView) findViewById(R.id.myself_user_coin);
		tvFan = (TextView) findViewById(R.id.myself_user_fan);

		rltMyAttention = (RelativeLayout) findViewById(R.id.myself_rlt_attention);
		rltMyInvolved = (RelativeLayout) findViewById(R.id.myself_rlt_involved);
		rltMyQuestion = (RelativeLayout) findViewById(R.id.myself_rlt_question);
		rltMyRecord = (RelativeLayout) findViewById(R.id.myself_rlt_record);
		rltSetting = (RelativeLayout) findViewById(R.id.myself_rlt_setting);
		rltRanking = (RelativeLayout) findViewById(R.id.myself_rlt_ranking_list);
		
		lltGold = (LinearLayout) findViewById(R.id.myself_user_llt_gold);
		lltLevel = (LinearLayout) findViewById(R.id.myself_user_llt_level);
		lltFan = (LinearLayout) findViewById(R.id.myself_user_llt_fan);

		// 为控件注册监听器
		imgIcon.setOnClickListener(this);
		rltMyAttention.setOnClickListener(this);
		rltMyQuestion.setOnClickListener(this);
		rltMyInvolved.setOnClickListener(this);
		rltMyRecord.setOnClickListener(this);
		rltSetting.setOnClickListener(this);
		rltRanking.setOnClickListener(this);
		lltGold.setOnClickListener(this);
		lltLevel.setOnClickListener(this);
		lltFan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.myself_user_llt_gold:
		case R.id.myself_user_llt_level:
		case R.id.myself_user_icon:
			// 点击进入个人信息
			intent = new Intent(ActivityMyself.this, ActivityMyInfo.class);
			startActivity(intent);
			break;
		case R.id.myself_user_llt_fan:
			//点击查看粉丝列表
			intent = new Intent(ActivityMyself.this, MyAttention.class);
			intent.putExtra("type", 1);
			startActivity(intent);
			break;
		case R.id.myself_rlt_ranking_list:
			//排行榜
			intent = new Intent(ActivityMyself.this, RankingList.class);
			startActivity(intent);
			break;
		case R.id.myself_rlt_question:
			// 我发布的问题
			intent = new Intent(ActivityMyself.this, MyQuestions.class);
			startActivity(intent);
			break;

		case R.id.myself_rlt_involved:
			// 我参与的
			intent = new Intent(ActivityMyself.this, MyInvolved.class);
			startActivity(intent);
			break;

		case R.id.myself_rlt_record:
			// 我的兑换记录
			intent = new Intent(ActivityMyself.this, MyRecord.class);
			startActivity(intent);
			break;

		case R.id.myself_rlt_attention:
			// 关注列表
			intent = new Intent(ActivityMyself.this, MyAttention.class);
			intent.putExtra("type", 2);
			startActivity(intent);
			break;

		case R.id.myself_rlt_setting:
			// 设置
			intent = new Intent(ActivityMyself.this, ActivitySetting.class);
			startActivity(intent);
			break;
		}

	}
	
	@Override
	protected void onResume() {
		getData(infoUrl);
		super.onResume();
	}
	
	/**
	 * 获取用户信息
	 * @param url
	 */
	private void getData(String url){
		
		HashMap<String, String> map = new HashMap<>();//不需要传参数
		MyJsonRequest jsonRequest = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				System.out.println("response=="+response);
				try {
					if(response.getInt("ret") == 0){
						String level = String.valueOf(response.getInt("level"));
						String avatar = response.getString("avatar");
						String fan = response.getString("fanCount");
						String gold = String.valueOf(response.getInt("gold"));
						String nickName = response.getString("nickname");
						int type = response.getInt("type");
						String address = response.getString("address");
						String phoen = response.getString("phone");
						String account = response.getString("guestAccount");
						
						share.setAccount(account);
						share.setIntegration(level);
						share.setAddress(address);
						share.setCoin(gold);
						share.setHead(avatar);
						share.setNickName(nickName);
						share.setFan(fan);
						share.setPhone(phoen);
						share.setType(type);
						
						tvName.setText(nickName);
						tvFan.setText(fan);
						tvIntegration.setText(level);
						tvCoin.setText(gold);
						if(!share.getHead().equals("")){
							Picasso.with(ActivityMyself.this).load(share.getHead()).resize(120, 120).placeholder(R.drawable.default_picture2x).into(imgIcon);
						}
					}else{
						if(getString(R.string.not_login).equals(response.getString("msg"))){
							//未登录
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
				tvName.setText(share.getNickName());
				tvFan.setText(share.getFan());
				tvIntegration.setText(share.getIntegration());
				tvCoin.setText(share.getCoin());
				if(!share.getHead().equals("")){
					Picasso.with(ActivityMyself.this).load(share.getHead()).resize(120, 120).placeholder(R.drawable.default_picture2x).into(imgIcon);
				}
			//	Toast.makeText(getApplicationContext(), "用户信息加载失败", Toast.LENGTH_SHORT).show();
			}
		}, map);
		
		applicationUtil.getRequestQueue().add(jsonRequest);
	}

}
