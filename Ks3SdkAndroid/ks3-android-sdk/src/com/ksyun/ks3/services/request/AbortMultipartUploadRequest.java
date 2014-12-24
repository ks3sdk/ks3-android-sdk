package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;


public class AbortMultipartUploadRequest extends Ks3HttpRequest {

	private String uploadId;
	
	public AbortMultipartUploadRequest(String bucketname,String objectkey,String uploadId){
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
		this.setUploadId(uploadId);
	}
	
	@Override
	protected void setupRequest() {
		this.setHttpMethod(HttpMethod.DELETE);
		this.addParams("uploadId",this.uploadId);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
		if(StringUtils.isBlank(this.getBucketname()))
			throw new IllegalArgumentException("bucket name can not be null");
		if(StringUtils.isBlank(this.getObjectkey()))
			throw new IllegalArgumentException("object key can not be null");
		if(StringUtils.isBlank(this.uploadId))
			throw new IllegalArgumentException("uploadId can not be null");
	}
	
	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

}
