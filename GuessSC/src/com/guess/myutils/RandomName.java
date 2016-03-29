package com.guess.myutils;

import java.util.Random;

import com.cdrongyao.caisichuan.R;

import android.content.Context;

/**
 * 随机生成用户名
 * @author YXC
 *
 */
public class RandomName {
	
	private String nameStart[];
	private String nameEnd[];
	public RandomName(Context context){
		nameStart = context.getResources().getStringArray(R.array.name_start);
		nameEnd = context.getResources().getStringArray(R.array.name_end);
	}
	
	/**
	 * 返回随机用户昵称
	 * @return
	 */
	public String getRandomName(){
		String name = "love";
		Random random = new Random();
		int start = random.nextInt(nameStart.length);
		int end = random.nextInt(nameEnd.length);
		
		name = nameStart[start] + nameEnd[end];
		return name;
	}
}
