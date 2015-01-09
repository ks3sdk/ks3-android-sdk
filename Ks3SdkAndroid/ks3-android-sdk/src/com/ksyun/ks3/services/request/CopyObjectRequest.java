package com.ksyun.ks3.services.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.ksyun.ks3.util.StringUtils;

public class CopyObjectRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = -2905675368285940188L;
	private String sourceBucket;
	private String sourceKey;
	private CannedAccessControlList cannedAcl;
	private AccessControlList accessControlList;

	public CopyObjectRequest(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey) {
		super.setBucketname(destinationBucket);
		super.setObjectkey(destinationObject);
		this.setSourceBucket(sourceBucket);
		this.setSourceKey(sourceKey);
	}

	public CopyObjectRequest(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey,
			CannedAccessControlList cannedAcl) {
		this(destinationBucket, destinationObject, sourceBucket, sourceKey);
		this.setCannedAcl(cannedAcl);
	}

	public CopyObjectRequest(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey,
			AccessControlList accessControlList) {
		this(destinationBucket, destinationObject, sourceBucket, sourceKey);
		this.setAccessControlList(accessControlList);
	}

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.PUT);
		try {
			this.addHeader(
					HttpHeaders.XKssCopySource,
					"/" + this.getSourceBucket() + "/"
							+ URLEncoder.encode(this.getSourceKey(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (this.cannedAcl != null) {
			this.addHeader(HttpHeaders.CannedAcl.toString(),
					cannedAcl.toString());
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
		if (ValidateUtil.validateBucketName(this.sourceBucket) == null)
			throw new Ks3ClientException("source-bucket name is not correct");
		if (StringUtils.isBlank(sourceKey))
			throw new Ks3ClientException("sourceKey can not be null");
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("destination-bucket name is not correct");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException(
					"destinationObject can not be null");
	}

	public String getSourceBucket() {
		return sourceBucket;
	}

	public void setSourceBucket(String sourceBucket) {
		this.sourceBucket = sourceBucket;
	}

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
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
