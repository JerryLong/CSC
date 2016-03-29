package com.guess.myadapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.cdrongyao.caisichuan.R;
import com.guess.tools.BaseTools;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class MyMessageAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	private SimpleDateFormat df;
	
	@SuppressLint("SimpleDateFormat")
	public MyMessageAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
		df = new SimpleDateFormat("yy-MM-dd");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewHolder = new ViewHolder();
		if(convertView == null){
			view = BaseTools.getView(context, R.layout.item_message);
			
			ImageView imgAvatar = (ImageView) view.findViewById(R.id.item_message_avatar);
			TextView tvTime = (TextView) view.findViewById(R.id.item_message_time);
			TextView tvContent = (TextView) view.findViewById(R.id.item_message_content);
			
			viewHolder.imgAvatar = imgAvatar;
			viewHolder.tvTime = tvTime;
			viewHolder.tvContent = tvContent;
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		final HashMap<String, Object> data = list.get(position);
		String icon = data.get("imageUrl").toString();
		if((icon != null) && (!"".equals(icon))){
			Picasso.with(context).load(icon).resize(100, 100).placeholder(R.drawable.default_picture2x).into(viewHolder.imgAvatar);
		}
		viewHolder.tvTime.setText(df.format(Long.parseLong(data.get("time").toString())));
		viewHolder.tvContent.setText(data.get("title").toString());
		
		return view;
	}
	
	class ViewHolder{
		ImageView imgAvatar;
		TextView tvTime;
		TextView tvContent;
	}

}
