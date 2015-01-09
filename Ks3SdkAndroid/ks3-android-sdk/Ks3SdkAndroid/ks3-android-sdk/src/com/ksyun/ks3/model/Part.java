package com.ksyun.ks3.model;

import java.util.Date;

public class Part {
	private int partNumber;
	private Date lastModified;
	private String ETag;
	private long size;

	@Override
	public String toString() {
		return "Part[partNumber=" + this.partNumber + ";lastModified="
				+ this.lastModified + ";ETag=" + this.ETag + ";size="
				+ this.size + "]";
	}

	public int getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getETag() {
		return ETag;
	}

	public void setETag(String eTag) {
		ETag = eTag;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
