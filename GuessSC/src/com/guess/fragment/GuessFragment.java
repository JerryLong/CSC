package com.guess.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.activity.AnswerActivity;
import com.guess.adapter.GuessAdapter;
import com.guess.bean.GuessBean;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.myutils.MyJsonRequest;
import com.guess.utils.UrlContants;
import com.guess.utils.VolleyErrorHelper;
import com.jerry.pulltorefresh.library.ILoadingLayout;
import com.jerry.pulltorefresh.library.PullToRefreshBase;
import com.jerry.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jerry.pulltorefresh.library.PullToRefreshGridView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GuessFragment extends Fragment implements OnRefreshListener2<GridView> {
	private PullToRefreshGridView mGridView;
	private Context mContext;
	private ArrayList<GuessBean> mList;
	private GuessAdapter mAdapter;
	RelativeLayout mProgressLayout;
	public final static int SET_NEWSLIST = 0x000001;
	public final static int REFRESH_DATA = 0x000002;
	public final static int LOADING_MORE = 0x000003;
	public final static int SET_DATA = 0x000004;
	public final static int ERROR_DATA = 0x000005;
	private String TYPE = "";
	private int PAGE_NO = 0;
	private int PAGE_SIZE = 20;
	private int CURRENT_PAGE = 0;
	private boolean isFirst = true;
	ApplicationUtil mApplicationUtil;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_NEWSLIST:
				if (TYPE.equals(UrlContants.NCWC_NEW_ID) || "".equals(TYPE))
					getData(UrlContants.NCWC_NEW_NUM);
				else
					getData(TYPE);
				break;
			case REFRESH_DATA:
				if (TYPE.equals(UrlContants.NCWC_NEW_ID)) {
					getData(UrlContants.NCWC_NEW_NUM);
				} else {
					PAGE_NO = 0;
					CURRENT_PAGE = 0;
					getData(UrlContants.NCWC_HOT);
				}
				break;
			case LOADING_MORE:
				if (TYPE.equals(UrlContants.NCWC_NEW_ID)) {
					if (mList.size() >= 20)
						getData(UrlContants.NCWC_NEW_ID);
					else
						mGridView.onRefreshComplete();
				} else {
					PAGE_NO += 1;
					CURRENT_PAGE = 1;
					getData(UrlContants.NCWC_HOT);
				}
				break;
			case SET_DATA:
				mList = new ArrayList<>();
				mList = (ArrayList<GuessBean>) msg.obj;
				if (isFirst) {
					mAdapter.setData(mList);
					mProgressLayout.setVisibility(View.GONE);
				} else
					mAdapter.addData(mList);

				// mGridView.onRefreshComplete();
				break;
			}
		}

	};

	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		mApplicationUtil = new ApplicationUtil(mContext);
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		TYPE = args != null ? args.getString("type") : "";
		mAdapter = new GuessAdapter(mContext, getActivity().getWindowManager().getDefaultDisplay().getWidth());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guess, null);
		mGridView = (PullToRefreshGridView) view.findViewById(R.id.guess_fragment_gridview);
		mProgressLayout = (RelativeLayout) view.findViewById(R.id.guess_fragment_progress);
		mGridView.setOnRefreshListener(this);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), AnswerActivity.class);
				GuessBean bean = (GuessBean) parent.getAdapter().getItem(position);
				intent.putExtra("id", bean.getId());
				intent.putExtra("answered", bean.isAnswered());
				startActivity(intent);
			}
		});
		mGridView.setAdapter(mAdapter);
		initIndicator();
		return view;
	}

	private void getData(String type) {
		String url = UrlContants.LOCATION + UrlContants.NCWC_;
		HashMap<String, String> map = null;
		if (type.equals(UrlContants.NCWC_NEW_NUM)) {
			isFirst = true;
			url += UrlContants.NCWC_NEW_NUM;
			map = new HashMap<String, String>();
			map.put("number", "20");
		} else if (type.equals(UrlContants.NCWC_NEW_ID)) {
			isFirst = false;
			url += UrlContants.NCWC_NEW_ID;
			map = new HashMap<String, String>();
			map.put("number", "20");
			map.put("id", String.valueOf(mList.get(19).getId() - 1));
		} else if (type.equals(UrlContants.NCWC_HOT)) {
			url += UrlContants.NCWC_HOT;
			map = new HashMap<String, String>();
			map.put("offset", String.valueOf(PAGE_SIZE * PAGE_NO + CURRENT_PAGE));
			map.put("limit", String.valueOf(PAGE_SIZE));
			if (PAGE_NO == 0)
				isFirst = true;
			else
				isFirst = false;
		}
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				mGridView.onRefreshComplete();
				try {
					if (response.getInt("ret") == 0) {
						mList = new ArrayList<GuessBean>();
						JSONArray array = response.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							GuessBean bean = new GuessBean();
							JSONObject json = new JSONObject(array.get(i).toString());
							bean.setId(json.getInt("id"));
							bean.setTitle(json.getString("title"));
							bean.setImageUrl(json.getString("imageUrl"));
							bean.setVideoUrl(json.getString("videoUrl"));
							bean.setCandidateAnswer(json.getString("candidateAnswer"));
							bean.setCreateTime(json.getString("createTime"));
							bean.setAnswerLength(json.getInt("answerLength"));
							bean.setLove(json.getInt("love"));
							bean.setUnlove(json.getInt("unlove"));
							bean.setCommentNumber(json.getInt("commentNumber"));
							bean.setFinishCount(json.getInt("finishCount"));
							bean.setAvatar(json.getString("avatar"));
							bean.setNickname(json.getString("nickname"));
							bean.setAuthorId(json.getInt("authorId"));
							bean.setAnswered(json.getBoolean("answered"));
							bean.setEvaluation(json.getInt("evaluation"));
							bean.setAnswer(json.getString("answer"));
							if (json.has("location")) {
								if (!json.get("location").equals("") && !json.getString("location").equals("null")) {
									JSONObject location = new JSONObject(json.getString("location").toString());
									if (json.has("location")) {
										bean.setLocation(location.getString("location"));
										if (!location.get("position").equals("")) {
											JSONObject position = new JSONObject(location.getString("position"));
											bean.setLatitude(position.getString("latitude"));
											bean.setLongitude(position.getString("longitude"));
										}
									}
								}
							} else {
								bean.setLocation("");
							}
							mList.add(bean);
						}
						if (mList.size() > 0) {
							Message msg = Message.obtain();
							msg.obj = mList;
							msg.what = SET_DATA;
							mHandler.sendMessage(msg);

						}
					} else if (response.getInt("ret") == Constant.NOT_LOGIN) {
						LoginUtil login = new LoginUtil(getActivity());
						login.login(new LoginListener() {

							@Override
							public void onLoginAfter() {
								getData(UrlContants.NCWC_NEW_NUM);
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				mGridView.onRefreshComplete();
				Toast.makeText(mContext, VolleyErrorHelper.getMessage(error, mContext), Toast.LENGTH_SHORT).show();
			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);

	}

	/**
	 * 设置fragment是否可见
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据
			if (mList != null && mList.size() != 0) {

				mHandler.obtainMessage(SET_NEWSLIST).sendToTarget();
			} else {   
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mHandler.obtainMessage(SET_NEWSLIST).sendToTarget();
					}
				}).start();

			}
		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		mHandler.obtainMessage(REFRESH_DATA).sendToTarget();
		// new GetDataTask().execute();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		mHandler.obtainMessage(LOADING_MORE).sendToTarget();
		// new GetDataTask().execute();
	}

	private void initIndicator() {
		ILoadingLayout startLabels = mGridView.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在刷新...");// 刷新时
		startLabels.setReleaseLabel("松开刷新...");// 下来达到一定距离时，显示的提示

		ILoadingLayout endLabels = mGridView.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("上拉加载更多...");// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel("正在加载...");// 刷新时
		endLabels.setReleaseLabel("松开加载...");// 下来达到一定距离时，显示的提示
	}

}
