package com.guess.myadapter;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.tools.BaseTools;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义Adapter 我参与的
 * 
 * @author YXC
 * 
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
public class MySquareAdapter extends BaseAdapter {
	LayoutParams params;
	private int size;
	// 上下文对象
	private Context context;
	private ApplicationUtil application;
	private HashMap<String, String> map;
	// 数据源
	private List<HashMap<String, Object>> list;
	
	//private ImageLoader imageIconLoader;
	SimpleDateFormat df;
	
	private String likeUrl = "http://api.caisichuan.com/gambleGuess/evaluationQuestion";
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param list
	 *            数据源
	 */
	@SuppressLint("SimpleDateFormat")
	public MySquareAdapter(Context context, List<HashMap<String, Object>> list, int size) {
		this.context = context;
		this.list = list;
		application = new ApplicationUtil(context);
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//	imageIconLoader = new ImageLoader(context, 50, 50);
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
			view = BaseTools.getView(context, R.layout.item_square);
			
		//	ImageView icon = (ImageView) view.findViewById(R.id.item_square_icon);
			ImageView picture = (ImageView) view.findViewById(R.id.item_square_image);

		//	TextView name = (TextView) view.findViewById(R.id.item_square_user_name);
			TextView hot = (TextView) view.findViewById(R.id.item_square_hot);
			TextView gold = (TextView) view.findViewById(R.id.item_square_gold);
			TextView question = (TextView) view.findViewById(R.id.item_square_question);
			TextView like = (TextView) view.findViewById(R.id.item_square_like);
			TextView time = (TextView) view.findViewById(R.id.item_square_time);
			
		//	viewHolder.imgIcon = icon;
			viewHolder.imgPicture = picture;
			viewHolder.tvGold = gold;
			viewHolder.tvTime = time;
		//	viewHolder.tvName = name;
			viewHolder.tvHot = hot;
			viewHolder.tvQuestion = question;
			viewHolder.tvLike = like;
			
			params = (LayoutParams) viewHolder.imgPicture.getLayoutParams();
			params.height = params.width = size;
			viewHolder.imgPicture.setLayoutParams(params);
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		// 获取当前点击的项的数据信息
		final HashMap<String, Object> data = this.list.get(position);
	//	String strIcon = data.get("imageUrl").toString();
		String strPicture = data.get("imageUrl").toString();
	//	String strName = data.get("nickName").toString();
		String strTime = df.format(Long.parseLong(data.get("createTime").toString()));
		String strHot = data.get("attention").toString();
		String strGold = data.get("totalBet").toString();
		String strQuestion = data.get("description").toString();
		final String id = data.get("id").toString();
	    final String strLike = data.get("love").toString();
	    final String bLike = data.get("evaluation").toString();//是否喜欢
		final TextView tvLike = viewHolder.tvLike;
		
	//	imageIconLoader.DisplayImage(strIcon, viewHolder.imgIcon);//设置发布者头像 此处不设置 系统默认
		Picasso.with(context).load(strPicture).resize(400, 400).placeholder(R.drawable.default_picture2x).into(viewHolder.imgPicture);
	//	imagePictureLoader.DisplayImage(strPicture, viewHolder.imgPicture);
	//	viewHolder.tvName.setText(strName);//设置发布者昵称  此次不设置 系统默认
		viewHolder.tvGold.setText("金币: " + strGold);
		viewHolder.tvHot.setText("热度: " + strHot);
		viewHolder.tvQuestion.setText(strQuestion);
		viewHolder.tvTime.setText(strTime.substring(5, strTime.length()-3));
		
		final Drawable drawableUnLike = context.getResources().getDrawable(R.drawable.ic_square_unlike2x);
		final Drawable drawableLike = context.getResources().getDrawable(R.drawable.ic_square_like2x);
		drawableUnLike.setBounds(0, 0, drawableUnLike.getMinimumWidth(), drawableUnLike.getMinimumHeight());
		drawableLike.setBounds(0, 0, drawableLike.getMinimumWidth(), drawableLike.getMinimumHeight());
		if("1".equals(bLike)){
			tvLike.setCompoundDrawables(drawableLike, null, null, null);
			tvLike.setText("已赞  " + strLike);
		}else{
			tvLike.setCompoundDrawables(drawableUnLike, null, null, null);
			tvLike.setText("赞  " + strLike);
		}
		tvLike.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//like or unlike
				map = new HashMap<>();
				String bLike = data.get("evaluation").toString();
				String strLike = data.get("love").toString();
				if("1".equals(bLike)){
					map.put("questionId", id);
					map.put("evaluation", "0");
					setLove(map);
					tvLike.setCompoundDrawables(drawableUnLike, null, null, null);
					tvLike.setText("赞  " + (Integer.parseInt(strLike) - 1));
					data.put("evaluation", "0");
					data.put("love", (Integer.parseInt(strLike) - 1));
				}else{
					map.put("questionId", id);
					map.put("evaluation", "1");
					setLove(map);
					tvLike.setCompoundDrawables(drawableLike, null, null, null);
					tvLike.setText("已赞  " + (Integer.parseInt(strLike) + 1));					
					data.put("evaluation", "1");
					data.put("love", (Integer.parseInt(strLike) + 1));
				}
			}
		});
		return view;
	}

	/**
	 * 辅助类 用于临时存储listview的一个条目的信息
	 * 
	 * @author YXC
	 * 
	 */
	private static final class ViewHolder {

	//	ImageView imgIcon;
		ImageView imgPicture;
		
	//	TextView tvName;
		TextView tvQuestion;
		TextView tvLike;
		TextView tvTime;
		TextView tvHot;
		TextView tvGold;
	}
	
	/**
	 * 点赞
	 * @param map
	 */
	private void setLove(final HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(likeUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == Constant.NOT_LOGIN){
						LoginUtil login = new LoginUtil(context);
						login.login(new LoginListener() {
							
							@Override
							public void onLoginAfter() {
								setLove(map);
							}
						});
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
			}
		}, map);
		application.getRequestQueue().add(json);
	}

}
