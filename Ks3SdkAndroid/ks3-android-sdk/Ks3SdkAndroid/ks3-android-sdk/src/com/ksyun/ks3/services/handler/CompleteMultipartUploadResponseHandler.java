package com.ksyun.ks3.services.handler;

import org.apache.http.Header;

import com.ksyun.ks3.model.result.CompleteMultipartUploadResult;

public abstract class CompleteMultipartUploadResponseHandler extends
		Ks3HttpResponceHandler {
	
	public abstract void onFailure(int statesCode, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,CompleteMultipartUploadResult result);
	
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		this.onSuccess(statesCode, responceHeaders, new CompleteMultipartUploadResult());
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		this.onFailure(statesCode, responceHeaders, response==null?"":new String(response), throwable);
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
