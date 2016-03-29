package com.guess.myadapter;

import java.util.List;

import com.cdrongyao.caisichuan.R;
import com.guess.myutils.ShareData;
import com.guess.user.ActivityLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MyPagerAdapter extends PagerAdapter {  
	  
    private List<View> views;  
    private Activity activity;  
    private ShareData share;
    public MyPagerAdapter(List<View> views, Activity activity) {  
        this.views = views;  
        this.activity = activity;  
        share = ShareData.getInstance(activity);
    }  
  
    @Override  
    public void destroyItem(View arg0, int arg1, Object arg2) {  
        ((ViewPager) arg0).removeView(views.get(arg1));  
    }  
  
    @Override  
    public void finishUpdate(View arg0) {  
    }  
  
    @Override  
    public int getCount() {  
        if (views != null) {  
            return views.size();  
        }  
        return 0;  
    }  
  
    @Override  
    public Object instantiateItem(View arg0, int arg1) {  
        ((ViewPager) arg0).addView(views.get(arg1), 0);  
        if (arg1 == views.size() - 1) {  
            LinearLayout view = (LinearLayout) arg0  
                    .findViewById(R.id.guide_enter);  
            view.setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                    setGuided();  
                    goHome();  
  
                }  
  
            });  
        }  
        return views.get(arg1);  
    }  
  
    private void goHome() {  
        Intent intent = null;
        if(share.getLogin()){
        	//跳过登录
        	intent = new Intent(activity, ActivityLogin.class);  
        }else{
        	//进入登录页面
        	intent = new Intent(activity, ActivityLogin.class); 
        }
        activity.startActivity(intent);  
        activity.finish();  
    }  
 
    private void setGuided() {  
        share.setFirstLogin(false);  
    }  
  
    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return (arg0 == arg1);  
    }  
  
    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
    }  
  
    @Override  
    public Parcelable saveState() {  
        return null;  
    }  
  
    @Override  
    public void startUpdate(View arg0) {  
    }  
  
} 
