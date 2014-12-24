package com.ksyun.ks3.model.acl;

public enum GranteeUri implements Grantee {
	AllUsers("http://acs.ksyun.com/groups/global/AllUsers"),
	 AuthenticatedUsers("http://acs.amazonaws.com/groups/global/AuthenticatedUsers"),
	 LogDelivery("http://acs.amazonaws.com/groups/s3/LogDelivery");
	;
	private String uri;

	private GranteeUri(String uri) {
		this.uri = uri;
	}

	public String getTypeIdentifier() {
		return "uri";
	}

	public void setIdentifier(String uri) {
		this.uri = uri;
	}

	public String getIdentifier() {
		return uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public static GranteeUri parse(String groupUri) {
		for (GranteeUri grantee : GranteeUri.values()) {
			if (grantee.uri.equals(groupUri)) {
				return grantee;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "GranteeUri[uri=" + this.uri + "]";
	}
}
