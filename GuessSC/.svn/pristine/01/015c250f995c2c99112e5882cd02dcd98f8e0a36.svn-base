package com.guess.user;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.myadapter.MyRankAdapter;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.MyNetManager;
import com.guess.myview.XListView;
import com.guess.myview.XListView.IXListViewListener;
import com.guess.tools.BaseTools;
import com.guess.view.TitleScrollView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我参与的
 * 
 * @author YXC
 *
 */
@SuppressLint("InflateParams")
public class RankingList extends Activity
		implements OnClickListener, IXListViewListener {
	private TitleScrollView mTopTitle;
	LinearLayout mRadioGroup_content;
	LinearLayout more_columns;
	RelativeLayout title_column;
	/**
	 * 新闻分类列表
	 */
	private ArrayList<String> listColumn = new ArrayList<String>();
	/**
	 * 当前选中的栏目
	 */
	private int columnSelectIndex = 0;
	/**
	 * 左阴影部分
	 */
	public ImageView shade_left;
	/**
	 * 右阴影部分
	 */
	public ImageView shade_right;
	/**
	 * 屏幕宽度
	 */
	private int mScreenWidth = 0;
	/**
	 * Item宽度
	 */
	private int mItemWidth = 0;

	private MyNetManager myNet;
	
	private TextView tvBack;

	private TextView tvRank;
	private TextView tvNum;// 我的金币或者积分数
	private TextView tvOrder;// 我的排名

	private XListView mListView;
	private MyRankAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mList;

	private String levelRankUrl = "http://api.caisichuan.com/user/levelRankList";
	private String goldRankUrl = "http://api.caisichuan.com/user/goldRankList";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking_list);

		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		listColumn.add("金币榜");
		listColumn.add("积分榜");
		mScreenWidth = BaseTools.getWindowsWidth(this);
		// 一个Item宽度为屏幕的1/6
		mItemWidth = mScreenWidth / 6;
		mTopTitle = (TitleScrollView) findViewById(R.id.mTopTitleScrollViewRank);
		mRadioGroup_content = (LinearLayout)findViewById(R.id.mRadioGroup_content_rank);
		initColumn();
		
		myNet = new MyNetManager(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.ranking_tv_back);

		tvRank = (TextView) findViewById(R.id.ranking_name);
		tvNum = (TextView) findViewById(R.id.ranking_num);
		tvOrder = (TextView) findViewById(R.id.ranking_order);

		mListView = (XListView) findViewById(R.id.ranklist_lv_content);
		mList = new ArrayList<>();
		mAdapter = new MyRankAdapter(RankingList.this, mList);
		mListView.setAdapter(mAdapter);
		mListView.setPullLoadEnable(false);// 设置不可加载更多
		mListView.setXListViewListener(this);

		mListView.setLoading(this, R.string.emptyview_tv_rank);

		if(myNet.netIsAvailable()){
			HashMap<String, String> map = new HashMap<>();
			getData(goldRankUrl, map);
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
			.show();
		}

		tvBack.setText(getString(R.string.title_myself));

		tvBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ranking_tv_back:
			finish();
		//	new ApplicationUtil(getApplicationContext()).onShare("", "", "", "", "", "");
			break;
		}
	}
	
	/**
	 * 初始化Column栏目项
	 */
	private void initColumn() {
		mRadioGroup_content.removeAllViews();
		int count = listColumn.size();
		mTopTitle.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, more_columns,
				title_column);
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = 3;
			params.rightMargin = 3;
			params.topMargin = 3;
			params.bottomMargin = 3;
			TextView columnTextView = new TextView(this);
			columnTextView.setBackgroundResource(R.drawable.top_guess_selector);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setPadding(3, 8, 3, 8);
			columnTextView.setId(i);
			columnTextView.setText(listColumn.get(i));
			columnTextView.setTextColor(getResources().getColor(R.color.white));
			if (columnSelectIndex == i) {
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView != v)
							localView.setSelected(false);
						else {
							localView.setSelected(true);
							selectTab(i);
							changeRankList();
						}
					}
				}
			});
			mRadioGroup_content.addView(columnTextView, i, params);
		}

	}
	
	/**
	 * 选择的Column里面的一项
	 */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			mTopTitle.smoothScrollTo(i2, 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}

	/**
	 * get data
	 * 
	 * @param url
	 * @param map
	 */
	private void getData(final String url, final HashMap<String, String> map) {
		MyJsonRequest jsonRequest = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				//System.out.println("排行榜response="+response);
				mListView.stopRefresh();
				mListView.setRefreshTime(getApplicationContext(), "rank");
				try {
					if (response.getInt("ret") == 0) {
						if (columnSelectIndex == 0) {
							JSONObject myObject = response.getJSONObject("myRank");
							tvNum.setText(myObject.getString("historyGold"));
							tvOrder.setText(myObject.getString("goldRank"));

							JSONArray rankArray = response.getJSONArray("list");
							HashMap<String, Object> dataMap;
							for (int i = 0; i < rankArray.length(); i++) {
								dataMap = new HashMap<>();
								JSONObject rank = rankArray.getJSONObject(i);
								dataMap.put("avatar", rank.getString("avatar"));
								dataMap.put("nickname", rank.getString("nickname"));
								dataMap.put("rank", rank.getString("goldRank"));
								dataMap.put("grade", rank.getString("historyGold"));
								mList.add(dataMap);
								mAdapter.notifyDataSetChanged();
							}
						} else {
							JSONObject myObject = response.getJSONObject("myRank");
							tvNum.setText(myObject.getString("historyLevel"));
							tvOrder.setText(myObject.getString("levelRank"));

							JSONArray rankArray = response.getJSONArray("list");
							HashMap<String, Object> dataMap;
							for (int i = 0; i < rankArray.length(); i++) {
								dataMap = new HashMap<>();
								JSONObject rank = rankArray.getJSONObject(i);
								dataMap.put("avatar", rank.getString("avatar"));
								dataMap.put("nickname", rank.getString("nickname"));
								dataMap.put("rank", rank.getString("levelRank"));
								dataMap.put("grade", rank.getString("historyLevel"));
								mList.add(dataMap);
								mAdapter.notifyDataSetChanged();
							}
						}

					} else {
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(RankingList.this);
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
				mListView.stopRefresh();
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(jsonRequest);
	}

	@Override
	public void onRefresh() {
		// refresh data
		if(myNet.netIsAvailable()){
			mListView.setLoading(this, R.string.emptyview_tv_rank);
			mList.clear();
			String rankUrl = "";
			if (columnSelectIndex != 0) {
				rankUrl = levelRankUrl;
			} else {
				rankUrl = goldRankUrl;
			}
	
			HashMap<String, String> map = new HashMap<>();
			getData(rankUrl, map);
		}else{
			mListView.stopRefresh();
			Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
			.show();
		}
	}

	@Override
	public void onLoadMore() {
		// load more
	}

	/**
	 * 改变当前是积分榜还是金币榜
	 */
	private void changeRankList() {
		mListView.setLoading(this, R.string.emptyview_tv_rank);
		String rankUrl = "";
		mList.clear();
		HashMap<String, String> map = new HashMap<>();
		if (columnSelectIndex != 0) {
			// 积分榜
			rankUrl = levelRankUrl;
			tvRank.setText("我的积分");
		} else {
			// 金币榜
			tvRank.setText("我的金币");
			rankUrl = goldRankUrl;
		}
		mAdapter = new MyRankAdapter(RankingList.this, mList);
		mListView.setAdapter(mAdapter);
		getData(rankUrl, map);
	}
}
