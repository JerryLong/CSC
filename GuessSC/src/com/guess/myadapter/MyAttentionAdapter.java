package com.guess.myadapter;

import java.util.HashMap;
import java.util.List;

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

/**
 * 自定义Adapter 关注列表
 * 
 * @author YXC
 * 
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
public class MyAttentionAdapter extends BaseAdapter {
	// 上下文对象
	private Context context;
	// 数据源
	private List<HashMap<String, Object>> list;
	
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param list
	 *            数据源
	 */
	public MyAttentionAdapter(Context context, List<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			view = BaseTools.getView(context, R.layout.item_my_attention);
			
			ImageView imgIcon = (ImageView) view.findViewById(R.id.item_attention_head);
			TextView tvName = (TextView) view.findViewById(R.id.item_attention_name);
			
			viewHolder.imgIcon = imgIcon;
			viewHolder.tvName = tvName;
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		// 获取当前点击的项的数据信息
		final HashMap<String, Object> data = this.list.get(position);
		
		//填充数据
		String icon = data.get("avatar").toString();
		if(!"null".equals(icon)){
			Picasso.with(context).load(icon).resize(120, 120).placeholder(R.drawable.default_avatar3x).into(viewHolder.imgIcon);
		}else{
			viewHolder.imgIcon.setImageResource(R.drawable.default_avatar3x);
		}
		
		viewHolder.tvName.setText(data.get("nickName").toString());
		return view;
	}

	/**
	 * 辅助类 用于临时存储listview的一个条目的信息
	 * 
	 * @author YXC
	 * 
	 */
	private static final class ViewHolder {
		/**
		 * 用户头像
		 */
		ImageView imgIcon;
		/**
		 * 用户昵称
		 */
		TextView tvName;
	}

}
