package com.loopj.android.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.ByteArrayBuffer;

import com.ksyun.ks3.model.LogRecord;

public abstract class AsyncHttpResponseHandler implements
		ResponseHandlerInterface {
	protected static final int SUCCESS_MESSAGE = 0;
	protected static final int FAILURE_MESSAGE = 1;
	protected static final int START_MESSAGE = 2;
	protected static final int FINISH_MESSAGE = 3;
	protected static final int PROGRESS_MESSAGE = 4;
	protected static final int RETRY_MESSAGE = 5;
	protected static final int CANCEL_MESSAGE = 6;
	protected static final int BUFFER_SIZE = 4096;
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String UTF8_BOM = "﻿";
	private String responseCharset = "UTF-8";
	private Handler handler;
	private boolean useSynchronousMode;
	private URI requestURI = null;
	private Header[] requestHeaders = null;
	private Looper looper = null;
	public LogRecord record = new LogRecord();

	public URI getRequestURI() {
		return this.requestURI;
	}

	public Header[] getRequestHeaders() {
		return this.requestHeaders;
	}

	public void setRequestURI(URI requestURI) {
		this.requestURI = requestURI;
	}

	public void setRequestHeaders(Header[] requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public boolean getUseSynchronousMode() {
		return this.useSynchronousMode;
	}

	public void setUseSynchronousMode(boolean sync) {
		if ((!sync) && (this.looper == null)) {
			sync = true;
			Log.w("AsyncHttpResponseHandler",
					"Current thread has not called Looper.prepare(). Forcing synchronous mode.");
		}

		if ((!sync) && (this.handler == null)) {
			this.handler = new ResponderHandler(this, this.looper);
		} else if ((sync) && (this.handler != null)) {
			this.handler = null;
		}

		this.useSynchronousMode = sync;
	}

	public void setCharset(String charset) {
		this.responseCharset = charset;
	}

	public String getCharset() {
		return this.responseCharset == null ? "UTF-8" : this.responseCharset;
	}

	public AsyncHttpResponseHandler() {
		this(null);
	}

	public AsyncHttpResponseHandler(Looper looper) {
		this.looper = (looper == null ? Looper.myLooper() : looper);

		setUseSynchronousMode(false);
	}

	public void onProgress(int bytesWritten, int totalSize) {
		Log.v("AsyncHttpResponseHandler",
				String.format(
						"Progress %d from %d (%2.0f%%)",
						new Object[] {
								Integer.valueOf(bytesWritten),
								Integer.valueOf(totalSize),
								Double.valueOf(totalSize > 0 ? bytesWritten
										* 1.0D / totalSize * 100.0D : -1.0D) }));
	}

	public void onStart() {
	}

	public void onFinish() {
	}

	public void onPreProcessResponse(ResponseHandlerInterface instance,
			HttpResponse response) {
	}

	public void onPostProcessResponse(ResponseHandlerInterface instance,
			HttpResponse response) {
	}

	public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader,
			byte[] paramArrayOfByte);

	public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader,
			byte[] paramArrayOfByte, Throwable paramThrowable);

	public void onRetry(int retryNo) {
		Log.d("AsyncHttpResponseHandler",
				String.format("Request retry no. %d",
						new Object[] { Integer.valueOf(retryNo) }));
	}

	public void onCancel() {
		Log.d("AsyncHttpResponseHandler", "Request got cancelled");
	}

	public final void sendProgressMessage(int bytesWritten, int bytesTotal) {
		sendMessage(obtainMessage(
				4,
				new Object[] { Integer.valueOf(bytesWritten),
						Integer.valueOf(bytesTotal) }));
	}

	public final void sendSuccessMessage(int statusCode, Header[] headers,
			byte[] responseBytes) {
		sendMessage(obtainMessage(0, new Object[] {
				Integer.valueOf(statusCode), headers, responseBytes }));
	}

	public final void sendFailureMessage(int statusCode, Header[] headers,
			byte[] responseBody, Throwable throwable) {
		sendMessage(obtainMessage(1, new Object[] {
				Integer.valueOf(statusCode), headers, responseBody, throwable }));
	}

	public final void sendStartMessage() {
		sendMessage(obtainMessage(2, null));
	}

	public final void sendFinishMessage() {
		sendMessage(obtainMessage(3, null));
	}

	public final void sendRetryMessage(int retryNo) {
		sendMessage(obtainMessage(5, new Object[] { Integer.valueOf(retryNo) }));
	}

	public final void sendCancelMessage() {
		sendMessage(obtainMessage(6, null));
	}

	protected void handleMessage(Message message) {
		Object[] response;
		switch (message.what) {
		case 0:
			response = (Object[]) (Object[]) message.obj;
			if ((response != null) && (response.length >= 3))
				onSuccess(((Integer) response[0]).intValue(),
						(Header[]) (Header[]) response[1],
						(byte[]) (byte[]) response[2]);
			else {
				Log.e("AsyncHttpResponseHandler",
						"SUCCESS_MESSAGE didn't got enough params");
			}
			break;
		case 1:
			response = (Object[]) (Object[]) message.obj;
			if ((response != null) && (response.length >= 4))
				onFailure(((Integer) response[0]).intValue(),
						(Header[]) (Header[]) response[1],
						(byte[]) (byte[]) response[2], (Throwable) response[3]);
			else {
				Log.e("AsyncHttpResponseHandler",
						"FAILURE_MESSAGE didn't got enough params");
			}
			break;
		case 2:
			onStart();
			break;
		case 3:
			onFinish();
			break;
		case 4:
			response = (Object[]) (Object[]) message.obj;
			if ((response != null) && (response.length >= 2))
				try {
					onProgress(((Integer) response[0]).intValue(),
							((Integer) response[1]).intValue());
				} catch (Throwable t) {
					Log.e("AsyncHttpResponseHandler",
							"custom onProgress contains an error", t);
				}
			else {
				Log.e("AsyncHttpResponseHandler",
						"PROGRESS_MESSAGE didn't got enough params");
			}
			break;
		case 5:
			response = (Object[]) (Object[]) message.obj;
			if ((response != null) && (response.length == 1))
				onRetry(((Integer) response[0]).intValue());
			else {
				Log.e("AsyncHttpResponseHandler",
						"RETRY_MESSAGE didn't get enough params");
			}
			break;
		case 6:
			onCancel();
		}
	}

	protected void sendMessage(Message msg) {
		if ((getUseSynchronousMode()) || (this.handler == null)) {
			handleMessage(msg);
		} else if (!Thread.currentThread().isInterrupted()) {
			AssertUtils.asserts(this.handler != null,
					"handler should not be null!");
			this.handler.sendMessage(msg);
		}
	}

	protected void postRunnable(Runnable runnable) {
		if (runnable != null)
			if ((getUseSynchronousMode()) || (this.handler == null)) {
				runnable.run();
			} else {
				AssertUtils.asserts(this.handler != null,
						"handler should not be null!");
				this.handler.post(runnable);
			}
	}

	protected Message obtainMessage(int responseMessageId,
			Object responseMessageData) {
		return Message.obtain(this.handler, responseMessageId,
				responseMessageData);
	}

	@Override
	public void sendLogRecordMessage(LogRecord pass_record) {
		record.copyRecord(pass_record);
		pass_record = null;
	}

	public void sendResponseMessage(HttpResponse response) throws IOException {
		if (!Thread.currentThread().isInterrupted()) {
			StatusLine status = response.getStatusLine();

			byte[] responseBody = getResponseData(response.getEntity());

			if (!Thread.currentThread().isInterrupted())
				if (status.getStatusCode() >= 300)
					sendFailureMessage(status.getStatusCode(),
							response.getAllHeaders(), responseBody,
							new HttpResponseException(status.getStatusCode(),
									status.getReasonPhrase()));
				else
					sendSuccessMessage(status.getStatusCode(),
							response.getAllHeaders(), responseBody);
		}
	}

	byte[] getResponseData(HttpEntity entity) throws IOException {
		byte[] responseBody = null;
		if (entity != null) {
			InputStream instream = entity.getContent();
			if (instream != null) {
				long contentLength = entity.getContentLength();
				if (contentLength > 2147483647L) {
					throw new IllegalArgumentException(
							"HTTP entity too large to be buffered in memory");
				}
				int buffersize = contentLength <= 0L ? 4096
						: (int) contentLength;
				try {
					ByteArrayBuffer buffer = new ByteArrayBuffer(buffersize);
					try {
						byte[] tmp = new byte[4096];
						int count = 0;
						int l;
						while (((l = instream.read(tmp)) != -1)
								&& (!Thread.currentThread().isInterrupted())) {
							count += l;
							buffer.append(tmp, 0, l);
							sendProgressMessage(count,
									(int) (contentLength <= 0L ? 1L
											: contentLength));
						}
					} finally {
						AsyncHttpClient.silentCloseInputStream(instream);
						AsyncHttpClient.endEntityViaReflection(entity);
					}
					responseBody = buffer.toByteArray();
				} catch (OutOfMemoryError e) {
					System.gc();
					throw new IOException(
							"File too large to fit into available memory");
				}
			}
		}
		return responseBody;
	}

	private static class ResponderHandler extends Handler {
		private final AsyncHttpResponseHandler mResponder;

		ResponderHandler(AsyncHttpResponseHandler mResponder, Looper looper) {
			super();
			this.mResponder = mResponder;
		}

		public void handleMessage(Message msg) {
			this.mResponder.handleMessage(msg);
		}
	}
}
