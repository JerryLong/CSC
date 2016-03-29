package com.guess.activity;

import com.cdrongyao.caisichuan.R;
import com.guess.fragment.CommentFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CommentActivity extends FragmentActivity implements OnClickListener {
	private int QUESTION_ID = 0;
	private Button mFinishBtn;
	private TextView mTipsText;
	private boolean isAnswered = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comment_layout);

		QUESTION_ID = getIntent().getIntExtra("questionId", 0);
		isAnswered = getIntent().getBooleanExtra("isAnswered", false);
		// PDialogUtil.showDialog(this, getString(R.string.data_loading),
		// false);
		initViews();

	}

	/**
	 * 初始化控件
	 */
	private void initViews() {
		// mRelative = (RelativeLayout) findViewById(R.id.comment_relative);
		mFinishBtn = (Button) findViewById(R.id.comment_finish);
		mFinishBtn.setOnClickListener(this);

		mTipsText = (TextView) findViewById(R.id.comment_tips);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		CommentFragment fragment = new CommentFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("questionId", QUESTION_ID);
		bundle.putInt("flag", 0);
		fragment.setArguments(bundle);
		fragmentTransaction.add(R.id.comment_relative, fragment);

		fragmentTransaction.commit();

		if (!isAnswered) {
			mTipsText.setVisibility(View.VISIBLE);
		} else {
			mTipsText.setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.comment_finish:
			finish();
			break;
		}
	}

	// @Override
	// public void onRefresh() {
	// setComment(PAGE_NO = 0);
	// onLoad();
	// }
	//
	// @Override
	// public void onLoadMore() {
	// mList.clear();
	// setComment(++PAGE_NO);
	// onLoad();
	// }
	//
	// private void onLoad() {
	// mListView.stopRefresh();
	// mListView.stopLoadMore();
	// mListView.setRefreshTime(this, "comments");
	// }

	// public void setComment(int page) {
	// String url = UrlContants.LOCATION;
	// HashMap<String, String> map;
	// url += UrlContants.COMMENT_NCWC_GET;
	// map = new HashMap<String, String>();
	// map.put("questionId", String.valueOf(QUESTION_ID));
	// if (mList.isEmpty() || mList.size() < 20) {
	// CURRENT_PAGE = 0;
	// } else if (mList.size() > 20) {
	// CURRENT_PAGE = 1;
	//
	// }
	// map.put("offset", String.valueOf(CURRENT_PAGE + PAGE_SIZE * PAGE_NO));
	// map.put("limit", String.valueOf(PAGE_SIZE));
	//
	// MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>()
	// {
	//
	// @Override
	// public void onResponse(JSONObject response) {
	// onLoad();
	// try {
	// if (response.getInt("ret") == 0) {
	//
	// JSONArray array = response.getJSONArray("comments");
	// for (int i = 0; i < array.length(); i++) {
	// Gson gson = new Gson();
	// CommentBean bean = gson.fromJson(array.get(i).toString(),
	// CommentBean.class);
	// mList.add(bean);
	// }
	// mHandler.obtainMessage(MSG_ONE).sendToTarget();
	//
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }, new ErrorListener() {
	//
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// Toast.makeText(CommentActivity.this, VolleyErrorHelper.getMessage(error,
	// CommentActivity.this),
	// Toast.LENGTH_SHORT).show();
	// }
	// }, map);
	// request.setShouldCache(false);
	// mAppliciationUtil.getRequestQueue().add(request);
	// mList.clear();
	// }

	// private void controlComment(int id, String comment, String hint) {
	// String url = UrlContants.LOCATION;
	// HashMap<String, String> map;
	// if (!"".equals(comment) && "".equals(hint)) {
	// url += UrlContants.COMMENT_NCWC_SUBMIT;
	// map = new HashMap<String, String>();
	// map.put("quizId", String.valueOf(id));
	// map.put("comment", comment);
	// } else if (!hint.equals("")) {
	// url += UrlContants.COMMENT_NCWC_REPLY;
	// map = new HashMap<String, String>();
	// map.put("replyCommentId", String.valueOf(id));
	// map.put("comment", comment);
	// } else {
	// url += UrlContants.COMMENT_NCWC_DELETE;
	// map = new HashMap<String, String>();
	// map.put("commentId", String.valueOf(id));
	// }
	// MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>()
	// {
	//
	// @Override
	// public void onResponse(JSONObject response) {
	// try {
	// if (response.getInt("ret") == 0) {
	// mHandler.obtainMessage(MSG_TWO).sendToTarget();
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }, new ErrorListener() {
	//
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// Toast.makeText(CommentActivity.this, VolleyErrorHelper.getMessage(error,
	// CommentActivity.this),
	// Toast.LENGTH_SHORT).show();
	// }
	// }, map);
	// request.setShouldCache(false);
	// mAppliciationUtil.getRequestQueue().add(request);
	// }

	// public void subCommentClick(int id) {
	// String comment = mEditText.getText().toString();
	// String hint = " ".equals(mEditText.getHint().toString()) ? "" :
	// mEditText.getHint().toString();
	// controlComment(id, comment, hint);
	//
	// ID = QUESTION_ID;
	// mEditText.setText("");
	// mEditText.setHint("");
	// }

	// class CommentAdapter extends BaseAdapter {
	// private Context mContext;
	// private ImageLoadUtils mImageLoadUtils;
	// private ShareData mShareData;
	// private ArrayList<CommentBean> mBList = new ArrayList<CommentBean>();
	//
	// public CommentAdapter(Context context) {
	// mContext = context;
	// mImageLoadUtils = ImageLoadUtils.getInstanceAsycnImage();
	// mImageLoadUtils.setThreadPoolExecutor();
	// mShareData = ShareData.getInstance(mContext);
	// }
	//
	// void setData(ArrayList<CommentBean> list) {
	// if (PAGE_NO == 0)
	// mBList = list;
	// else
	// mBList.addAll(list);
	// notifyDataSetChanged();
	// }
	//
	// @Override
	// public int getCount() {
	// return mBList.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return mBList.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View view, ViewGroup parent) {
	// Holder holder = null;
	// if (view == null) {
	// holder = new Holder();
	// view = BaseTools.getView(mContext, R.layout.view_comment_adapter);
	// holder.mImage = (CircleImageView)
	// view.findViewById(R.id.view_comment_image);
	// holder.mReplyImage = (ImageView)
	// view.findViewById(R.id.view_comment_reply_image);
	// holder.mComment = (TextView)
	// view.findViewById(R.id.view_comment_comment);
	// holder.mFloor = (TextView) view.findViewById(R.id.view_comment_position);
	// holder.mName = (TextView) view.findViewById(R.id.view_comment_username);
	// holder.mTimes = (TextView) view.findViewById(R.id.view_comment_time);
	// holder.mReply = (TextView) view.findViewById(R.id.view_comment_reply);
	// holder.mDelete = (TextView) view.findViewById(R.id.view_comment_delete);
	// holder.mLayout = (LinearLayout)
	// view.findViewById(R.id.view_comment_delete_layout);
	// view.setTag(holder);
	// } else {
	// holder = (Holder) view.getTag();
	// }
	// final CommentBean bean = (CommentBean) getItem(position);
	// if (bean.getAvatar() != null) {
	// mImageLoadUtils.loadBitmap(mContext.getResources(), bean.getAvatar(),
	// holder.mImage,
	// R.drawable.default_avatar3x, 0, 0);
	// }
	// holder.mFloor.setText("" + ++position + "楼");
	// holder.mName.setText("" + bean.getNickname());
	// holder.mComment.setText("" + bean.getComment());
	// for (int i = 0; i < mBList.size(); i++) {
	// CommentBean beans = (CommentBean) getItem(i);
	// if (bean.getReplyCommentId() == 0) {
	// holder.mReply.setVisibility(View.GONE);
	// break;
	// } else if (bean.getReplyCommentId() == beans.getId()) {
	// holder.mReply.setVisibility(View.VISIBLE);
	// holder.mReply.setText(Html.fromHtml("@ " + "<font color=#EC6833>" +
	// bean.getNickname() + "</font> "
	// + ":" + "<font color=#000000>" + beans.getComment() + "</font>"));
	// break;
	// }
	// }
	// if (bean.getUid() == Integer.valueOf(mShareData.getId())) {
	// holder.mLayout.setVisibility(View.VISIBLE);
	// } else {
	// holder.mLayout.setVisibility(View.GONE);
	// }
	// int time = Tools.getTimeDifference(bean.getCommentTime());
	// if (time < 1) {
	// holder.mTimes.setText("刚刚");
	// } else if (time < 60) {
	// holder.mTimes.setText("" + time + "分钟前");
	// } else {
	// holder.mTimes.setText("" +
	// DateTools.getStrTime_ymd(String.valueOf(bean.getCommentTime())));
	// }
	// holder.mReplyImage.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// mEditText.setFocusable(true);
	// mEditText.setHint("@ " + bean.getNickname());
	// InputMethodManager imm = (InputMethodManager)
	// getSystemService(Context.INPUT_METHOD_SERVICE);
	// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	// ID = bean.getId();
	// }
	// });
	// holder.mDelete.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// controlComment(bean.getId(), "", "");
	// }
	// });
	// return view;
	// }
	//
	// class Holder {
	// TextView mDelete, mReply, mTimes, mComment, mFloor, mName;
	// ImageView mReplyImage;
	// CircleImageView mImage;
	// LinearLayout mLayout;
	// }
	//
	// }

}
