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
import com.android.volley.VolleyError;
import com.cdrongyao.caisichuan.R;
import com.easemob.chat.EMChat;
import com.google.gson.Gson;
import com.guess.bean.AnswerBean;
import com.guess.bean.GuessBean;
import com.guess.bean.WordButton;
import com.guess.database.EaseUserInfo;
import com.guess.interfac.IWordButton;
import com.guess.message.ChatActivity;
import com.guess.message.EaseLogin;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.MySoundPlayer;
import com.guess.myutils.PDialogUtil;
import com.guess.myutils.ShareData;
import com.guess.myview.CircleImageView;
import com.guess.shareutil.OneKeyShare;
import com.guess.shareutil.ScreenShot;
import com.guess.tools.BaseTools;
import com.guess.tools.DateTools;
import com.guess.user.UserDetail;
import com.guess.utils.ImageLoadUtils;
import com.guess.utils.Md5;
import com.guess.utils.PublicTools;
import com.guess.utils.UrlContants;
import com.guess.utils.VolleyErrorHelper;
import com.guess.view.GifView;
import com.guess.view.GridButtonView;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
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

public class AnswerActivity extends OneKeyShare implements IWordButton, OnClickListener {
	private ShareData share;
	private int myGold;
	private final int GOLD = 10;
	private TextView tvMyGold;

	private ArrayList<WordButton> mAllWords;
	private GridButtonView mGridButtonView;
	private ArrayList<WordButton> mSelectWords;
	private ArrayList<WordButton> mPropWords;
	private ImageLoadUtils mImageLoadUtils;
	private GifView mGifView;
	private Button mPropBtn, mHelpBtn, mPraiseBtn, mCommentBtn, mReportBtn;
	TextView tvBack;
	private TextView mNameTxt, mTimeTxt, mPraiseTxt, mTitleTxt;
	private RelativeLayout mPriogressLayout, mBarLayout, mUserLayout;
	private CircleImageView mAvatarImage;

	// private String videoPath = null;
	private SurfaceView mSurfaceView;
	private MediaPlayer mMediaPlayer;
	public boolean isVideo = false, isPlay = false;

	String result, answer;
	private static int ANSWER_NUM = 0;
	private int zhan, love, Index;
	// 已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	private ImageView mImageView, mVideoImg;
	private int QUESTIONS_ID = 0;
	private static final int SET_NEWSLIST = 0x000001;
	/** 答案状态 --正确 */
	private static final int ANSWER_RIGHT = 0x000002;
	/** 答案状态 --错误 */
	private static final int ANSWER_WRONG = 0x000003;
	/** 答案状态 --不完整 */
	private static final int ANSWER_LACK = 0x000004;
	AlertDialog alertDialog;
	private ApplicationUtil mApplicationUtil;
	private GuessBean Bean;
	Drawable like, unlike;

	private TranslateAnimation tAnimation;
	private MySoundPlayer soundSelect;
	private MySoundPlayer soundFail;
	private MySoundPlayer soundSuccess;
	private MySoundPlayer soundProp;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SET_NEWSLIST:
				getDataInit();
				initCurrentData();
				ready();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_answer_layout);
		QUESTIONS_ID = getIntent().getIntExtra("id", 0);

		initViews();
	}

	@SuppressWarnings("deprecation")
	void initViews() {
		soundSelect = new MySoundPlayer(AnswerActivity.this, R.raw.answer_select);
		soundSuccess = new MySoundPlayer(AnswerActivity.this, R.raw.answer_success);
		soundFail = new MySoundPlayer(AnswerActivity.this, R.raw.answer_fail);
		soundProp = new MySoundPlayer(AnswerActivity.this, R.raw.shake);

		tAnimation = new TranslateAnimation(0, 5, 0, 0);
		tAnimation.setRepeatCount(8);
		tAnimation.setDuration(40);

		share = ShareData.getInstance(getApplicationContext());
		myGold = Integer.parseInt(share.getCoin());

		mGridButtonView = (GridButtonView) findViewById(R.id.activity_answer_gridview);
		mGridButtonView.registerOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.activity_answer_container);
		mImageView = (ImageView) findViewById(R.id.activity_answer_imageview);
		mPropBtn = (Button) findViewById(R.id.activity_answer_prop);
		mCommentBtn = (Button) findViewById(R.id.activity_answer_comment);
		mHelpBtn = (Button) findViewById(R.id.activity_answer_help);
		mPraiseBtn = (Button) findViewById(R.id.activity_answer_chat);
		mNameTxt = (TextView) findViewById(R.id.activity_answer_name);
		mTimeTxt = (TextView) findViewById(R.id.activity_answer_time);
		mPraiseTxt = (TextView) findViewById(R.id.activity_answer_assist);
		mAvatarImage = (CircleImageView) findViewById(R.id.activity_answer_avatar);
		mTitleTxt = (TextView) findViewById(R.id.answer_layout_title);
		mReportBtn = (Button) findViewById(R.id.answer_layout_report);
		tvBack = (TextView) findViewById(R.id.answer_layout_back);
		mPriogressLayout = (RelativeLayout) findViewById(R.id.progress_relative_layout);
		mBarLayout = (RelativeLayout) findViewById(R.id.answer_layout_bar);
		mUserLayout = (RelativeLayout) findViewById(R.id.answer_layout_user);

		mSurfaceView = (SurfaceView) findViewById(R.id.activity_answer_surfaceview);
		mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceView.getHolder().setFixedSize(720, 1280);
		mSurfaceView.getHolder().setKeepScreenOn(true);
		mSurfaceView.getHolder().addCallback(new SurfaceCallBack());

		mVideoImg = (ImageView) findViewById(R.id.activity_answer_video_image);

		mImageLoadUtils = ImageLoadUtils.getInstanceAsycnImage();
		mImageLoadUtils.setThreadPoolExecutor();
		mApplicationUtil = new ApplicationUtil(this);

		mPropBtn.setOnClickListener(this);
		mCommentBtn.setOnClickListener(this);
		mHelpBtn.setOnClickListener(this);
		mPraiseBtn.setOnClickListener(this);
		mPraiseTxt.setOnClickListener(this);
		mAvatarImage.setOnClickListener(this);
		mReportBtn.setOnClickListener(this);
		tvBack.setOnClickListener(this);
		mImageView.setOnClickListener(this);

		like = getResources().getDrawable(R.drawable.question_like);
		unlike = getResources().getDrawable(R.drawable.ic_square_unlike2x);
		like.setBounds(0, 0, like.getMinimumWidth(), like.getMinimumHeight());
		unlike.setBounds(0, 0, like.getMinimumWidth(), like.getMinimumHeight());
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				setPlay(isPlay);
			}
		});

		getData();
		// 加载图片

	}

	private void getData() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", String.valueOf(QUESTIONS_ID));
		map.put("number", "1");
		String url = UrlContants.LOCATION + UrlContants.NCWC_ + UrlContants.NCWC_NEW_ID;
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						JSONArray array = response.getJSONArray("list");
						Bean = new GuessBean();
						Gson gson = new Gson();
						Bean = gson.fromJson(array.get(0).toString(), GuessBean.class);
						// }
					} else if (response.getInt("ret") == Constant.NOT_LOGIN) {
						LoginUtil login = new LoginUtil(AnswerActivity.this);
						login.login(new LoginListener() {

							@Override
							public void onLoginAfter() {
								getData();
							}
						});

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (Bean != null) {
					mHandler.obtainMessage(SET_NEWSLIST).sendToTarget();
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
		soundSelect.playSound();
		for (int i = 0; i < ANSWER_NUM; i++) {
			if (mSelectWords.get(i).mWordString.length() != 0)
				continue;
			else {
				setSelectedWord(wordButton);

				int checkResult = checkTheAnswer();
				if (checkResult == ANSWER_RIGHT) {
					getAnswer(answer);

				} else if (checkResult == ANSWER_WRONG) {
					sparkTheWord();
					soundFail.playSound();
				} else if (checkResult == ANSWER_LACK) {

				}
				break;
			}
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
	 * 检查答案
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// 答案不完整
		for (int i = 0; i < ANSWER_NUM; i++) {
			if (mSelectWords.get(i).mWordString.length() == 0) {
				return ANSWER_LACK;
			}
		}
		// 答案完整继续检查
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ANSWER_NUM; i++) {
			sb.append(mSelectWords.get(i).mWordString);
		}
		answer = sb.toString();
		result = Md5.MD5Encode(sb.toString());
		return result.equals(Bean.getAnswer()) ? ANSWER_RIGHT : ANSWER_WRONG;

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
						for (int i = 0; i < ANSWER_NUM; i++) {
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
		params.bottomMargin = 5;
		params.topMargin = 5;

		params.gravity = Gravity.CENTER_VERTICAL;
		for (int i = 0; i < ANSWER_NUM; i++) {
			mViewWordsContainer.addView(mSelectWords.get(i).mViewButton, params);
		}

		// 获得数据
		mAllWords = initAllWord();
		// 更新数据到GridButtonView
		mGridButtonView.updateData(mAllWords, (getWindowManager().getDefaultDisplay().getWidth() - 40) / 6);

		int width = getWindowManager().getDefaultDisplay().getWidth() / 2;

		if (Bean.getAvatar() != null) {
			Picasso.with(getApplicationContext()).load(Bean.getAvatar()).resize(width, width)
					.placeholder(R.drawable.default_avatar3x).into(mAvatarImage);
		} else {
			mAvatarImage.setImageResource(R.drawable.default_avatar3x);
		}
		Picasso.with(getApplicationContext()).load(Bean.getImageUrl()).resize(width, width)
				.placeholder(R.drawable.download_failed).into(mImageView);
	}

	/**
	 * 初始化已选择文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		for (int i = 0; i < ANSWER_NUM; i++) {
			View view = BaseTools.getView(AnswerActivity.this, R.layout.view_gridview_btn);

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
			wordButton.mAllIndex = 99;
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

	/**
	 * 初始化待选文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		// 获得所有待选文字
		String[] words = new String[GridButtonView.COUNT_WORDS];
		words = Bean.getCandidateAnswer().split(",");
		for (int i = 0; i < GridButtonView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();

			button.mWordString = words[i];

			data.add(button);
		}
		return data;
	}

	private void getAnswer(String answer) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("questionId", String.valueOf(QUESTIONS_ID));
		map.put("answer", answer);
		String url = UrlContants.LOCATION + UrlContants.NCWC_ANSWER;
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Gson gson = new Gson();
				AnswerBean bean = gson.fromJson(response.toString(), AnswerBean.class);
				setDialog(bean);
				myGold = bean.getGold();
				share.setCoin(String.valueOf(myGold));
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getBaseContext(), VolleyErrorHelper.getMessage(error, AnswerActivity.this),
						Toast.LENGTH_LONG).show();
			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.answer_dialog_share:
			onShare(mApplicationUtil.getApplicationName(), "你能猜出这是什么吗?",
					new ScreenShot(AnswerActivity.this).getScreenShotBitmap());
			alertDialog.dismiss();
			break;
		case R.id.answer_dialog_comfire:
			alertDialog.dismiss();
			Intent intent = new Intent(AnswerActivity.this, CommentActivity.class);
			intent.putExtra("questionId", QUESTIONS_ID);
			intent.putExtra("isAnswered", true);
			startActivity(intent);
			break;
		case R.id.activity_answer_prop:// 提示框Dialog弹出按钮
			setPropDialog();
			break;
		case R.id.answer_prop_btn:// 答案提示框确定按钮
			alertDialog.dismiss();
			break;
		case R.id.activity_answer_comment:// 评论页按钮
			intent = new Intent(AnswerActivity.this, CommentActivity.class);
			intent.putExtra("questionId", QUESTIONS_ID);
			intent.putExtra("isAnswered", Bean.isAnswered());
			startActivity(intent);
			break;
		case R.id.activity_answer_help:// 答案求助分享按钮
			onShare(mApplicationUtil.getApplicationName(), "你能猜出这是什么吗?",
					new ScreenShot(AnswerActivity.this).getScreenShotBitmap());
			break;
		case R.id.activity_answer_assist:
			if (zhan == 0) {
				love += 1;
				mPraiseTxt.setCompoundDrawables(like, null, null, null);
				mPraiseTxt.setText("赞(" + love + ")");
				zhan = 1;
			} else {
				love -= 1;
				mPraiseTxt.setCompoundDrawables(unlike, null, null, null);
				mPraiseTxt.setText("赞(" + love + ")");
				zhan = 0;
			}
			PublicTools.setPraise(mApplicationUtil, QUESTIONS_ID, zhan);

			break;
		case R.id.activity_answer_chat:// 问题私信按钮
			EaseUserInfo userInfo = new EaseUserInfo(getApplicationContext());
			userInfo.addUserInfo(String.valueOf(Bean.getAuthorId()), Bean.getNickname(), Bean.getAvatar());
			if (EMChat.getInstance().isLoggedIn()) {
				Intent chat = new Intent(AnswerActivity.this, ChatActivity.class);
				chat.putExtra("userId", String.valueOf(Bean.getAuthorId()));
				startActivity(chat);
			} else {
				new EaseLogin(getApplicationContext()).init();
			}
			break;
		case R.id.answer_layout_back:// 返回按钮
			finish();
			break;
		case R.id.answer_layout_report:// 举报按钮
			intent = new Intent(AnswerActivity.this, ActivityReport.class);
			intent.putExtra("id", QUESTIONS_ID);
			startActivity(intent);
			break;
		case R.id.activity_answer_avatar:// 头像点击跳转到个人中心
			Intent user = new Intent(AnswerActivity.this, UserDetail.class);
			user.putExtra("id", String.valueOf(Bean.getAuthorId()));
			startActivity(user);
			break;
		case R.id.activity_answer_video_image:// 播放视屏

			if (mMediaPlayer.isPlaying()) {
				isPlay = true;
			}
			setPlay(isPlay);
			break;
		case R.id.activity_answer_surfaceview:// 播放视屏
			if (mMediaPlayer.isPlaying()) {
				isPlay = true;
			}
			setPlay(isPlay);
			break;

		case R.id.activity_answer_imageview:// 播放视屏
			if (isVideo) {
				if (mMediaPlayer.isPlaying()) {
					isPlay = false;
				}
				setPlay(isPlay);
			} else {
				intent = new Intent(AnswerActivity.this, ActivityShowPicture.class);
				intent.putExtra("imageUrl", Bean.getImageUrl());
				startActivity(intent);
			}
			break;

		}

	}

	private void setPlay(boolean isplay) {
		if (isplay) {
			mBarLayout.setVisibility(View.VISIBLE);
			mUserLayout.setVisibility(View.VISIBLE);
			mVideoImg.setVisibility(View.VISIBLE);
			mPropBtn.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.VISIBLE);
			ready();
			isPlay = false;
		} else {
			mBarLayout.setVisibility(View.GONE);
			mUserLayout.setVisibility(View.GONE);
			mVideoImg.setVisibility(View.GONE);
			mPropBtn.setVisibility(View.GONE);
			mImageView.setVisibility(View.GONE);
			mMediaPlayer.start();
			isPlay = true;
		}
	}

	private void setDialog(AnswerBean bean) {
		alertDialog = new AlertDialog.Builder(this).create();

		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.view_answer_dialog);
		LinearLayout before = (LinearLayout) window.findViewById(R.id.answer_before);
		RelativeLayout after = (RelativeLayout) window.findViewById(R.id.answer_after);
		if (!Bean.isAnswered()) {
			after.setVisibility(View.GONE);
			before.setVisibility(View.VISIBLE);
			mGifView = (GifView) window.findViewById(R.id.answer_dialog_gif);
			mGifView.setMovieResource(R.drawable.gamble_gold_animation);
			TextView gold = (TextView) window.findViewById(R.id.answer_dialog_gold);
			gold.setText("+ " + bean.getReward() + " 金币");
			TextView index = (TextView) window.findViewById(R.id.answer_dialog_index);
			index.setText("你是第 " + bean.getAnsweredIndex() + " 个答对的用户");
			soundSuccess.playSound();
			Bean.setAnswered(true);
		} else {
			before.setVisibility(View.GONE);
			after.setVisibility(View.VISIBLE);
			TextView index = (TextView) window.findViewById(R.id.answer_after_index);
			index.setText("已有 " + bean.getAnsweredIndex() + " 人答对此题");
		}
		Button mShareBtn = (Button) window.findViewById(R.id.answer_dialog_share);
		mShareBtn.setOnClickListener(AnswerActivity.this);
		Button mComfireBtn = (Button) window.findViewById(R.id.answer_dialog_comfire);
		mComfireBtn.setOnClickListener(AnswerActivity.this);
	}

	private void setPropDialog() {
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.view_answer_prop_dialog);
		LinearLayout mLinear = (LinearLayout) window.findViewById(R.id.answer_prop_linear);
		tvMyGold = (TextView) window.findViewById(R.id.answer_prop_gold);
		tvMyGold.setText("剩余金币：" + myGold + "金币");
		Button mConfirm = (Button) window.findViewById(R.id.answer_prop_btn);
		mConfirm.setOnClickListener(AnswerActivity.this);

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
			View view = BaseTools.getView(AnswerActivity.this, R.layout.view_gridview_btn);

			final WordButton holder = new WordButton();

			holder.mViewButton = (Button) view.findViewById(R.id.view_item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;
			holder.mViewButton.setBackgroundResource(R.drawable.normal_prop_question);
			holder.mViewButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (myGold > GOLD) {
						PDialogUtil.showDialog(AnswerActivity.this, "", false);
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

	private void getProp(int index, final WordButton wordButton) {

		String url = UrlContants.LOCATION + UrlContants.NCWC_QUESTION_PROP;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("itemId", "1");
		map.put("index", String.valueOf(index));
		map.put("questionId", String.valueOf(QUESTIONS_ID));
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				PDialogUtil.cancelDialog();
				try {

					if (response.getInt("ret") == 0) {

						String prop = response.getString("value");
						if (prop != null) {
							setPropWords(wordButton, prop);
							myGold -= GOLD;
							tvMyGold.setText("剩余金币：" + myGold + "金币");
							share.setCoin(String.valueOf(myGold));
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

	private void getDataInit() {
		mCommentBtn.setText("评论(" + Bean.getCommentNumber() + ")");
		mNameTxt.setText(Bean.getNickname());
		mTimeTxt.setText(DateTools.getStrTime_ymd(Bean.getCreateTime()));
		ANSWER_NUM = Bean.getAnswerLength();
		zhan = Bean.getEvaluation();
		love = Bean.getLove();
		mTitleTxt.setText("" + Bean.getTitle());
		if ("null".equals(Bean.getVideoUrl()) || Bean.getVideoUrl() == null || "".equals(Bean.getVideoUrl())) {
			isVideo = false;
			setVideo(isVideo);

		} else {
			// videoPath = Bean.getVideoUrl();
			isVideo = true;

			setVideo(isVideo);

		}
		if (zhan == 0) {
			mPraiseTxt.setCompoundDrawables(unlike, null, null, null);
			mPraiseTxt.setText("赞(" + love + ")");
		} else {
			mPraiseTxt.setCompoundDrawables(like, null, null, null);
			mPraiseTxt.setText("赞(" + love + ")");
		}
		mPriogressLayout.setVisibility(View.GONE);
	}

	private void ready() {
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(Bean.getVideoUrl());
			mMediaPlayer.setDisplay(mSurfaceView.getHolder());
			mMediaPlayer.prepare();
			mMediaPlayer.seekTo(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final class SurfaceCallBack implements Callback {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
		}

	}

	private void setVideo(boolean isVideo) {
		if (isVideo) {
			// ready();
			mVideoImg.setOnClickListener(this);
			mSurfaceView.setOnClickListener(this);
			mImageView.setOnClickListener(this);
			mVideoImg.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.VISIBLE);
		} else {
			mVideoImg.setVisibility(View.GONE);
			mImageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		mMediaPlayer.release();
		mMediaPlayer = null;
		super.onDestroy();
	}
}
