package com.ksyun.ks3.auth;

public class AuthEvent {
	public AuthEventCode code;
	public String content;

	public AuthEventCode getCode() {
		return code;
	}

	public void setCode(AuthEventCode code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
