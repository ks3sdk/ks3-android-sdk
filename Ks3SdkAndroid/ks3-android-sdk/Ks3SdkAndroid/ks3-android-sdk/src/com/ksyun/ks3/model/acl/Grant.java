package com.ksyun.ks3.model.acl;


public class Grant {
	private Grantee grantee = null;
	private Permission permission = null;

	public Grant() {
	}

	public Grant(Grantee grantee, Permission permission) {
		this.grantee = grantee;
		this.permission = permission;
	}

	public Grantee getGrantee() {
		return grantee;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setGrantee(Grantee grantee) {
		this.grantee = grantee;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grantee == null) ? 0 : grantee.hashCode());
		result = prime * result
				+ ((permission == null) ? 0 : permission.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grant other = (Grant) obj;
		if (grantee == null) {
			if (other.grantee != null)
				return false;
		} else if (!grantee.equals(other.grantee))
			return false;
		if (permission != other.permission)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Grant [grantee=" + grantee + ", permission=" + permission + "]";
	}
}
