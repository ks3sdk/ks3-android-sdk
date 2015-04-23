package com.ksyun.ks3.services;

public interface AuthListener {

	public AuthResult onCalculateAuth(String httpMethod, String ContentType,
			String Date, String ContentMD5, String Resource, String Headers);
}
