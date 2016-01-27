package com.ksyun.ks3.services.handler;

import org.apache.http.Header;
import android.util.Log;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.services.LogClient;
import com.ksyun.ks3.services.LogUtil;
import com.ksyun.ks3.util.Constants;

public abstract class AbortMultipartUploadResponseHandler extends
		Ks3HttpResponceHandler {

	public abstract void onFailure(int statesCode, Ks3Error error,
			Header[] responceHeaders, String response, Throwable paramThrowable, StringBuffer stringBuffer);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders, StringBuffer stringBuffer);

	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,
			byte[] response) {
		Log.i(Constants.LOG_TAG, "AbortMultipartUpload Request Success");
		LogUtil.setSuccessLog(statesCode, response, responceHeaders, record);
		try {
			LogClient.getInstance().put(record.toString());
		} catch (Ks3ClientException e) {
			e.printStackTrace();
		}
		this.onSuccess(statesCode, responceHeaders, getTraceBuffer());
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,
			byte[] response, Throwable throwable) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		Log.e(Constants.LOG_TAG,
				"AbortMultipartUpload Request Failed, Error Code: "
						+ error.getErrorCode() + ",Error Message:"
						+ error.getErrorMessage());
		LogUtil.setFailureLog(statesCode, response, throwable, error, record);
		try {
			LogClient.getInstance().put(record.toString());
		} catch (Ks3ClientException e) {
			e.printStackTrace();
		}
		this.onFailure(statesCode, error, responceHeaders,
				response == null ? "" : new String(response), throwable, getTraceBuffer());
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {
	}

	@Override
	public final void onStart() {
	}

	@Override
	public final void onFinish() {
	}

	@Override
	public final void onCancel() {
	}

}
