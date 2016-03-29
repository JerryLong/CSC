package com.guess.user;

import com.cdrongyao.caisichuan.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ActivityHelp extends Activity{
	
	private TextView tvBack;
	private TextView tvTitle;
	private TextView tvHelp;
	
	String help1 = "1.官方活动规则说明:官方活动有普通猜题活动,竞猜活动和扫描二维码活动,所有活动均有成都蓉耀科技有限公司提供.";
	String help2 = "2.金币商城中兑换物品由成都蓉耀科技有限公司提供.";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		tvHelp = (TextView) findViewById(R.id.help_conten);
		
		tvBack.setText(getString(R.string.activity_myself_set));
		tvTitle.setText(getString(R.string.activity_help_title));
		
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		tvHelp.setText(help1 + "\n\n" + help2);
	}
}
