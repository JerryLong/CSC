package com.guess.user;

import com.cdrongyao.caisichuan.R;
import com.guess.activity.MainActivity;
import com.guess.myutils.ShareData;
import com.squareup.picasso.Picasso;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class FragmentMyself extends Fragment implements OnClickListener {
	public static MainActivity mainInstance = null;
	private Activity activity;
	// 显示头像
	private ImageView imgIcon;
	// 个人信息
	private TextView tvName, tvCoin, tvIntegration, tvFan;
	private RelativeLayout rltMyQuestion, rltMyInvolved, rltMyAttention, rltSetting, rltMyRecord, rltRanking;
	private LinearLayout lltGold, lltLevel, lltFan;

	private ShareData share;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainInstance = (MainActivity) activity;
		initView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_myself, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	/**
	 * 初始化
	 */
	private void initView() {
		share = ShareData.getInstance(activity);

		imgIcon = (ImageView) activity.findViewById(R.id.myself_user_icon);
		tvName = (TextView) activity.findViewById(R.id.myself_user_name);
		tvIntegration = (TextView) activity.findViewById(R.id.myself_user_integration);
		tvCoin = (TextView) activity.findViewById(R.id.myself_user_coin);
		tvFan = (TextView) activity.findViewById(R.id.myself_user_fan);

		rltMyAttention = (RelativeLayout) activity.findViewById(R.id.myself_rlt_attention);
		rltMyInvolved = (RelativeLayout) activity.findViewById(R.id.myself_rlt_involved);
		rltMyQuestion = (RelativeLayout) activity.findViewById(R.id.myself_rlt_question);
		rltMyRecord = (RelativeLayout) activity.findViewById(R.id.myself_rlt_record);
		rltSetting = (RelativeLayout) activity.findViewById(R.id.myself_rlt_setting);
		rltRanking = (RelativeLayout) activity.findViewById(R.id.myself_rlt_ranking_list);

		lltGold = (LinearLayout) activity.findViewById(R.id.myself_user_llt_gold);
		lltLevel = (LinearLayout) activity.findViewById(R.id.myself_user_llt_level);
		lltFan = (LinearLayout) activity.findViewById(R.id.myself_user_llt_fan);

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
		
		//设置用户极光推送的别名
		JPushInterface.setAlias(activity, share.getAlias(), null);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.myself_user_llt_gold:
		case R.id.myself_user_llt_level:
		case R.id.myself_user_icon:
			// 点击进入个人信息
			intent = new Intent(activity, ActivityMyInfo.class);
			startActivity(intent);
			break;
		case R.id.myself_user_llt_fan:
			// 点击查看粉丝列表
			intent = new Intent(activity, MyAttention.class);
			intent.putExtra("type", 1);
			startActivity(intent);
			break;
		case R.id.myself_rlt_ranking_list:
			// 排行榜
			intent = new Intent(activity, RankingList.class);
			startActivity(intent);
			break;
		case R.id.myself_rlt_question:
			// 我发布的问题
			intent = new Intent(activity, MyQuestions.class);
			startActivity(intent);
			break;

		case R.id.myself_rlt_involved:
			// 我参与的
			intent = new Intent(activity, MyInvolved.class);
			startActivity(intent);
			break;

		case R.id.myself_rlt_record:
			// 我的兑换记录
			intent = new Intent(activity, MyRecord.class);
			startActivity(intent);
			break;

		case R.id.myself_rlt_attention:
			// 关注列表
			intent = new Intent(activity, MyAttention.class);
			intent.putExtra("type", 2);
			startActivity(intent);
			break;

		case R.id.myself_rlt_setting:
			// 设置
			intent = new Intent(activity, ActivitySetting.class);
			startActivity(intent);
			break;
		}

	}

	@Override
	public void onResume() {
		getData();
		super.onResume();
	}

	/**
	 * 获取用户信息
	 * 
	 * @param url
	 */
	private void getData() {

		tvName.setText(share.getNickName());
		tvFan.setText(share.getFan());
		tvIntegration.setText(share.getIntegration());
		tvCoin.setText(share.getCoin());
		// applicationUtil.getBitmap(imgIcon,
		// avatar.equals("null")?head:avatar);
		if (!share.getHead().equals("")) {
			Picasso.with(activity).load(share.getHead()).resize(120, 120).placeholder(R.drawable.default_picture2x).into(imgIcon);
		}
		
	}
}
