package com.ksyun.ks3.model.transfer;

public class UploadResult {
	private String bucketName;
	private String key;
	private String eTag;
	private String versionId;

	public String getBucketName() {
		return this.bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getETag() {
		return this.eTag;
	}

	public void setETag(String etag) {
		this.eTag = etag;
	}

	public String getVersionId() {
		return this.versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
}
