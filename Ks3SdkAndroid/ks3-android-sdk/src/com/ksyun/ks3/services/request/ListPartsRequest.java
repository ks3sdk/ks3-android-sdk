package com.ksyun.ks3.services.request;

import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.util.StringUtils;

public class ListPartsRequest extends Ks3HttpRequest {
	
	private String uploadId;
	
	private Integer maxParts = 1000;
	
	private Integer partNumberMarker = -1;
	
	private String encodingType;
	
	public ListPartsRequest(String bucketname,String objectkey,String uploadId){
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
		this.uploadId = uploadId;
	}
	
	public ListPartsRequest(String bucketname,String objectkey,String uploadId,int maxParts){
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
		this.uploadId = uploadId;
		this.maxParts = maxParts;
	}
	
	public ListPartsRequest(String bucketname,String objectkey,String uploadId,int maxParts,int partNumberMarker){
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
		this.uploadId = uploadId;
		this.maxParts = maxParts;
		this.partNumberMarker = partNumberMarker;
	}
	
	@Override
	protected void setupRequest() {
		this.setHttpMethod(HttpMethod.GET);
		this.addParams("max-parts",String.valueOf(this.maxParts));
		this.addParams("uploadId",this.uploadId);
		if(partNumberMarker!=null&&this.partNumberMarker>=0)
		{
			this.addParams("part-number-marker", String.valueOf(this.partNumberMarker));
		}
		if(!StringUtils.isBlank(this.encodingType))
			this.addParams("encoding-type",this.encodingType);
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
		if(StringUtils.isBlank(this.getBucketname()))
			throw new IllegalArgumentException("bucket name can not be null");
		if(StringUtils.isBlank(this.getObjectkey()))
			throw new IllegalArgumentException("object key can not be null");
		if(StringUtils.isBlank(this.uploadId))
			throw new IllegalArgumentException("uploadId can not be null");
		if(this.maxParts!=null&&(this.maxParts>1000||this.maxParts<1))
			throw new IllegalArgumentException("maxParts should between 1 and 1000");
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
