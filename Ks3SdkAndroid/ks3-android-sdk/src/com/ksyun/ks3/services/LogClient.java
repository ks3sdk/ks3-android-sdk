package com.ksyun.ks3.services;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.entity.StringEntity;

import com.ksyun.ks3.db.DBManager;
import com.ksyun.ks3.model.AsyncHttpRequsetParam;
import com.ksyun.ks3.model.LogRecord;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.GzipUtil;
import com.ksyun.ks3.util.NetworkUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import android.content.Context;
import android.util.Log;

public class LogClient {
	private static final long TIMER_INTERVAL = 30 * 1000;
	private static LogClient mInstance;
	private static Object mLockObject = new Object();
	private static AsyncHttpClient client;
	private static SyncHttpClient syncClient;
	private static int current_log_count = 1;
	private static int DISMISS_FREQUENCY = 1;
	private static Context mContext;
	private volatile boolean mStarted = false;
	private long interval;
	private long UPTATE_INTERVAL_WIFI_TIME = 1000 * 60 * 2;
	private long UPTATE_INTERVAL_OTHER_TIME = 1000 * 60 * 2;
	private Timer timer;

	public static LogClient getInstance() {
		if (null == mInstance) {
			synchronized (mLockObject) {
				if (null == mInstance) {
					mInstance = new LogClient();
					client = AsyncHttpClientFactory
							.getInstance(Ks3ClientConfiguration
									.getDefaultConfiguration());
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
					client = AsyncHttpClientFactory
							.getInstance(Ks3ClientConfiguration
									.getDefaultConfiguration());
					syncClient = new SyncHttpClient();
				}
			}
		}
		return mInstance;
	}

	/*
	 * public void insertAndSendLog(LogRecord record) { if (record != null) { if
	 * (client != null) { if (current_log_count == 1) { sendRecord(record); }
	 * else { if (current_log_count % DISMISS_FREQUENCY == 0) {
	 * sendRecord(record); } else { Log.i(Constants.LOG_TAG,
	 * "log send dismiss"); } } current_log_count++; } else {
	 * Log.i(Constants.LOG_TAG, "http client is null"); }
	 * 
	 * } else { Log.i(Constants.LOG_TAG, "log record is null"); }
	 * 
	 * }
	 */

	private void sendRecord(String recordsJson) {
		// AsyncHttpRequsetParam params = new AsyncHttpRequsetParam(
		// Constants.LOG_SERVER_URL, record.toHashMap(), null);
		//
		// client.put(mContext, Constants.LOG_SERVER_URL, params.getHeader(),
		// null,
		// null, new AsyncHttpResponseHandler() {
		//
		// @Override
		// public void onSuccess(int paramInt,
		// Header[] paramArrayOfHeader, byte[] paramArrayOfByte) {
		// Log.i(Constants.LOG_TAG, "send log ok");
		// }
		//
		// @Override
		// public void onFailure(int paramInt,
		// Header[] paramArrayOfHeader,
		// byte[] paramArrayOfByte, Throwable paramThrowable) {
		// Log.i(Constants.LOG_TAG, "send log failure");
		// }
		// }, null,null);
		RequestParams params = new RequestParams();
		params.setContentEncoding("gzip");
		StringEntity entity = null;
		try {
			entity = new StringEntity(recordsJson);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (entity != null) {
			syncClient.post(mContext, Constants.LOG_SERVER_URL, entity,
					"application/json", new AsyncHttpResponseHandler() {
						
						@Override
						public void onSuccess(int paramInt, Header[] paramArrayOfHeader,
								byte[] paramArrayOfByte) {
							// Delete Records
							
						}
						
						@Override
						public void onFailure(int paramInt, Header[] paramArrayOfHeader,
								byte[] paramArrayOfByte, Throwable paramThrowable) {
							// Do nothing
							
						}
					});
		}

	}

	public void start() {
		if (mStarted) {
			return;
		}

		interval = NetworkUtil.isWifiConnected(mContext) ? UPTATE_INTERVAL_WIFI_TIME
				: UPTATE_INTERVAL_OTHER_TIME;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Log.d(Constants.LOG_TAG, "send schedule");
				Log.d(Constants.LOG_TAG, "current thread id = "
						+ Thread.currentThread().getId());
				if (NetworkUtil.isNetworkAvailable(mContext)) {
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Judge the way of send
					int current_count = DBManager.getInstance(mContext)
							.queryCount();
					if (current_count > 0) {
						if (current_count > 120) {
							// divide send
						} else {
							// send all
							String jsonRecords = DBManager
									.getInstance(mContext).getAllRecords();
							sendRecord(GzipUtil.zip(jsonRecords));
						}
					} else {
						Log.d(Constants.LOG_TAG, "no record");
					}

					// LogBean logBean = DBManager.getInstance(context)
					// .fetchLogAndRemove();
					// if (null != logBean) {
					// synchronized (logs) {
					// logs.add(logBean);
					// long currentUpdateTime = System.currentTimeMillis();
					// long timeInterval = currentUpdateTime
					// - lastUpdateTime;
					// // long interval =
					// if (logs.size() >= 120 || timeInterval > interval) {
					// StringBuilder strBuilder = new StringBuilder();
					// for (LogBean log : logs) {
					// String insteadString = log.getContent().replace("+",
					// "%20");
					// try {
					// strBuilder
					// .append("uri[]=")
					// .append(URLEncoder.encode(
					// "/x.gif?"
					// + insteadString,
					// "UTF-8")).append("&");
					// } catch (UnsupportedEncodingException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// }
					// sendBlockLog(strBuilder.toString());
					// lastUpdateTime = currentUpdateTime;
					// // clear the queue
					// logs.clear();
					// }
					// }
					//
					// }
				}
			}
		}, 0, TIMER_INTERVAL);
	}

	public void stop() {
		if (!mStarted) {
			return;
		}
		if (null != timer) {
			timer.cancel();
		}
		mStarted = false;
	}

	public void put(String message) {
		Log.d(Constants.LOG_TAG, "new log: " + message);
		DBManager.getInstance(mContext).insertLog(message);
	}
}
