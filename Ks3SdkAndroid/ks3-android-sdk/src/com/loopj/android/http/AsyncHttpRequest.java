package com.loopj.android.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.ksyun.ks3.util.Constants;

public class AsyncHttpRequest
		implements Runnable
{

	private final AbstractHttpClient client;
	private final HttpContext context;
	private final HttpUriRequest request;
	private final ResponseHandlerInterface responseHandler;
	private int executionCount;
	private boolean isCancelled;
	private boolean cancelIsNotified;
	private boolean isFinished;
	private boolean isRequestPreProcessed;

	public AsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request, ResponseHandlerInterface responseHandler)
	{

		this.client = client;
		this.context = context;
		this.request = request;
		this.responseHandler = responseHandler;
	}

	public void onPreProcessRequest(AsyncHttpRequest request)
	{

	}

	public void onPostProcessRequest(AsyncHttpRequest request) throws IOException
	{

	}

	@Override
	public void run()
	{

		if (isCancelled()) {
			return;
		}

		if (!this.isRequestPreProcessed) {
			this.isRequestPreProcessed = true;
			onPreProcessRequest(this);
		}

		if (isCancelled()) {
			return;
		}

		if (this.responseHandler != null) {
			this.responseHandler.sendStartMessage();
		}

		if (isCancelled()) {
			return;
		}
		try
		{
			makeRequestWithRetries();
		} catch (IOException e) {
			if ((!isCancelled()) && (this.responseHandler != null))
				this.responseHandler.sendFailureMessage(0, null, null, e);
			else {
				Log.e(Constants.LOG_TAG, "makeRequestWithRetries returned error, but handler is null", e);
			}
		} finally {
			// modified , fixed steam close problem
			if (this.request != null && this.request instanceof HttpEntityEnclosingRequestBase) {
				try {
					HttpEntity entity = ((HttpEntityEnclosingRequestBase) this.request).getEntity();
					if (entity != null && entity.isStreaming())
						entity.consumeContent();
				} catch (IOException e) {
					Log.e(Constants.LOG_TAG, "consume stream entity failed , cause exception :" + e);
				}
			}
		}

		if (isCancelled()) {
			return;
		}

		if (this.responseHandler != null) {
			this.responseHandler.sendFinishMessage();
		}

		if (isCancelled()) {
			return;
		}

		try {
			onPostProcessRequest(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.isFinished = true;
	}

	private void makeRequest() throws IOException {

		if (isCancelled()) {
			return;
		}

		if (this.request.getURI().getScheme() == null)
		{
			throw new MalformedURLException("No valid URI scheme was provided");
		}

		HttpResponse response = this.client.execute(this.request, this.context);

		if ((isCancelled()) || (this.responseHandler == null)) {
			return;
		}

		this.responseHandler.onPreProcessResponse(this.responseHandler, response);

		if (isCancelled()) {
			return;
		}

		this.responseHandler.sendResponseMessage(response);

		if (isCancelled()) {
			return;
		}

		this.responseHandler.onPostProcessResponse(this.responseHandler, response);
	}

	private void makeRequestWithRetries() throws IOException {

		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = this.client.getHttpRequestRetryHandler();
		try {
			while (retry) {
				try {
					makeRequest();
					return;
				} catch (UnknownHostException e)
				{
					cause = new IOException("UnknownHostException exception: " + e.getMessage());
					retry = (this.executionCount > 0) && (retryHandler.retryRequest(cause, ++this.executionCount, this.context));
				} catch (NullPointerException e)
				{
					cause = new IOException("NPE in HttpClient: " + e.getMessage());
					retry = retryHandler.retryRequest(cause, ++this.executionCount, this.context);
				} catch (IOException e) {
					if (isCancelled())
					{
						return;
					}
					cause = e;
					retry = retryHandler.retryRequest(cause, ++this.executionCount, this.context);
				}
				if ((retry) && (this.responseHandler != null))
					this.responseHandler.sendRetryMessage(this.executionCount);
			}
		} catch (Exception e)
		{
			Log.e("AsyncHttpRequest", "Unhandled exception origin cause", e);
			cause = new IOException("Unhandled exception: " + e.getMessage());
		}

		throw cause;
	}

	public boolean isCancelled() {

		if (this.isCancelled) {
			sendCancelNotification();
		}
		return this.isCancelled;
	}

	private synchronized void sendCancelNotification() {

		if ((!this.isFinished) && (this.isCancelled) && (!this.cancelIsNotified)) {
			this.cancelIsNotified = true;
			if (this.responseHandler != null)
				this.responseHandler.sendCancelMessage();
		}
	}

	public boolean isDone() {

		return (isCancelled()) || (this.isFinished);
	}

	public boolean cancel(boolean mayInterruptIfRunning) {

		this.isCancelled = true;
		this.request.abort();
		return isCancelled();
	}

}
