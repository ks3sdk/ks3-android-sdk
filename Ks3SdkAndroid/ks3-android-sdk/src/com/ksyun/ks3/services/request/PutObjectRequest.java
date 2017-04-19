package com.ksyun.ks3.services.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.ksyun.ks3.auth.ValidateUtil;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.Mimetypes;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.ObjectMetadata.Meta;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.acl.Grant;
import com.ksyun.ks3.model.acl.Permission;
import com.ksyun.ks3.model.transfer.MD5DigestCalculatingInputStream;
import com.ksyun.ks3.model.transfer.RepeatableFileInputStream;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.Md5Utils;
import com.ksyun.ks3.util.StringUtils;

public class PutObjectRequest extends Ks3HttpRequest implements
		MD5CalculateAble {
	private static final long serialVersionUID = 8398633676278496457L;
	private File file;
	private ObjectMetadata objectMeta = new ObjectMetadata();
	private CannedAccessControlList cannedAcl;
	private AccessControlList acl = new AccessControlList();
	private String redirectLocation;
	private String callBackUrl;
	private String callBackBody;
	private Map<String,String> callBackHeaders;

	public PutObjectRequest(String bucketname, String key, File file) {
		this.setBucketname(bucketname);
		this.setObjectkey(key);
		this.setFile(file);
	}

	public PutObjectRequest(String bucketname, String key,File file, ObjectMetadata metadata) {
		this(bucketname,key,file);
		this.setObjectMeta(metadata == null ? this.objectMeta : metadata);	
	}
	
	public void setCallBack(String callBackUrl, String callBackBody,Map<String,String> callBackHeaders){
		this.callBackUrl = callBackUrl;
		this.callBackBody = callBackBody;
		this.callBackHeaders = callBackHeaders;
	}
	
	
	@Override
	protected void setupRequest() throws Ks3ClientException {
		try {
			this.setRequestBody(new RepeatableFileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Ks3ClientException(e);
		}
		if(StringUtils.isBlank(getContentType()))
			objectMeta.setContentType(Mimetypes.getInstance().getMimetype(file));
		objectMeta.setContentLength(String.valueOf(file.length()));
		this.addHeader(HttpHeaders.ContentLength, String.valueOf(file.length()));
		try {
			String contentMd5_b64 = Md5Utils.md5AsBase64(file);
			this.addHeader(HttpHeaders.ContentMD5.toString(), contentMd5_b64);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Ks3ClientException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Ks3ClientException(
					"calculate file md5 error (" + e + ")", e);
		}
		if(!StringUtils.isBlank(this.callBackUrl) && !StringUtils.isBlank(this.callBackBody)){
			this.addHeader(HttpHeaders.XKssCallBackUrl, this.callBackUrl);
			this.addHeader(HttpHeaders.XKssCallBackBody, this.callBackBody);
			
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
		
		for (Entry<Meta, String> entry : this.objectMeta.getMetadata()
				.entrySet()) {
			if (!entry.getKey().equals(Meta.ContentLength.toString())) {
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
		if (this.redirectLocation != null) {
			this.addHeader(HttpHeaders.XKssWebsiteRedirectLocation,
					this.redirectLocation);
		}
		this.setHttpMethod(HttpMethod.PUT);
	}

	@Override
	protected void validateParams() throws Ks3ClientException {
		if (ValidateUtil.validateBucketName(this.getBucketname()) == null)
			throw new Ks3ClientException("bucket name is not correct");
		if (StringUtils.isBlank(this.getObjectkey()))
			throw new Ks3ClientException("object key can not be null");
		if (file == null)
			throw new Ks3ClientException("upload object can not be null");
		if (this.acl != null && this.acl.getGrants() != null) {
			for (Grant grant : this.acl.getGrants()) {
				if (grant.getPermission() == null)
					throw new Ks3ClientException("grant :" + grant.getGrantee()
							+ ",permission can not be null");
			}
		}
		if (this.redirectLocation != null) {
			if (!this.redirectLocation.startsWith("/")
					&& !this.redirectLocation.startsWith("http://")
					&& !this.redirectLocation.startsWith("https://"))
				throw new Ks3ClientException(
						"redirectLocation should start with / http:// or https://");
		}

	}

	public File getFile() {
		return file;
	}

	private void setFile(File file) {
		this.file = file;
	}

	public ObjectMetadata getObjectMeta() {
		return objectMeta;
	}

	public void setObjectMeta(ObjectMetadata objectMeta) {
		this.objectMeta = objectMeta;
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
	
	public Map<String, String> getCallBackHeaders() {
		return callBackHeaders;
	}

	public void setCallBackHeaders(Map<String, String> callBackHeaders) {
		this.callBackHeaders = callBackHeaders;
	}
	
	public String getMd5() {
		return Base64
				.encodeToString(
						((MD5DigestCalculatingInputStream) super.getRequestBody())
								.getMd5Digest(), Base64.DEFAULT).trim();
	}

}
