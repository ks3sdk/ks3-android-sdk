package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class HeadBucketRequest extends Ks3HttpRequest {

	public HeadBucketRequest(String bucketname) {
		super.setBucketname(bucketname);
	}

	public void setBucketname(String bucketname) {
		super.setBucketname(bucketname);
	}
	
	@Override
	protected void setupRequest() {
		this.setHttpMethod(HttpMethod.HEAD);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
		if(StringUtils.isBlank(this.getBucketname()))
			throw new IllegalArgumentException("bucket name can not be null");
	}

}
