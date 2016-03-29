package com.guess.fragment;

import java.util.ArrayList;

import com.cdrongyao.caisichuan.R;
import com.guess.adapter.FragmentMessageAdapter;
import com.guess.message.fragment.FragmentConversion;
import com.guess.message.fragment.FragmentMessage;
import com.guess.tools.BaseTools;
import com.guess.view.TitleScrollView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SessionFragment extends Fragment {
	private TitleScrollView mTopTitle;
	LinearLayout mRadioGroup_content;
	LinearLayout more_columns;
	RelativeLayout title_column;
	private ViewPager mViewPager;
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
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.session_fragment, container, false);
		//initSlidingMenu();
		mScreenWidth = BaseTools.getWindowsWidth(getActivity());
		// 一个Item宽度为屏幕的1/6
		mItemWidth = mScreenWidth / 6;
		mTopTitle = (TitleScrollView) view.findViewById(R.id.mTopTitleScrollViewMessage);
		mRadioGroup_content = (LinearLayout) view.findViewById(R.id.mRadioGroup_content_message);
		mViewPager = (ViewPager) view.findViewById(R.id.message_fragment_viewpager);
		setChangelView();
		return view;
	}

	/**
	 * 当栏目项发生变化时候调用
	 */
	private void setChangelView() {
		getColumn();
		initColumn();
		initFragment();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// initMenu();
	}

	/**
	 * 获取Column栏目数据
	 */
	private void getColumn() {
		listColumn.add("会话");
		listColumn.add("消息");
	}

	/**
	 * 初始化Column栏目项
	 */
	private void initColumn() {
		mRadioGroup_content.removeAllViews();
		int count = listColumn.size();
		mTopTitle.setParam(getActivity(), mScreenWidth, mRadioGroup_content, shade_left, shade_right, more_columns,
				title_column);
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = 3;
			params.rightMargin = 3;
			params.topMargin = 3;
			params.bottomMargin = 3;
			TextView columnTextView = new TextView(getActivity());
			columnTextView.setBackgroundResource(R.drawable.top_guess_selector);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setPadding(3, 8, 3, 8);
			columnTextView.setId(i);
			columnTextView.setText(listColumn.get(i));
			columnTextView.setTextColor(getActivity().getResources().getColor(R.color.white));
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
							mViewPager.setCurrentItem(i);
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
	 * 初始化Fragment
	 */
	private void initFragment() {
		fragments.add(new FragmentConversion());
		fragments.add(new FragmentMessage());
		FragmentMessageAdapter mAdapetr = new FragmentMessageAdapter(getActivity().getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}

	/**
	 * ViewPager切换监听方法
	 */
	public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};
}

