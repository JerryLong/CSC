package com.guess.tools;

public class Tools {
	public static String getIntToString(int number) {
		String str = String.valueOf(number);
		return str;
	}

	public static int getTimeDifference(int time) {
		StringBuffer str = new StringBuffer();
		str.append(time);
		str.append("000");
		int s = (int) (System.currentTimeMillis() - Long.parseLong(str.toString()));
		int m = (int) Math.ceil(s / 60000);
		return m;

	}
}
