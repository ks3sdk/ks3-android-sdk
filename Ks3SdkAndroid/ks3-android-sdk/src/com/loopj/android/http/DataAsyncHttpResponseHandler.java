package com.loopj.android.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;

import android.os.Message;
import android.util.Log;

public abstract class DataAsyncHttpResponseHandler extends AsyncHttpResponseHandler
{

	private static final String LOG_TAG = "DataAsyncHttpResponseHandler";
	protected static final int PROGRESS_DATA_MESSAGE = 6;

	public void onProgressData(byte[] responseBody)
	{

		Log.d("DataAsyncHttpResponseHandler", "onProgressData(byte[]) was not overriden, but callback was received");
	}

	public final void sendProgressDataMessage(byte[] responseBytes)
	{

		sendMessage(obtainMessage(6, new Object[] { responseBytes }));
	}

	@Override
	protected void handleMessage(Message message)
	{

		super.handleMessage(message);

		switch (message.what) {
		case 6:
			Object[] response = (Object[]) message.obj;
			if ((response != null) && (response.length >= 1))
				try {
					onProgressData((byte[]) response[0]);
				} catch (Throwable t) {
					Log.e("DataAsyncHttpResponseHandler", "custom onProgressData contains an error", t);
				}
			else
				Log.e("DataAsyncHttpResponseHandler", "PROGRESS_DATA_MESSAGE didn't got enough params");
		}
	}

	@Override
	byte[] getResponseData(HttpEntity entity, int statusCode)
			throws IOException
	{

		byte[] responseBody = null;
		if (entity != null) {
			InputStream instream = entity.getContent();
			if (instream != null) {
				long contentLength = entity.getContentLength();
				if (contentLength > 2147483647L) {
					throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
				}
				if (contentLength < 0L)
					contentLength = 4096L;
				try
				{
					ByteArrayBuffer buffer = new ByteArrayBuffer((int) contentLength);
					try {
						byte[] tmp = new byte[4096];
						int l;
						while (((l = instream.read(tmp)) != -1) && (!Thread.currentThread().isInterrupted())) {
							buffer.append(tmp, 0, l);
							sendProgressDataMessage(copyOfRange(tmp, 0, l));
						}
					} finally {
						AsyncHttpClient.silentCloseInputStream(instream);
					}
					responseBody = buffer.toByteArray();
				} catch (OutOfMemoryError e) {
					System.gc();
					throw new IOException("File too large to fit into available memory");
				}
			}
		}
		return responseBody;
	}

	public static byte[] copyOfRange(byte[] original, int start, int end)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException, NullPointerException
	{

		if (start > end) {
			throw new IllegalArgumentException();
		}
		int originalLength = original.length;
		if ((start < 0) || (start > originalLength)) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int resultLength = end - start;
		int copyLength = Math.min(resultLength, originalLength - start);
		byte[] result = new byte[resultLength];
		System.arraycopy(original, start, result, 0, copyLength);
		return result;
	}
}
