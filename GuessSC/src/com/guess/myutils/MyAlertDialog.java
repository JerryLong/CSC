package com.guess.myutils;

import com.cdrongyao.caisichuan.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MyAlertDialog {
	
	public static AlertDialog builder;
	
	/**
	 * 显示提示对话框    一个按钮
	 * @param context  上下文对象
	 * @param content  提示内容
	 * @param finish  是否结束当前Activity
	 * @param backCancel 按返回键是否可取消对话框 如果不可取消的话直接结束当前页面
	 */
	public static void setDialog(final Activity context, String content, final boolean finish, boolean backCancel) {

		builder = new AlertDialog.Builder(context).create();
		builder.show();
		builder.setCanceledOnTouchOutside(false);
		Window window = builder.getWindow();
		window.setContentView(R.layout.cmark_view_goldless);
		TextView tvContent = (TextView) window.findViewById(R.id.cmark_goldless_context);
		tvContent.setText(content);
		Button mConfirmBtn = (Button) window.findViewById(R.id.cmark_goldless_ok);
		mConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.dismiss();
				if(finish){
					context.finish();
				}
			}
		});
		if(!backCancel){
			builder.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					context.finish();
				}
			});
		}
	}
	
	/**
	 * 显示提示对话框   两个按钮
	 * @param context  上下文对象
	 * @param leftText 左边按钮的文字
	 * @param rightText 右边按钮的文字
	 * @param title      标题栏
	 * @param listener   按钮监听事件
	 */
	public static void showAlertDialog(final Activity context, String leftText, String rightText, String title, final DialogListener listener) {

		builder = new AlertDialog.Builder(context).create();
		builder.show();
		builder.setCanceledOnTouchOutside(false);
		Window window = builder.getWindow();
		window.setContentView(R.layout.cmark_view_goldless);
		Button btnLeft = (Button) window.findViewById(R.id.setting_account_bind_cancel);
		TextView tvContent = (TextView) window.findViewById(R.id.cmark_goldless_context);
		Button mConfirmBtn = (Button) window.findViewById(R.id.cmark_goldless_ok);
		
		btnLeft.setVisibility(View.VISIBLE);
		tvContent.setText(title);
		btnLeft.setText(leftText);
		mConfirmBtn.setText(rightText);

		mConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onOkDialog();
				builder.dismiss();
			}
		});
		btnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.onCancelDialog();
				builder.dismiss();
			}
		});
	}
	
	
	public static interface DialogListener{
		public void onOkDialog();
		public void onCancelDialog();
	}
}
