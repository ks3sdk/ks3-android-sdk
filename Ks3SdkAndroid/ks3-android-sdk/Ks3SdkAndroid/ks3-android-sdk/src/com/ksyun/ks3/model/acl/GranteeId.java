package com.ksyun.ks3.model.acl;

public class GranteeId implements Grantee {
	private String id;
	private String displayName;

	public String getTypeIdentifier() {
		return "id";
	}

	public void setIdentifier(String id) {
		this.id = id;
	}

	public String getIdentifier() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "GranteeId[id=" + this.id + ";displayName=" + this.displayName
				+ "]";
	}
}
