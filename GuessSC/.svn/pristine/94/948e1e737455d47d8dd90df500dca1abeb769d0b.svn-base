package com.guess.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.shareutil.OneKeyShare;
import com.guess.shareutil.ScreenShot;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class DarenDetail extends OneKeyShare implements OnClickListener {
	private ApplicationUtil applicationUtil;

	private int flag;

	private TextView tvTitle, tvBack;

	private Button btnContinue;

	private ImageView imgPicture;
	private ImageView imgShare;

	private TextView tvDescription;
	private GridLayout gdLayout;

	private ArrayList<HashMap<String, Object>> lableList;
	private String description;
	private String title, picture;

	private String url = "http://api.caisichuan.com/cityTalent/evaluationTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daren_detail);

		initView();
	}

	/**
	 * 初始化
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void initView() {
		applicationUtil = new ApplicationUtil(this);

		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);

		btnContinue = (Button) findViewById(R.id.detail_daren_continue);

		imgPicture = (ImageView) findViewById(R.id.detail_daren_picture);
		imgShare = (ImageView) findViewById(R.id.detail_daren_share);
		tvDescription = (TextView) findViewById(R.id.detail_daren_description);
		gdLayout = (GridLayout) findViewById(R.id.detail_daren_lable_dg);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		description = intent.getStringExtra("description");
		picture = intent.getStringExtra("picture");
		lableList = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("lable");
		flag = intent.getIntExtra("flag", 0);
		if (flag == 1) {
			btnContinue.setVisibility(View.VISIBLE);
		} else {
			btnContinue.setVisibility(View.GONE);
		}
		LayoutParams lp = new LayoutParams();

		Random random = new Random();
		for (int i = 0; i < lableList.size(); i++) {
			final HashMap<String, Object> map = lableList.get(i);
			lp.width = LayoutParams.WRAP_CONTENT;
			lp.height = 40;
			lp.setGravity(Gravity.CENTER);
			lp.setMargins(5, 5, 0, 0);
			final TextView lable = new TextView(this);
			lable.setGravity(Gravity.CENTER);
			lable.setTextColor(Color.WHITE);
			lable.setLayoutParams(lp);
			final boolean agree = (boolean) map.get("agree");
			final String id = map.get("id").toString();
			lable.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!agree) {
						HashMap<String, String> map1 = new HashMap<>();
						map1.put("tagId", id);
						map1.put("agreed", "1");
						getData(url, map1);

						String text = map.get("name") + "("
								+ String.valueOf(Integer.parseInt(map.get("count").toString()) + 1) + ")";
						lable.setText(text);
					}
				}
			});

			int index = 4 + random.nextInt(97);
			if (index % 4 == 0) {
				lable.setBackground(getResources().getDrawable(R.drawable.daren_tag_12x));
			} else if (index % 4 == 1) {
				lable.setBackground(getResources().getDrawable(R.drawable.daren_tag_22x));
			} else if (index % 4 == 2) {
				lable.setBackground(getResources().getDrawable(R.drawable.daren_tag_32x));
			} else if (index % 4 == 3) {
				lable.setBackground(getResources().getDrawable(R.drawable.daren_tag_42x));
			}
			String text = map.get("name") + "(" + map.get("count") + ")";
			lable.setText(text);
			gdLayout.addView(lable);
		}

		tvBack.setText(R.string.back);
		tvTitle.setText(title);
		tvDescription.setText(description);
		Picasso.with(DarenDetail.this).load(picture).resize(480, 300).placeholder(R.drawable.default_picture2x)
				.into(imgPicture);

		tvBack.setOnClickListener(this);
		imgShare.setOnClickListener(this);
		btnContinue.setOnClickListener(this);
		imgPicture.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.detail_daren_continue) {
			finish();
		} else if (v.getId() == R.id.detail_daren_picture) {
			Intent show = new Intent(DarenDetail.this, ActivityShowPicture.class);
			show.putExtra("imageUrl", picture);
			startActivity(show);
		} else {
			if (v instanceof TextView) {

				String text = ((TextView) v).getText().toString();
				if ("返回".equals(text)) {
					if (flag == 1) {
						Intent daren = new Intent(DarenDetail.this, SCPersonActivity.class);
						startActivity(daren);
						DaRenAnswer.instance.finish();

						finish();
					} else {
						finish();
					}
				}
			} else {
				if (v.getId() == R.id.detail_daren_share) {
					// 分享
					onShare(title, title, new ScreenShot(DarenDetail.this).getScreenShotBitmap());
				}
			}
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (flag == 1) {
				Intent daren = new Intent(DarenDetail.this, SCPersonActivity.class);
				startActivity(daren);
				DaRenAnswer.instance.finish();

				finish();
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 网络操作
	 * 
	 * @param url
	 * @param map
	 */
	private void getData(final String url, final HashMap<String, String> map) {
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					if (response.getInt("ret") == Constant.NOT_LOGIN) {
						LoginUtil login = new LoginUtil(DarenDetail.this);
						login.login(new LoginListener() {

							@Override
							public void onLoginAfter() {
								getData(url, map);
							}
						});
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

		applicationUtil.getRequestQueue().add(json);
	}

}
