package com.ksyun.ks3.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectListing {
	private List<Ks3ObjectSummary> objectSummaries = new ArrayList<Ks3ObjectSummary>();
	private List<String> commonPrefixes = new ArrayList<String>();
	private String bucketName;
	private String nextMarker;
	private boolean isTruncated;
	private String prefix;
	private String marker;
	private int maxKeys;
	private String delimiter;

	@Override
	public String toString() {
		return "ObjectListing[bucket=" + this.bucketName + ";nextMarker="
				+ this.nextMarker + ";isTruncated=" + this.isTruncated
				+ ";prefix=" + this.prefix + ";marker=" + this.marker
				+ ";maxKeys=" + this.maxKeys + ";delimiter=" + this.delimiter
				+ ";commonPrefixs=" + this.commonPrefixes + ";objectSummaries="
				+ this.objectSummaries + "]";
	}

	public List<Ks3ObjectSummary> getObjectSummaries() {
		return objectSummaries;
	}

	public void setObjectSummaries(List<Ks3ObjectSummary> objs) {
		this.objectSummaries = objs;
	}

	public List<String> getCommonPrefixes() {
		return commonPrefixes;
	}

	public void setCommonPrefixes(List<String> commonPrefixes) {
		this.commonPrefixes = commonPrefixes;
	}

	public String getNextMarker() {
		return nextMarker;
	}

	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public int getMaxKeys() {
		return maxKeys;
	}

	public void setMaxKeys(int maxKeys) {
		this.maxKeys = maxKeys;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isTruncated() {
		return isTruncated;
	}

	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

}
