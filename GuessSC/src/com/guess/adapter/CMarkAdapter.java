package com.guess.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.bean.CMarkBean;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.ShareData;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.tools.BaseTools;
import com.guess.tools.Tools;
import com.guess.user.EditAddress;
import com.guess.utils.ImageLoadUtils;
import com.guess.utils.UrlContants;
import com.guess.utils.VolleyErrorHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CMarkAdapter extends BaseAdapter {
	private Context mContext;
	private ImageLoadUtils mImageLoadUtils;
	private ShareData mShareData;
	private ApplicationUtil mApplicationUtil;
	private ArrayList<CMarkBean> mList = new ArrayList<CMarkBean>();

	public CMarkAdapter(Context context) {
		mContext = context;
		mImageLoadUtils = ImageLoadUtils.getInstanceAsycnImage();
		mImageLoadUtils.setThreadPoolExecutor();
		mShareData = ShareData.getInstance(mContext);
		mApplicationUtil = new ApplicationUtil(mContext);
	}

	public void setData(ArrayList<CMarkBean> list) {
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
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if (view == null) {
			view = BaseTools.getView(mContext, R.layout.view_cmark_listview);
			holder = new ViewHolder();
			holder.mImg = (ImageView) view.findViewById(R.id.view_cmark_imageview);
			holder.mTitle = (TextView) view.findViewById(R.id.view_cmark_title);
			holder.mDesc = (TextView) view.findViewById(R.id.view_cmark_desc);
			holder.mPrice = (TextView) view.findViewById(R.id.view_cmark_price);
			holder.mNeed = (TextView) view.findViewById(R.id.view_cmark_gold);
			holder.mNum = (TextView) view.findViewById(R.id.view_cmark_quantity);
			holder.mExchangeBtn = (Button) view.findViewById(R.id.view_cmark_exchange);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final CMarkBean bean = (CMarkBean) getItem(position);
		holder.mTitle.setText("" + bean.getName());
		holder.mDesc.setText("" + bean.getDetails());
		holder.mPrice.setText("" + bean.getMarketPrice());
		holder.mNeed.setText("" + bean.getGoldPrice());
		holder.mNum.setText("" + bean.getNumber());
		mImageLoadUtils.loadBitmap(mContext.getResources(), bean.getImageUrl(), holder.mImg, R.drawable.download_failed,
				0, 0);
		holder.mExchangeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// if (300 > bean.getGoldPrice()) {
				if (Integer.parseInt(mShareData.getCoin()) > bean.getGoldPrice()) {
					setExchangeDialog(bean.getId());
				} else {
					setDialog();
				}
			}
		});
		return view;
	}

	class ViewHolder {
		ImageView mImg;
		TextView mTitle, mDesc, mPrice, mNum, mNeed;
		Button mExchangeBtn;
	}

	public void setExchangeDialog(final int id) {
		final AlertDialog builder = new AlertDialog.Builder(mContext).create();
		builder.show();
		builder.setCanceledOnTouchOutside(false);
		Window window = builder.getWindow();
		window.setContentView(R.layout.view_cmark_dialog);
		Button mAddressBtn = (Button) window.findViewById(R.id.cmark_dialog_address);
		mAddressBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.dismiss();
				Intent intent = new Intent(mContext, EditAddress.class);
				mContext.startActivity(intent);
			}
		});
		Button mConfirmBtn = (Button) window.findViewById(R.id.cmark_dialog_exchange);
		mConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.dismiss();
				exchangeGoods(id);
			}
		});
		Button mCancleBtn = (Button) window.findViewById(R.id.cmark_dialog_cancel);
		mCancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
	}

	private void setDialog() {

		final AlertDialog builder = new AlertDialog.Builder(mContext).create();
		builder.show();
		builder.setCanceledOnTouchOutside(false);
		Window window = builder.getWindow();
		window.setContentView(R.layout.cmark_view_goldless);
		Button mConfirmBtn = (Button) window.findViewById(R.id.cmark_goldless_ok);
		mConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
	}

	private void exchangeGoods(final int id) {
		String url = UrlContants.LOCATION + UrlContants.CMARK_GOODS;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("goodsId", Tools.getIntToString(id));
		MyJsonRequest request = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						Toast.makeText(mContext, R.string.cmark_exchange_ok, Toast.LENGTH_SHORT).show();
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(mContext);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									exchangeGoods(id);
								}
							});
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(mContext, VolleyErrorHelper.getMessage(error, mContext), Toast.LENGTH_SHORT).show();
			}
		}, map);
		request.setShouldCache(false);
		mApplicationUtil.getRequestQueue().add(request);
	}
}
