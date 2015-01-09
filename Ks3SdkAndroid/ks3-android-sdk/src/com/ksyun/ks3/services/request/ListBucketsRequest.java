package com.ksyun.ks3.services.request;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;

public class ListBucketsRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = 6856359673102461593L;

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.GET);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {

	}

}
