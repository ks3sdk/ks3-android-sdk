package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class GetObjectACLRequest extends Ks3HttpRequest {
	private String acl;
	@Override
	protected void setupRequest() {
		 this.setHttpMethod(HttpMethod.GET);
	     this.addParams("acl",acl);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
        if(StringUtils.validateBucketName(this.getBucketname())==null)
            throw new IllegalArgumentException("bucket name is not correct");
    
        if(StringUtils.isBlank(this.getObjectkey())){
        	throw new IllegalArgumentException("object key can not be null");
        }
	}
	public GetObjectACLRequest(String bucketName , String objectKey){
		setBucketname(bucketName);
		setObjectkey(objectKey);
	}
}
