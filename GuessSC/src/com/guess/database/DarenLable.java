package com.guess.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 用户信息
 * 
 * @author YXC
 * 
 */
public class DarenLable {
	private MySqliteOpnner sql;

	/**
	 * 构造函数
	 */
	public DarenLable(Context context) {
		sql = new MySqliteOpnner(context);
	}


	public synchronized void addLable(String gameLevel, HashMap<String, Object> map) {
		SQLiteDatabase db = sql.getReadableDatabase();

		if (!isExist(map.get("id").toString(), gameLevel)) {
			ContentValues cv = new ContentValues();
			cv.put("gameLevel", gameLevel);
			cv.put("name", map.get("name").toString());
			cv.put("id", map.get("id").toString());
			if((boolean) map.get("agree")){
				cv.put("agree", "1");
			}else{
				cv.put("agree", "0");
			}
			cv.put("count", map.get("count").toString());
			db.insert("DarenLable", null, cv);
		} else {
			updateUserInfo(gameLevel, map);
		}
		db.close();
	}

	/**
	 * 更新用户信息
	 * @param userName 用户名
	 * @param nickName 昵称
	 * @param avatar   头像
	 */
	private void updateUserInfo(String gameLevel, HashMap<String, Object> map) {
		SQLiteDatabase db = sql.getReadableDatabase();

		ContentValues cv = new ContentValues();
		if((boolean) map.get("agree")){
			cv.put("agree", "1");
		}else{
			cv.put("agree", "0");
		}
		cv.put("name", map.get("name").toString());
		cv.put("count", map.get("count").toString());
		db.update("DarenLable", cv, "gameLevel=? and id=?", new String[] { gameLevel, map.get("id").toString() });

		db.close();
	}

	/**
	 * 是否存在
	 * @param id
	 * @param gameLevel
	 * @return
	 */
	private boolean isExist(String id, String gameLevel) {
		SQLiteDatabase db = sql.getReadableDatabase();

		String sql = "select* from DarenLable";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String level = cursor.getString(cursor.getColumnIndex("gameLevel"));
			String ID = cursor.getString(cursor.getColumnIndex("id"));
			if (id.equals(ID) && gameLevel.equals(level)) {
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
	 * 获取指定用户的信息
	 */
	public synchronized ArrayList<HashMap<String, Object>> getLable(String gameLevel) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> map = null;
		SQLiteDatabase db = sql.getReadableDatabase();

		String sql = "select* from DarenLable";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			String level = cursor.getString(cursor.getColumnIndex("gameLevel"));
			if (gameLevel.equals(level)) {
				map = new HashMap<>();
				String id = cursor.getString(cursor.getColumnIndex("id"));
				String count = cursor.getString(cursor.getColumnIndex("count"));
				String agree = cursor.getString(cursor.getColumnIndex("agree"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				if("1".equals(agree)){
					map.put("agree", true);
				}else{
					map.put("agree", false);
				}
				map.put("id", id);
				map.put("count", count);
				map.put("name", name);
				list.add(map);
				break;
			}
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 清除所有用户信息
	 */
	public void clearAll() {
		SQLiteDatabase db = sql.getReadableDatabase();

		db.delete("DarenLable", null, null);

		db.close();
	}
}
