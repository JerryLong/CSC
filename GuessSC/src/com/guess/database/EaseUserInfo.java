package com.guess.database;

import com.easemob.easeui.domain.EaseUser;

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
public class EaseUserInfo {
	private MySqliteOpnner sql;

	/**
	 * 构造函数
	 */
	public EaseUserInfo(Context context) {
		sql = new MySqliteOpnner(context);
	}

	/**
	 * 添加用户信息
	 * 
	 * @param userName
	 *            用户名
	 * @param nickName
	 *            昵称
	 * @param avatar
	 *            头像
	 */
	public synchronized void addUserInfo(String userName, String nickName, String avatar) {
		SQLiteDatabase db = sql.getReadableDatabase();

		if (!isExist(userName)) {
			ContentValues cv = new ContentValues();
			cv.put("username", userName);
			cv.put("nickname", nickName);
			cv.put("avatar", avatar);
			db.insert("EaseUser", null, cv);
		} else {
			updateUserInfo(userName, nickName, avatar);
		}
		db.close();
	}

	/**
	 * 更新用户信息
	 * @param userName 用户名
	 * @param nickName 昵称
	 * @param avatar   头像
	 */
	private void updateUserInfo(String userName, String nickName, String avatar) {
		SQLiteDatabase db = sql.getReadableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("nickname", nickName);
		cv.put("avatar", avatar);
		db.update("EaseUser", cv, "username=?", new String[] { userName });

		db.close();
	}

	/**
	 * 判断指定用户是否存在
	 * 
	 * @param userName
	 *            用户名
	 * @return
	 */
	private boolean isExist(String userName) {
		SQLiteDatabase db = sql.getReadableDatabase();

		String sql = "select* from EaseUser";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndex("username"));
			if (userName.equals(name)) {
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
	public synchronized EaseUser getUserInfo(String userName) {
		EaseUser easeUser = new EaseUser(userName);
		SQLiteDatabase db = sql.getReadableDatabase();

		String sql = "select* from EaseUser";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndex("username"));
			if (userName.equals(name)) {
				String nickName = cursor.getString(cursor
						.getColumnIndex("nickname"));
				String avatar = cursor.getString(cursor
						.getColumnIndex("avatar"));

				easeUser.setNick(nickName);
				if((avatar != null) && (!"null".equals(avatar)) && (!"".equals(avatar))){
					easeUser.setAvatar(avatar);
				}
				break;
			}
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return easeUser;
	}

	/**
	 * 清除所有用户信息
	 */
	public void clearAll() {
		SQLiteDatabase db = sql.getReadableDatabase();

		db.delete("EaseUser", null, null);

		db.close();
	}
}
