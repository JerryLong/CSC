package com.guess.bean;

import android.widget.Button;

public class WordButton {
	public int mIndex;
	public int mAllIndex;
	public Boolean mIsVisiable;
	public String mWordString;
	public Button mViewButton;

	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}

	public int getmIndex() {
		return mIndex;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public int getmAllIndex() {
		return mAllIndex;
	}

	public void setmAllIndex(int mAllIndex) {
		this.mAllIndex = mAllIndex;
	}

}
