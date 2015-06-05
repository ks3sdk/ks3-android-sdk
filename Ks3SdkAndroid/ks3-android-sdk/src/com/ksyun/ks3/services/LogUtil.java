package com.ksyun.ks3.services;

import org.apache.http.Header;
import android.util.Log;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.LogRecord;
import com.ksyun.ks3.util.Constants;

public class LogUtil {

	public static void setSuccessLog(int statesCode, byte[] response,
			Header[] responceHeaders, LogRecord record) {
		setSuccessRequestId(responceHeaders, record);
		setCommonLog(statesCode, response, record);
	}

	public static void setSuccessRequestId(Header[] responceHeaders,
			LogRecord record) {
		for (Header header : responceHeaders) {
			if (header.getName().equals(HttpHeaders.RequestId.toString())) {
				record.setRequestId(header.getValue());
			}
		}
	}

	public static void setFailureLog(int statesCode, byte[] response,
			Throwable throwable, Ks3Error error, LogRecord record) {
		setCommonLog(statesCode, response, record);
		setResponceSize(response, record);
		setRecordError(error, throwable, record);
	}

	public static void setCommonLog(int statesCode, byte[] response,
			LogRecord record) {
		setClientState(statesCode, record);
		setRecordTime(record);
		setResponceSize(response, record);
	}

	public static void setRecordError(Ks3Error error, Throwable throwable,
			LogRecord record) {
		if (error.getErrorCode() != 100) {
			record.setError("code = " + error.getErrorCode() + "& message = "
					+ error.getKs3ServerError().getServerErrorMessage());
			record.setRequestId(error.getKs3ServerError()
					.getServerErrorRequsetId());
		} else {
			record.setError("code = " + Ks3Error.ERROR_CODE_CLIENT_ERROR
					+ ",error =" + throwable.getMessage());
		}
	}

	public static void setRecordTime(LogRecord record) {
		if (record != null) {
			long send_complete_time = System.currentTimeMillis();
			record.setSend_complete_time(send_complete_time);
		} else {
			Log.i(Constants.LOG_TAG, "record is null");
		}
	}

	public static void setClientState(int statesCode, LogRecord record) {
		if (record != null) {
			switch (statesCode) {
			case 0:
				record.setClient_state("failure, client error");
				break;
			case 200:
				record.setClient_state("success");
				break;
			default:
				record.setClient_state("failure, server response error");
				break;
			}
		}
	}

	public static void setResponceSize(byte[] response, LogRecord record) {
		if (record != null) {
			if (response != null) {
				record.setResponce_size("" + response.length);
			}
		}
	}

}
