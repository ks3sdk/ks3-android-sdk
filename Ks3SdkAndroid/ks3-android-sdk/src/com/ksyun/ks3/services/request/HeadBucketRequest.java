package com.ksyun.ks3.services.request;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;

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
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
	}

}
