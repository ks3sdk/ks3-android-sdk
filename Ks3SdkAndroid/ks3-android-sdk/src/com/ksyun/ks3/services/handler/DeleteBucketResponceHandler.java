package com.ksyun.ks3.services.handler;

import org.apache.http.Header;

public abstract class DeleteBucketResponceHandler extends Ks3HttpResponceHandler {
	
	public abstract void onFailure(int statesCode, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders);
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		onSuccess(statesCode, responceHeaders);
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		onFailure(statesCode, responceHeaders, response == null ?"":new String(response), throwable);
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}

	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}
}
