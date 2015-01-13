package com.ksyun.ks3.services.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.Part;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.StringUtils;

public class CompleteMultipartUploadRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = -7600788989122388243L;
	private String uploadId;
	private List<PartETag> partETags = new ArrayList<PartETag>();
	private String callBackUrl;
	private String callBackBody;
	private Map<String,String> callBackHeaders;
	
	public CompleteMultipartUploadRequest(String bucketname, String objectkey,String uploadId, List<PartETag> eTags) {
		this.setBucketname(bucketname);
		this.setObjectkey(objectkey);
		this.uploadId = uploadId;
		if (eTags != null)
			this.partETags = eTags;
	}
	

	public CompleteMultipartUploadRequest(ListPartsResult result) {
		if(result != null){
			this.setBucketname(result.getBucketname());
			this.setObjectkey(result.getKey());
			this.uploadId = result.getUploadId();
			for (Part p : result.getParts()) {
				PartETag tag = new PartETag();
				tag.seteTag(p.getETag());
				tag.setPartNumber(p.getPartNumber());
				this.partETags.add(tag);
			}
		}
	}

	public CompleteMultipartUploadRequest(String bucketname, String objectkey) {
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
	}

	public void setCallBack(String callBackUrl, String callBackBody,Map<String,String> callBackHeaders){
		this.callBackUrl = callBackUrl;
		this.callBackBody = callBackBody;
		this.callBackHeaders = callBackHeaders;
	}
	
	@Override
	protected void setupRequest() throws Ks3ClientException {
		try {
			XmlSerializer serializer = Xml.newSerializer();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			serializer.setOutput(stream, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "CompleteMultipartUpload");
			for (PartETag eTag : partETags) {
				serializer.startTag(null, "Part").startTag(null, "PartNumber")
						.text(String.valueOf(eTag.getPartNumber()))
						.endTag(null, "PartNumber").startTag(null, "ETag")
						.text(eTag.geteTag()).endTag(null, "ETag")
						.endTag(null, "Part");
			}
			serializer.endTag(null, "CompleteMultipartUpload");
			serializer.endDocument();

			byte[] bytes = stream.toByteArray();
			this.setRequestBody(new ByteArrayInputStream(bytes));
			this.addHeader(HttpHeaders.ContentLength,String.valueOf(bytes.length));
			this.setHttpMethod(HttpMethod.POST);
			this.addParams("uploadId", this.uploadId);
			if(!StringUtils.isBlank(this.callBackUrl) && !StringUtils.isBlank(this.callBackBody)){
				this.addHeader(HttpHeaders.XKssCallBackUrl, this.callBackUrl);
				this.addHeader(HttpHeaders.XKssCallBackBody,this.callBackBody);
				
				if(this.callBackHeaders!= null && this.callBackHeaders.size() > 0){
					for(Map.Entry<String, String> entry: this.callBackHeaders.entrySet()){
						String key = entry.getKey();
						String val = entry.getValue();
						if(!StringUtils.isBlank(key) && key.startsWith(Constants.CALL_BACK_CUSTOM_PREFIX) && !StringUtils.isBlank(val)){
								this.addHeader(key, val);
						}else{
							Log.e(Constants.LOG_TAG,"the header:"+key +"-"+val + " is not correct ,this head will be ignored");
						}
					}
				}else{
					Log.d(Constants.LOG_TAG, "the callbackheaders is null");
				}
			}else{
				Log.d(Constants.LOG_TAG, "the callbacurl or callbackbody is null , ignore set the callback");
			}
		} catch (IllegalStateException e) {
			throw new Ks3ClientException(e);
		} catch (IOException e) {
			throw new Ks3ClientException(e);
		}
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException("object key can not be null");
		if (StringUtils.isBlank(this.uploadId))
			throw new Ks3ClientException("uploadId can not be null");
		if (this.partETags == null)
			throw new Ks3ClientException("partETags can not be null");
	}
	
	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public List<PartETag> getPartETags() {
		return partETags;
	}

	public void setPartETags(List<PartETag> partETags) {
		this.partETags = partETags;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getCallBackBody() {
		return callBackBody;
	}

	public void setCallBackBody(String callBackBody) {
		this.callBackBody = callBackBody;
	}
}
