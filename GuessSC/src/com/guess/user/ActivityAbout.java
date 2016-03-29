package com.guess.user;

import com.cdrongyao.caisichuan.R;
import com.guess.myutils.ApplicationUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ActivityAbout extends Activity{
	private ApplicationUtil applicationUtil;
	private TextView tvBack, tvTitle, tvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		
		applicationUtil = new ApplicationUtil(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		tvVersion = (TextView) findViewById(R.id.about_version);
		
		tvBack.setText(getString(R.string.activity_myself_set));
		tvTitle.setText(getString(R.string.activity_about_title));
		
		tvVersion.setText(applicationUtil.getApplicationName() + applicationUtil.getVersionName());
		
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
