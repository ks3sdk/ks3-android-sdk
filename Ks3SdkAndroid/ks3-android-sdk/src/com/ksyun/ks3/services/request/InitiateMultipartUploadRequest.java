package com.ksyun.ks3.services.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.widget.Toast;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.ObjectMetadata.Meta;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.acl.Grant;
import com.ksyun.ks3.model.acl.Permission;
import com.ksyun.ks3.util.StringUtils;

public class InitiateMultipartUploadRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = 7282026856520472721L;
	private ObjectMetadata objectMeta = new ObjectMetadata();
	private AccessControlList acl = new AccessControlList();
	private CannedAccessControlList cannedAcl;

	public InitiateMultipartUploadRequest(String bucketName, String key) {
		this.setBucketname(bucketName);
		this.setObjectkey(key);
	}

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.POST);
		this.addParams("uploads", null);
		if(StringUtils.isBlank(getContentType()))
			this.addHeader(HttpHeaders.ContentType, "application/octet-stream");
		for (Entry<Meta, String> entry : this.objectMeta.getMetadata()
				.entrySet()) {
			if (!entry.getKey().equals(Meta.ContentLength)) {
				this.addHeader(entry.getKey().toString(), entry.getValue());
			}
		}
		for (Entry<String, String> entry : this.objectMeta.getUserMetadata()
				.entrySet()) {
			if (entry.getKey().startsWith(ObjectMetadata.userMetaPrefix))
				this.addHeader(entry.getKey(), entry.getValue());
		}
		if (this.cannedAcl != null) {
			this.addHeader(HttpHeaders.CannedAcl.toString(),
					cannedAcl.toString());
		}
		if (this.acl != null) {
			List<String> grants_fullcontrol = new ArrayList<String>();
			List<String> grants_read = new ArrayList<String>();
			List<String> grants_write = new ArrayList<String>();
			for (Grant grant : acl.getGrants()) {
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
						StringUtils.join(grants_fullcontrol, ","));
			}
			if (grants_read.size() > 0) {
				this.addHeader(HttpHeaders.GrantRead,
						StringUtils.join(grants_read, ","));
			}
			if (grants_write.size() > 0) {
				this.addHeader(HttpHeaders.GrantWrite,
						StringUtils.join(grants_write, ","));
			}
		}
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (StringUtils.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException("object key can not be null");
	}

	public ObjectMetadata getObjectMeta() {
		return objectMeta;
	}

	public void setObjectMeta(ObjectMetadata objectMeta) {
		this.objectMeta = objectMeta;
	}

	public AccessControlList getAcl() {
		return acl;
	}

	public void setAcl(AccessControlList acl) {
		this.acl = acl;
	}

	public CannedAccessControlList getCannedAcl() {
		return cannedAcl;
	}

	public void setCannedAcl(CannedAccessControlList cannedAcl) {
		this.cannedAcl = cannedAcl;
	}

}
