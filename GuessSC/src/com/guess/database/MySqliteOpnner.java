package com.guess.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySqliteOpnner extends SQLiteOpenHelper{
	
	private static final String NAME = "GUESS_SC";
	
	public MySqliteOpnner(Context context) {
		super(context, NAME, null, 1);
	}
	
	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table if not exists EaseUser(username text, nickname text, avatar text)");
		db.execSQL("create table if not exists DarenData(answer text, title text, imageUrl text, description text, gameLevel text)");
		db.execSQL("create table if not exists DarenLable(gameLevel text, id text, count text, agree text, name text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
	}

}
