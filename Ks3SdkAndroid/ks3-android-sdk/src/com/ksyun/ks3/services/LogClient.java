package com.ksyun.ks3.services;

import org.apache.http.Header;
import com.ksyun.ks3.model.AsyncHttpRequsetParam;
import com.ksyun.ks3.model.LogRecord;
import com.ksyun.ks3.util.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.util.Log;

public class LogClient {
	private static LogClient mInstance;
	private static Object mLockObject = new Object();
	private static AsyncHttpClient client;
	private static int current_log_count = 1;
	private static int DISMISS_FREQUENCY = 1;

	public static LogClient getInstance() {
		if (null == mInstance) {
			synchronized (mLockObject) {
				if (null == mInstance) {
					mInstance = new LogClient();
					client = AsyncHttpClientFactory
							.getInstance(Ks3ClientConfiguration
									.getDefaultConfiguration());
				}
			}
		}
		return mInstance;
	}

	public void insertAndSendLog(LogRecord record) {
		if (record != null) {
			if (client != null) {
				if (current_log_count == 1) {
					sendRecord(record);
				} else {
					if (current_log_count % DISMISS_FREQUENCY == 0) {
						sendRecord(record);
					} else {
						Log.i(Constants.LOG_TAG, "log send dismiss");
					}
				}
				current_log_count++;
			} else {
				Log.i(Constants.LOG_TAG, "http client is null");
			}

		} else {
			Log.i(Constants.LOG_TAG, "log record is null");
		}

	}

	private void sendRecord(LogRecord record) {
		AsyncHttpRequsetParam params = new AsyncHttpRequsetParam(
				Constants.LOG_SERVER_URL, record.toHashMap(), null);

		client.put(null, Constants.LOG_SERVER_URL, params.getHeader(), null,
				null, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int paramInt,
							Header[] paramArrayOfHeader, byte[] paramArrayOfByte) {
						Log.i(Constants.LOG_TAG, "send log ok");
					}

					@Override
					public void onFailure(int paramInt,
							Header[] paramArrayOfHeader,
							byte[] paramArrayOfByte, Throwable paramThrowable) {
						Log.i(Constants.LOG_TAG, "send log failure");
					}
				}, null);
	}

}
