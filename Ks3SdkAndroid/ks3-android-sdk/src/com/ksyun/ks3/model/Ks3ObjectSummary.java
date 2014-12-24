package com.ksyun.ks3.model;

import java.util.Date;

public class Ks3ObjectSummary {
	protected String bucketName;
	protected String key;
	protected String eTag;
	protected long size;
	protected Date lastModified;
	protected String storageClass;
	protected Owner owner;

	public String toString() {
		return "Ks3ObjectSummary[bucket=" + this.bucketName + ";key="
				+ this.key + ";eTag=" + this.eTag + ";size=" + this.size
				+ ";lastModified=" + this.lastModified + ";storgeClass="
				+ this.storageClass + ";owner=" + this.owner + "]";
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getETag() {
		return eTag;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public String getStorageClass() {
		return storageClass;
	}

	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}
}
