package com.ksyun.ks3.services.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.ksyun.ks3.auth.AuthEvent;
import com.ksyun.ks3.auth.AuthEventCode;
import com.ksyun.ks3.auth.AuthUtils;
import com.ksyun.ks3.auth.DefaultSigner;
import com.ksyun.ks3.auth.RepeatableInputStreamRequestEntity;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.AsyncHttpRequsetParam;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.model.transfer.MD5DigestCalculatingInputStream;
import com.ksyun.ks3.model.transfer.RequestProgressListener;
import com.ksyun.ks3.services.AuthListener;
import com.ksyun.ks3.services.AuthResult;
import com.ksyun.ks3.util.ByteUtil;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.DateUtil;
import com.ksyun.ks3.util.RequestUtils;
import com.ksyun.ks3.util.StringUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

public abstract class Ks3HttpRequest implements Serializable {

	private static final long serialVersionUID = -5871616471337887313L;
	private String url;
	private String bucketname;
	private String objectkey;
	private String paramsToSign = "";
	private HttpEntity entity;
	private InputStream requestBody;
	private HttpMethod httpMethod;
	private Map<String, String> header = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	private Authorization authorization;
	private Context context;
	private AsyncHttpRequsetParam asyncHttpRequestParam;
	private AuthListener authListener;
	private AuthResult authResult;
	private RequestProgressListener progressListener;
	private RequestHandle handler;

	private static final Pattern ENCODED_CHARACTERS_PATTERN;
	static {
		StringBuilder pattern = new StringBuilder();

		pattern.append(Pattern.quote("+")).append("|")
				.append(Pattern.quote("*")).append("|")
				.append(Pattern.quote("%7E")).append("|");

		ENCODED_CHARACTERS_PATTERN = Pattern.compile(pattern.toString());
	}

	/* url */
	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	/* bucket */
	public void setBucketname(String bucketname) {

		this.bucketname = bucketname;
	}

	public String getBucketname() {

		return bucketname;
	}

	/* Entity */
	public HttpEntity getEntity() {

		return entity;
	}

	public void setEntity(HttpEntity entity) {

		this.entity = entity;
	}

	/* Endpoint */
	public String getEndpoint() {

		return this.header.get(HttpHeaders.Host.toString());
	}

	public void setEndpoint(String endpoint) {

		this.addHeader(HttpHeaders.Host.toString(), endpoint);
	}

	/* object */
	public void setObjectkey(String objectkey) {

		this.objectkey = objectkey;
	}

	public String getObjectkey() {

		return objectkey;
	}

	/* authorization */
	public void setAuthorization(Authorization authorization) {

		this.authorization = authorization;
	}

	public Authorization getAuthorization() {

		return authorization;
	}

	/* Request body */
	public InputStream getRequestBody() {

		return requestBody;
	}

	public void setRequestBody(InputStream requestBody) {

		this.requestBody = requestBody;
	}

	/* Header */
	public void addHeader(String key, String value) {

		this.header.put(key, value);
	}

	protected void addHeader(HttpHeaders key, String value) {

		this.addHeader(key.toString(), value);
	}

	public void setHeader(Map<String, String> header) {

		this.header = header;
	}

	public Map<String, String> getHeader() {

		return header;
	}

	/* paramsToSign */
	protected void setParamsToSign(String paramsToSign) {

		this.paramsToSign = paramsToSign;
	}

	public String getParamsToSign() {

		return paramsToSign;
	}

	/* params */
	protected void addParams(String key, String value) {

		this.params.put(key, value);
	}

	public void setParams(Map<String, String> params) {

		this.params = params;
	}

	public Map<String, String> getParams() {

		return params;
	}

	/* httpMethod */
	public void setHttpMethod(HttpMethod httpMethod) {

		this.httpMethod = httpMethod;
	}

	public HttpMethod getHttpMethod() {

		return httpMethod;
	}

	/* ContentMD5 */
	protected void setContentMD5(String md5) {

		this.addHeader(HttpHeaders.ContentMD5.toString(), md5);
	}

	public String getContentMD5() {

		return this.header.get(HttpHeaders.ContentMD5.toString());
	}

	/* ContentHandler Type */
	protected void setContentType(String type) {

		this.header.put(HttpHeaders.ContentType.toString(), type);
	}

	public String getContentType() {

		return this.header.get(HttpHeaders.ContentType.toString());
	}

	/* Date */
	public String getDate() {

		String s = this.header.get(HttpHeaders.Date.toString());
		if (TextUtils.isEmpty(s)) {
			return null;
		} else {
			return s;
		}
	}

	protected void setDate(String string) {

		this.addHeader(HttpHeaders.Date.toString(), string);
	}

	/* Context */
	public Context getContext() {

		return context;
	}

	public void setContext(Context context) {

		this.context = context;
	}

	/* AsyncHttpRequsetParam */
	public AsyncHttpRequsetParam getAsyncHttpRequestParam() {

		return asyncHttpRequestParam;
	}

	public void setAsyncHttpRequestParam(
			AsyncHttpRequsetParam asyncHttpRequestParam) {

		this.asyncHttpRequestParam = asyncHttpRequestParam;
	}

	/**
	 * Important, Should call it when completed a request
	 */
	public void completeRequset(AsyncHttpResponseHandler handler)
			throws Ks3ClientException {

		this.validateParams();
		setupRequestDefault();
		setupRequest();
		if (handler instanceof RequestProgressListener) {
			this.progressListener = (RequestProgressListener) handler;
		}
		this.asyncHttpRequestParam = finishHttpRequest();
		if (authListener != null) {
			if (authResult != null && authResult.validateAuth()
					&& authResult.validateDate()) {
				AuthEvent event = new AuthEvent();
				event.setCode(AuthEventCode.Success);
				event.setContent("auth :" + authResult.getAuthStr() + ",date :"
						+ authResult.getDateStr());
			} else {
				AuthEvent event = new AuthEvent();
				event.setCode(AuthEventCode.Failure);
				String failReason = "UnKown";
				if (authResult == null)
					failReason = "retrieve auth result is null!!";
				else if (!authResult.validateAuth())
					failReason = "retrieve auth str is null";
				else if (!authResult.validateDate()) {
					failReason = "retrieve auth date is not correct , date :"
							+ authResult.getDateStr();
				}
				event.setContent("failure reason :" + failReason);
				Log.e(Constants.LOG_TAG,
						"AppServer Response failed, " + event.getContent());
			}
		}
	}

	private void setupRequestDefault() {
		url = getEndpoint().toString();
		if (url.startsWith("http://") || url.startsWith("https://"))
			url = url.replace("http://", "").replace("https://", "");
		httpMethod = HttpMethod.POST;
		this.setContentMD5("");
		this.addHeader(HttpHeaders.UserAgent, Constants.KS3_SDK_USER_AGENT);
		this.setContentType("text/plain");
		this.setDate(DateUtil.GetUTCTime());
	}

	@SuppressWarnings("deprecation")
	private AsyncHttpRequsetParam finishHttpRequest() throws Ks3ClientException {

		// Prepare md5 if need
		if (this instanceof MD5CalculateAble && this.getRequestBody() != null) {
			if (!(this.getRequestBody() instanceof MD5DigestCalculatingInputStream))
				this.setRequestBody(new MD5DigestCalculatingInputStream(this
						.getRequestBody()));
		}
		String encodedParams = encodeParams();
		String encodedObjectKey = (StringUtils.isBlank(this.objectkey)) ? ""
				: URLEncoder.encode(this.objectkey);
		url = new StringBuffer("http://").append(url).append("/")
				.append(encodedObjectKey).toString();
		url = urlEncode(url);
		if (!TextUtils.isEmpty(encodedParams))
			url += "?" + encodedParams;
		// Pass url
		this.setUrl(url);
		if (this.getHttpMethod() == HttpMethod.POST) {
			if (requestBody == null && params != null) {
				try {
					setEntity(new StringEntity(encodedParams));
				} catch (UnsupportedEncodingException e) {
					throw new Ks3ClientException(
							"Unable to create HTTP entity:" + e, e);
				}
			} else {
				String length = this.getHeader().get(
						HttpHeaders.ContentLength.toString());
				HttpEntity entity = new RepeatableInputStreamRequestEntity(
						requestBody, length);
				try {
					entity = new BufferedHttpEntity(entity);
				} catch (IOException e) {
					e.printStackTrace();
					throw new Ks3ClientException("init http request error(" + e
							+ ")", e);
				}
				// Set entity
				setEntity(entity);
			}
		} else if (this.getHttpMethod() == HttpMethod.GET) {
			if (requestBody != null) {
				Map<String, String> headrs = this.getHeader();
				String length = headrs
						.get(HttpHeaders.ContentLength.toString());
				if (length == null)
					throw new Ks3ClientException(
							"content-length can not be null when put request");
				RepeatableInputStreamRequestEntity entity = new RepeatableInputStreamRequestEntity(
						requestBody, length);
				entity.setProgressLisener(this.progressListener);
				setEntity(entity);
			}
		} else if (this.getHttpMethod() == HttpMethod.PUT) {
			if (requestBody != null) {
				Map<String, String> headrs = this.getHeader();
				String length = headrs
						.get(HttpHeaders.ContentLength.toString());
				if (length == null)
					throw new Ks3ClientException(
							"content-length can not be null when put request");
				RepeatableInputStreamRequestEntity entity = new RepeatableInputStreamRequestEntity(
						requestBody, length);
				entity.setProgressLisener(this.progressListener);
				setEntity(entity);
			}
		} else if (this.getHttpMethod() == HttpMethod.DELETE) {

		} else if (this.getHttpMethod() == HttpMethod.HEAD) {

		} else {
			throw new Ks3ClientException("Unknow http method : "
					+ this.getHttpMethod());
		}

		if (!StringUtils.isBlank(header.get(HttpHeaders.ContentLength
				.toString()))) {
			header.remove(HttpHeaders.ContentLength.toString());
		}
		if (authListener != null) {
			authResult = authListener.onCalculateAuth(this.getHttpMethod()
					.toString(), this.getContentType(), this.getDate(), this
					.getContentMD5(), AuthUtils.CanonicalizedKSSResource(this),
					AuthUtils.CanonicalizedKSSHeaders(this));
			if (authResult != null) {
				this.addHeader(HttpHeaders.Authorization.toString(), authResult
						.getAuthStr().trim());
				if (!StringUtils.isBlank(authResult.getDateStr())) {
					this.setDate(authResult.getDateStr());
				}
				Log.i(Constants.LOG_TAG, "AppServer response token is = "
						+ authResult.getAuthStr().trim() + ",Date is ="
						+ authResult.getDateStr());
			}

		} else {
			this.addHeader(HttpHeaders.Authorization.toString(),
					new DefaultSigner().calculate(authorization, this).trim());
		}
		if (entity != null) {
			return new AsyncHttpRequsetParam(url, getContentType(), header,
					params, entity);
		} else {
			return new AsyncHttpRequsetParam(url, header, params);
		}

	}

	@SuppressWarnings("deprecation")
	private String encodeParams() {

		List<Map.Entry<String, String>> arrayList = new ArrayList<Map.Entry<String, String>>(
				this.params.entrySet());
		Collections.sort(arrayList,
				new Comparator<Map.Entry<String, String>>() {

					@Override
					public int compare(Entry<String, String> o1,
							Entry<String, String> o2) {

						return ByteUtil.compareTo(o1.getKey().toString()
								.getBytes(), o2.getKey().toString().getBytes());
					}
				});
		List<String> kvList = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		for (Entry<String, String> entry : arrayList) {
			String value = null;
			String key = entry.getKey()
					.replace(String.valueOf((char) 8203), "");
			if (!StringUtils.isBlank(entry.getValue()))
				value = URLEncoder.encode(entry.getValue());
			if (RequestUtils.subResource.contains(entry.getKey())) {
				if (value != null && !value.equals(""))
					kvList.add(key + "=" + value);
				else
					kvList.add(key);
			}
			if (value != null && !value.equals("")) {
				list.add(key + "=" + value);
			} else {
				if (RequestUtils.subResource.contains(key))
					list.add(key);
			}
		}
		String queryParams = TextUtils.join("&", list.toArray());
		this.setParamsToSign(TextUtils.join("&", kvList.toArray()));
		return queryParams;
	}

	/* Setup header,parameter and so on */
	protected abstract void setupRequest() throws Ks3ClientException;

	/* Validate parameters */
	protected abstract void validateParams() throws Ks3ClientException;

	public AuthListener getAuthListener() {

		return authListener;
	}

	public void setAuthListener(AuthListener authListener) {

		this.authListener = authListener;

	}

	public void setRequestHandler(RequestHandle handler) {

		if (this.handler != null) {
			Log.e(Constants.LOG_TAG,
					"method : setRequestHandler , is an internal method, and the handler is already set up , ingnore ! ");
			return;
		}

		this.handler = handler;
	}

	public boolean abort() {

		if (this.handler != null) {
			return this.handler.cancel(true);
		} else {
			Log.e(Constants.LOG_TAG,
					"the request is on RUNNING status , or the request is on sync mode , igonre abort request ! ");
			return false;
		}
	}

	public static String urlEncode(final String value) {

		if (value == null) {
			return "";
		}

		Matcher matcher = ENCODED_CHARACTERS_PATTERN.matcher(value);
		StringBuffer buffer = new StringBuffer(value.length());

		while (matcher.find()) {
			String replacement = matcher.group(0);

			if ("+".equals(replacement)) {
				replacement = "%20";
			} else if ("*".equals(replacement)) {
				replacement = "%2A";
			} else if ("%7E".equals(replacement)) {
				replacement = "~";
			}

			matcher.appendReplacement(buffer, replacement);
		}

		matcher.appendTail(buffer);
		return buffer.toString();

	}

}