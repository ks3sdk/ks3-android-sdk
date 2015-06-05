package com.ksyun.ks3.services.handler;

import org.apache.http.Header;
import android.util.Log;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.result.CompleteMultipartUploadResult;
import com.ksyun.ks3.services.LogClient;
import com.ksyun.ks3.services.LogUtil;
import com.ksyun.ks3.util.Constants;

public abstract class CompleteMultipartUploadResponseHandler extends
		Ks3HttpResponceHandler {
	
	public abstract void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,CompleteMultipartUploadResult result);
	
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		Log.i(Constants.LOG_TAG, "CompleteMultipartUpload Request Success");
		LogUtil.setSuccessLog(statesCode, response,responceHeaders,record);
		LogClient.getInstance().insertAndSendLog(record);
		this.onSuccess(statesCode, responceHeaders, new CompleteMultipartUploadResult());
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		Log.e(Constants.LOG_TAG, "CompleteMultipartUpload Request Failed, Error Code: "+error.getErrorCode()+",Error Message:"+error.getErrorMessage());
		LogUtil.setFailureLog(statesCode, response, throwable, error,record);
		LogClient.getInstance().insertAndSendLog(record);
		this.onFailure(statesCode, error,responceHeaders, response==null?"":new String(response), throwable);
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}

	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}

	@Override
	public final void onCancel() {}
	
}
