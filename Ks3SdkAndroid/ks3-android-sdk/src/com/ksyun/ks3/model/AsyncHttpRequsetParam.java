package com.ksyun.ks3.model;

import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import com.ksyun.ks3.util.ModelUtil;
import com.loopj.android.http.RequestParams;

public class AsyncHttpRequsetParam {
	private String url;
	private Header[] header;
	private RequestParams finalParams;
	private String contentType;
	private HttpEntity entity;
	
	public AsyncHttpRequsetParam() {
	}

	public AsyncHttpRequsetParam(String url) {
		this.url = url;
	}

	public AsyncHttpRequsetParam(String url, Map<String, String> header,
			Map<String, String> params) {
		this.url = url;
		this.header = ModelUtil.convertHeaderArray(header);
		this.finalParams = ModelUtil.convertRequsetParams(params);
	}

	public AsyncHttpRequsetParam(String url, String contentType,
			Map<String, String> header, Map<String, String> params,
			HttpEntity entity) {
		this.url = url;
		this.contentType = contentType;
		this.header = ModelUtil.convertHeaderArray(header);
		this.finalParams = ModelUtil.convertRequsetParams(params);
		this.entity = entity;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Header[] getHeader() {
		return header;
	}

	public void setHeader(Header[] header) {
		this.header = header;
	}

	public RequestParams getParams() {
		return finalParams;
	}

	public void setParams(RequestParams finalParams) {
		this.finalParams = finalParams;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

}
