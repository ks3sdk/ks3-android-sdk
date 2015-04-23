package com.ksyun.ks3.model.acl;

public class Authorization {
	private String accessKeyId;
	private String accessKeySecret;

	public Authorization(String id, String secret) {
		this.accessKeyId = id;
		this.accessKeySecret = secret;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
}
