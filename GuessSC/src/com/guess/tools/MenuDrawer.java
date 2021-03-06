package com.guess.tools;

import com.cdrongyao.caisichuan.R;
import com.dralong.slidingmenu.lib.SlidingMenu;
import com.dralong.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.guess.activity.CMarkActivity;
import com.guess.activity.MainActivity;
import com.guess.activity.OfficialActivity;
import com.guess.activity.SCPersonActivity;
import com.guess.activity.SquareActivity;
import com.guess.myutils.ShareData;
import com.guess.myview.CircleImageView;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuDrawer implements OnClickListener {
	private final Activity activity;
	SlidingMenu menu;
	private CircleImageView imgAvatar;
	private TextView tvName;
	private ShareData share;
	private RelativeLayout mSCPersonLayout, mSystemLayout, mCMarkLayout, mSquareLayout;

	public MenuDrawer(Activity activity) {
		this.activity = activity;
	}

	public SlidingMenu initSlidingMenu() {
		menu = new SlidingMenu(activity);
		menu.setMode(SlidingMenu.LEFT);// 设置左右滑菜单
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// 设置要使菜单滑动，触碰屏幕的范围
		// menu.setTouchModeBehind(SlidingMenu.RIGHT);
		menu.setShadowWidthRes(R.dimen.shadow_width);// 设置阴影图片的宽度
		// menu.setShadowDrawable(R.drawable.shadow);//设置阴影图片
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// SlidingMenu划出时主页面显示的剩余宽度
		menu.setFadeDegree(0.35F);// SlidingMenu滑动时的渐变程度
		menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);// 使SlidingMenu附加在Activity右边
		// menu.setBehindWidthRes(R.dimen.left_drawer_avatar_size);//设置SlidingMenu菜单的宽度
		menu.setMenu(R.layout.main_menu);// 设置menu的布局文件
		menu.setBackgroundImage(R.drawable.slidingmenu);
		// SlidingMenu滑动时的渐变程度
		menu.setFadeDegree(0);
		menu.setBehindScrollScale(0.25f);
		menu.toggle();// 动态判断自动关闭或开启SlidingMenu
		menu.setAboveCanvasTransformer(new CanvasTransformer() {

			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.15);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		menu.setBehindCanvasTransformer(new CanvasTransformer() {

			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2, canvas.getHeight() / 2);
			}
		});
		initView();
		return menu;
	}

	private void initView() {
		share = ShareData.getInstance(activity);
		tvName = (TextView) menu.findViewById(R.id.menu_tv_name);
		tvName.setText(share.getNickName());

		imgAvatar = (CircleImageView) menu.findViewById(R.id.menu_img_avatar);
		String avatar = share.getHead();
		if (!"".equals(avatar)) {
			Picasso.with(activity).load(avatar).resize(120, 120).placeholder(R.drawable.default_picture2x)
					.into(imgAvatar);
		}

		imgAvatar.setOnClickListener(this);

		mSCPersonLayout = (RelativeLayout) menu.findViewById(R.id.menu_layout_one);
		mSCPersonLayout.setOnClickListener(this);

		mSystemLayout = (RelativeLayout) menu.findViewById(R.id.menu_layout_two);
		mSystemLayout.setOnClickListener(this);

		mCMarkLayout = (RelativeLayout) menu.findViewById(R.id.menu_layout_three);
		mCMarkLayout.setOnClickListener(this);

		mSquareLayout = (RelativeLayout) menu.findViewById(R.id.menu_layout_four);
		mSquareLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_layout_one:
			/* 四川达人 */
			activity.startActivity(new Intent(activity, SCPersonActivity.class));
			// activity.overridePendingTransition(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			break;
		case R.id.menu_layout_two:
			/* 官方活动 */
			activity.startActivity(new Intent(activity, OfficialActivity.class));
			// activity.overridePendingTransition(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			break;
		case R.id.menu_layout_three:
			/* 猜商城 */
			activity.startActivity(new Intent(activity, CMarkActivity.class));
			// activity.overridePendingTransition(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			break;
		case R.id.menu_layout_four:
			/* 竞猜广场 */
			activity.startActivity(new Intent(activity, SquareActivity.class));
			// activity.overridePendingTransition(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			break;

		case R.id.menu_img_avatar:
			// 点击头像返回
			menu.toggle(); // 返回之后跳到我的页面
			MainActivity.mTabHost.onTabChanged("tab3");
			MainActivity.mTabHost.setCurrentTabByTag("tab3");
			break;
		}
	}
}
