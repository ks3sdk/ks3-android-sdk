package com.ksyun.ks3.services.handler;

import org.apache.http.Header;
import android.util.Log;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.transfer.RequestProgressListener;
import com.ksyun.ks3.services.LogClient;
import com.ksyun.ks3.services.LogUtil;
import com.ksyun.ks3.util.Constants;

public abstract class PutObjectResponseHandler extends Ks3HttpResponceHandler
		implements RequestProgressListener {

	public abstract void onTaskFailure(int statesCode, Ks3Error error,
			Header[] responceHeaders, String response, Throwable paramThrowable);

	public abstract void onTaskSuccess(int statesCode, Header[] responceHeaders);

	public abstract void onTaskStart();

	public abstract void onTaskFinish();

	public abstract void onTaskCancel();

	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,
			byte[] response) {
		Log.i(Constants.LOG_TAG, "PutObject Request Success");
		LogUtil.setSuccessLog(statesCode, response,responceHeaders,record);
		LogClient.getInstance().insertAndSendLog(record);
		onTaskSuccess(statesCode, responceHeaders);
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,
			byte[] response, Throwable throwable) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		Log.e(Constants.LOG_TAG, "PutObject Request Failed, Error Code: "+error.getErrorCode()+",Error Message:"+error.getErrorMessage());
		LogUtil.setFailureLog(statesCode, response, throwable, error,record);
		LogClient.getInstance().insertAndSendLog(record);
		onTaskFailure(statesCode, error, responceHeaders, response == null ? ""
				: new String(response), throwable);
	}

	@Override
	public final void onStart() {
		onTaskStart();
	}

	@Override
	public final void onFinish() {
		onTaskFinish();
	}

	@Override
	public final void onCancel() {
		onTaskCancel();
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {
	}


}
