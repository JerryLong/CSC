package com.guess.view;

import java.util.ArrayList;

import com.cdrongyao.caisichuan.R;
import com.guess.bean.WordButton;
import com.guess.interfac.IWordButton;
import com.guess.tools.BaseTools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class GridButtonView extends GridView {
	public final static int COUNT_WORDS = 18;
	private ArrayList<WordButton> mList = new ArrayList<WordButton>();
	private GridAdapter mAdapter;
	private Context mContext;
	private Animation mScaleAnimation;
	private IWordButton mListener;

	private int width;

	public GridButtonView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		mAdapter = new GridAdapter();
		this.setAdapter(mAdapter);
	}

	public void updateData(ArrayList<WordButton> list, int width) {
		mList = list;
		this.width = width;
		mAdapter.notifyDataSetChanged();
		setAdapter(mAdapter);

	}

	class GridAdapter extends BaseAdapter {

		public GridAdapter() {

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
			final WordButton holder;
			if (parent.getChildCount() == position) {
				if (view == null) {
					view = BaseTools.getView(mContext, R.layout.answer_text_button);
					// 加载动画
					mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.button_scale);
					// 设置动画延迟时间
					mScaleAnimation.setStartOffset(position * 100);
					holder = mList.get(position);
					holder.mAllIndex = parent.getChildCount();

					holder.mViewButton = (Button) view.findViewById(R.id.view_item_btn1);

					android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) holder.mViewButton
							.getLayoutParams();

					params.height = width;
					params.width = width;
					holder.mViewButton.setLayoutParams(params);

					holder.mViewButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mListener.onWordButtonClick(holder);
						}
					});
					view.setTag(holder);
					// }
				} else {
					holder = (WordButton) view.getTag();
				}
			} else {
				view = BaseTools.getView(mContext, R.layout.answer_text_button);
				holder = new WordButton();
				holder.mAllIndex = position;

				holder.mViewButton = (Button) view.findViewById(R.id.view_item_btn1);

			}
			holder.mViewButton.setText(holder.mWordString);
			// 播放动画
			view.startAnimation(mScaleAnimation);

			return view;
		}

	}

	/**
	 * 注册监听接口
	 * 
	 * @param listener
	 */
	public void registerOnWordButtonClick(IWordButton listener) {
		mListener = listener;
	}
}
