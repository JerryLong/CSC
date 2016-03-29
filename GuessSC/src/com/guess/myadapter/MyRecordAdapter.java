package com.guess.myadapter;

import java.text.SimpleDateFormat;
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
 * 自定义Adapter 兑换记录
 * 
 * @author YXC
 * 
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
public class MyRecordAdapter extends BaseAdapter {
	// 上下文对象
	private Context context;
	// 数据源
	private List<HashMap<String, Object>> list;
	private SimpleDateFormat df;
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param list
	 *            数据源
	 */
	@SuppressLint("SimpleDateFormat")
	public MyRecordAdapter(Context context, List<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
		df = new SimpleDateFormat("yyyy-MM-dd");
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
			view = BaseTools.getView(context, R.layout.item_my_record);
			
			ImageView imgPicture = (ImageView) view.findViewById(R.id.item_record_picture);
			TextView tvName = (TextView) view.findViewById(R.id.item_record_num);
			TextView tvCoin = (TextView) view.findViewById(R.id.item_record_coin);
			TextView tvTime = (TextView) view.findViewById(R.id.item_record_time);
			
			viewHolder.imgPicture = imgPicture;
			viewHolder.tvName = tvName;
			viewHolder.tvCoin = tvCoin;
			viewHolder.tvTime = tvTime;
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		// 获取当前点击的项的数据信息
		final HashMap<String, Object> data = this.list.get(position);

		String picture = data.get("imageUrl").toString();
		viewHolder.tvName.setText(data.get("name").toString());
		if(!"null".equals(picture)){
			Picasso.with(context).load(picture).resize(150, 120).placeholder(R.drawable.default_picture2x).into(viewHolder.imgPicture);
		}else{
			viewHolder.imgPicture.setImageResource(R.drawable.default_avatar3x);
		}
		
		viewHolder.tvCoin.setText(context.getString(R.string.coin) + data.get("goldPrice").toString());
		viewHolder.tvTime.setText(context.getString(R.string.time) + df.format(Long.parseLong(data.get("time").toString())));
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
		 * 兑换物品的图片
		 */
		ImageView imgPicture;
		/**
		 * 兑换物品名称
		 */
		TextView tvName;
		/**
		 * 金币价格
		 */
		TextView tvCoin;
		/**
		 * 兑换时间
		 */
		TextView tvTime;
	}

}
