package com.guess.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.google.gson.Gson;
import com.guess.bean.CommentBean;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.ShareData;
import com.guess.myview.CircleImageView;
import com.guess.myview.XListView;
import com.guess.myview.XListView.IXListViewListener;
import com.guess.tools.BaseTools;
import com.guess.tools.DateTools;
import com.guess.tools.Tools;
import com.guess.utils.ImageLoadUtils;
import com.guess.utils.Logs;
import com.guess.utils.UrlContants;
import com.guess.utils.VolleyErrorHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

@SuppressLint({ "HandlerLeak", "InflateParams" })
public class CommentFragment extends Fragment implements IXListViewListener {
	private Activity mActivity;
	private XListView mListView;
	private CommentAdapter mAdapter;
	private int QUESTION_ID, SET_ID, FLAG;
	private EditText mEditText;
	private ArrayList<CommentBean> mList;

	private String SubmitUrl, ReplyUrl, GetUrl, DeleteUrl;
	private ApplicationUtil mAppliciationUtil;
	private int PAGE_SIZE = 20;
	private int CURRENT_PAGE = 0;
	private int PAGE_NO = 0;
	private static final int MSG_ONE = 0x000001;
	private static final int MSG_TWO = 0x000002;

	// public static final Fragment newInstance(String key) {
	// Fragment fragment = new CommentFragment();
	// Bundle bundle = new Bundle();
	// bundle.putString("key", key);
	// fragment.setArguments(bundle);
	// return fragment;
	// }

	Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ONE:
				mAdapter.setData((ArrayList<CommentBean>) msg.obj);
				if (((ArrayList<CommentBean>) msg.obj).size() < 20) {
					mListView.setPullLoadEnable(false);
				} else {
					mListView.setPullLoadEnable(true);

				}
				// mListView.setVisibility(View.VISIBLE);
				mListView.setSelection(mAdapter.getCount() - 1);
				// PDialogUtil.cancelDialog();
				Logs.i("MSG_ mList  " + (ArrayList<CommentBean>) msg.obj);
				break;
			case MSG_TWO:
				setComment(PAGE_NO);
				mAdapter.setData(mList);
				mListView.setSelection(mAdapter.getCount() - 1);
				break;

			}
		};
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		mAppliciationUtil = new ApplicationUtil(mActivity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();

		QUESTION_ID = bundle.getInt("questionId");
		FLAG = bundle.getInt("flag");
		setFlag(FLAG);
		SET_ID = QUESTION_ID;
		setComment(QUESTION_ID);
	}

	private void setFlag(int flag) {
		if (FLAG == 0) {
			SubmitUrl = UrlContants.COMMENT_NCWC_SUBMIT;
			ReplyUrl = UrlContants.COMMENT_NCWC_REPLY;
			DeleteUrl = UrlContants.COMMENT_NCWC_DELETE;
			GetUrl = UrlContants.COMMENT_NCWC_GET;
		} else {
			SubmitUrl = UrlContants.COMMENT_GAMBLE_SUBMIT;
			ReplyUrl = UrlContants.COMMENT_GAMBLE_REPLY;
			DeleteUrl = UrlContants.COMMENT_GAMBLE_DELETE;
			GetUrl = UrlContants.COMMENT_GAMBLE_GET;

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_comment, null);
		mListView = (XListView) view.findViewById(R.id.comment_listview);
		mAdapter = new CommentAdapter(mActivity);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mEditText = (EditText) view.findViewById(R.id.comment_edittext);
		Button mSendBtn = (Button) view.findViewById(R.id.comment_send);
		
		
		
		mSendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				subCommentClick(SET_ID);
				SET_ID = QUESTION_ID;
			}
		});
		return view;
	}

	public void setComment(int page) {
		String url = UrlContants.LOCATION;
		HashMap<String, String> map;
		url += GetUrl;
		map = new HashMap<String, String>();
		map.put("questionId", String.valueOf(QUESTION_ID));
		// if (mList.isEmpty() || mList.size() < 20) {
		CURRENT_PAGE = 0;
		// } else if (mList.size() > 20) {
		// CURRENT_PAGE = 1;
		//
		// }
		map.put("offset", String.valueOf(CURRENT_PAGE + PAGE_SIZE * PAGE_NO));
		map.put("limit", String.valueOf(PAGE_SIZE));
		

		
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Logs.i("response  " + response);
				onLoad();
				try {
					if (response.getInt("ret") == 0) {
						mList = new ArrayList<CommentBean>();
						JSONArray array = response.getJSONArray("comments");
						for (int i = 0; i < array.length(); i++) {
							Gson gson = new Gson();
							CommentBean bean = gson.fromJson(array.get(i).toString(), CommentBean.class);
							mList.add(bean);
						}
						if (!mList.isEmpty()) {
							Message msg = Message.obtain();
							msg.obj = mList;
							msg.what = MSG_ONE;
							mHandler.sendMessage(msg);
							// mHandler.obtainMessage(MSG_ONE).sendToTarget();
						}else{
							//setEmpty();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(mActivity, VolleyErrorHelper.getMessage(error, mActivity), Toast.LENGTH_SHORT).show();
			}
		}, map);
		request.setShouldCache(false);
		mAppliciationUtil.getRequestQueue().add(request);
		// mList.clear();
	}

	private void controlComment(int id, String comment, String hint) {
		String url = UrlContants.LOCATION;
		HashMap<String, String> map;
		if (!"".equals(comment) && "".equals(hint)) {
			url += SubmitUrl;
			map = new HashMap<String, String>();
			if (FLAG == 0) {
				map.put("quizId", String.valueOf(id));
			} else {
				map.put("questionId", String.valueOf(id));

			}
			map.put("comment", comment);
		} else if (!hint.equals("")) {
			url += ReplyUrl;
			map = new HashMap<String, String>();
			map.put("replyCommentId", String.valueOf(id));
			map.put("comment", comment);
		} else {
			url += DeleteUrl;
			map = new HashMap<String, String>();
			map.put("commentId", String.valueOf(id));
		}
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						mHandler.obtainMessage(MSG_TWO).sendToTarget();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(mActivity, VolleyErrorHelper.getMessage(error, mActivity), Toast.LENGTH_SHORT).show();
			}
		}, map);
		request.setShouldCache(false);
		mAppliciationUtil.getRequestQueue().add(request);
	}

	public void subCommentClick(int id) {
		String comment = mEditText.getText().toString();
		String hint = " ".equals(mEditText.getHint().toString()) ? "" : mEditText.getHint().toString();
		controlComment(id, comment, hint);
		SET_ID = QUESTION_ID;
		mEditText.setText("");
		mEditText.setHint("");
	}

	@Override
	public void onRefresh() {
		setComment(PAGE_NO = 0);
		onLoad();
	}

	@Override
	public void onLoadMore() {
		mList.clear();
		setComment(++PAGE_NO);
		onLoad();
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(mActivity, "comments");
	}

	class CommentAdapter extends BaseAdapter {
		private Context mContext;
		private ImageLoadUtils mImageLoadUtils;
		private ShareData mShareData;
		private ArrayList<CommentBean> mBList = new ArrayList<CommentBean>();

		public CommentAdapter(Context context) {
			mContext = context;
			mImageLoadUtils = ImageLoadUtils.getInstanceAsycnImage();
			mImageLoadUtils.setThreadPoolExecutor();
			mShareData = ShareData.getInstance(mContext);
		}

		public void setData(ArrayList<CommentBean> list) {
			mBList = list;
			Logs.i("mBList   " + mBList);
			notifyDataSetChanged();
		}

		public void addData(ArrayList<CommentBean> list) {
			mBList.addAll(list);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mBList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			Holder holder = null;
			if (view == null) {
				holder = new Holder();
				view = BaseTools.getView(mContext, R.layout.view_comment_adapter);
				holder.mImage = (CircleImageView) view.findViewById(R.id.view_comment_image);
				holder.mReplyImage = (ImageView) view.findViewById(R.id.view_comment_reply_image);
				holder.mComment = (TextView) view.findViewById(R.id.view_comment_comment);
				holder.mFloor = (TextView) view.findViewById(R.id.view_comment_position);
				holder.mName = (TextView) view.findViewById(R.id.view_comment_username);
				holder.mTimes = (TextView) view.findViewById(R.id.view_comment_time);
				holder.mReply = (TextView) view.findViewById(R.id.view_comment_reply);
				holder.mDelete = (TextView) view.findViewById(R.id.view_comment_delete);
				holder.mLayout = (LinearLayout) view.findViewById(R.id.view_comment_delete_layout);
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}
			final CommentBean bean = (CommentBean) getItem(position);
			if (bean.getAvatar() != null) {
				mImageLoadUtils.loadBitmap(mContext.getResources(), bean.getAvatar(), holder.mImage,
						R.drawable.default_avatar3x, 0, 0);
			}
			holder.mFloor.setText("" + ++position + "楼");
			holder.mName.setText("" + bean.getNickname());
			holder.mComment.setText("" + bean.getComment());
			for (int i = 0; i < mBList.size(); i++) {
				CommentBean beans = (CommentBean) getItem(i);
				if (bean.getReplyCommentId() == 0) {
					holder.mReply.setVisibility(View.GONE);
					break;
				} else if (bean.getReplyCommentId() == beans.getId()) {
					holder.mReply.setVisibility(View.VISIBLE);
					holder.mReply.setText(Html.fromHtml("@ " + "<font color=#EC6833>" + bean.getNickname() + "</font> "
							+ ":" + "<font color=#000000>" + beans.getComment() + "</font>"));
					break;
				}
			}
			if (bean.getUid() == Integer.valueOf(mShareData.getId())) {
				holder.mLayout.setVisibility(View.VISIBLE);
			} else {
				holder.mLayout.setVisibility(View.GONE);
			}
			int time = Tools.getTimeDifference(bean.getCommentTime());
			if (time < 1) {
				holder.mTimes.setText("刚刚");
			} else if (time < 60) {
				holder.mTimes.setText("" + time + "分钟前");
			} else {
				holder.mTimes.setText("" + DateTools.getStrTime_ymd(String.valueOf(bean.getCommentTime())));
			}
			holder.mReplyImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mEditText.setFocusable(true);
					mEditText.setHint("@ " + bean.getNickname());
					InputMethodManager imm = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					SET_ID = bean.getId();
				}
			});
			holder.mDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					controlComment(bean.getId(), "", "");
				}
			});
			return view;
		}

		class Holder {
			TextView mDelete, mReply, mTimes, mComment, mFloor, mName;
			ImageView mReplyImage;
			CircleImageView mImage;
			LinearLayout mLayout;
		}
	}
	
	/**
	 * 无评论时显示
	 */
	private void setEmpty1(){
		View emptyView;
		TextView emptyDes;
		ProgressBar pb;
		
		emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_view, null);
		emptyDes = (TextView) emptyView.findViewById(R.id.empty_tv);
		pb = (ProgressBar) emptyView.findViewById(R.id.empty_pb);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mActivity.addContentView(emptyView, lp);
		pb.setVisibility(View.GONE);
		emptyView.setVisibility(View.GONE);
		emptyDes.setText("暂无评论");
		mListView.setEmptyView(emptyView);
	}
}
