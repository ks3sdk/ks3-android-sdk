package com.ksyun.ks3.auth;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.services.request.Ks3HttpRequest;

public class DefaultSigner implements Signer {

	@Override
	public String calculate(Authorization auth, Ks3HttpRequest request) {
		try {
			return AuthUtils.calcAuthorization(auth, request);
		} catch (Exception e) {
			throw new Ks3ClientException(
					"calculate user authorization has occured an exception ("
							+ e + ")", e);
		}
	}

}
