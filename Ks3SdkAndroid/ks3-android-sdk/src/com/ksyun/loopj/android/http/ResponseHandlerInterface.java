package com.ksyun.loopj.android.http;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.ksyun.ks3.model.LogRecord;

public abstract interface ResponseHandlerInterface
{

	public abstract void sendResponseMessage(HttpResponse paramHttpResponse)
			throws IOException;

	public abstract void sendStartMessage();

	public abstract void sendFinishMessage();

	public abstract void sendProgressMessage(int paramInt1, int paramInt2);

	public abstract void sendCancelMessage();

	public abstract void sendSuccessMessage(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte);

	public abstract void sendFailureMessage(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte, Throwable paramThrowable);

	public abstract void sendRetryMessage(int paramInt);

	public abstract URI getRequestURI();

	public abstract Header[] getRequestHeaders();

	public abstract void setRequestURI(URI paramURI);

	public abstract void setRequestHeaders(Header[] paramArrayOfHeader);

	public abstract void setUseSynchronousMode(boolean paramBoolean);

	public abstract boolean getUseSynchronousMode();

	public abstract void onPreProcessResponse(ResponseHandlerInterface paramResponseHandlerInterface, HttpResponse paramHttpResponse);

	public abstract void onPostProcessResponse(ResponseHandlerInterface paramResponseHandlerInterface, HttpResponse paramHttpResponse);

	public abstract void sendLogRecordMessage(LogRecord record);

	public abstract void sendTraceBuffer(StringBuffer traceBuffer);
}
