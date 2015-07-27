package com.ksyun.ks3.db;

import com.ksyun.ks3.model.LogBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DBManager {
	private static final String SQL_INSERT_LOG = "INSERT INTO "
			+ DBConstant.TABLE_NAME_LOG + "("
			+ DBConstant.TABLE_LOG_COLUMN_CONTENT + ") VALUES (?)";
	private static final String SQL_DELETE_LOG = "DELETE FROM "
			+ DBConstant.TABLE_NAME_LOG + " WHERE "
			+ DBConstant.TABLE_LOG_COLUMN_ID + " = ?";

	/**
	 * Singleton instance
	 */
	private static DBManager mInstance;
	private static Object mLockObject = new Object();
	private Context context;
	private SQLiteDatabase mDatabase;
	private SQLiteStatement mInsertStatement;
	private SQLiteStatement mDeleteStatement;

	/**
	 * Singleton pattern
	 */
	public static DBManager getInstance(Context context) {
		if (null == mInstance) {
			synchronized (mLockObject) {
				if (null == mInstance) {
					mInstance = new DBManager(context);
				}
			}
		}
		return mInstance;
	}

	public DBManager(Context context) {
		this.context = context;
		SQLiteOpenHelper helper = new DBHelper(context);
		mDatabase = helper.getWritableDatabase();
		mInsertStatement = mDatabase.compileStatement(SQL_INSERT_LOG);
		mDeleteStatement = mDatabase.compileStatement(SQL_DELETE_LOG);
	}

	@Override
	protected void finalize() throws Throwable {
		mDatabase.close();
		super.finalize();
	}

	/**
	 * Insert log content into database.
	 */
	public void insertLog(String log) {
		synchronized (mLockObject) {
			if (queryCount() < 1200) {
				mDatabase.beginTransaction();
				// mInsertStatement.clearBindings();
				mInsertStatement.bindString(1, log);
				mInsertStatement.executeInsert();
				mDatabase.setTransactionSuccessful();
				mDatabase.endTransaction();
			} else {
				fetchLogAndRemove();
				insertLog(log);
			}
		}
	}

	/**
	 * Fetch log content and remove it from database.
	 */
	@SuppressLint("NewApi")
	public LogBean fetchLogAndRemove() {
		LogBean result = null;
		synchronized (mLockObject) {
			mDatabase.beginTransaction();
			Cursor cursor = mDatabase.query(DBConstant.TABLE_NAME_LOG,
					new String[] { DBConstant.TABLE_LOG_COLUMN_ID,
							DBConstant.TABLE_LOG_COLUMN_CONTENT }, null, null,
					null, null, null, "1");
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int logId = cursor.getInt(cursor
							.getColumnIndex(DBConstant.TABLE_LOG_COLUMN_ID));
					String logContent = cursor
							.getString(cursor
									.getColumnIndex(DBConstant.TABLE_LOG_COLUMN_CONTENT));
					result = new LogBean(logId, logContent);
					// Delete this log
					mDeleteStatement.clearBindings();
					mDeleteStatement.bindLong(1, logId);
					mDeleteStatement.executeUpdateDelete();
				}
				cursor.close();
			}
			mDatabase.setTransactionSuccessful();
			mDatabase.endTransaction();
		}
		return result;
	}

	public int queryCount() {
		int result = 0;
		synchronized (mLockObject) {
			mDatabase.beginTransaction();
			Cursor cursor = mDatabase.query(DBConstant.TABLE_NAME_LOG,
					new String[] { DBConstant.TABLE_LOG_COLUMN_ID,
							DBConstant.TABLE_LOG_COLUMN_CONTENT }, null, null,
					null, null, null, null);
			if (null != cursor) {
				result = cursor.getCount();
				cursor.close();
			}
			mDatabase.setTransactionSuccessful();
			mDatabase.endTransaction();
		}
		return result;
	}

	/**
	 * Delete log content within database
	 */
	@SuppressLint("NewApi")
	public void deleteLog(long logId) {
		synchronized (mLockObject) {
			mDatabase.beginTransaction();
			mDeleteStatement.clearBindings();
			mDeleteStatement.bindLong(1, logId);
			mDeleteStatement.executeUpdateDelete();
			mDatabase.setTransactionSuccessful();
			mDatabase.endTransaction();
		}
	}

	public String getAllRecords() {
		// TODO Auto-generated method stub
		return null;
	}
}
