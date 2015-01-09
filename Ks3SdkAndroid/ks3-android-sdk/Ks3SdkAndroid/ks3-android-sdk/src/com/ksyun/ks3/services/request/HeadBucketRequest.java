package com.ksyun.ks3.services.request;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class HeadBucketRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = -3575015587209514328L;

	public HeadBucketRequest(String bucketname) {
		super.setBucketname(bucketname);
	}

	public void setBucketname(String bucketname) {
		super.setBucketname(bucketname);
	}

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.HEAD);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (StringUtils.isBlank(this.getBucketname()))
			throw new Ks3ClientException("bucket name can not be null");
	}

}
