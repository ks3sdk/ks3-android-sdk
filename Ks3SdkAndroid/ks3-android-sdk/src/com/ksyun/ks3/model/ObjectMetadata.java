package com.ksyun.ks3.model;

import java.util.HashMap;
import java.util.Map;

import com.ksyun.ks3.util.Constants;

import android.util.Log;

/**
 * 
 * @author TANGLUO
 * 
 */
public class ObjectMetadata {
	public static enum Meta {
		ContentType(HttpHeaders.ContentType), CacheControl(
				HttpHeaders.CacheControl), ContentLength(
				HttpHeaders.ContentLength), ContentDisposition(
				HttpHeaders.ContentDisposition), ContentEncoding(
				HttpHeaders.ContentEncoding), Expires(HttpHeaders.Expires), LastModified(
				HttpHeaders.LastModified), Etag(HttpHeaders.ETag), ContentMD5(
				HttpHeaders.ContentMD5);
		;
		private HttpHeaders header;

		public HttpHeaders getHeader() {
			return this.header;
		}

		private Meta(HttpHeaders header) {
			this.header = header;
		}

		public String toString() {
			return header.toString();
		}
	}

	// public static final String userMetaPrefix = "x-kss-meta-";
	public static final String userMetaPrefix = "x-kss-meta-";
	private Map<String, String> userMetadata = new HashMap<String, String>();
	private Map<Meta, String> metadata = new HashMap<Meta, String>();

	@Override
	public String toString() {
		return "ObjectMetadata[metadata=" + this.metadata + ";userMetadata="
				+ this.userMetadata + "]";
	}

	public Map<Meta, String> getMetadata() {
		return metadata;
	}

	public void setContentType(String s) {
		this.metadata.put(Meta.ContentType, s);
	}

	public void setCacheControl(String s) {
		this.metadata.put(Meta.CacheControl, s);
	}

	public void setContentDisposition(String s) {
		this.metadata.put(Meta.ContentDisposition, s);
	}

	public void setContentEncoding(String s) {
		this.metadata.put(Meta.ContentEncoding, s);
	}

	public void setExpires(String s) {
		this.metadata.put(Meta.Expires, s);
	}

	public void setContentLength(String s) {
		this.metadata.put(Meta.ContentLength, s);
	}

	public long getContentLength() {
		Long contentLength = Long
				.valueOf(this.metadata.get(Meta.ContentLength));

		if (contentLength == null)
			return 0L;
		return contentLength.longValue();
	}

	public void setContentMD5(String md5Base64) {
		if (md5Base64 == null) {
			metadata.remove(HttpHeaders.ContentMD5.toString());
		} else {
			metadata.put(Meta.ContentMD5, md5Base64);
		}

	}

	public String getContentMD5() {
		return this.metadata.get(Meta.ContentMD5);
	}

	public String getContentEtag() {
		return this.metadata.get(Meta.Etag);
	}

	public void addOrEditUserMeta(String key, String value) {
		if (!key.startsWith(ObjectMetadata.userMetaPrefix)) {
			key = ObjectMetadata.userMetaPrefix + key;
		} else {
			Log.d(Constants.LOG_TAG, "key already have userMetaPrefix");
		}
		this.userMetadata.put(key, value);
	}

	public void addOrEditMeta(Meta key, String value) {
		this.metadata.put(key, value);
	}

	public Map<String, String> getUserMetadata() {
		return userMetadata;
	}

}
