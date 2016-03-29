package com.guess.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.bean.WordButton;
import com.guess.interfac.IWordButton;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyAlertDialog;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.MySoundPlayer;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.shareutil.OneKeyShare;
import com.guess.shareutil.ScreenShot;
import com.guess.tools.BaseTools;
import com.guess.utils.Logs;
import com.guess.view.GridButtonView;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class OfficialCommonDetail extends OneKeyShare implements IWordButton, OnClickListener {

	private int type, activityId, Index;
	private String response;

	private TextView tvBack, tvDescription;
	private ImageView imgShare;

	private ArrayList<WordButton> mAllWords;
	private GridButtonView mGridButtonView;
	private ArrayList<WordButton> mSelectWords;

	String result, answer;

	// 已选择文字狂UI容器
	private LinearLayout mViewWordsContainer;
	private ImageView mImageView;

	private static final int SET_NEWSLIST = 0x000001;
	AlertDialog alertDialog;
	private ApplicationUtil mApplicationUtil;
	private HashMap<String, Object> dataMap;

	private MySoundPlayer soundSelect;

	private String detailUrl = "http://api.caisichuan.com/officalActivity/activityDetail";
	private String commitUrl = "http://api.caisichuan.com/officalActivity/submitNormalActivityAnswer";// 提交答案
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SET_NEWSLIST:
				tvDescription.setText(dataMap.get("title").toString());
				Picasso.with(OfficialCommonDetail.this).load(dataMap.get("imageUrl").toString()).resize(400, 280)
						.placeholder(R.drawable.default_picture2x).into(mImageView);
				initCurrentData();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.official_common_detail);

		initViews();

	}

	void initViews() {
		Intent intent = getIntent();
		activityId = intent.getIntExtra("id", -1);
		type = intent.getIntExtra("type", -1);
		response = intent.getStringExtra("response");

		soundSelect = new MySoundPlayer(this, R.raw.answer_select);

		mGridButtonView = (GridButtonView) findViewById(R.id.official_common_detail_gridview);
		mGridButtonView.registerOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.official_common_detail_container);
		mImageView = (ImageView) findViewById(R.id.official_common_detail_image);
		tvDescription = (TextView) findViewById(R.id.official_common_detail_description);
		imgShare = (ImageView) findViewById(R.id.official_common_detail_share);

		tvBack = (TextView) findViewById(R.id.official_common_detail_back);
		tvBack.setOnClickListener(this);
		imgShare.setOnClickListener(this);
		mImageView.setOnClickListener(this);

		mApplicationUtil = new ApplicationUtil(this);
		getData();
	}

	private void getData() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", String.valueOf(type));
		map.put("activityId", String.valueOf(activityId));
		MyJsonRequest request = new MyJsonRequest(detailUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						JSONObject json = response.getJSONObject("activity");
						dataMap = new HashMap<>();
						dataMap.put("title", json.get("title"));
						dataMap.put("candidateAnswer", json.get("candidateAnswer"));
						dataMap.put("imageUrl", json.getString("imageUrl"));
						dataMap.put("answerLength", json.get("answerLength"));
					} else {
						if (response.getInt("ret") == Constant.NOT_LOGIN) {
							LoginUtil login = new LoginUtil(OfficialCommonDetail.this);
							login.login(new LoginListener() {

								@Override
								public void onLoginAfter() {
									getData();
								}
							});
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(SET_NEWSLIST);

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);

	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		soundSelect.playSound();
		for (int i = 0; i < mSelectWords.size(); i++) {
			if (mSelectWords.get(i).mWordString.length() != 0)
				continue;
			else {
				setSelectedWord(wordButton);
				if (checkTheAnswer()) {
					getAnswer(answer);
				}
				break;
			}
		}
	}

	private void setSelectedWord(WordButton wordButton) {
		mSelectWords.get(Index).mViewButton.setText(wordButton.mWordString);
		mSelectWords.get(Index).mIsVisiable = true;
		mSelectWords.get(Index).mWordString = wordButton.mWordString;
		// 记录索引
		mSelectWords.get(Index).mAllIndex = wordButton.mAllIndex;
		mSelectWords.get(Index).mViewButton.setBackgroundResource(R.drawable.xuanze2);
		setButtonVisiable(wordButton, View.INVISIBLE);
		for (int i = 0; i < mSelectWords.size(); i++) {
			if (mSelectWords.get(i).mWordString.length() == 0) {
				Index = i;
				mSelectWords.get(i).mWordString = "";
				mSelectWords.get(i).mIsVisiable = false;
				mSelectWords.get(i).mViewButton.setBackgroundResource(R.drawable.checked);
				break;
			}
		}
	}

	/**
	 * 设置待选文字框是否可见
	 * 
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button, int visibility) {
		System.out.println("visivibility==" + button.mViewButton.getVisibility());
		button.mViewButton.setVisibility(visibility);
		System.out.println("visivibility==" + button.mViewButton.getVisibility());
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;
	}

	private void clearTheAnswer(WordButton wordButton) {
		mSelectWords.get(Index).mViewButton.setBackgroundResource(R.drawable.xuanze2);
		Index = wordButton.mIndex;
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;
		wordButton.mViewButton.setBackgroundResource(R.drawable.checked);
		Logs.i("Index  " + Index + "  mAllIndex  " + mSelectWords.get(Index).mAllIndex);
		Toast.makeText(getApplicationContext(), "Index  " + Index + "  mAllIndex  " + mSelectWords.get(Index).mAllIndex,
				Toast.LENGTH_SHORT).show();
		if (mSelectWords.get(Index).mAllIndex != 99) {
			Logs.i("mIndex  " + mAllWords.get(wordButton.mAllIndex).mAllIndex);
			Toast.makeText(getApplicationContext(), "mIndex  " + mAllWords.get(wordButton.mAllIndex).mAllIndex,
					Toast.LENGTH_SHORT).show();
			// 设置待选框可见性
			setButtonVisiable(mAllWords.get(wordButton.mAllIndex), View.VISIBLE);
		}
	}

	/**
	 * 检查答案
	 * 
	 * @return
	 */
	private boolean checkTheAnswer() {
		// 答案不完整
		for (int i = 0; i < mSelectWords.size(); i++) {
			if (mSelectWords.get(i).mWordString.length() == 0) {
				return false;
			}
		}
		return true;

	}

	@SuppressWarnings("deprecation")
	private void initCurrentData() {
		// 初始化已选择框
		mViewWordsContainer.removeAllViews();
		mSelectWords = initWordSelect();
		LayoutParams params = new LayoutParams(mViewWordsContainer.getHeight() - 25, LayoutParams.MATCH_PARENT);
		params.bottomMargin = 5;
		params.topMargin = 5;
		params.rightMargin = 0;
		params.gravity = Gravity.CENTER;
		for (int i = 0; i < mSelectWords.size(); i++) {
			mViewWordsContainer.addView(mSelectWords.get(i).mViewButton, params);
		}

		// 获得数据
		mAllWords = initAllWord();
		// 更新数据到GridButtonView
		mGridButtonView.updateData(mAllWords, (getWindowManager().getDefaultDisplay().getWidth() - 40) / 6);
	}

	/**
	 * 初始化已选择文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < (int) dataMap.get("answerLength"); i++) {
			View view = BaseTools.getView(OfficialCommonDetail.this, R.layout.view_gridview_btn);

			final WordButton holder = new WordButton();

			holder.mViewButton = (Button) view.findViewById(R.id.view_item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;
			holder.setmIndex(i);
			holder.setmAllIndex(99);
			holder.mViewButton.setBackgroundResource(R.drawable.xuanze2);
			if (holder.mIndex == 0) {
				holder.mViewButton.setBackgroundResource(R.drawable.checked);
			}
			holder.mViewButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					soundSelect.playSound();
					clearTheAnswer(holder);
				}
			});
			data.add(holder);
		}

		return data;
	}

	/**
	 * 初始化待选文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		// 获得所有待选文字
		String[] words = new String[GridButtonView.COUNT_WORDS];
		words = dataMap.get("candidateAnswer").toString().split(",");
		for (int i = 0; i < GridButtonView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();

			button.mWordString = words[i];

			data.add(button);
		}
		return data;
	}

	/**
	 * 提交答案
	 * 
	 * @param answer
	 */
	private void getAnswer(final String answer) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("answer", answer);
		map.put("activityId", String.valueOf(activityId));
		MyJsonRequest request = new MyJsonRequest(commitUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == Constant.NOT_LOGIN) {
						LoginUtil login = new LoginUtil(OfficialCommonDetail.this);
						login.login(new LoginListener() {

							@Override
							public void onLoginAfter() {
								getAnswer(answer);
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

			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.official_common_detail_back:
			finish();
			break;
		case R.id.official_common_detail_share:// 分享按钮
			onShare("官方活动", dataMap.get("title").toString(),
					new ScreenShot(OfficialCommonDetail.this).getScreenShotBitmap());
			break;
		case R.id.official_common_detail_image:
			Intent picture = new Intent(OfficialCommonDetail.this, ActivityShowPicture.class);
			picture.putExtra("imageUrl", dataMap.get("imageUrl").toString());
			startActivity(picture);
			break;

		}
	}

}
