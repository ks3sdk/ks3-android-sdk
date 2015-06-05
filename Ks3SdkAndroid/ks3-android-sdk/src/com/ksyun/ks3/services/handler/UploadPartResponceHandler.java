package com.ksyun.ks3.services.handler;

import org.apache.http.Header;
import android.util.Log;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.transfer.RequestProgressListener;
import com.ksyun.ks3.services.LogClient;
import com.ksyun.ks3.services.LogUtil;
import com.ksyun.ks3.util.Constants;

public abstract class UploadPartResponceHandler extends Ks3HttpResponceHandler implements RequestProgressListener{

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,PartETag result);

	public abstract void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders,String response, Throwable throwable);

	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		Log.i(Constants.LOG_TAG, "UploadPart Request Success");
		LogUtil.setSuccessLog(statesCode, response,responceHeaders,record);
		LogClient.getInstance().insertAndSendLog(record);
		onSuccess(statesCode, responceHeaders, parse(responceHeaders));
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		Log.e(Constants.LOG_TAG, "UploadPartRequest Failed, Error Code: "+error.getErrorCode()+",Error Message:"+error.getErrorMessage());
		LogUtil.setFailureLog(statesCode, response, throwable, error,record);
		LogClient.getInstance().insertAndSendLog(record);
		onFailure(statesCode,error, responceHeaders, response == null?"":new String(response), throwable);
	}
	
	
	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}
	
	@Override
	public final void onCancel() {}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}
	
	private PartETag parse(Header[] responceHeaders) {
		PartETag result = new PartETag();
		for (int i = 0; i < responceHeaders.length; i++) {
			Header header = responceHeaders[i];
			if (header.getName().equals(HttpHeaders.ETag.toString())) {
				result.seteTag(header.getValue());
			}
		}
		return result;
	}	
	
}
