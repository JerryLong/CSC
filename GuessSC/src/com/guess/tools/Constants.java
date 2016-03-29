package com.guess.tools;

import java.util.ArrayList;

import com.guess.bean.TitleBean;

public class Constants {
	public static ArrayList<TitleBean> getTitle() {
		ArrayList<TitleBean> title = new ArrayList<TitleBean>();
		TitleBean item = new TitleBean();
		item.setId(0);
		item.setType("/getNcwcAndVideoQuestionsById");
		item.setTitle("最新");
		title.add(item);
		item = new TitleBean();
		item.setId(1);
		item.setType("/getHotNcwcAndVideoQuestions");
		item.setTitle("最热");
		title.add(item);
		return title;
	}

}
