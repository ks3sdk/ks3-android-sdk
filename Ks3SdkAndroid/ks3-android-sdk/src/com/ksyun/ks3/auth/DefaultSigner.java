package com.ksyun.ks3.auth;

import android.util.Log;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.util.Constants;

public class DefaultSigner implements Signer {

	@Override
	public String calculate(Authorization auth, Ks3HttpRequest request) {
		try {
			return AuthUtils.calcAuthorization(auth, request);
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "calculate user authorization has occured an exception ");
		}
		return null;
	}

}
