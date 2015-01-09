package com.ksyun.ks3.services.handler;

import org.apache.http.Header;

import com.ksyun.ks3.model.transfer.RequestProgressListener;

public abstract class PutObjectResponseHandler extends Ks3HttpResponceHandler implements RequestProgressListener{

	public abstract void onTaskFailure(int statesCode, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onTaskSuccess(int statesCode, Header[] responceHeaders);
	
	public abstract void onTaskStart();
	
	public abstract void onTaskFinish();
	
	public abstract void onTaskCancel();

	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		onTaskSuccess(statesCode, responceHeaders);
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		onTaskFailure(statesCode, responceHeaders, response == null? "":new String(response), throwable);
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
	public final void onProgress(int bytesWritten, int totalSize) {}
	
	
	
	
}
