package com.guess.guide;

import java.util.ArrayList;
import java.util.List;

import com.cdrongyao.caisichuan.R;
import com.guess.myadapter.MyPagerAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;


@SuppressLint("InflateParams")
public class MyGuide extends Activity implements OnPageChangeListener {  
	  
    private ViewPager vp;  
    private MyPagerAdapter vpAdapter;  
    private List<View> views;  
   
  //  private ImageView[] dots;  
  
   // private int currentIndex;  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.guide_activity);  
  
        initViews();  
  
     //   initDots();  
    }  
  
    private void initViews() {  
        LayoutInflater inflater = LayoutInflater.from(this);  
  
        views = new ArrayList<View>();  

        /*views.add(inflater.inflate(R.layout.guide_one, null));  
        views.add(inflater.inflate(R.layout.guide_two, null));  
        views.add(inflater.inflate(R.layout.guide_three, null)); */ 
        views.add(inflater.inflate(R.layout.guide_one,null));
        views.add(inflater.inflate(R.layout.guide_two ,null));
        views.add(inflater.inflate(R.layout.guide_three,null));
  
        vpAdapter = new MyPagerAdapter(views, this);  
          
        vp = (ViewPager) findViewById(R.id.viewpager);  
        vp.setAdapter(vpAdapter);  
        
        vp.setOnPageChangeListener(this);  
    }  
  
    /*private void initDots() {  
        LinearLayout ll = null;
        ll = (LinearLayout)findViewById(R.id.guide_ll_dots);  
  
        dots = new ImageView[views.size()];  
  
        for (int i = 0; i < views.size(); i++) {  
            dots[i] = (ImageView) ll.getChildAt(i);  
            dots[i].setBackgroundResource(R.drawable.shape_dots);
        }  
  
        currentIndex = 0;  
        dots[currentIndex].setBackgroundResource(R.drawable.shape_dots_current);
    }  
  
    private void setCurrentDot(int position) {  
        if (position < 0 || position > views.size() - 1  
                || currentIndex == position) {  
            return;  
        }  
  
        dots[position].setBackgroundResource(R.drawable.shape_dots_current);  
        dots[currentIndex].setBackgroundResource(R.drawable.shape_dots);  
  
        currentIndex = position;  
    } */ 
  
    @Override  
    public void onPageScrollStateChanged(int arg0) {  
    }  
 
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
    }  
 
    @Override  
    public void onPageSelected(int arg0) {   
       // setCurrentDot(arg0);  
    }  
  
}
