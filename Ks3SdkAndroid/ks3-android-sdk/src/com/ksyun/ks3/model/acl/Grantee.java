package com.ksyun.ks3.model.acl;

public interface Grantee {
	public String getTypeIdentifier();

	public String getIdentifier();

	public void setIdentifier(String id);

}
