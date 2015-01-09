package com.ksyun.ks3.services.request;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class GetBucketACLRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = 1509787613917335360L;
	String acl;

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.GET);
		this.addParams("acl", acl);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (StringUtils.isBlank(this.getBucketname()))
			throw new Ks3ClientException("bucket name is not correct");
	}

	public GetBucketACLRequest(String bucketName) {
		this.setBucketname(bucketName);
	}
}
