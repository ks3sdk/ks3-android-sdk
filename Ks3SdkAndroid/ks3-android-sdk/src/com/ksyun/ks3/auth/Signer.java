package com.ksyun.ks3.auth;

import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.services.request.Ks3HttpRequest;

public interface Signer {
	public String calculate(Authorization auth,Ks3HttpRequest request);
}
