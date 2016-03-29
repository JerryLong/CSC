package com.guess.adapter;

import java.util.ArrayList;

import com.cdrongyao.caisichuan.R;
import com.guess.bean.OfficialBean;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class OfficialAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<OfficialBean> mList = new ArrayList<OfficialBean>();

	public OfficialAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setData(ArrayList<OfficialBean> list) {
		mList = list;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.view_official_adapter, null);
			holder = new ViewHolder();
			holder.mImage = (ImageView) convertView.findViewById(R.id.view_official_image);
			holder.mImageState = (ImageView) convertView.findViewById(R.id.view_official_activity);
			holder.mTitle = (TextView) convertView.findViewById(R.id.view_official_acTitle);
			holder.mDesc = (TextView) convertView.findViewById(R.id.view_official_acDesc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		OfficialBean bean = (OfficialBean) getItem(position);
		Picasso.with(mContext).load(bean.getPosterUrl()).resize(240, 200).placeholder(R.drawable.default_picture2x).into(holder.mImage);
		if (bean.getStatus() == 1) {
			holder.mImageState.setImageResource(R.drawable.ic_activity_will_begin);
		} else if (bean.getStatus() == 2) {

			holder.mImageState.setImageResource(R.drawable.ic_activity_underway);
		} else if (bean.getStatus() == 3) {
			holder.mImageState.setImageResource(R.drawable.ic_activity_end);

		}
		holder.mTitle.setText(bean.getAcTitle());
		holder.mDesc.setText(bean.getAcDesc());
		return convertView;
	}

	class ViewHolder {
		ImageView mImage;
		ImageView mImageState;
		TextView mDesc;
		TextView mTitle;

	}
}
