package com.ksyun.ks3.model.acl;

import java.util.HashSet;
import java.util.Set;

import com.ksyun.ks3.model.Owner;


public class AccessControlPolicy {
	private AccessControlList accessControlList = new AccessControlList();
	private Owner owner;

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

	public HashSet<Grant> getGrants() {
		return this.accessControlList.getGrants();
	}

	public void setGrants(HashSet<Grant> grants) {
		this.accessControlList.setGrants(grants);
	}

	public String toString() {
		return "AccessControlPolicy[accessControlList="
				+ this.accessControlList + ";owner=" + this.owner + "]";
	}
}
