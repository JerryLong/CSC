package com.guess.image.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class ClipImageLayout extends RelativeLayout {

	public ClipImageView mClipView;
	private ClipBorderView mClipBorderView;

	/**
	 * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
	 */
	private int mHorizontalPadding = 0;

	public ClipImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mClipView = new ClipImageView(context);
		mClipBorderView = new ClipBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		mClipView.setBackgroundColor(Color.BLACK);
	
		this.addView(mClipView, lp);
		this.addView(mClipBorderView, lp);

		// 计算padding的px
		mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding,
				getResources().getDisplayMetrics());
		mClipView.setHorizontalPadding(mHorizontalPadding);
		mClipBorderView.setHorizontalPadding(mHorizontalPadding);
	}

	/**
	 * 对外公布设置边距的方法,单位为dp
	 * 
	 * @param mHorizontalPadding
	 */
	public void setHorizontalPadding(int mHorizontalPadding) {
		this.mHorizontalPadding = mHorizontalPadding;
	}

	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip() {
		return mClipView.clip();
	}

}
