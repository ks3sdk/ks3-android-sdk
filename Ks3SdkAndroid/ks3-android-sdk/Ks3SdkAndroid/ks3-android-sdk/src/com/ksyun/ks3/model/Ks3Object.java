package com.ksyun.ks3.model;

import java.io.File;

/**
 * 
 * @author TANGLUO
 * 
 */
public class Ks3Object{

	private String key = null;
	private String bucketName = null;
	private ObjectMetadata objectMetadata;
	private File file;
	private String redirectLocation;

	@Override
	public String toString() {
		return "Ks3Object[bucket=" + this.bucketName + ";key=" + this.key
				+ ";redirectLocation=" + this.redirectLocation
				+ ";objectMetadata=" + this.objectMetadata + "]";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public long getFileLength(){
		return this.file.length();
	}
	
	public String getContentMd5(){
		return this.objectMetadata == null ? "" : this.objectMetadata.getContentMD5();
	}
	
	public String getContentETag(){
		return this.objectMetadata == null ? "" : this.objectMetadata.getContentEtag();
	}

	public String getRedirectLocation() {
		return redirectLocation;
	}

	public void setRedirectLocation(String redirectLocation) {
		this.redirectLocation = redirectLocation;
	}
	
	public ObjectMetadata getObjectMetadata() {
		return objectMetadata;
	}

	public void setObjectMetadata(ObjectMetadata objectMetadata) {
		this.objectMetadata = objectMetadata;
	}

}
