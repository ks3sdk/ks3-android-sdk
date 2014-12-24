package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class GetBucketACLRequest extends Ks3HttpRequest {
	String acl ;
	@Override
	protected void setupRequest() {
		this.setHttpMethod(HttpMethod.GET);
        this.addParams("acl",acl);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
        if(StringUtils.isBlank(this.getBucketname()))
            throw new IllegalArgumentException("bucket name is not correct");
    }

	public GetBucketACLRequest(String bucketName){
		this.setBucketname(bucketName);
	}
}
