package com.ksyun.ks3.services.request;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class ListPartsRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = -4078058209726379593L;

	private String uploadId;

	private Integer maxParts = 1000;

	private Integer partNumberMarker = -1;

	private String encodingType;

	public ListPartsRequest(String bucketname, String objectkey, String uploadId) {
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
		this.setUploadId(uploadId);
	}

	public ListPartsRequest(String bucketname, String objectkey,
			String uploadId, int maxParts) {
		this(bucketname,objectkey,uploadId);
		this.setMaxParts(maxParts);
	}

	public ListPartsRequest(String bucketname, String objectkey,
			String uploadId, int maxParts, int partNumberMarker) {
		this(bucketname,objectkey,uploadId,maxParts);
		this.setPartNumberMarker(partNumberMarker);
	}

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.GET);
		this.addParams("max-parts", String.valueOf(this.maxParts));
		this.addParams("uploadId", this.uploadId);
		if (partNumberMarker != null && this.partNumberMarker >= 0) {
			this.addParams("part-number-marker",
					String.valueOf(this.partNumberMarker));
		}
		if (!StringUtils.isBlank(this.encodingType))
			this.addParams("encoding-type", this.encodingType);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException("object key can not be null");
		if (StringUtils.isBlank(this.uploadId))
			throw new Ks3ClientException("uploadId can not be null");
		if (this.maxParts != null
				&& (this.maxParts > 1000 || this.maxParts < 1))
			throw new Ks3ClientException(
					"maxParts should between 1 and 1000");
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public Integer getMaxParts() {
		return maxParts;
	}

	public void setMaxParts(Integer maxParts) {
		this.maxParts = maxParts;
	}

	public Integer getPartNumberMarker() {
		return partNumberMarker;
	}

	public void setPartNumberMarker(Integer partNumberMarker) {
		this.partNumberMarker = partNumberMarker;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

}
