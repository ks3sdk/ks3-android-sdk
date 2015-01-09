package com.ksyun.ks3.services.request;

import java.io.File;


public class UploadPartRequestFactory {
	private String bucketName;
	private String key;
	private String uploadId;
	private long optimalPartSize;
	private File file;
	private PutObjectRequest putObjectRequest;
	public int partNumber = 1;
	private long offset = 0L;
	private long remainingBytes;
	public long allsSize;

	public UploadPartRequestFactory(String bucketname, String objectKey,
			File mFile, String multipartUoloadId, long optimalPartSize,
			int beginPartNum) {
		this.uploadId = multipartUoloadId;
		this.optimalPartSize = optimalPartSize;
		this.bucketName = bucketname;
		this.key = objectKey;
		this.file = mFile;
		this.remainingBytes = mFile.length();
		this.allsSize = ((int)remainingBytes/ Math.min(this.optimalPartSize, this.remainingBytes))+1;
		this.partNumber = beginPartNum;
	}

	public synchronized boolean hasMoreRequests() {
		return this.remainingBytes > 0L;
	}

	public synchronized UploadPartRequest getNextUploadPartRequest() {
		long partsize = Math.min(this.optimalPartSize, this.remainingBytes);
		boolean bool = this.remainingBytes - partsize <= 0L;
		UploadPartRequest localUploadPartRequest = null;
		localUploadPartRequest = new UploadPartRequest(bucketName, key,
				uploadId, file, offset, partNumber++, partsize);
		this.offset += partsize;
		this.remainingBytes -= partsize;
		localUploadPartRequest.setLastPart(bool);
		return localUploadPartRequest;
	}
}
