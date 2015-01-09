package com.ksyun.ks3.services.request;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.acl.Grant;
import com.ksyun.ks3.model.acl.Permission;

public class PutBucketACLRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = 612889209303233181L;
	private AccessControlList accessControlList;
	private CannedAccessControlList cannedAcl;

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.PUT);
		this.addParams("acl", "");

		if (getCannedAcl() != null) {
			this.addHeader(HttpHeaders.CannedAcl, getCannedAcl().toString());
		}

		if (this.accessControlList != null) {
			List<String> grants_fullcontrol = new ArrayList<String>();
			List<String> grants_read = new ArrayList<String>();
			List<String> grants_write = new ArrayList<String>();
			for (Grant grant : accessControlList.getGrants()) {
				if (grant.getPermission().equals(Permission.FullControl)) {
					grants_fullcontrol.add("id=\""
							+ grant.getGrantee().getIdentifier() + "\"");
				} else if (grant.getPermission().equals(Permission.Read)) {
					grants_read.add("id=\""
							+ grant.getGrantee().getIdentifier() + "\"");
				} else if (grant.getPermission().equals(Permission.Write)) {
					grants_write.add("id=\""
							+ grant.getGrantee().getIdentifier() + "\"");
				}
			}
			if (grants_fullcontrol.size() > 0) {
				this.addHeader(HttpHeaders.GrantFullControl,
						TextUtils.join(",", grants_fullcontrol));
			}
			if (grants_read.size() > 0) {
				this.addHeader(HttpHeaders.GrantRead,
						TextUtils.join(",", grants_read));
			}
			if (grants_write.size() > 0) {
				this.addHeader(HttpHeaders.GrantWrite,
						TextUtils.join(",", grants_write));
			}
		}
	}

	@Override
	protected void validateParams() throws Ks3ClientException {

		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
		if (this.accessControlList == null && this.cannedAcl == null)
			throw new Ks3ClientException(
					"acl and cannedAcl can not both null");

		if (this.accessControlList != null
				&& this.getAccessControlList().getGrants() != null) {
			for (Grant grant : this.accessControlList.getGrants()) {
				if (grant.getPermission() == null)
					throw new Ks3ClientException("grant :"
							+ grant.getGrantee()
							+ ",permission can not be null");
			}
		}

	}

	public PutBucketACLRequest(String bucketName) {
		super.setBucketname(bucketName);
	}

	public PutBucketACLRequest(String bucketName,
			CannedAccessControlList cannedAcl) {
		this(bucketName);
		this.setCannedAcl(cannedAcl);
	}

	public PutBucketACLRequest(String bucketName,
			AccessControlList accessControlList) {
		setBucketname(bucketName);
		this.setAccessControlList(accessControlList);
	}

	public CannedAccessControlList getCannedAcl() {
		return cannedAcl;
	}

	public void setCannedAcl(CannedAccessControlList cannedAcl) {
		this.cannedAcl = cannedAcl;
	}

	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

}
