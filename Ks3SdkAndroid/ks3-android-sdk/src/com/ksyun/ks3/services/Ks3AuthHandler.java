package com.ksyun.ks3.services;

import com.ksyun.ks3.auth.AuthEvent;

public abstract class Ks3AuthHandler {
	public boolean isNeedCalculateAuth = true;
	public abstract void onSuccess (AuthEvent event);
	public abstract void onFailure (AuthEvent event);
	
	public void onSuccessEvent(AuthEvent event){
		onSuccess(event);
	}
	
	public void onFailureEvent(AuthEvent event){
		onFailure(event);
	}
}
