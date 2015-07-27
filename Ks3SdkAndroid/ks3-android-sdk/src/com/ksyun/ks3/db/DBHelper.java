package com.ksyun.ks3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	private static final String SQL_CREATE_LOG = "CREATE TABLE IF NOT EXISTS " + 
									DBConstant.TABLE_NAME_LOG + " (" +
									DBConstant.TABLE_LOG_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
									DBConstant.TABLE_LOG_COLUMN_CONTENT + " TEXT DEFAULT \"\")";

	public DBHelper(Context context) {
		super(context, DBConstant.DB_NAME, null, DBConstant.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_LOG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
