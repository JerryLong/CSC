package com.guess.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
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
import com.guess.myutils.ShareData;
import com.guess.myutils.LoginUtil.LoginListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class SquareQuestionFragment extends Fragment implements OnClickListener{
	private boolean init = false;
	private Activity activity;
	private String deadLine, attention, totalBet, description;// 问题的信息
	private int mAnswerIndex = -1, myBet = 0, income = 0;// 我选择的答案
	private int clickIndex;//当前选择的答案选项
	
	private int authorBet = 0;//作者的押注
	private final float BET = 100;//最大押注为authorBet*100  最小押注为authorBet/100
	
	private int answerIndex = -1;// 正确答案
	private String answerContent = "";// 正确答案
	private int questionId = -1;// 此问题id
	private boolean finish = false;// 此问题是否结束

	private ArrayList<HashMap<String, Object>> answersList;

	private TextView tvDead, tvAttention, tvTotalBet, tvDescription;

	private LinearLayout lltResult;
	private TextView tvResult;
	private TextView tvAnswer;

	private LinearLayout lltAnswer;

	// 投注
	private AlertDialog dialog;
	private int lowBet = 5, highBet = 2000;
	private int currentBet;
	private int myGold = 0;

	private SeekBar seekBar;
	private EditText etGold;

	private TextView tvMyGold;
	private TextView tvLow, tvHigh;
	private TextView tvTips;

	private String betUrl = "http://api.caisichuan.com/gambleGuess/submitChoice";
	private String guessUrl = "http://api.caisichuan.com/officalActivity/submitGambleActivityAnswer";
	private String url = betUrl;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		if (!init) {
			tvDead = (TextView) activity.findViewById(R.id.square_detail_join_time);
			tvAttention = (TextView) activity.findViewById(R.id.square_detail_join_num);
			tvTotalBet = (TextView) activity.findViewById(R.id.square_detail_join_gold);
			tvDescription = (TextView) activity.findViewById(R.id.square_detail_question_name);

			tvResult = (TextView) activity.findViewById(R.id.square_detail_result);
			tvAnswer = (TextView) activity.findViewById(R.id.square_detail_show_answer);

			lltAnswer = (LinearLayout) activity.findViewById(R.id.square_detail_llt);
			lltResult = (LinearLayout) activity.findViewById(R.id.square_detail_show_result);

			tvAttention.setText(attention);
			tvTotalBet.setText(totalBet);
			tvDescription.setText(description);
			if (System.currentTimeMillis() > Long.parseLong(deadLine)) {
				tvDead.setText("已结束");
			} else {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String deadTime = df.format(Long.parseLong(deadLine));
				tvDead.setText(deadTime.subSequence(5, deadTime.length() - 3));
			}
			// System.out.println("list==="+answersList);
			setLayout();
			init = true;
		}
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_square_detail_question, container, false);
	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		HashMap<String, Object> map;
		LinearLayout llt;
		for (int i = 0; i < answersList.size(); i++) {
			map = answersList.get(i);
			final RelativeLayout  rlt = (RelativeLayout) lltAnswer.getChildAt(3 + i);
			rlt.setVisibility(View.VISIBLE);
			((TextView) rlt.getChildAt(1)).setText(map.get("content").toString());
			if (finish) {
				rlt.getChildAt(3).setVisibility(View.INVISIBLE);
			}
			
			llt = (LinearLayout) rlt.getChildAt(2);
			if ((i == mAnswerIndex) || (i == answerIndex)) {
				rlt.setBackgroundResource(R.drawable.square_red_answer2x);
			//	rlt.setBackgroundColor(Color.parseColor("#ff6600"));
				if (i == mAnswerIndex) {
					((TextView) llt.getChildAt(0)).setText("押注:  " + myBet);
				} else {
					((TextView) llt.getChildAt(0)).setText("押注:  0");
				}
				((TextView) rlt.getChildAt(1)).setTextColor(Color.WHITE);
				((TextView) llt.getChildAt(0)).setTextColor(Color.WHITE);
				((TextView) llt.getChildAt(1)).setTextColor(Color.WHITE);
				
			} else {
				((TextView) llt.getChildAt(0)).setText("押注:  0");
			}
			((TextView) llt.getChildAt(1)).setText("总押注: " + map.get("totalBet").toString());

			// 设置监听
			final int index = i;
			
			rlt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 活动没有结束
					if (!finish && ((mAnswerIndex == -1) || (mAnswerIndex == index))) {
						clickIndex = index;
						if(mAnswerIndex == -1){
							rlt.setBackgroundResource(R.drawable.square_red_answer2x);
						}
						
						LinearLayout llt = (LinearLayout) rlt.getChildAt(2);
						((TextView) rlt.getChildAt(1)).setTextColor(Color.WHITE);
						((TextView) llt.getChildAt(0)).setTextColor(Color.WHITE);
						((TextView) llt.getChildAt(1)).setTextColor(Color.WHITE);
					//	rlt.setBackgroundResource(R.drawable.square_detail_anwser_bg2x);
	
						dialog = new AlertDialog.Builder(activity).create();
						dialog.show();
						dialog.setCancelable(false);
						Window window = dialog.getWindow();
						window.setContentView(R.layout.dialog_bet);
						
						View view = window.getDecorView();
						seekBar = (SeekBar) view.findViewById(R.id.dialog_bet_seek);
						etGold = (EditText) view.findViewById(R.id.dialog_bet_my_bet);
						Button btnSure = (Button) view.findViewById(R.id.dialog_bet_ok);
						Button btnCancle = (Button) view.findViewById(R.id.dialog_bet_cancel);
						ImageView btnAdd = (ImageView) view.findViewById(R.id.dialog_bet_add);
						ImageView btnReduce = (ImageView) view.findViewById(R.id.dialog_bet_reduce);
						tvHigh = (TextView) view.findViewById(R.id.dialog_bet_high);
						tvLow = (TextView) view.findViewById(R.id.dialog_bet_low);
						tvMyGold = (TextView) view.findViewById(R.id.dialog_bet_my_gold);
						tvTips = (TextView) view.findViewById(R.id.dialog_bet_tips);

						initDialog();
						
						btnCancle.setOnClickListener(SquareQuestionFragment.this);
						btnSure.setOnClickListener(SquareQuestionFragment.this);
						btnAdd.setOnClickListener(SquareQuestionFragment.this);
						btnReduce.setOnClickListener(SquareQuestionFragment.this);

					}
				}
			});
		}

		// 结果显示
		if (finish) {
			lltResult.setVisibility(View.VISIBLE);
			if (myBet > 0) {
				if (income > 0) {
					tvResult.setText("恭喜你获得了 " + income + "金币");
				} else {
					tvResult.setText("很遗憾您未猜中!");
				}
			} else {
				tvResult.setText("很遗憾您未参与竞猜!");
			}
			tvAnswer.setText("正确答案为:" + ((char) (answerIndex + 65)) + " " + answerContent);
		} else {
			lltResult.setVisibility(View.GONE);
		}
	}

	private void initDialog() {
		tvLow.setText("最小投注额 " + lowBet + "金币");
		tvHigh.setText("最大投注额 " + highBet + "金币");
		tvMyGold.setText("我的金币:" + myGold + "金币");
		seekBar.setMax(highBet - myBet);
		currentBet = (highBet + lowBet) / 2;
		seekBar.setProgress(currentBet);
		etGold.setText(String.valueOf(currentBet));
		seekBar.setOnSeekBarChangeListener(listener);
	}

	private SeekBar.OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			currentBet = progress;
			etGold.setText(String.valueOf(progress));
			if (progress > myGold) {
				tvTips.setText("你当前的金币不足以支付此赌注");
			} else {
				if (progress < lowBet) {
					tvTips.setText("最低投注为" + lowBet + "金币");
				} else {
					tvTips.setText("");
				}

			}
		}
	};

	/**
	 * 
	 * @param url
	 * 			赌注url  分为官方活动的竞猜和竞猜广场的竞猜两个
	 * 设置数据
	 * @param authorBet
	 * 			作者押注
	 * 
	 * @param confirmAnswer
	 *            正确答案信息
	 * @param myInfo
	 *            信息
	 * @param dead
	 *            截止日期
	 * @param description
	 *            描述(问题)
	 * @param totalBet
	 *            总赌注
	 * @param attention
	 *            热度
	 * @param list
	 *            答案列表
	 * @param finish
	 *            是否结束
	 */
	public void setData(String url, int authorBet, String confirmAnswer, String myInfo, String dead, String description, String totalBet,
			String attention, ArrayList<HashMap<String, Object>> list, boolean finish) {
		this.url = url;
		this.answersList = list;
		this.attention = attention;
		this.deadLine = dead;
		this.description = description;
		this.totalBet = totalBet;
		this.finish = finish;
		this.authorBet = authorBet;
		lowBet = (int) Math.ceil(this.authorBet/BET);
		highBet = (int) (this.authorBet*BET);
		myGold = Integer.parseInt(ShareData.getInstance(activity).getIntegration());
		if (!"".equals(myInfo) && (myInfo != null) && (!"null".equals(myInfo))) {
			try {
				JSONObject json = new JSONObject(myInfo);
				questionId = json.getInt("questionId");
				income = json.getInt("income");
				JSONArray array = json.getJSONArray("choices");
				JSONObject object = array.getJSONObject(0);
				myBet = object.getInt("bet");
				mAnswerIndex = object.getInt("answerIndex");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!"".equals(confirmAnswer) && (confirmAnswer != null) && (!"null".equals(confirmAnswer))) {
			try {
				JSONObject confirm = new JSONObject(confirmAnswer);
				answerIndex = confirm.getInt("answerIndex");
				answerContent = confirm.getString("content");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_bet_ok:
			if((currentBet > lowBet) && (currentBet < myGold)){
				mAnswerIndex = clickIndex;
				myBet += currentBet;
				HashMap<String, String> map = new HashMap<>();
				if(url.equals(betUrl)){
					//提交竞猜广场的答案
					map.put("questionId", String.valueOf(questionId)); 
				}else if(url.equals(guessUrl)){
					//提交官方活动的答案
					map.put("activityId", String.valueOf(questionId)); 
				}
				map.put("answerIndex", String.valueOf(mAnswerIndex));
				  
				map.put("bet", String.valueOf(currentBet));
				
				bet(url, map);
				
				dialog.dismiss();
			}else{
				tvTips.setText("不能提交赌注!");
			}
			
			break;
		case R.id.dialog_bet_cancel:
			RelativeLayout rltChoise = (RelativeLayout) lltAnswer.getChildAt(3 + clickIndex);
			if(mAnswerIndex == -1){
				rltChoise.setBackgroundResource(R.drawable.x);
				LinearLayout  llt = (LinearLayout) rltChoise.getChildAt(2);
				((TextView) rltChoise.getChildAt(1)).setTextColor(Color.BLACK);
				((TextView) llt.getChildAt(0)).setTextColor(Color.BLACK);
				((TextView) llt.getChildAt(1)).setTextColor(Color.BLACK);

			}
			clickIndex = -1;
			dialog.dismiss();
			break;
		case R.id.dialog_bet_add:
			currentBet = Integer.parseInt(etGold.getText().toString()) + 1;
			etGold.setText(String.valueOf(currentBet));
			seekBar.setProgress(currentBet);
			if(currentBet > myGold){
				tvTips.setText("你当前的金币不足以支付此赌注");
			}else{
				if(currentBet < lowBet){
					tvTips.setText("最低投注为" + lowBet + "金币");
				}else{
					tvTips.setText("");
				}	
			}
			break;
		case R.id.dialog_bet_reduce:
			currentBet = Integer.parseInt(etGold.getText().toString()) - 1;
			etGold.setText(String.valueOf(currentBet));
			seekBar.setProgress(currentBet);
			if(currentBet > myGold){
				tvTips.setText("你当前的金币不足以支付此赌注");
			}else{
				if(currentBet < lowBet){
					tvTips.setText("最低投注为" + lowBet + "金币");
				}else{
					tvTips.setText("");
				}
			}
			break;
		}
		
	}
	
	/**
	 * 提交赌注
	 * @param map
	 */
	private void bet(final String url, final HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
			//	System.out.println("response=="+response);
				try {
					if((response.getInt("ret") == 0) && (response.getBoolean("success"))){
						//提交赌注成功
						RelativeLayout rltChoise = (RelativeLayout) lltAnswer.getChildAt(3 + mAnswerIndex);
						LinearLayout llt = (LinearLayout) rltChoise.getChildAt(2);
						((TextView) llt.getChildAt(0)).setText("押注: " + myBet);
						((TextView) llt.getChildAt(1)).setText("总押注: " + (currentBet + totalBet));
						tvTotalBet.setText("总金币:" + (currentBet + totalBet));
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(activity);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									bet(url, map);
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
		new ApplicationUtil(activity).getRequestQueue().add(json);
	}
}
