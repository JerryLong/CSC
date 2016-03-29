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
public class UserQuestionAdapter extends BaseAdapter {
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
	public UserQuestionAdapter(Context context, List<HashMap<String, Object>> list) {
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
			view = BaseTools.getView(context, R.layout.item_user_questions);
			
			ImageView imgPic = (ImageView) view.findViewById(R.id.item_user_question_picture);
			ImageView imgAva = (ImageView) view.findViewById(R.id.item_user_question_avatar);
			TextView tvName = (TextView) view.findViewById(R.id.item_user_question_nickname);
			TextView tvQue = (TextView) view.findViewById(R.id.item_user_question_name);
			TextView tvLove = (TextView) view.findViewById(R.id.item_user_question_love);
			
			viewHolder.imgPicture = imgPic;
			viewHolder.imgAvatar = imgAva;
			viewHolder.tvName = tvName;
			viewHolder.questionName = tvQue;
			viewHolder.tvLove = tvLove;
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		// 获取当前点击的项的数据信息
		final HashMap<String, Object> data = this.list.get(position);
		
		//填充数据
		String avatar = data.get("avatar").toString();
		if(!"null".equals(avatar)){
			Picasso.with(context).load(avatar).resize(400, 400).placeholder(R.drawable.default_avatar3x).into(viewHolder.imgAvatar);
		}else{
			viewHolder.imgAvatar.setImageResource(R.drawable.default_avatar3x);
		}
		Picasso.with(context).load(data.get("imageUrl").toString()).resize(150, 150).placeholder(R.drawable.default_picture2x).into(viewHolder.imgPicture);
		viewHolder.questionName.setText(data.get("title").toString());
		viewHolder.tvLove.setText(data.get("love").toString());
		viewHolder.tvName.setText(data.get("nickname").toString());

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
		 * 图片
		 */
		ImageView imgPicture;
		/**
		 * 用户头像
		 */
		ImageView imgAvatar;
		/**
		 * 用户昵称
		 */
		TextView tvName;
		/**
		 * 问题
		 */
		TextView questionName;
		/**
		 * 喜欢数量
		 */
		TextView tvLove;
	}

}
