package com.guess.image.adapter;

import java.util.ArrayList;

import com.cdrongyao.caisichuan.R;
import com.guess.image.bean.FolderBeanParcel;
import com.guess.image.utils.ImageLoader;
import com.guess.image.utils.ImageLoader.Type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListDirAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<FolderBeanParcel> mDatas = new ArrayList<FolderBeanParcel>();

	public ListDirAdapter(Context context) {
		// mDatas = objects;
		mInflater = LayoutInflater.from(context);
	}

	public void setData(ArrayList<FolderBeanParcel> list) {
		mDatas = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.view_popup, parent, false);

			holder.mImg = (ImageView) convertView.findViewById(R.id.view_popup_image);
			holder.mDirName = (TextView) convertView.findViewById(R.id.view_popup_name);
			holder.mDirCount = (TextView) convertView.findViewById(R.id.view_popup_count);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FolderBeanParcel bean = (FolderBeanParcel) getItem(position);
		// 重置
		holder.mImg.setImageResource(R.drawable.pictures_no);
		ImageLoader.getInstance(3, Type.LIFO).loadImage(bean.getFirstImgPath(), holder.mImg);
		holder.mDirCount.setText(bean.getCount() + "张");
		holder.mDirName.setText(bean.getName());

		return convertView;
	}

	private class ViewHolder {
		ImageView mImg;
		TextView mDirName;
		TextView mDirCount;

	}

}