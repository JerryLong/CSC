package com.guess.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 存储四川达人页面已答过的题
 * 
 * @author YXC
 * 
 */
public class DarenData {
	private MySqliteOpnner sql;
	private DarenLable sqlLable;

	/**
	 * 构造函数
	 */
	public DarenData(Context context) {
		sql = new MySqliteOpnner(context);
		sqlLable = new DarenLable(context);
	}


	public synchronized void addDarenInfo(HashMap<String, Object> map) {
		SQLiteDatabase db = sql.getReadableDatabase();

		if (!isExist(map.get("gameLevel").toString())) {
			ContentValues cv = new ContentValues();
			cv.put("gameLevel", map.get("gameLevel").toString());
			cv.put("imageUrl", map.get("imageUrl").toString());
			cv.put("title", map.get("title").toString());
			cv.put("description", map.get("description").toString());
			cv.put("answer", map.get("answer").toString());
			db.insert("DarenData", null, cv);
		} else {
			updateUserInfo(map);
		}
		db.close();
	}

	/**
	 * 更新数据
	 */
	private void updateUserInfo(HashMap<String, Object> map) {
		SQLiteDatabase db = sql.getReadableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("imageUrl", map.get("imageUrl").toString());
		cv.put("title", map.get("title").toString());
		cv.put("description", map.get("description").toString());
		cv.put("answer", map.get("answer").toString());
		db.update("DarenData", cv, "gameLevel=?", new String[] { map.get("gameLevel").toString() });

		db.close();
	}

	/**
	 * 判断制定gameLevel的题是否存在
	 * @param gameLevel 
	 * @return
	 */
	private boolean isExist(String gameLevel) {
		SQLiteDatabase db = sql.getReadableDatabase();

		String sql = "select* from DarenData";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String level = cursor.getString(cursor.getColumnIndex("gameLevel"));
			if (gameLevel.equals(level)) {
				cursor.close();
				return true;
			}

			cursor.moveToNext();
		}
		cursor.close();
		// db.close();
		return false;
	}

	/**
	 * 所有已打过的题目
	 */
	public synchronized ArrayList<HashMap<String, Object>> getDarenInfo() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		ArrayList<HashMap<String, Object>> lableList = new ArrayList<>();
		HashMap<String, Object> map = null;
		SQLiteDatabase db = sql.getReadableDatabase();

		String sql = "select* from DarenData";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			map = new HashMap<>();
			String gameLevel = cursor.getString(cursor.getColumnIndex("gameLevel"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
			String description = cursor.getString(cursor.getColumnIndex("description"));
			String answer = cursor.getString(cursor.getColumnIndex("answer"));
			map.put("gameLevel", gameLevel);
			map.put("title", title);
			map.put("description", description);
			map.put("imageUrl", imageUrl);
			map.put("answer", answer);
			lableList = sqlLable.getLable(gameLevel);
			map.put("lable", lableList);
			list.add(map);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 清除所有题目
	 */
	public void clearAll() {
		SQLiteDatabase db = sql.getReadableDatabase();

		db.delete("DarenData", null, null);

		db.close();
	}
}
