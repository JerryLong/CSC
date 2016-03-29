package com.guess.fragment;

import java.util.ArrayList;

import com.cdrongyao.caisichuan.R;
import com.dralong.slidingmenu.lib.SlidingMenu;
import com.guess.activity.MainActivity;
import com.guess.activity.SetQuestionActivity;
import com.guess.adapter.FragmentPageAdapter;
import com.guess.bean.TitleBean;
import com.guess.tools.BaseTools;
import com.guess.tools.Constants;
import com.guess.tools.MenuDrawer;
import com.guess.view.TitleScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GamesFragment extends Fragment {
	private TitleScrollView mTopTitle;
	LinearLayout mRadioGroup_content;
	LinearLayout more_columns;
	RelativeLayout title_column;
	private ViewPager mViewPager;
	private ImageView mMenuImg;
	/**
	 * 新闻分类列表
	 */
	private ArrayList<TitleBean> listColumn = new ArrayList<TitleBean>();
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
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		// }

		View view = inflater.inflate(R.layout.games_fragment, container, false);

		mScreenWidth = BaseTools.getWindowsWidth(getActivity());
		// 一个Item宽度为屏幕的1/6
		mItemWidth = mScreenWidth / 6;
		mTopTitle = (TitleScrollView) view.findViewById(R.id.mTopTitleScrollView);
		mRadioGroup_content = (LinearLayout) view.findViewById(R.id.mRadioGroup_content);
		mViewPager = (ViewPager) view.findViewById(R.id.games_fragment_viewpager);
		mMenuImg = (ImageView) view.findViewById(R.id.sliding_menu);
		Button setQuestion = (Button) view.findViewById(R.id.games_top_set_question);
		setQuestion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SetQuestionActivity.class);
				startActivity(intent);
			}
		});

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
		listColumn = Constants.getTitle();
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
			params.leftMargin = 5;
			params.rightMargin = 5;
			TextView columnTextView = new TextView(getActivity());
			columnTextView.setBackgroundResource(R.drawable.top_guess_selector);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setPadding(3, 11, 3, 11);
			columnTextView.setId(i);
			columnTextView.setText(listColumn.get(i).getTitle());
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
		int count = listColumn.size();
		for (int i = 0; i < count; i++) {
			Bundle data = new Bundle();
			data.putString("type", listColumn.get(i).getType());

			GuessFragment fragment = new GuessFragment();
			fragment.setArguments(data);
			fragments.add(fragment);
		}
		FragmentPageAdapter mAdapetr = new FragmentPageAdapter(getActivity().getSupportFragmentManager(), fragments);
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

	public void onResume() {

		mMenuImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SlidingMenu menu = ((MainActivity) getActivity()).side_drawer;
				if (menu.isMenuShowing()) {
					menu.showContent();
				} else {
					menu.showMenu();
				}
			}
		});
		super.onResume();
	};
}
