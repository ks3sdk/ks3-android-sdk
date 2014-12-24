package com.ksyun.ks3.model.result;

public class InitiateMultipartUploadResult {
	private String bucket;
	private String key;
	private String uploadId;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public String toString() {
		return "InitiateMultipartUploadResult[bucket=" + this.bucket + ";key="
				+ this.key + ";uploadId=" + this.uploadId + "]";
	}
}
