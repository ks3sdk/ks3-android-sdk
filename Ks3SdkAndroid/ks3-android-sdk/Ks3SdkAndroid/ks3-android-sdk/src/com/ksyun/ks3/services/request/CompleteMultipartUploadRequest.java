package com.ksyun.ks3.services.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.Part;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.util.StringUtils;

public class CompleteMultipartUploadRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = -7600788989122388243L;
	private String uploadId;
	private List<PartETag> partETags = new ArrayList<PartETag>();

	public CompleteMultipartUploadRequest(String bucketname, String objectkey,
			String uploadId, List<PartETag> eTags) {
		this.setBucketname(bucketname);
		this.setObjectkey(objectkey);
		this.uploadId = uploadId;
		if (eTags == null)
			eTags = new ArrayList<PartETag>();
		this.partETags = eTags;
	}

	public CompleteMultipartUploadRequest(ListPartsResult result) {

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

	public CompleteMultipartUploadRequest(String bucketname, String objectkey) {
		super.setBucketname(bucketname);
		super.setObjectkey(objectkey);
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
			this.addHeader(HttpHeaders.ContentLength,
					String.valueOf(bytes.length));
			this.setHttpMethod(HttpMethod.POST);
			this.addParams("uploadId", this.uploadId);
		} catch (IllegalStateException e) {
			throw new Ks3ClientException(e);
		} catch (IOException e) {
			throw new Ks3ClientException(e);
		}
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (StringUtils.isBlank(this.getBucketname()))
			throw new Ks3ClientException("bucket name can not be null");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException("object key can not be null");
		if (StringUtils.isBlank(this.uploadId))
			throw new Ks3ClientException("uploadId can not be null");
		if (this.partETags == null)
			throw new Ks3ClientException("partETags can not be null");
	}

}
