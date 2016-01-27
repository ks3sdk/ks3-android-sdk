package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import android.content.Context;
import android.util.Log;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class FileAsyncHttpResponseHandler extends
		AsyncHttpResponseHandler {
	public File mTempFile;
	protected final boolean append;
	public File mOriginFile;
	private static final String LOG_TAG = "FileAsyncHttpResponseHandler";

	public FileAsyncHttpResponseHandler(File file) {
		this(file, false);
	}

	public FileAsyncHttpResponseHandler(File file, boolean append) {
		// AssertUtils
		// .asserts(file != null,
		// "File passed into FileAsyncHttpResponseHandler constructor must not be null");
		// this.mFile = file;
		// this.append = append;

		// modified for download logic, 2016/01/21
		this.mOriginFile = file;
		this.mTempFile = createTempFile(file);
		this.append = append;
	}

	// modified for download logic, 2016/01/21
	private File createTempFile(File file) {
		File tempFile = new File(file.getParent(), file.getName() + ".temp");
		return tempFile;
	}

	// public FileAsyncHttpResponseHandler(Context context) {
	// this.mFile = getTemporaryFile(context);
	// this.append = false;
	// }

	public boolean deleteTempFile() {
		return (getTempFile() != null) && (getTempFile().delete());
	}

	// protected File getTemporaryFile(Context context) {
	// // AssertUtils.asserts(context != null,
	// // "Tried creating temporary file without having Context");
	// try {
	// assert (context != null);
	// return File.createTempFile("temp_", "_handled",
	// context.getCacheDir());
	// } catch (IOException e) {
	// Log.e("FileAsyncHttpResponseHandler",
	// "Cannot create temporary file", e);
	// }
	// return null;
	// }

	protected File getTempFile() {
		assert (this.mTempFile != null);
		return this.mTempFile;
	}

	public final void onFailure(int statusCode, Header[] headers,
			byte[] responseBytes, Throwable throwable) {
		onFailure(statusCode, headers, throwable, responseBytes, getTempFile());
	}

	public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader,
			Throwable paramThrowable, byte[] responseBytes, File paramFile);

	public final void onSuccess(int statusCode, Header[] headers,
			byte[] responseBytes) {
		// modified for download logic, 2016/01/21
		onSuccess(statusCode, headers, renameTempFile());
	}

	// modified for download logic, 2016/01/21
	private File renameTempFile() {
		if (!this.mTempFile.exists()) {
			try {
				throw new Ks3ClientException(
						"download complete, but target temp file is not exist");
			} catch (Ks3ClientException e) {
				e.printStackTrace();
				return null;
			}
		}
		if (this.mOriginFile.exists()) {
			try {
				throw new Ks3ClientException(
						"download complete, but origin file is existing ,caused renameing temp file failure");
			} catch (Ks3ClientException e) {
				e.printStackTrace();
				return null;
			}
		}
		mTempFile.renameTo(mOriginFile);
		return mTempFile;
	}

	public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader,
			File paramFile);

	// modified for step download , 2016/01/20
	protected byte[] getResponseData(HttpEntity entity, int statusCode)
			throws IOException {
		if (entity != null) {
			if (statusCode >= 300) {
				// in failure situation, do not write response into file
				return super.getResponseData(entity, statusCode);
			} else {
				if (traceBuffer != null) {
					traceBuffer
							.append("Step ==>Writting httpclient response data into target File")
							.append("\n");
				}
				InputStream instream = entity.getContent();
				long contentLength = entity.getContentLength();
				FileOutputStream buffer = new FileOutputStream(getTempFile(),
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
		}
		return null;
	}
}
