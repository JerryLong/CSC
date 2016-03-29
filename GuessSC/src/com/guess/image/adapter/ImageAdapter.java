package com.guess.image.adapter;

import java.util.List;

import com.cdrongyao.caisichuan.R;
import com.guess.image.utils.ImageLoader;
import com.guess.image.utils.ImageLoader.Type;
import com.guess.tools.BaseTools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private String mDirPath;
	private List<String> mImgPaths;
	private Context mContext;

	public ImageAdapter(Context context, String dirPath) {
		this.mDirPath = dirPath;
		mContext = context;
	}

	public void setData(List<String> list) {
		mImgPaths = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mImgPaths.size();
	}

	@Override
	public Object getItem(int position) {
		return mImgPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = BaseTools.getView(mContext, R.layout.view_gridview);
			holder = new ViewHolder();
			holder.mImg = (ImageView) convertView.findViewById(R.id.view_item_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mImg.setImageResource(R.drawable.pictures_no);
		holder.mImg.setColorFilter(null);

		final String path = mDirPath + "/" + mImgPaths.get(position);

		ImageLoader.getInstance(3, Type.LIFO).loadImage(path, holder.mImg);
		return convertView;
	}

	private class ViewHolder {
		ImageView mImg;


	}
}