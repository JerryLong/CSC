package com.guess.myutils;

import android.content.Context;
import android.media.MediaPlayer;

public class MySoundPlayer {
	
	private MediaPlayer mp;
	
	private ShareData share;
	
	/**
	 * 构造函数初始化
	 * @param context 上下文对象
	 * @param source  本地音乐 raw文件夹下得资源
	 */
	public MySoundPlayer(Context context, Integer source){
		share = ShareData.getInstance(context);
		if(share.isVoice()){
			try {
				mp = MediaPlayer.create(context, source);
				mp.prepare();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 播放声音
	 */
	public void playSound(){
		if(share.isVoice()){
			try {
				
				mp.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
