package com.ks3.demo.main;

import java.io.File;
import java.io.Serializable;

import com.ksyun.ks3.services.request.UploadPartRequest;

/**
 *
 * UploadPartRequest工厂，用于分块上传操作
 *
 */
public class UploadPartRequestFactory implements Serializable{
	private static final long serialVersionUID = 1L;
	private String bucketName;
	private String key;
	private String uploadId;
	private File file;
	public int partNumber = 1;
	private long offset = 0L;
	private long remainingBytes;
	public long partSize ;
	private long uploadedSize;
	
	public UploadPartRequestFactory(String bucketName ,String objectKey ,  String uploadId , File file , long partSize ) {
		this.uploadId = uploadId;
		this.bucketName = bucketName;
		this.key = objectKey;
		this.file = file;
		this.remainingBytes = file.length();
		this.partSize = partSize;
		this.partNumber = 1;
		this.uploadedSize = 0;
	}

	public synchronized boolean hasMoreRequests() {
		return this.remainingBytes > 0L;
	}

	// 计算下一个UploadPartRequest配置参数，并返回对象
	public synchronized UploadPartRequest getNextUploadPartRequest() {
		boolean bool = this.remainingBytes - this.partSize <= 0L;
		UploadPartRequest localUploadPartRequest = null;
		uploadedSize = file.length() - this.remainingBytes ;
		localUploadPartRequest = new UploadPartRequest(bucketName, key,uploadId, file, offset, partNumber++, partSize);
		this.offset += partSize;
		this.remainingBytes -= partSize;
		localUploadPartRequest.setLastPart(bool);
		return localUploadPartRequest;
	}	
	
	
	public synchronized long getUploadedSize (){
		return this.uploadedSize;
	}
	
	public synchronized File getFile(){
		return this.file;
	}
	
	public synchronized String getBucketName(){
		return this.bucketName;
	}
	
	public synchronized String getUploadId(){
		return this.uploadId;
	}
	public synchronized String getObjectKey() {
		return this.key;
	}
	
}
