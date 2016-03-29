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
 * 自定义Adapter 我发布的问题
 * 
 * @author YXC
 * 
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
public class MyQuestionAdapter extends BaseAdapter {
	// 上下文对象
	private Context context;
	// 数据源
	private List<HashMap<String, Object>> list;
	
	SimpleDateFormat df;
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param list
	 *            数据源
	 */
	@SuppressLint("SimpleDateFormat")
	public MyQuestionAdapter(Context context, List<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
		df = new SimpleDateFormat("yy-MM-dd HH:mm");
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
			view = BaseTools.getView(context, R.layout.item_my_question);
			
			ImageView imgPicture = (ImageView) view.findViewById(R.id.item_question_picture);
			ImageView imgAudit = (ImageView) view.findViewById(R.id.item_question_audit);
			ImageView imgActivity = (ImageView) view.findViewById(R.id.item_question_activity);
			TextView tvQuestion = (TextView) view.findViewById(R.id.item_question_question);
			TextView tvAnswer = (TextView) view.findViewById(R.id.item_question_answer);
			TextView tvTime = (TextView) view.findViewById(R.id.item_question_time);
			
			viewHolder.imgPicture = imgPicture;
			viewHolder.imgAudit = imgAudit;
			viewHolder.imgActivity = imgActivity;
			viewHolder.tvAnswer = tvAnswer;
			viewHolder.tvQuestion = tvQuestion;
			viewHolder.tvTime = tvTime;
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		// 获取当前点击的项的数据信息
		final HashMap<String, Object> data = this.list.get(position);

		String picture = data.get("imageUrl").toString();
		if(!"null".equals(picture)){
			Picasso.with(context).load(picture).resize(120, 120).placeholder(R.drawable.default_picture2x).into(viewHolder.imgPicture);
		}else{
			viewHolder.imgPicture.setImageResource(R.drawable.default_avatar3x);
		}
		String flag = data.get("finish").toString();
		if("1".equals(flag)){//活动已经结束
			viewHolder.imgActivity.setVisibility(View.VISIBLE);
			viewHolder.imgAudit.setVisibility(View.GONE);
			viewHolder.imgActivity.setImageResource(R.drawable.ic_more_end2x);
		}else if("0".equals(flag)){//活动进行中
			viewHolder.imgActivity.setVisibility(View.VISIBLE);
			viewHolder.imgAudit.setVisibility(View.GONE);
			viewHolder.imgActivity.setImageResource(R.drawable.ic_more_conduct2x);
		}else{
			viewHolder.imgActivity.setVisibility(View.GONE);
			int audit = (int) data.get("audit");
			if(audit == 2){
				//审核不通过
				viewHolder.imgAudit.setVisibility(View.VISIBLE);
			}
		}
		
		viewHolder.tvQuestion.setText(data.get("title").toString());
		viewHolder.tvAnswer.setText(context.getString(R.string.my_answer) + data.get("answer").toString());
		viewHolder.tvTime.setText(context.getString(R.string.time) + df.format(Long.parseLong(data.get("createTime").toString())));
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
		 * 发布的题目的图片
		 */
		ImageView imgPicture;
		/**
		 * 活动进行标志
		 */
		ImageView imgActivity;
		/**
		 * 审核未通过图标
		 */
		ImageView imgAudit;
		/**
		 * 题目
		 */
		TextView tvQuestion;
		/**
		 * 答案
		 */
		TextView tvAnswer;
		/**
		 * 发布时间
		 */
		TextView tvTime;
	}

}
