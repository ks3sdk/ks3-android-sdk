package com.ksyun.ks3.model.result;

public class PutObjectResult {
	private String eTag;

	public String toString() {
		return "PutObjectResult[ETag=" + this.eTag + "]";
	}

	public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}

	public String getETag() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersionId() {
		// TODO Auto-generated method stub
		return null;
	}

}
