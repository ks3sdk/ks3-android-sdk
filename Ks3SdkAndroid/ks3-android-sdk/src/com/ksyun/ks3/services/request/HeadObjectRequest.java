package com.ksyun.ks3.services.request;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class HeadObjectRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = 3060892869127898914L;

	public HeadObjectRequest(String bucketname, String objectkey) {
		this.setBucketname(bucketname);
		this.setObjectkey(objectkey);
	}

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.HEAD);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (StringUtils.isBlank(this.getBucketname()))
			throw new Ks3ClientException("bucket name can not be null");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException("object key can not be null");
	}

}
