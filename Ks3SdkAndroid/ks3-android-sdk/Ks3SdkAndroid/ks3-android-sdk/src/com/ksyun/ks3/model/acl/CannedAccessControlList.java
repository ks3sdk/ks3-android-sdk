package com.ksyun.ks3.model.acl;

public enum CannedAccessControlList {
	Private("private"), 
	PublicRead("public-read"), 
	PublicReadWrite("public-read-write");
	private final String cannedAclHeader;

	private CannedAccessControlList(String cannedAclHeader) {
		this.cannedAclHeader = cannedAclHeader;
	}

	public String toString() {
		return cannedAclHeader;
	}
}
