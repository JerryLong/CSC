package com.guess.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class FragmentMessageAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> mList;

	public FragmentMessageAdapter(FragmentManager fragment) {
		super(fragment);
	}

	public FragmentMessageAdapter(FragmentManager fragment, ArrayList<Fragment> list) {
		super(fragment);
		mList = list;
	}

	@Override
	public Fragment getItem(int i) {
		return mList.get(i);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		return super.instantiateItem(container, position);
	}
}
