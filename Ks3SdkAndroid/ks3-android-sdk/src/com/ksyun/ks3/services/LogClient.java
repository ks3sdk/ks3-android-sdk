package com.ksyun.ks3.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ksyun.ks3.db.DBManager;
import com.ksyun.ks3.db.RecordResult;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.LogRecord;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.GzipUtil;
import com.ksyun.ks3.util.NetworkUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class LogClient {
	private static final int LOG_ONCE_LIMIT = 120;
	private static final long TIMER_INTERVAL = 60 * 60 * 1000;
	private static LogClient mInstance;
	private static Object mLockObject = new Object();
	private static SyncHttpClient syncClient;
	private static Context mContext;
	private volatile boolean mStarted = false;
	private long interval;
	private long UPTATE_INTERVAL_WIFI_TIME = 1000 * 60 * 2;
	private Timer timer;
	private boolean isNeedloop;
	private int sendCount;

	private LogClient() {
	};

	public static LogClient getInstance() {
		if (null == mInstance) {
			synchronized (mLockObject) {
				if (null == mInstance) {
					mInstance = new LogClient();
					syncClient = new SyncHttpClient();
				}
			}
		}
		return mInstance;
	}

	public static LogClient getInstance(Context context) {
		if (null == mInstance) {
			synchronized (mLockObject) {
				if (null == mInstance) {
					mContext = context;
					mInstance = new LogClient();
					syncClient = new SyncHttpClient();
				}
			}
		}
		return mInstance;
	}

	private void sendRecordJson(final RecordResult recordsResult,
			final int sendCount, final int allCount, final boolean isNeedloop) {
		ByteArrayEntity byteArrayEntity = null;
		String jsonString = makeJsonLog(recordsResult.contentBuffer.toString());
		try {
			byteArrayEntity = new ByteArrayEntity(GzipUtil.compress(jsonString)
					.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(Constants.LOG_TAG, "gzip is failed, send log ingored");
			return;
		}
		syncClient.addHeader("accept-encoding", "gzip, deflate");
		syncClient.post(mContext, Constants.LOG_SERVER_URL, byteArrayEntity,
				"text/plain", new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int paramInt,
							Header[] paramArrayOfHeader, byte[] paramArrayOfByte) {
						Log.d(Constants.LOG_TAG,
								"log send success, response code = " + paramInt);
						DBManager.getInstance(mContext).deleteLogs(
								recordsResult.idBuffer.toString());
						Log.d(Constants.LOG_TAG, "log send count:" + sendCount
								+ ",next count : " + (allCount - sendCount));
						recordsResult.release();
						if (isNeedloop) {
							if (allCount - sendCount > 0) {
								sendRecord(allCount - sendCount);
							} else {
								/*Log.d(Constants.LOG_TAG,
										"more than 120 mode, last send all over");*/
							}
						} else {
						/*	Log.d(Constants.LOG_TAG,
									"less than 120 mode, send all over");*/
						}
					}

					@Override
					public void onFailure(int paramInt,
							Header[] paramArrayOfHeader,
							byte[] paramArrayOfByte, Throwable paramThrowable) {
					/*	Log.d(Constants.LOG_TAG,
								"log send failure, response code = " + paramInt);*/
						if (paramArrayOfByte != null) {
							Log.d(Constants.LOG_TAG, ",response = "
									+ new String(paramArrayOfByte));
						}
					}
				});
	}

	private String makeJsonLog(String recordsJson) {
		JSONArray array = new JSONArray();
		String[] singlgLogJson = recordsJson.split("/n");
		for (int i = 0; i < singlgLogJson.length; i++) {
			JSONObject record;
			try {
				record = new JSONObject(singlgLogJson[i]);
				array.put(record);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array.toString();
	}

	public void start() {
		if (mStarted) {
			return;
		}
		mStarted = true;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Judge the way of send
				// For test
				int current_count = DBManager.getInstance(mContext)
						.queryCount();
				/*Log.d(Constants.LOG_TAG, "send schedule, current thread id = "
						+ Thread.currentThread().getId() + ",log count = "
						+ current_count);*/
				if (NetworkUtil.isNetworkAvailable(mContext)) {
					Log.d(Constants.LOG_TAG, "network valiable");
					if (current_count > 0) {
						Log.d(Constants.LOG_TAG, "send record");
						sendRecord(current_count);
					} else {
						Log.d(Constants.LOG_TAG, "no record");
					}
				} else {
					Log.d(Constants.LOG_TAG, "network unvaliable");
				}
			}
		}, 5000, TIMER_INTERVAL);
	}

	private void sendRecord(int all_count) {
		isNeedloop = all_count >= LOG_ONCE_LIMIT;
		sendCount = isNeedloop ? LOG_ONCE_LIMIT : all_count;
		RecordResult recordResults = new RecordResult();
		DBManager.getInstance(mContext).getRecords(sendCount, recordResults);
		if (!TextUtils.isEmpty(recordResults.contentBuffer.toString())
				&& !TextUtils.isEmpty(recordResults.idBuffer.toString())) {
			sendRecordJson(recordResults, sendCount, all_count, isNeedloop);
		} else {
			Log.d(Constants.LOG_TAG, "read record result is not correct");
		}
	}

	private void stop() {
		if (!mStarted) {
			return;
		}
		if (null != timer) {
			timer.cancel();
		}
		mStarted = false;
	}

	public void put(String message) throws Ks3ClientException {
/*		Log.d(Constants.LOG_TAG, "new log: " + message);
*/		if (jsonCheck(message)) {
			DBManager.getInstance(mContext).insertLog(message);
		} else {
			throw new Ks3ClientException(
					"new log format is not correct, sdk will ingore it");
		}
	}

	private boolean jsonCheck(String message) {
		boolean isJson = true;
		try {
			JSONObject object = new JSONObject(message);
		} catch (JSONException e) {
			isJson = false;
		}
		return isJson;
	}

	public void put(LogRecord record) throws Ks3ClientException {
		if (record != null) {
/*			Log.d(Constants.LOG_TAG, "new log: " + record.toString());
*/			DBManager.getInstance(mContext).insertLog(record.toString());
		} else {
			throw new Ks3ClientException("record can not be null");
		}
	}

	/*
	 * private void saveToSDCard(String filename, String content) throws
	 * Exception { File file = new
	 * File(Environment.getExternalStorageDirectory(), filename); OutputStream
	 * out = new FileOutputStream(file); out.write(content.getBytes());
	 * out.close(); }
	 * 
	 * private void saveToSDCard(String filename, byte[] content) throws
	 * Exception { File file = new
	 * File(Environment.getExternalStorageDirectory(), filename); OutputStream
	 * out = new FileOutputStream(file); out.write(content); out.close(); }
	 */
}
