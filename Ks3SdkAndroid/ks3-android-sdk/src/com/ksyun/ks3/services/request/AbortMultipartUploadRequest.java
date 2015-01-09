package com.ksyun.ks3.services.request;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class AbortMultipartUploadRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = -2964026558210723101L;
	private String uploadId;

	public AbortMultipartUploadRequest(String bucketname, String objectkey,
			String uploadId) {
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
		this.setUploadId(uploadId);
	}

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.DELETE);
		this.addParams("uploadId", this.uploadId);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException("object key can not be null");
		if (StringUtils.isBlank(this.uploadId))
			throw new Ks3ClientException("uploadId can not be null");
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

}
