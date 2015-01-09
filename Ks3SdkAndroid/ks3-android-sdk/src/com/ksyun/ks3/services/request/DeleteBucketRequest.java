package com.ksyun.ks3.services.request;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;

public class DeleteBucketRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = 4174895324045826637L;

	public DeleteBucketRequest(String bucketname) {
		setBucketname(bucketname);
	}

	protected void setupRequest() throws Ks3ClientException {
		setHttpMethod(HttpMethod.DELETE);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
	}

}
