package com.guess.user;

import com.cdrongyao.caisichuan.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class TermsAndPolicy extends Activity{
	private TextView tvBack, tvTitle;
	
	private WebView webView;

	private String termsUrl = "http://api.caisichuan.com/service/use";//使用条款
	private String policyUrl = "http://api.caisichuan.com/service/private";//隐私政策
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_and_policy);
		
		initView();
	}
	/**
	 * 初始化
	 */
	private void initView() {
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		webView = (WebView) findViewById(R.id.tips_webview);
		
		int flag = getIntent().getIntExtra("flag", 1);
		tvBack.setText(R.string.title_user_login);
		tvTitle.setText((flag == 1)?R.string.title_user_terms:R.string.title_user_policy);
		
		String url = "";
		if(flag == 1){
			url = termsUrl;
		}else{
			url = policyUrl;
		}
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
