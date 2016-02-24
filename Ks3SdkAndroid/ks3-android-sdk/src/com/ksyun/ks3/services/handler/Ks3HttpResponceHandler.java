package com.ksyun.ks3.services.handler;


import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.ksyun.loopj.android.http.AsyncHttpResponseHandler;
import com.ksyun.loopj.android.http.ResponseHandlerInterface;

public abstract class Ks3HttpResponceHandler extends AsyncHttpResponseHandler{
	@Override
	public void onSuccess(int statesCode, Header[] responceHeaders, byte[] response) {}
	
	@Override
	public void onFailure(int statesCode, Header[] responceHeaders, byte[] response, Throwable throwable) {}

	@Override
	public final void onPreProcessResponse(ResponseHandlerInterface instance,HttpResponse response) {}

	@Override
	public final void onPostProcessResponse(ResponseHandlerInterface instance,	HttpResponse response) {}

	@Override
	public final void onRetry(int retryNo) {}
}
