package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;

import android.util.Log;

public abstract class RangeFileAsyncHttpResponseHandler extends FileAsyncHttpResponseHandler
{

	private static final String LOG_TAG = "RangeFileAsyncHttpResponseHandler";
	private long current = 0L;
	private boolean append = false;

	public RangeFileAsyncHttpResponseHandler(File file)
	{

		super(file);
	}

	@Override
	public void sendResponseMessage(HttpResponse response) throws IOException
	{

		if (!Thread.currentThread().isInterrupted()) {
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() == 416)
			{
				if (!Thread.currentThread().isInterrupted())
					sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), null);
			} else if (status.getStatusCode() >= 300) {
				if (!Thread.currentThread().isInterrupted())
					sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), null, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
			}
			else if (!Thread.currentThread().isInterrupted()) {
				Header header = response.getFirstHeader("Content-Range");
				if (header == null) {
					this.append = false;
					this.current = 0L;
				} else {
					Log.v("RangeFileAsyncHttpResponseHandler", "Content-Range: " + header.getValue());
				}
				sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), getResponseData(response.getEntity(),200));
			}
		}
	}

	@Override
	protected byte[] getResponseData(HttpEntity entity, int statusCode)
			throws IOException
	{

		if (entity != null) {
			InputStream instream = entity.getContent();
			long contentLength = entity.getContentLength() + this.current;
			FileOutputStream buffer = new FileOutputStream(getTempFile(), this.append);
			if (instream != null) {
				try {
					byte[] tmp = new byte[4096];
					int l;
					while ((this.current < contentLength) && ((l = instream.read(tmp)) != -1) && (!Thread.currentThread().isInterrupted())) {
						this.current += l;
						buffer.write(tmp, 0, l);
						sendProgressMessage((int) this.current, (int) contentLength);
					}
				} finally {
					instream.close();
					buffer.flush();
					buffer.close();
				}
			}
		}
		return null;
	}

	public void updateRequestHeaders(HttpUriRequest uriRequest) {

		if ((this.mTempFile.exists()) && (this.mTempFile.canWrite()))
			this.current = this.mTempFile.length();
		if (this.current > 0L) {
			this.append = true;
			uriRequest.setHeader("Range", "bytes=" + this.current + "-");
		}
	}
}
