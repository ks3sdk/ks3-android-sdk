package com.ksyun.ks3.db;

import com.ksyun.ks3.model.LogBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.test.mock.MockContext;

public class DBManager {

	private static final String SQL_INSERT_LOG = "INSERT INTO "
			+ DBConstant.TABLE_NAME_LOG + "("
			+ DBConstant.TABLE_LOG_COLUMN_CONTENT + ") VALUES (?)";
	private static final String SQL_DELETE_LOG = "DELETE FROM "
			+ DBConstant.TABLE_NAME_LOG + " WHERE "
			+ DBConstant.TABLE_LOG_COLUMN_ID + " = ?";

	private static DBManager mInstance;
	private static Object mLockObject = new Object();
	private Context context;
	private SQLiteDatabase mDatabase;
	private SQLiteStatement mInsertStatement;
	private SQLiteStatement mDeleteStatement;

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

	@SuppressLint("NewApi")
	public DBManager(Context context) {
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
					if (Build.VERSION.SDK_INT >= 11) {
						mDeleteStatement.clearBindings();
						mDeleteStatement.bindLong(1, logId);
						mDeleteStatement.executeUpdateDelete();
					} else {
						mDatabase.delete(DBConstant.TABLE_NAME_LOG,
								DBConstant.TABLE_LOG_COLUMN_ID + " = ?",
								new String[] { String.valueOf(logId) });
					}
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
			if (Build.VERSION.SDK_INT >= 11) {
				mDatabase.beginTransaction();
				mDeleteStatement.clearBindings();
				mDeleteStatement.bindLong(1, logId);
				mDeleteStatement.executeUpdateDelete();
				mDatabase.setTransactionSuccessful();
				mDatabase.endTransaction();
			} else {
				mDatabase.delete(DBConstant.TABLE_NAME_LOG,
						DBConstant.TABLE_LOG_COLUMN_ID + " = ?",
						new String[] { String.valueOf(logId) });
			}
		}
	}

	public RecordResult getRecords(int logOnceLimit, RecordResult recordResults) {
		synchronized (mLockObject) {
			mDatabase.beginTransaction();
			Cursor cursor = mDatabase.query(DBConstant.TABLE_NAME_LOG,
					new String[] { DBConstant.TABLE_LOG_COLUMN_ID,
							DBConstant.TABLE_LOG_COLUMN_CONTENT }, null, null,
					null, null, null, String.valueOf(logOnceLimit));
			if (null != cursor) {
				while (cursor.moveToNext()) {
					int logId = cursor.getInt(cursor
							.getColumnIndex(DBConstant.TABLE_LOG_COLUMN_ID));
					String logContent = cursor
							.getString(cursor
									.getColumnIndex(DBConstant.TABLE_LOG_COLUMN_CONTENT));
					recordResults.idBuffer.append(logId);
					recordResults.idBuffer.append("/n");
					recordResults.contentBuffer.append(logContent);
					recordResults.contentBuffer.append("/n");
				}
				cursor.close();
			}
			mDatabase.setTransactionSuccessful();
			mDatabase.endTransaction();
		}
		return recordResults;
	}

	@SuppressLint("NewApi")
	public void deleteLogs(String recordsId) {
		String[] ids = recordsId.split("/n");
		synchronized (mLockObject) {
			if (Build.VERSION.SDK_INT >= 11) {
				mDatabase.beginTransaction();
				mDeleteStatement.clearBindings();
				for (int i = 0; i < ids.length; i++) {
					mDeleteStatement.bindLong(1, Long.valueOf(ids[i]));
					mDeleteStatement.executeUpdateDelete();
				}
				mDatabase.setTransactionSuccessful();
				mDatabase.endTransaction();
			} else {
				mDatabase.delete(DBConstant.TABLE_NAME_LOG,
						DBConstant.TABLE_LOG_COLUMN_ID + " = ?", ids);
			}

		}
	}

}
