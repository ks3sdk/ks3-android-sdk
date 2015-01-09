package com.ksyun.ks3.model.result;

import java.util.ArrayList;
import java.util.List;

import com.ksyun.ks3.model.Owner;
import com.ksyun.ks3.model.Part;

public class ListPartsResult {
	private String bucketname;
	private String key;
	private String uploadId;
	private String partNumberMarker;
	private String nextPartNumberMarker;
	private String maxParts;
	private boolean isTruncated;
	private String encodingType;
	private Owner initiator = new Owner();
	private Owner owner = new Owner();
	private List<Part> parts = new ArrayList<Part>();

	@Override
	public String toString() {
		return "ListPartsResult[bucket=" + this.bucketname + ";key=" + this.key
				+ ";uploadId=" + this.uploadId + ";partNumberMarker="
				+ this.partNumberMarker + ";nextPartNumberMarker="
				+ this.nextPartNumberMarker + ";maxParts=" + this.maxParts
				+ ";isTruncated=" + this.isTruncated + ";initiator="
				+ this.initiator + ";owner=" + this.owner + "]";
	}

	public String getBucketname() {
		return bucketname;
	}

	public void setBucketname(String bucketname) {
		this.bucketname = bucketname;
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

	public String getPartNumberMarker() {
		return partNumberMarker;
	}

	public void setPartNumberMarker(String partNumberMarker) {
		this.partNumberMarker = partNumberMarker;
	}

	public String getNextPartNumberMarker() {
		return nextPartNumberMarker;
	}

	public void setNextPartNumberMarker(String nextPartNumberMarker) {
		this.nextPartNumberMarker = nextPartNumberMarker;
	}

	public String getMaxParts() {
		return maxParts;
	}

	public void setMaxParts(String maxParts) {
		this.maxParts = maxParts;
	}

	public boolean isTruncated() {
		return isTruncated;
	}

	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	public Owner getInitiator() {
		return initiator;
	}

	public void setInitiator(Owner initiator) {
		this.initiator = initiator;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}
}
