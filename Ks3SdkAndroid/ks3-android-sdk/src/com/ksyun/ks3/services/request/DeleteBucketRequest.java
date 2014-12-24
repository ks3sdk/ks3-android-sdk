package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class DeleteBucketRequest extends Ks3HttpRequest {

	public DeleteBucketRequest(String bucketname) {
		setBucketname(bucketname);
	}

	@Override
	protected void setupRequest() {
		setHttpMethod(HttpMethod.DELETE);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
		if(StringUtils.isBlank(this.getBucketname()))
			throw new IllegalArgumentException("bucket name is not correct");
	}

}
