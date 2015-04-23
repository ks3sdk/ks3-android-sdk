package com.ksyun.ks3.model.result;

import java.util.Date;

import com.ksyun.ks3.model.ObjectMetadata;

public class HeadObjectResult {
	private ObjectMetadata objectMetadata = new ObjectMetadata();
	private String ETag;
	private Date lastmodified;

	public ObjectMetadata getObjectMetadata() {
		return objectMetadata;
	}

	public void setObjectMetadata(ObjectMetadata objectMetadata) {
		this.objectMetadata = objectMetadata;
	}

	public String getETag() {
		return ETag;
	}

	public void setETag(String eTag) {
		ETag = eTag;
	}

	public Date getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(Date lastmodified) {
		this.lastmodified = lastmodified;
	}

	@Override
	public String toString() {
		return "HeadObjectResult[ETag=" + this.ETag + ";lastmodified="
				+ this.lastmodified + ";objectMetadata=" + this.objectMetadata
				+ "]";
	}
}
