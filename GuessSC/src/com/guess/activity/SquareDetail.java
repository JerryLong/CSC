package com.guess.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.adapter.FragmentMessageAdapter;
import com.guess.fragment.CommentFragment;
import com.guess.fragment.SquareQuestionFragment;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class SquareDetail extends FragmentActivity implements OnClickListener, OnPageChangeListener{
	private ViewPager mPager;
	private FragmentMessageAdapter mAdapter;
	private ArrayList<Fragment> fragmentList;
	
	private SquareQuestionFragment questionFragment;
	
	private ApplicationUtil application;
	
	private TextView tvBack, tvReport;
	
	private ImageView imgPicture;
	
	private TextView tvCreateTime;
	private TextView tvComment;
	private TextView tvLove;
	
	//详细信息
	private String confirmAnswer, myInfo, createTime, deadLine, imageUrl, evaluation, love, attention, totalBet, commentNumber, id, description;
	private int authorBet;
	private ArrayList<HashMap<String, Object>> answerList;
	private boolean finish;
	
	private LinearLayout lltQuestion, lltComment;
	private TextView tvQuestion;
	private ImageView imgQuestion, imgComment;
	
	private Drawable drawableLike, drawableUnLike;
	private final String COLOR_WITE = "#ffffff";
	private final String COLOR_YELLOW = "#fd9034";
	
	private int flag = 0;//0:题目栏  1:评论栏
	
	private String likeUrl = "http://api.caisichuan.com/gambleGuess/evaluationQuestion";
	private String betUrl = "http://api.caisichuan.com/gambleGuess/submitChoice";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_detail);
		
		initView();
		initData();
		initViewPager();
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void initData() {
		Intent intent = getIntent();
		imageUrl = intent.getStringExtra("imageUrl");
		id = intent.getStringExtra("id");
		createTime = intent.getStringExtra("createTime");
		deadLine = intent.getStringExtra("deadline");
		attention = intent.getStringExtra("attention");
		totalBet = intent.getStringExtra("totalBet");
		commentNumber = intent.getStringExtra("commentNumber");
		description = intent.getStringExtra("description");
		evaluation = intent.getStringExtra("evaluation");
		authorBet = intent.getIntExtra("authorBet", 1);
		love = intent.getStringExtra("love");
		myInfo = intent.getStringExtra("myInfo");
		finish = intent.getBooleanExtra("finished", false);
		confirmAnswer = intent.getStringExtra("confirmAnswer");		
		answerList = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("answers");
		
		drawableLike = getResources().getDrawable(R.drawable.ic_square_like2x);
		drawableUnLike = getResources().getDrawable(R.drawable.ic_square_unlike2x);
		drawableLike.setBounds(0, 0, drawableLike.getMinimumWidth(), drawableLike.getMinimumHeight());
		drawableUnLike.setBounds(0, 0, drawableUnLike.getMinimumWidth(), drawableUnLike.getMinimumHeight());
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Picasso.with(SquareDetail.this).load(imageUrl).resize(480, 300).placeholder(R.drawable.default_picture2x).into(imgPicture);
		String startTime = df.format(Long.parseLong(createTime));
		
		tvCreateTime.setText(startTime.substring(5, startTime.length()-3));
		
		if("1".equals(evaluation)){
			tvLove.setText("已赞 " + love);
			tvLove.setCompoundDrawables(drawableLike, null, null, null);
		}else{
			tvLove.setText("赞 " + love);
			tvLove.setCompoundDrawables(drawableUnLike, null, null, null);
		}
		
		tvComment.setTextColor(Color.parseColor(COLOR_YELLOW));
		tvComment.setText("评论/" + commentNumber);
		
	}

	/**
	 * 初始化
	 */
	private void initView() {
		application = new ApplicationUtil(this);
		
		
		
		tvBack = (TextView) findViewById(R.id.square_detail_back);
		tvReport = (TextView) findViewById(R.id.square_detail_report);
		
		imgPicture = (ImageView) findViewById(R.id.square_detail_image);
		imgQuestion = (ImageView) findViewById(R.id.square_detail_left_icon);
		imgComment = (ImageView) findViewById(R.id.square_detail_right_icon);
		
		lltQuestion = (LinearLayout) findViewById(R.id.square_detail_llt_question);
		lltComment = (LinearLayout) findViewById(R.id.square_detail_llt_comment);
		
		tvCreateTime = (TextView) findViewById(R.id.square_detail_time);
		tvLove = (TextView) findViewById(R.id.square_detail_like);

		tvComment = (TextView) findViewById(R.id.square_detail_right_text);
		tvQuestion = (TextView) findViewById(R.id.square_detail_left_text);

		tvBack.setOnClickListener(this);
		tvReport.setOnClickListener(this);
		lltComment.setOnClickListener(this);
		lltQuestion.setOnClickListener(this);
		tvLove.setOnClickListener(this);
	}
	
	/**
	 * 初始化Viewpager
	 */
	private void initViewPager(){
		questionFragment = new SquareQuestionFragment();
		questionFragment.setData(betUrl, authorBet, confirmAnswer, myInfo, deadLine, description, totalBet, attention, answerList, finish);
		mPager = (ViewPager) findViewById(R.id.square_detail_vp);
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(questionFragment);
		CommentFragment commentFragment = new CommentFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("flag", 1);
		bundle.putInt("questionId", Integer.parseInt(id));
		commentFragment.setArguments(bundle);
		fragmentList.add(commentFragment);
		mAdapter = new FragmentMessageAdapter(getSupportFragmentManager(), fragmentList);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.square_detail_back:
			finish();
			break;

		case R.id.square_detail_report:
			Intent report = new Intent(SquareDetail.this, ActivityReport.class);
			report.putExtra("id", id);
			startActivity(report);
			break;
		case R.id.square_detail_llt_comment:
			
		//	setDataChange();
		case R.id.square_detail_llt_question:
		//	setDataChange();
			if(flag == 0){
				mPager.setCurrentItem(1);
			}else{
				mPager.setCurrentItem(0);
			}
			break;
		case R.id.square_detail_like:
			HashMap<String, String> map = new HashMap<>();
			map.put("questionId", id);
			
			if("1".equals(evaluation)){
				evaluation = "0";
				map.put("evaluation", evaluation);
				setLove(map);
				love = String.valueOf(Integer.parseInt(love) - 1);
				tvLove.setText("赞 " + love);
				tvLove.setCompoundDrawables(drawableUnLike, null, null, null);
			}else{
				evaluation = "1";
				map.put("evaluation", evaluation);
				setLove(map);
				love = String.valueOf(Integer.parseInt(love) + 1);
				tvLove.setText("赞 " + love);
				tvLove.setCompoundDrawables(drawableLike, null, null, null);
			}
			break;
		}
		
	}
	
	
	/**
	 * 点赞
	 * @param map
	 */
	private void setLove(final HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(likeUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == Constant.NOT_LOGIN){
						LoginUtil login = new LoginUtil(SquareDetail.this);
						login.login(new LoginListener() {
							
							@Override
							public void onLoginAfter() {
								setLove(map);
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
		application.getRequestQueue().add(json);
	}
	
	@SuppressWarnings("deprecation")
	private void setDataChange(){
		if(flag == 1){
			lltQuestion.setBackgroundColor(Color.parseColor(COLOR_YELLOW));
			tvQuestion.setTextColor(Color.parseColor(COLOR_WITE));
			imgQuestion.setImageDrawable(getResources().getDrawable(R.drawable.square_detail_question_white2x));
			
			lltComment.setBackgroundColor(Color.parseColor(COLOR_WITE));
			tvComment.setTextColor(Color.parseColor(COLOR_YELLOW));
			imgComment.setImageDrawable(getResources().getDrawable(R.drawable.square_detail_comment2x));
			flag = 0;
		}else if(flag == 0){
			lltComment.setBackgroundColor(Color.parseColor(COLOR_YELLOW));
			tvComment.setTextColor(Color.parseColor(COLOR_WITE));
			imgComment.setImageDrawable(getResources().getDrawable(R.drawable.square_detail_comment_white2x));
			
			lltQuestion.setBackgroundColor(Color.parseColor(COLOR_WITE));
			tvQuestion.setTextColor(Color.parseColor(COLOR_YELLOW));
			imgQuestion.setImageDrawable(getResources().getDrawable(R.drawable.square_detail_question2x));
			flag = 1;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		//mPager.setCurrentItem(arg0);
		setDataChange();
	}

}
