package com.ksyun.ks3.model.acl;

import java.util.HashSet;

public class AccessControlList {
	private HashSet<Grant> grants = new HashSet<Grant>();

	public HashSet<Grant> getGrants() {
		return grants;
	}

	public void setGrants(HashSet<Grant> grants) {
		this.grants = grants;
	}

	public void addGrant(Grant grant) {
		this.grants.add(grant);
	}

	public void addGrant(Grantee grantee, Permission permission) {
		Grant grant = new Grant();
		grant.setGrantee(grantee);
		grant.setPermission(permission);
		this.addGrant(grant);
	}

	@Override
	public String toString() {
		return grants.toString();
	}
}
