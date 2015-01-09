package com.ksyun.ks3.services.handler;

import org.apache.http.Header;

public abstract class AbortMultipartUploadResponseHandler extends
		Ks3HttpResponceHandler {

	public abstract void onFailure(int statesCode, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders);
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		this.onSuccess(statesCode, responceHeaders);
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
