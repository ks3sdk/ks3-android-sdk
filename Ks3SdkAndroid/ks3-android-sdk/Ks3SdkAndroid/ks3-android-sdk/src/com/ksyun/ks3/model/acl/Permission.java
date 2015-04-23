package com.ksyun.ks3.model.acl;

public enum Permission {
	FullControl("FULL_CONTROL", "x-kss-grant-full-control"), 
	Read("READ","x-kss-grant-read"), 
	Write("WRITE", "x-kss-grant-write");

	private String permissionString;
	private String headerName;

	private Permission(String permissionString, String headerName) {
		this.permissionString = permissionString;
		this.headerName = headerName;
	}

	public String getHeaderName() {
		return headerName;
	}

	public String toString() {
		return permissionString;
	}

	public static Permission getInstance(String value) {
		if (value.equals(FullControl.toString())) {
			return Permission.FullControl;
		} else if (value.equals(Read.toString())) {
			return Permission.Read;
		} else if (value.equals(Write.toString())) {
			return Permission.Write;
		}
		return null;
	}
}
