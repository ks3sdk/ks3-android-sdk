package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;

public class ListBucketsRequest extends Ks3HttpRequest {

	@Override
	protected void setupRequest() {
		this.setHttpMethod(HttpMethod.GET);
		
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {

	}

}
