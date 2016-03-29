package com.guess.myadapter;

import java.util.HashMap;
import java.util.List;

import com.cdrongyao.caisichuan.R;
import com.guess.tools.BaseTools;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义Adapter 我参与的
 * 
 * @author YXC
 * 
 */
@SuppressLint({ "HandlerLeak", "InflateParams", "NewApi" })
public class MyDarenAdapter extends BaseAdapter {
	// 上下文对象
	private Context context;
	// 数据源 lableList 标签表
	private List<HashMap<String, Object>> dataList;
	private List<HashMap<String, Object>> lableList;
	private HashMap<String, Object> lableMap;
	
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param list
	 *            数据源
	 */
	public MyDarenAdapter(Context context, List<HashMap<String, Object>> dataList) {
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings({"deprecation", "unchecked" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			view = BaseTools.getView(context, R.layout.item_daren);
			TextView order = (TextView) view.findViewById(R.id.item_daren_order);
			TextView question = (TextView) view.findViewById(R.id.item_daren_question);
			ImageView picture = (ImageView)view.findViewById(R.id.item_daren_picture);
			GridLayout gd = (GridLayout) view.findViewById(R.id.item_daren_grid);

			viewHolder.tvOrder = order;
			viewHolder.tvQuestion = question;
			viewHolder.imgPicture = picture;
			viewHolder.gdLayout = gd;

			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		
		HashMap<String, Object> data = dataList.get(position);
		final String title = data.get("title").toString();
		final String icon = data.get("imageUrl").toString();
		viewHolder.tvOrder.setText("第"+ data.get("gameLevel").toString()+"题");
		viewHolder.tvQuestion.setText(title);
		
		if(!"null".equals(icon)){
			Picasso.with(context).load(icon).resize(200, 180).placeholder(R.drawable.download_failed).into(viewHolder.imgPicture);
			//imageLoader.DisplayImage(icon, viewHolder.imgPicture);
		}
		
		lableList = (List<HashMap<String, Object>>) data.get("lable");
		if(lableList.size() > 0){
			viewHolder.gdLayout.setVisibility(View.VISIBLE);
			if(viewHolder.gdLayout.getChildCount() == 0){
				LayoutParams lp = new LayoutParams();
				for(int i = 0; i < lableList.size(); i++){
					lableMap = lableList.get(i);
					lp.setGravity(Gravity.CENTER);
					lp.setMargins(5, 10, 0, 0);
					lp.width = LayoutParams.WRAP_CONTENT;
					lp.height = LayoutParams.WRAP_CONTENT;
					TextView tvLable = new TextView(context);
					tvLable.setTextColor(Color.WHITE);
					tvLable.setLayoutParams(lp);
					tvLable.setGravity(Gravity.CENTER);
					tvLable.setBackground(context.getResources().getDrawable(R.drawable.scdr_tag_bg2x));
					viewHolder.gdLayout.addView(tvLable);
					String text = lableMap.get("name") + "(" + lableMap.get("count") + ")";
					tvLable.setText(text);
				}
			}
		}else{
			viewHolder.gdLayout.setVisibility(View.GONE);
		}
		
		//设置item背景
		if(position % 2 == 0){
			view.setBackgroundColor(Color.parseColor("#F5F1E6"));
			viewHolder.tvOrder.setBackground(context.getResources().getDrawable(R.drawable.daren_number_red_bg2x));
			viewHolder.tvQuestion.setBackground(context.getResources().getDrawable(R.drawable.daren_question_red_bg2x));
		}else{
			view.setBackgroundColor(Color.parseColor("#EFE7D3"));
			viewHolder.tvOrder.setBackground(context.getResources().getDrawable(R.drawable.daren_number_yellow_2x));
			viewHolder.tvQuestion.setBackground(context.getResources().getDrawable(R.drawable.daren_question_yellow_bg2x));
		}
		return view;
	}

	/**
	 * 辅助类 用于临时存储listview的一个条目的信息
	 * 
	 * @author YXC
	 * 
	 */
	private static final class ViewHolder {
		GridLayout gdLayout;
		TextView tvOrder;
		TextView tvQuestion;
		ImageView imgPicture;
	}

}
