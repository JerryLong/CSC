package com.guess.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
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
import com.guess.myutils.PDialogUtil;
import com.guess.myutils.ShareData;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.shareutil.OneKeyShare;
import com.guess.shareutil.ScreenShot;
import com.guess.tools.BaseTools;
import com.guess.utils.Md5;
import com.guess.utils.VolleyErrorHelper;
import com.guess.view.GifView;
import com.guess.view.GridButtonView;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DaRenAnswer extends OneKeyShare implements IWordButton, OnClickListener {
	private final String TAG = "四川达人";
	public static DaRenAnswer instance;

	private int currentLevel = 0;// 当前游戏级别
	private int reword = 2;// 奖励
	private ShareData share;
	private int myGold;// 我的金币
	private int rank;// 排行
	private String description;// 当前所答的题的描述
	private ArrayList<HashMap<String, Object>> lableList;// 当前所打的题的标签
	private String currentAnswer;// 当前问题的答案

	private final int GOLD = 10;
	private final int LIMIT = 10;

	private TextView tvBack, tvTitle;

	private ArrayList<WordButton> mAllWords;
	private GridButtonView mGridButtonView;
	private ArrayList<WordButton> mSelectWords;
	private ArrayList<WordButton> mPropWords;
	private GifView mGifView;
	private Button mPropBtn, mHelpBtn;
	private TextView mGoldText;

	String result, answer;
	private static int ANSWER_NUM = 0, Index;

	// 已选择文字狂UI容器
	private LinearLayout mViewWordsContainer;
	private ImageView mImageView;
	private int QUESTIONS_ID = 0;
	private static final int SET_NEWSLIST = 0x000001;
	/** 答案状态 --正确 */
	private static final int ANSWER_RIGHT = 0x000002;
	/** 答案状态 --错误 */
	private static final int ANSWER_WRONG = 0x000003;
	/** 答案状态 --不完整 */
	private static final int ANSWER_LACK = 0x000004;
	/** 题目已经答完 */
	private static final int NO_QUESTION = 0x000005;
	AlertDialog alertDialog;
	private ApplicationUtil mApplicationUtil;
	private ArrayList<HashMap<String, Object>> mList;

	private String questionUrl = "http://api.caisichuan.com/api/v2/city/getQuestions";
	// private String getPropUrl =
	// "http://api.caisichuan.com/cityTalent/itemList";//获取道具
	private String usePropUrl = "http://api.caisichuan.com/api/v1/getCityTalentAnswerByIndex";// 使用道具
	private String commitUrl = "http://api.caisichuan.com/api/v2/city/submitAnswer";// 提交答案

	private TranslateAnimation tAnimation;
	private MySoundPlayer soundSelect;
	private MySoundPlayer soundFail;
	private MySoundPlayer soundSuccess;
	private MySoundPlayer soundProp;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SET_NEWSLIST:
				HashMap<String, Object> map = mList.get(currentLevel);
				ANSWER_NUM = (int) map.get("answerLength");
				tvTitle.setText(map.get("gameLevel") + "." + map.get("title").toString());

				Picasso.with(DaRenAnswer.this).load(map.get("imageUrl").toString()).resize(400, 280)
						.placeholder(R.drawable.default_picture2x).into(mImageView);
				QUESTIONS_ID = (int) map.get("gameLevel");
				initCurrentData();
				break;
			case NO_QUESTION:
				MyAlertDialog.setDialog(DaRenAnswer.this, "你是真正的达人,题都不够你答啦!", true, false);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_daren_answer);
		// QUESTIONS_ID = getIntent().getIntExtra("id", 0);
		instance = this;
		initViews();
	}

	@Override
	protected void onResume() {

		getData();
		super.onResume();
	}

	void initViews() {
		soundSelect = new MySoundPlayer(DaRenAnswer.this, R.raw.answer_select);
		soundSuccess = new MySoundPlayer(DaRenAnswer.this, R.raw.answer_success);
		soundFail = new MySoundPlayer(DaRenAnswer.this, R.raw.answer_fail);
		soundProp = new MySoundPlayer(DaRenAnswer.this, R.raw.shake);

		tAnimation = new TranslateAnimation(0, 5, 0, 0);
		tAnimation.setRepeatCount(8);
		tAnimation.setDuration(40);

		mGridButtonView = (GridButtonView) findViewById(R.id.daren_answer_gridview);
		mGridButtonView.registerOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.daren_answer_container);
		mImageView = (ImageView) findViewById(R.id.daren_answer_image);
		mPropBtn = (Button) findViewById(R.id.daren_answer_tip);
		mHelpBtn = (Button) findViewById(R.id.daren_answer_help);

		tvBack = (TextView) findViewById(R.id.daren_answer_back);
		tvTitle = (TextView) findViewById(R.id.daren_answer_title);
		tvBack.setOnClickListener(this);

		mApplicationUtil = new ApplicationUtil(this);
		share = ShareData.getInstance(getApplicationContext());
		myGold = Integer.parseInt(share.getCoin());

		mPropBtn.setOnClickListener(this);
		mHelpBtn.setOnClickListener(this);
		mImageView.setOnClickListener(this);
	}

	private void getData() {
		mList = new ArrayList<>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("number", String.valueOf(LIMIT));
		MyJsonRequest request = new MyJsonRequest(questionUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						JSONArray data = response.getJSONArray("data");
						HashMap<String, Object> map;
						JSONObject json;
						if (data.length() <= 0) {
							mHandler.sendEmptyMessage(NO_QUESTION);
						} else {
							for (int i = 0; i < data.length(); i++) {
								json = data.getJSONObject(i);
								map = new HashMap<>();
								map.put("gameLevel", json.getInt("gameLevel"));
								map.put("title", json.get("title"));
								map.put("md5Answer", json.get("md5Answer"));
								map.put("candidateAnswer", json.get("candidateAnswer"));
								map.put("imageUrl", json.getString("imageUrl"));
								map.put("answerLength", json.get("answerLength"));
								mList.add(map);
							}
							mHandler.sendEmptyMessage(SET_NEWSLIST);
						}
					} else {
						if (response.getInt("ret") == Constant.NOT_LOGIN) {
							LoginUtil login = new LoginUtil(DaRenAnswer.this);
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
		Toast.makeText(getApplicationContext(), "Index " + Index + "  ====  " + wordButton.mAllIndex,
				Toast.LENGTH_SHORT).show();
		soundSelect.playSound();
		setSelectedWord(wordButton);

		int checkResult = checkTheAnswer();
		if (checkResult == ANSWER_RIGHT) {
			getAnswer(answer);
			soundSuccess.playSound();
		} else if (checkResult == ANSWER_WRONG) {
			sparkTheWord();
			soundFail.playSound();
		} else if (checkResult == ANSWER_LACK) {

		}
	}

	private void setSelectedWord(WordButton wordButton) {
		// 设置文字选择框选项可见
		mSelectWords.get(Index).mViewButton.setText(wordButton.mWordString);
		mSelectWords.get(Index).mIsVisiable = true;
		mSelectWords.get(Index).mWordString = wordButton.mWordString;
		// 记录索引
		mSelectWords.get(Index).mAllIndex = wordButton.mAllIndex;
		mSelectWords.get(Index).mViewButton.setBackgroundResource(R.drawable.xuanze2);

		setButtonVisiable(wordButton, View.INVISIBLE);
		for (int i = 0; i < ANSWER_NUM; i++) {
			if (mSelectWords.get(i).mWordString.length() == 0) {
				Index = i;
				mSelectWords.get(Index).mWordString = "";
				mSelectWords.get(Index).mIsVisiable = false;
				mSelectWords.get(Index).mViewButton.setBackgroundResource(R.drawable.checked);
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
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;
	}

	private void clearTheAnswer(WordButton wordButton) {
		mSelectWords.get(Index).mViewButton.setBackgroundResource(R.drawable.xuanze2);
		Index = wordButton.mIndex;
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;
		wordButton.mViewButton.setBackgroundResource(R.drawable.checked);
		if (mSelectWords.get(Index).mAllIndex != 99) {
			// 设置待选框可见性
			setButtonVisiable(mAllWords.get(wordButton.mAllIndex), View.VISIBLE);
		}
	}

	/**
	 * 检查答案
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// 答案不完整
		for (int i = 0; i < mSelectWords.size(); i++) {
			if (mSelectWords.get(i).mWordString.length() == 0) {
				return ANSWER_LACK;
			}
		}
		// 答案完整继续检查
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mSelectWords.size(); i++) {
			sb.append(mSelectWords.get(i).mWordString);
		}
		answer = sb.toString();
		result = Md5.MD5Encode(sb.toString());
		return result.equals(mList.get(currentLevel).get("md5Answer")) ? ANSWER_RIGHT : ANSWER_WRONG;

	}

	/**
	 * 闪烁文字
	 */
	private void sparkTheWord() {
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (++mSpardTimes >= 6) {
							return;
						}
						for (int i = 0; i < mSelectWords.size(); i++) {
							mSelectWords.get(i).mViewButton.setTextColor(mChange ? Color.RED : Color.WHITE);
						}
						mChange = !mChange;
					}
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	@SuppressWarnings("deprecation")
	private void initCurrentData() {
		// 初始化已选择框
		mViewWordsContainer.removeAllViews();
		mSelectWords = initWordSelect();
		LayoutParams params = new LayoutParams(mViewWordsContainer.getHeight() - 25, LayoutParams.MATCH_PARENT);
		params.rightMargin = 0;
		params.topMargin = 5;
		params.bottomMargin = 5;
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
		for (int i = 0; i < ANSWER_NUM; i++) {
			View view = BaseTools.getView(DaRenAnswer.this, R.layout.view_gridview_btn);

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
					clearTheAnswer(holder);
					soundSelect.playSound();
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
		words = mList.get(currentLevel).get("candidateAnswer").toString().split(",");
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
		currentAnswer = answer;
		lableList = new ArrayList<>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("answer", answer);
		MyJsonRequest request = new MyJsonRequest(commitUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						description = response.getString("description");
						reword = response.getInt("rewardGold");
						myGold = response.getInt("gold");
						rank = response.getInt("rank");
						share.setCoin(String.valueOf(myGold));
						JSONArray array = response.getJSONArray("tags");
						JSONObject json;
						HashMap<String, Object> map;
						for (int i = 0; i < array.length(); i++) {
							json = array.getJSONObject(i);
							map = new HashMap<>();
							map.put("id", json.get("id"));
							map.put("name", json.get("name"));
							map.put("count", json.get("count"));
							map.put("agree", json.get("agreed"));
							lableList.add(map);
						}
						setDialog(reword, rank);
					} else {
						if (response.getInt("ret") == Constant.NOT_LOGIN) {
							LoginUtil login = new LoginUtil(DaRenAnswer.this);
							login.login(new LoginListener() {

								@Override
								public void onLoginAfter() {
									getAnswer(answer);
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
				Toast.makeText(getBaseContext(), VolleyErrorHelper.getMessage(error, DaRenAnswer.this),
						Toast.LENGTH_LONG).show();
			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.daren_answer_back:
			finish();
			break;
		case R.id.answer_dialog_share:
			alertDialog.dismiss();
			// 查看详情
			Intent darenDetail = new Intent(DaRenAnswer.this, DarenDetail.class);
			darenDetail.putExtra("title", currentAnswer);
			darenDetail.putExtra("description", description);
			darenDetail.putExtra("picture", mList.get(currentLevel).get("imageUrl").toString());
			darenDetail.putParcelableArrayListExtra("lable", (ArrayList<? extends Parcelable>) lableList);
			darenDetail.putExtra("flag", 1);
			startActivity(darenDetail);
			break;
		case R.id.answer_dialog_comfire:
			alertDialog.dismiss();
			// 下一题
			currentLevel++;
			mViewWordsContainer.removeAllViews();
			if (currentLevel >= LIMIT) {
				currentLevel = 0;
				getData();
			} else {
				mHandler.sendEmptyMessage(SET_NEWSLIST);
			}
			break;
		case R.id.daren_answer_tip:// 提示框Dialog弹出按钮
			setPropDialog();
			break;
		case R.id.answer_prop_btn:// 答案提示框确定按钮
			alertDialog.dismiss();
			break;
		case R.id.daren_answer_help:// 答案求助分享按钮
			onShare(TAG, mList.get(currentLevel).get("title").toString(),
					new ScreenShot(DaRenAnswer.this).getScreenShotBitmap());
			break;
		case R.id.daren_answer_image:
			Intent picture = new Intent(DaRenAnswer.this, ActivityShowPicture.class);
			picture.putExtra("imageUrl", mList.get(currentLevel).get("imageUrl").toString());
			startActivity(picture);
			break;
		}

	}

	private void setDialog(int reword, int rank) {
		alertDialog = new AlertDialog.Builder(this).create();

		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.view_answer_dialog);
		LinearLayout before = (LinearLayout) window.findViewById(R.id.answer_before);
		RelativeLayout after = (RelativeLayout) window.findViewById(R.id.answer_after);

		after.setVisibility(View.GONE);
		before.setVisibility(View.VISIBLE);
		mGifView = (GifView) window.findViewById(R.id.answer_dialog_gif);
		mGifView.setMovieResource(R.drawable.gamble_gold_animation);
		TextView gold = (TextView) window.findViewById(R.id.answer_dialog_gold);
		gold.setText("+ " + reword + " 金币");
		TextView index = (TextView) window.findViewById(R.id.answer_dialog_index);
		index.setText("已超越 " + (rank - 1) + " 人");

		Button mShareBtn = (Button) window.findViewById(R.id.answer_dialog_share);
		mShareBtn.setText("详情");
		mShareBtn.setOnClickListener(DaRenAnswer.this);
		Button mComfireBtn = (Button) window.findViewById(R.id.answer_dialog_comfire);
		mComfireBtn.setText("下一题");
		mComfireBtn.setOnClickListener(DaRenAnswer.this);
	}

	private void setPropDialog() {
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.view_answer_prop_dialog);
		LinearLayout mLinear = (LinearLayout) window.findViewById(R.id.answer_prop_linear);
		mGoldText = (TextView) window.findViewById(R.id.answer_prop_gold);
		mGoldText.setText("剩余金币：" + myGold + "金币");
		Button mConfirm = (Button) window.findViewById(R.id.answer_prop_btn);
		mConfirm.setOnClickListener(DaRenAnswer.this);

		// 初始化已选择框
		mPropWords = initPropWords();
		LayoutParams params = new LayoutParams(mViewWordsContainer.getHeight() - 25, LayoutParams.MATCH_PARENT);
		params.bottomMargin = 5;
		params.topMargin = 5;
		params.rightMargin = 5;

		for (int i = 0; i < mPropWords.size(); i++) {
			mLinear.addView(mPropWords.get(i).mViewButton, params);
		}
	}

	private ArrayList<WordButton> initPropWords() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		for (int i = 0; i < ANSWER_NUM; i++) {
			View view = BaseTools.getView(DaRenAnswer.this, R.layout.view_gridview_btn);

			final WordButton holder = new WordButton();

			holder.mViewButton = (Button) view.findViewById(R.id.view_item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;
			holder.mViewButton.setBackgroundResource(R.drawable.normal_prop_question);
			holder.mViewButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (myGold >= GOLD) {
						PDialogUtil.showDialog(DaRenAnswer.this, "", false);
						getProp(mPropWords.indexOf(holder), holder);
					} else {
						holder.mViewButton.startAnimation(tAnimation);
						soundProp.playSound();
						Toast.makeText(getApplicationContext(), "你的金币不足", Toast.LENGTH_SHORT).show();
					}
				}
			});
			data.add(holder);
		}

		return data;
	}

	private void setPropWords(WordButton wordButton, String prop) {
		wordButton.mViewButton.setBackgroundResource(R.drawable.xuanze2);
		wordButton.mViewButton.setText(prop);
	}

	/**
	 * 获取答案提示
	 * 
	 * @param index
	 * @param wordButton
	 */
	private void getProp(final int index, final WordButton wordButton) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("itemId", "1");
		map.put("index", String.valueOf(index));
		map.put("questionId", String.valueOf(QUESTIONS_ID));
		MyJsonRequest request = new MyJsonRequest(usePropUrl, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				PDialogUtil.cancelDialog();
				try {
					if (response.getInt("ret") == 0) {
						String prop = response.getString("value");
						if ((!"null".equals(prop))) {
							setPropWords(wordButton, prop);
							myGold -= GOLD;
							mGoldText.setText("剩余金币：" + myGold + "金币");
							share.setCoin(String.valueOf(myGold));
						}
					} else {
						if (response.getInt("ret") == Constant.NOT_LOGIN) {
							LoginUtil login = new LoginUtil(DaRenAnswer.this);
							login.login(new LoginListener() {

								@Override
								public void onLoginAfter() {
									getProp(index, wordButton);
								}
							});
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				PDialogUtil.cancelDialog();
			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

}
