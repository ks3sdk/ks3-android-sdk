package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class FileAsyncHttpResponseHandler extends
		AsyncHttpResponseHandler {
	protected final File mFile;
	protected final boolean append;
	private static final String LOG_TAG = "FileAsyncHttpResponseHandler";

	public FileAsyncHttpResponseHandler(File file) {
		this(file, false);
	}

	public FileAsyncHttpResponseHandler(File file, boolean append) {
//		AssertUtils
//				.asserts(file != null,
//						"File passed into FileAsyncHttpResponseHandler constructor must not be null");
		this.mFile = file;
		this.append = append;
	}

	public FileAsyncHttpResponseHandler(Context context) {
		this.mFile = getTemporaryFile(context);
		this.append = false;
	}

	public boolean deleteTargetFile() {
		return (getTargetFile() != null) && (getTargetFile().delete());
	}

	protected File getTemporaryFile(Context context) {
//		AssertUtils.asserts(context != null,
//				"Tried creating temporary file without having Context");
		try {
			assert (context != null);
			return File.createTempFile("temp_", "_handled",
					context.getCacheDir());
		} catch (IOException e) {
			Log.e("FileAsyncHttpResponseHandler",
					"Cannot create temporary file", e);
		}
		return null;
	}

	protected File getTargetFile() {
		assert (this.mFile != null);
		return this.mFile;
	}

	public final void onFailure(int statusCode, Header[] headers,
			byte[] responseBytes, Throwable throwable) {
		onFailure(statusCode, headers, throwable, responseBytes,getTargetFile());
	}

	public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader,
			Throwable paramThrowable, byte[] responseBytes, File paramFile);

	public final void onSuccess(int statusCode, Header[] headers,
			byte[] responseBytes) {
		onSuccess(statusCode, headers, getTargetFile());
	}

	public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader,
			File paramFile);

	protected byte[] getResponseData(HttpEntity entity) throws IOException {
		if (entity != null) {
			InputStream instream = entity.getContent();
			long contentLength = entity.getContentLength();
			FileOutputStream buffer = new FileOutputStream(getTargetFile(),
					this.append);
			if (instream != null) {
				try {
					byte[] tmp = new byte[4096];
					int count = 0;
					int l;
					while (((l = instream.read(tmp)) != -1)
							&& (!Thread.currentThread().isInterrupted())) {
						count += l;
						buffer.write(tmp, 0, l);
						sendProgressMessage(count, (int) contentLength);
					}
				} finally {
					AsyncHttpClient.silentCloseInputStream(instream);
					buffer.flush();
					AsyncHttpClient.silentCloseOutputStream(buffer);
				}
			}
		}
		return null;
	}
}
