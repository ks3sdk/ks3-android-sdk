package com.ksyun.ks3.model.result;

public class CompleteMultipartUploadResult {
	private String location;
	private String bucket;
	private String key;
	private String eTag;

	public String toString() {
		return "CompleteMultipartUploadResult[location=" + this.location
				+ ";bucket=" + this.bucket + ";key=" + this.key + ";eTag="
				+ this.eTag + "]";
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

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

	public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}

}
