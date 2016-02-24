package com.ksyun.ks3.services.handler;

import java.io.File;

import org.apache.http.Header;

import android.util.Log;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.ObjectMetadata.Meta;
import com.ksyun.ks3.model.result.GetObjectResult;
import com.ksyun.ks3.services.LogClient;
import com.ksyun.ks3.services.LogUtil;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.Md5Utils;
import com.ksyun.loopj.android.http.FileAsyncHttpResponseHandler;

public abstract class GetObjectResponseHandler extends
		FileAsyncHttpResponseHandler {

	private String mBucketName;
	private String mObjectKey;
	private long offset = 0;

	public GetObjectResponseHandler(File file, boolean append) {
		super(file, append);
	}

	public GetObjectResponseHandler(File file, String buckName, String objectKey) {
		this(file, true);
		this.mBucketName = buckName;
		this.mObjectKey = objectKey;
	}

	public abstract void onTaskProgress(double progress);

	public abstract void onTaskStart();

	public abstract void onTaskFinish();

	public abstract void onTaskCancel();

	public abstract void onTaskSuccess(int paramInt,
			Header[] paramArrayOfHeader, GetObjectResult getObjectResult,
			StringBuffer stringBuffer);

	public abstract void onTaskFailure(int paramInt, Ks3Error error,
			Header[] paramArrayOfHeader, Throwable paramThrowable,
			File paramFile, StringBuffer stringBuffer);

	@Override
	public final void onFailure(int statesCode, Header[] paramArrayOfHeader,
			Throwable throwable, byte[] response, File paramFile) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		LogUtil.setFailureLog(statesCode, response, throwable, error, record);
		try {
			LogClient.getInstance().put(record.toString());
		} catch (Ks3ClientException e) {
			e.printStackTrace();
		}
		this.onTaskFailure(statesCode, error, paramArrayOfHeader, throwable,
				paramFile, getTraceBuffer());
	}

	@Override
	public final void onSuccess(int paramInt, Header[] paramArrayOfHeader,
			File paramFile) {
		LogUtil.setSuccessLog(paramInt, paramFile, paramArrayOfHeader, record);
		try {
			LogClient.getInstance().put(record.toString());
		} catch (Ks3ClientException e) {
			e.printStackTrace();
		}
		this.onTaskSuccess(paramInt, paramArrayOfHeader,
				parse(paramInt, paramArrayOfHeader, paramFile),
				getTraceBuffer());
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {
		bytesWritten = offset > 0 ? (int) (bytesWritten + (int) offset)
				: bytesWritten;
		totalSize = offset > 0 ? (int) (totalSize + (int) offset) : totalSize;
		double progress = Double.valueOf(totalSize > 0 ? bytesWritten * 1.0D
				/ totalSize * 100.0D : -1.0D);
		onTaskProgress(progress);
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
	public final boolean deleteTempFile() {
		return (getTempFile() != null) && (getTempFile().delete());
	}

	@Override
	protected final File getTempFile() {
		assert (this.mTempFile != null);
		return this.mTempFile;
	}

	private GetObjectResult parse(int statesCode, Header[] responceHeaders,
			File file) {
		GetObjectResult result = new GetObjectResult();
		ObjectMetadata metaData = new ObjectMetadata();

		result.getObject().setBucketName(mBucketName);
		result.getObject().setKey(mObjectKey);
		if (statesCode == 200 || statesCode == 206) {
			result.getObject().setFile(file);
			Header[] headers = responceHeaders;
			for (int i = 0; i < headers.length; i++) {
				String name = headers[i].getName();
				String value = headers[i].getValue();
				if (HttpHeaders.XKssWebsiteRedirectLocation.toString()
						.equalsIgnoreCase(name)) {
					result.getObject().setRedirectLocation(value);
				} else if (name.startsWith(ObjectMetadata.userMetaPrefix)) {
					metaData.addOrEditUserMeta(headers[i].getName(), value);
				} else {
					if (name.equalsIgnoreCase(HttpHeaders.LastModified
							.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.LastModified, value);

					} else if (name.equalsIgnoreCase(HttpHeaders.ETag
							.toString())) {

						metaData.addOrEditMeta(ObjectMetadata.Meta.Etag,
								value.replace("\"", ""));
						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentMD5,
								Md5Utils.ETag2MD5(value));

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.CacheControl
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.CacheControl, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentDisposition
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.ContentDisposition, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentEncoding
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.ContentEncoding, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentLength
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.ContentLength, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentType
									.toString())) {

						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentType,
								value);

					} else if (name.equalsIgnoreCase(Meta.Expires.toString())) {

						metaData.setExpires(value);

					}
				}
			}
			result.getObject().setObjectMetadata(metaData);
		} else if (statesCode == 304) {
			result.setIfModified(false);
		} else if (statesCode == 412) {
			result.setIfPreconditionSuccess(false);
		}
		return result;
	}

	public void setOffset(long length) {
		Log.d(Constants.LOG_TAG, "last offset =" + length);
		this.offset = length;
	}

}
