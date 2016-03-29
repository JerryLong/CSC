package com.guess.myadapter;

import java.util.HashMap;
import java.util.List;

import com.cdrongyao.caisichuan.R;
import com.guess.tools.BaseTools;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义Adapter 排行榜
 * 
 * @author YXC
 * 
 */
@SuppressLint({ "HandlerLeak", "NewApi", "InflateParams" })
public class MyRankAdapter extends BaseAdapter {
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
	public MyRankAdapter(Context context, List<HashMap<String, Object>> list) {
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

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			view = BaseTools.getView(context, R.layout.item_rank_list);
			
			TextView tvOrder = (TextView) view.findViewById(R.id.item_rank_order);
			TextView tvGrage = (TextView) view.findViewById(R.id.item_rank_grade);
			TextView tvName = (TextView) view.findViewById(R.id.item_rank_name);
			ImageView imgIcon = (ImageView) view.findViewById(R.id.item_rank_icon);
			
			viewHolder.imgIcon = imgIcon;
			viewHolder.tvName = tvName;
			viewHolder.tvGrade = tvGrage;
			viewHolder.tvOrder = tvOrder;
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		// 获取当前点击的项的数据信息
		final HashMap<String, Object> data = this.list.get(position);
		
		String order = data.get("rank").toString();
		String icon = data.get("avatar").toString();
		String name = data.get("nickname").toString();
		String grade = data.get("grade").toString();
		
		//数据填充
		if("1".equals(order)){
			viewHolder.tvOrder.setText("");
			viewHolder.tvOrder.setBackground(context.getResources().getDrawable(R.drawable.ic_more_rank_golden_cup2x));
		}else if("2".equals(order)){
			viewHolder.tvOrder.setText("");
			viewHolder.tvOrder.setBackground(context.getResources().getDrawable(R.drawable.ic_more_rank_silver_cup2x));
		}else if("3".equals(order)){
			viewHolder.tvOrder.setText("");
			viewHolder.tvOrder.setBackground(context.getResources().getDrawable(R.drawable.ic_more_rank_copper_cup2x));
		}else{
			viewHolder.tvOrder.setText(order);
			viewHolder.tvOrder.setTextColor(Color.BLACK);
			viewHolder.tvOrder.setBackground(null);
		}
		viewHolder.tvName.setText(name);
		viewHolder.tvGrade.setText(grade);
		
		
		if(!"null".equals(icon)){
			Picasso.with(context).load(icon).resize(60, 60).placeholder(R.drawable.default_avatar3x).into(viewHolder.imgIcon);
			//imageLoader.DisplayImage(icon, viewHolder.imgIcon);
		}else{
			viewHolder.imgIcon.setImageResource(R.drawable.default_avatar3x);
		}
	//	new ApplicationUtil(context).getBitmap(viewHolder.imgIcon, icon.equals("null")?head:icon);
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
		 * 当前排名
		 */
		TextView tvOrder;
		/**
		 * 用户头像
		 */
		ImageView imgIcon;
		/**
		 * 用户昵称
		 */
		TextView tvName;
		/**
		 * 用户成绩
		 */
		TextView tvGrade;
	}

}
