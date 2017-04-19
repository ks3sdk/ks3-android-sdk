package com.ksyun.ks3.services.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.util.Log;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.transfer.InputSubStream;
import com.ksyun.ks3.model.transfer.MD5DigestCalculatingInputStream;
import com.ksyun.ks3.model.transfer.RepeatableFileInputStream;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.StringUtils;

public class UploadPartRequest extends Ks3HttpRequest {
	private static final long serialVersionUID = -376145159039630694L;
	private String uploadId;
	private int partNumber;
	private long partSize;
	private InputStream inputStream;
	private File file;
	private long fileOffset;
	private boolean isLastPart;
	public long contentLength = -1;
	private ObjectMetadata objectMeta = new ObjectMetadata();
	private CannedAccessControlList cannedAcl;
	private AccessControlList acl = new AccessControlList();
	private String redirectLocation;

	public UploadPartRequest(String bucketName, String key, String uploadId,
			File file, long offset, int partNumber, long partSize) {
		setBucketname(bucketName);
		setObjectkey(key);
		this.uploadId = uploadId;
		this.file = file;
		this.fileOffset = offset;
		this.partNumber = partNumber;
		this.partSize = partSize;
		if (file.length() - offset < partSize) {
			this.contentLength = file.length() - offset;
			isLastPart = true;
		} else {
			this.contentLength = partSize;
			isLastPart = false;
		}
	}

	@Override
	protected void setupRequest() throws Ks3ClientException {
		this.setHttpMethod(HttpMethod.PUT);
		this.addParams("uploadId", this.uploadId);
		this.addParams("partNumber", String.valueOf(this.partNumber));
		if(StringUtils.isBlank(getContentType()))
			this.addHeader(HttpHeaders.ContentType, "application/octet-stream");
		MD5DigestCalculatingInputStream inputStream = null;
		try {
			inputStream = new MD5DigestCalculatingInputStream(
					new InputSubStream(
							new RepeatableFileInputStream(this.file),
							this.fileOffset, contentLength, true));
			Log.d(Constants.LOG_TAG, "bucketName :" + this.getBucketname()
					+ ",objectkey :" + this.getObjectkey() + ",partNumber :"
					+ this.partNumber + ",partSzie :" + partSize
					+ ",conentLength:" + this.contentLength);
		} catch (FileNotFoundException e) {
			throw new Ks3ClientException(e);
		}
		this.addHeader(HttpHeaders.ContentLength,
				String.valueOf(this.contentLength));
		this.setRequestBody(inputStream);
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
		
		if (StringUtils.isBlank(this.getObjectkey())) {
			throw new Ks3ClientException("object key can not be null");
		}
		if (StringUtils.isBlank(this.uploadId)) {
			throw new Ks3ClientException("uploadId can not be null");
		}
		if (this.partSize <= 0) {
			throw new Ks3ClientException(
					"part size can not should bigger than 0");
		}
		if (partNumber < Constants.minPartNumber
				|| partNumber > Constants.maxPartNumber) {
			throw new Ks3ClientException("partNumber shoud between "
					+ Constants.minPartNumber + " and "
					+ Constants.maxPartNumber);
		}
		if (file == null) {
			throw new Ks3ClientException(
					"file and content can not both be null");
		}
		if (this.fileOffset < 0) {
			throw new Ks3ClientException("fileoffset(" + this.fileOffset
					+ ") should >= 0");
		}
		if (this.partSize > Constants.maxPartSize) {
			throw new Ks3ClientException("partsize(" + this.partSize
					+ ") should be small than" + Constants.maxPartSize);
		}
		if (this.partSize <= Constants.minPartSize) {
			throw new Ks3ClientException("partsize(" + this.partSize
					+ ") should be larger than" + Constants.minPartSize);
		}

	}

	public ObjectMetadata getObjectMeta() {
		return objectMeta;
	}

	public void setObjectMeta(ObjectMetadata objectMeta) {
		this.objectMeta = objectMeta;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public int getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	public long getPartSize() {
		return partSize;
	}

	public CannedAccessControlList getCannedAcl() {
		return cannedAcl;
	}

	public void setCannedAcl(CannedAccessControlList cannedAcl) {
		this.cannedAcl = cannedAcl;
	}

	public AccessControlList getAcl() {
		return acl;
	}

	public void setAcl(AccessControlList acl) {
		this.acl = acl;
	}

	public String getRedirectLocation() {
		return redirectLocation;
	}

	public void setRedirectLocation(String redirectLocation) {
		this.redirectLocation = redirectLocation;
	}

	public void setPartSize(long partSize) {
		this.partSize = partSize;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getFileOffset() {
		return fileOffset;
	}

	public void setFileOffset(long fileOffset) {
		this.fileOffset = fileOffset;
	}

	public boolean isLastPart() {
		return isLastPart;
	}

	public void setLastPart(boolean isLastPart) {
		this.isLastPart = isLastPart;
	}
}
