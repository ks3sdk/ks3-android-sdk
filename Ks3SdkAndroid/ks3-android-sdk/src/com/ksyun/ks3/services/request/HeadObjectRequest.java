package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class HeadObjectRequest extends Ks3HttpRequest {
	public HeadObjectRequest(String bucketname, String objectkey) {
		this.setBucketname(bucketname);
		this.setObjectkey(objectkey);
	}

	@Override
	protected void setupRequest() {
		this.setHttpMethod(HttpMethod.HEAD);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
		if (StringUtils.isBlank(this.getBucketname()))
			throw new IllegalArgumentException("bucket name can not be null");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new IllegalArgumentException("object key can not be null");
	}

}
