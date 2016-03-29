package com.guess.myutils;

import com.cdrongyao.caisichuan.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class PDialogUtil {

	private static Dialog dialog;

	/**
	 * showDialog
	 * 
	 * @param context
	 *            上下文对象
	 * @param msg
	 *            对话框所显示的提示信息
	 * @param  cancelable
	 * 			  对话框是否可以按返回键取消
	 */
	public static void showDialog(final Context context, String msg, boolean cancelable) {
	//	System.out.println("dialog->show");
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.item_dialog, null);// 查找布局控件并加载
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.dialog_img);
		TextView tipTextView = (TextView) v.findViewById(R.id.dialog_msg);
		
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.dialog_animation);
		//播放动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);
		
		if(dialog == null){
			dialog = new Dialog(context, R.style.loading_dialog);// 如果对话框对象为空的话则new
		}

		dialog.setCancelable(cancelable);// 设置对话框是否可以按返回键取消
		dialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();  
		lp.alpha = 0.8f;  
		
		window.setAttributes(lp);
		dialog.show();

	}

	/**
	 * dissmissDialog
	 */
	public static void cancelDialog() {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}
	}
}