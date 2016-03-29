package com.guess.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.cdrongyao.caisichuan.R;
import com.guess.bean.GuessBean;
import com.guess.myutils.ApplicationUtil;
import com.guess.myview.CircleImageView;
import com.guess.tools.BaseTools;
import com.guess.user.UserDetail;
import com.guess.utils.ImageLoadUtils;
import com.guess.utils.PublicTools;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class GuessAdapter extends BaseAdapter {
	ArrayList<GuessBean> mList = new ArrayList<GuessBean>();
	LayoutInflater mInflater;
	Context mContext;
	Drawable like, unlike;
	ApplicationUtil mApplicationUtil;
	private ImageLoadUtils mImageLoadUtils;
	SimpleDateFormat sdf;
	private LayoutParams params;
	private int width;

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	public GuessAdapter(Context context, int width) {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		this.width = width;

		mContext = context;
		mApplicationUtil = new ApplicationUtil(mContext);
		mImageLoadUtils = ImageLoadUtils.getInstanceAsycnImage();
		mImageLoadUtils.setThreadPoolExecutor();

		like = mContext.getResources().getDrawable(R.drawable.question_like);
		unlike = mContext.getResources().getDrawable(R.drawable.ic_square_unlike2x);
		like.setBounds(0, 0, like.getMinimumWidth(), like.getMinimumHeight());
		unlike.setBounds(0, 0, like.getMinimumWidth(), like.getMinimumHeight());
	}

	public void setData(ArrayList<GuessBean> list) {
		mList = list;
		notifyDataSetChanged();
	}

	public void addData(ArrayList<GuessBean> list) {
		mList.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Holder holder;
		if (view == null) {
			holder = new Holder();

			view = BaseTools.getView(mContext, R.layout.view_gridview_item);

			holder.mLayout = (RelativeLayout) view.findViewById(R.id.view_gridview_one);
			holder.mPeopelImg = (CircleImageView) view.findViewById(R.id.view_gridview_people_img);
			holder.mVideoImg = (ImageView) view.findViewById(R.id.view_gridview_main_video);
			holder.mNameTxt = (TextView) view.findViewById(R.id.view_gridview_person_name);
			holder.mTimeTxt = (TextView) view.findViewById(R.id.view_gridview_person_time);
			holder.mBrandTxt = (TextView) view.findViewById(R.id.view_gridview_brand);
			holder.mPosition = (TextView) view.findViewById(R.id.view_gridview_position);
			holder.mMainImg = (ImageView) view.findViewById(R.id.view_gridview_main_image);

			params = (LayoutParams) holder.mMainImg.getLayoutParams();
			params.height = params.width = width / 2;
			holder.mMainImg.setLayoutParams(params);

			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		holder.mAssistTxt = (TextView) view.findViewById(R.id.view_gridview_assist);
		final GuessBean gb = (GuessBean) getItem(position);
		holder.mNameTxt.setText("" + gb.getNickname());
		if (gb.getLocation() == null) {
			holder.mPosition.setText("火星");
		} else {
			holder.mPosition.setText("" + gb.getLocation());
		}
		Picasso.with(mContext).load(gb.getImageUrl()).resize(width / 2, width / 2)
				.placeholder(R.drawable.default_picture2x).into(holder.mMainImg);
		if ((gb.getAvatar() != null) && (!"".equals(gb.getAvatar())) && (!"null".equals(gb.getAvatar()))) {
			Picasso.with(mContext).load(gb.getAvatar()).resize(width / 2, width / 2)
					.placeholder(R.drawable.default_avatar3x).into(holder.mPeopelImg);
		}
		holder.mTimeTxt.setText(sdf.format(Long.parseLong(gb.getCreateTime() + "000")));
		if (gb.getEvaluation() == 1) {
			holder.mAssistTxt.setCompoundDrawables(like, null, null, null);
			holder.mAssistTxt.setText("赞(" + gb.getLove() + ")");
		} else {
			holder.mAssistTxt.setCompoundDrawables(unlike, null, null, null);
			holder.mAssistTxt.setText("赞(" + gb.getLove() + ")");
		}

		if (gb.getVideoUrl() == null || "".equals(gb.getVideoUrl()) || "null".equals(gb.getVideoUrl()))
			holder.mVideoImg.setVisibility(View.GONE);
		else
			holder.mVideoImg.setVisibility(View.VISIBLE);

		holder.mBrandTxt.setText(gb.getTitle());
		holder.mLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent user = new Intent(mContext, UserDetail.class);
				user.putExtra("id", String.valueOf(gb.getAuthorId()));
				mContext.startActivity(user);
			}
		});
		holder.mAssistTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int PRAISE = gb.getEvaluation();
				int LOVE = gb.getLove();
				if (PRAISE == 0) {
					LOVE += 1;
					holder.mAssistTxt.setCompoundDrawables(like, null, null, null);
					holder.mAssistTxt.setText("赞(" + LOVE + ")");
					PRAISE = 1;
					gb.setLove(LOVE);
					gb.setEvaluation(PRAISE);
				} else {
					LOVE -= 1;
					holder.mAssistTxt.setCompoundDrawables(unlike, null, null, null);
					holder.mAssistTxt.setText("赞(" + LOVE + ")");
					PRAISE = 0;
					gb.setLove(LOVE);
					gb.setEvaluation(PRAISE);
				}
				PublicTools.setPraise(mApplicationUtil, gb.getId(), PRAISE);
			}
		});
		return view;
	}

	class Holder {
		ImageView mMainImg;
		ImageView mVideoImg;
		CircleImageView mPeopelImg;
		TextView mNameTxt;
		TextView mTimeTxt;
		ImageView mAssistImg;
		TextView mAssistTxt;
		TextView mBrandTxt;
		TextView mPosition;
		RelativeLayout mLayout;
	}
}
