package com.ksyun.ks3.services.crypto;

import com.ksyun.ks3.services.request.Ks3HttpRequest;

public abstract class Ks3CryptoHandler {
	public abstract void onSuccess (Ks3HttpRequest ks3HttpRequest);
	public abstract void onFailure (Ks3HttpRequest ks3HttpRequest);
	
	public void onSuccessEvent(Ks3HttpRequest ks3HttpRequest){
		onSuccess(ks3HttpRequest);
	}
	
	public void onFailureEvent(Ks3HttpRequest ks3HttpRequest){
		onFailure(ks3HttpRequest);
	}
}
