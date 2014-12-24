package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class DeleteObjectRequest extends Ks3HttpRequest {

	@Override
	protected void setupRequest() {
		this.setHttpMethod(HttpMethod.DELETE);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
		if(StringUtils.isBlank(this.getBucketname()))
			throw new IllegalArgumentException("bucket name can not be null");
		if(StringUtils.isBlank(this.getObjectkey()))
			throw new IllegalArgumentException("object key can not be null");
	}

	public DeleteObjectRequest(String bucketname,String key){
		setBucketname(bucketname);
		setObjectkey(key);
	}
}
