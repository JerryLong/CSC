package com.guess.shareutil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class ScreenShot {
	
	private Activity context;
	
//	private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture/";
	
	
	public ScreenShot(Activity context){
		this.context = context;
	}
	/**
	 * 屏幕截图
	 */
	public Bitmap getScreenShotBitmap(){
		WindowManager windowManager = context.getWindowManager();  
	    Display display = windowManager.getDefaultDisplay();  
	    @SuppressWarnings("deprecation")
		int w = display.getWidth();  
	    @SuppressWarnings("deprecation")
		int h = display.getHeight();
	    
	    Bitmap Bmp = Bitmap.createBitmap( w, h, Config.ARGB_8888 );      
	      
	    //2.获取屏幕  
	    View decorview = context.getWindow().getDecorView();   
	    decorview.setDrawingCacheEnabled(true);   
	    Bmp = decorview.getDrawingCache(); 
	    
	    return Bmp;
	}
}
