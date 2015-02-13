package com.ksyun.ks3.services.handler;

import java.io.File;

import org.apache.http.Header;

import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.ObjectMetadata.Meta;
import com.ksyun.ks3.model.result.GetObjectResult;
import com.ksyun.ks3.util.Md5Utils;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

public abstract class GetObjectResponseHandler extends com.ksyun.ks3.asynchttpclient.FileAsyncHttpResponseHandler{

	private String mBucketName;
	private String mObjectKey;
	
	private GetObjectResponseHandler(File file , boolean append){
		super(file, append);
	}
	
	public GetObjectResponseHandler(File file,String buckName,String objectKey) {
		this(file, false);
		this.mBucketName = buckName;
		this.mObjectKey = objectKey;
	}
	
	public abstract void onTaskProgress(double progress);
	
	public abstract void onTaskStart();
	
	public abstract void onTaskFinish();
	
	public abstract void onTaskCancel();

	public abstract void onTaskSuccess(int paramInt, Header[] paramArrayOfHeader, GetObjectResult getObjectResult);
	
	public abstract void onTaskFailure(int paramInt, Ks3Error error, Header[] paramArrayOfHeader, Throwable paramThrowable, File paramFile);
	
	 
	@Override
	public final void onFailure(int statesCode, Header[] paramArrayOfHeader,Throwable throwable,  byte[] response,File paramFile) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		this.onTaskFailure(statesCode, error,paramArrayOfHeader, throwable, paramFile);
	}

	
	@Override
	public final void onSuccess(int paramInt, Header[] paramArrayOfHeader,File paramFile) {
		this.onTaskSuccess(paramInt, paramArrayOfHeader, parse(paramInt, paramArrayOfHeader, paramFile));
	}


	@Override
	public final void onProgress(int bytesWritten, int totalSize) {
		double progress = Double.valueOf(totalSize > 0 ? bytesWritten * 1.0D / totalSize * 100.0D : -1.0D);
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
	public final boolean deleteTargetFile() {
		return (getTargetFile() != null) && (getTargetFile().delete());
	}
	
	@Override
	protected final File getTargetFile() {
		assert (this.mFile != null);
		return this.mFile;
	}
	
	private GetObjectResult parse(int statesCode, Header[] responceHeaders,File file) {
		GetObjectResult result = new GetObjectResult();
		ObjectMetadata metaData = new ObjectMetadata();
		
		result.getObject().setBucketName(mBucketName);
		result.getObject().setKey(mObjectKey);
		if(statesCode == 200 || statesCode == 206){
			result.getObject().setFile(file);
			Header[] headers = responceHeaders;
			for (int i = 0; i < headers.length; i++) {
				String name = headers[i].getName();
				String value = headers[i].getValue();
				if(HttpHeaders.XKssWebsiteRedirectLocation.toString().equalsIgnoreCase(name)){
					result.getObject().setRedirectLocation(value);
				}
				else if (name.startsWith(ObjectMetadata.userMetaPrefix)) {
					metaData.addOrEditUserMeta(headers[i].getName(),value);
				} else {
					if(name.equalsIgnoreCase(HttpHeaders.LastModified.toString())){
						
						metaData.addOrEditMeta(ObjectMetadata.Meta.LastModified, value);
						
					}else if(name.equalsIgnoreCase(HttpHeaders.ETag.toString())){
						
						metaData.addOrEditMeta(ObjectMetadata.Meta.Etag, value.replace("\"", ""));
						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentMD5, Md5Utils.ETag2MD5(value));
						
					}else if (name.equalsIgnoreCase(ObjectMetadata.Meta.CacheControl.toString())) {
						
						metaData.addOrEditMeta(ObjectMetadata.Meta.CacheControl,value);
						
					} else if (name.equalsIgnoreCase(ObjectMetadata.Meta.ContentDisposition.toString())) {
						
						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentDisposition,value);
						
					} else if (name.equalsIgnoreCase(ObjectMetadata.Meta.ContentEncoding.toString())) {
						
						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentEncoding,	value);
						
					} else if (name.equalsIgnoreCase(ObjectMetadata.Meta.ContentLength.toString())) {
						
						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentLength,value);
						
					} else if (name.equalsIgnoreCase(ObjectMetadata.Meta.ContentType.toString())) {
						
						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentType,value);
						
					} else if (name.equalsIgnoreCase(Meta.Expires.toString())) {
						
						metaData.setExpires(value);
						
					}
				}
			}
			result.getObject().setObjectMetadata(metaData);
		}else if(statesCode ==304){
			result.setIfModified(false);
		}else if(statesCode == 412){
			result.setIfPreconditionSuccess(false);
		}
		return result;
	}
	
}
