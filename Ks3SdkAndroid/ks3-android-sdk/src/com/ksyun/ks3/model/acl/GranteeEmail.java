package com.ksyun.ks3.model.acl;

public class GranteeEmail implements Grantee {
	private String email;

	public String getTypeIdentifier() {
		return "emailAddress";
	}

	public void setIdentifier(String email) {
		this.email = email;
	}

	public String getIdentifier() {
		return email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "GranteeId[email=" + this.email + "]";
	}
}
