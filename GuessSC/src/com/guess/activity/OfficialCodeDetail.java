package com.guess.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyAlertDialog;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.shareutil.OneKeyShare;
import com.guess.shareutil.ScreenShot;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OfficialCodeDetail extends OneKeyShare implements OnClickListener{
	private ApplicationUtil application;
	
	private TextView tvBack, tvTitle;
	private TextView tvDescription;
	
	private ImageView imgShare, imgPic;
	
	private Button btnScan;
	
	private int type, activityId;
	private String response;
	private String imageUrl, description;
	
	private String detailUrl = "http://api.caisichuan.com/officalActivity/activityDetail";
	private String handleUrl = "http://api.caisichuan.com/officalActivity/submitQrActivityAnswer";
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				Picasso.with(OfficialCodeDetail.this).load(imageUrl).resize(400, 280).placeholder(R.drawable.default_picture2x).into(imgPic);
				tvDescription.setText(description);
			}else if(msg.what == 1){
				MyAlertDialog.setDialog(OfficialCodeDetail.this, response, false, true);
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.official_code_detail);
		
		initView();
	}
	/**
	 * 初始化
	 */
	private void initView() {
		
		application = new ApplicationUtil(getApplicationContext());
		
		Intent intent = getIntent();
		activityId = intent.getIntExtra("id", -1);
		type = intent.getIntExtra("type", -1);
		response = intent.getStringExtra("response");
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		
		tvDescription = (TextView) findViewById(R.id.official_code_detail_title);
		imgShare = (ImageView) findViewById(R.id.official_code_detail_share);
		imgPic = (ImageView) findViewById(R.id.official_code_detail_picture);
		
		btnScan = (Button) findViewById(R.id.official_code_detail_scan);
		
		tvBack.setText(R.string.back);
		tvTitle.setText("活动详情");
		
		HashMap<String, String> map = new HashMap<>();
		map.put("type", String.valueOf(type));
		map.put("activityId", String.valueOf(activityId));
		getData(detailUrl, map);
		
		tvBack.setOnClickListener(this);
		imgShare.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		imgPic.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			finish();
			break;

		case R.id.official_code_detail_share:
			onShare("扫描二维码", "快来帮我一起找找二维码吧", new ScreenShot(OfficialCodeDetail.this).getScreenShotBitmap());
			break;
		case R.id.official_code_detail_scan:
			//扫描二维码
			Intent intent = new Intent();
			intent.setClass(OfficialCodeDetail.this, MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 1);
			break;
		case R.id.official_code_detail_picture:
			Intent show = new Intent(OfficialCodeDetail.this, ActivityShowPicture.class);
			show.putExtra("imageUrl", imageUrl);
			startActivity(show);
			break;
		}
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case 1:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				String url = bundle.getString("result");
				HashMap<String, String> map = new HashMap<>();
				map.put("activityId", String.valueOf(activityId));
				map.put("answer", url);
				getData(handleUrl, map);
			//	mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		}
    }
	
	private void getData(final String url, final HashMap<String, String> map){
		
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						if(url.equals(detailUrl)){
							//获取活动详情
							JSONObject json = response.getJSONObject("activity");
							imageUrl = json.getString("imageUrl");
							description = json.getString("description");
							
							handler.sendEmptyMessage(0);
						}else{
							//提交答案
							handler.sendEmptyMessage(1);
						}
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(OfficialCodeDetail.this);
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
				// TODO Auto-generated method stub
				
			}
		}, map);
		application.getRequestQueue().add(json);
	}

}
