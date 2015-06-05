package com.loopj.android.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import com.ksyun.ks3.model.LogRecord;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

public class AsyncHttpClient {

	public static final String LOG_TAG = "AsyncHttpClient";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_RANGE = "Content-Range";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ENCODING_GZIP = "gzip";
	public static final int DEFAULT_MAX_CONNECTIONS = 10;
	public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
	public static final int DEFAULT_MAX_RETRIES = 5;
	public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;
	public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	private int maxConnections = 10;
	private int connectTimeout = 10000;
	private int responseTimeout = 10000;
	private final DefaultHttpClient httpClient;
	private final HttpContext httpContext;
	private ExecutorService threadPool;
	private final Map<Context, List<RequestHandle>> requestMap;
	private final Map<String, String> clientHeaderMap;
	private boolean isUrlEncodingEnabled = true;

	public AsyncHttpClient() {

		this(false, 80, 443);
	}

	public AsyncHttpClient(int httpPort) {

		this(false, httpPort, 443);
	}

	public AsyncHttpClient(int httpPort, int httpsPort) {

		this(false, httpPort, httpsPort);
	}

	public AsyncHttpClient(boolean fixNoHttpResponseException, int httpPort,
			int httpsPort) {

		this(getDefaultSchemeRegistry(fixNoHttpResponseException, httpPort,
				httpsPort));
	}

	private static SchemeRegistry getDefaultSchemeRegistry(
			boolean fixNoHttpResponseException, int httpPort, int httpsPort) {

		if (fixNoHttpResponseException) {
			Log.d("AsyncHttpClient",
					"Beware! Using the fix is insecure, as it doesn't verify SSL certificates.");
		}

		if (httpPort < 1) {
			httpPort = 80;
			Log.d("AsyncHttpClient",
					"Invalid HTTP port number specified, defaulting to 80");
		}

		if (httpsPort < 1) {
			httpsPort = 443;
			Log.d("AsyncHttpClient",
					"Invalid HTTPS port number specified, defaulting to 443");
		}
		SSLSocketFactory sslSocketFactory;
		if (fixNoHttpResponseException)
			sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
		else {
			sslSocketFactory = SSLSocketFactory.getSocketFactory();
		}

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), httpPort));
		schemeRegistry
				.register(new Scheme("https", sslSocketFactory, httpsPort));

		return schemeRegistry;
	}

	public AsyncHttpClient(SchemeRegistry schemeRegistry) {

		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, this.connectTimeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(this.maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams, 10);

		HttpConnectionParams.setSoTimeout(httpParams, this.responseTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams,
				this.connectTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);

		this.threadPool = getDefaultThreadPool();
		this.requestMap = Collections.synchronizedMap(new WeakHashMap<Context,List<RequestHandle>>());
		this.clientHeaderMap = new HashMap<String, String>();

		this.httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		this.httpClient = new DefaultHttpClient(cm, httpParams);
		this.httpClient.addRequestInterceptor(new HttpRequestInterceptor() {

			@Override
			public void process(HttpRequest request, HttpContext context) {

				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
				for (String header : AsyncHttpClient.this.clientHeaderMap
						.keySet()) {
					if (request.containsHeader(header)) {
						Header overwritten = request.getFirstHeader(header);

						request.removeHeader(overwritten);
					}
					request.addHeader(header,
							AsyncHttpClient.this.clientHeaderMap.get(header));
				}
			}

		});
		this.httpClient.addResponseInterceptor(new HttpResponseInterceptor() {

			@Override
			public void process(HttpResponse response, HttpContext context) {

				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				Header encoding = entity.getContentEncoding();
				if (encoding != null)
					for (HeaderElement element : encoding.getElements())
						if (element.getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new AsyncHttpClient.InflatingEntity(
									entity));
							break;
						}
			}

		});
		this.httpClient.addRequestInterceptor(new HttpRequestInterceptor() {

			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {

				AuthState authState = (AuthState) context
						.getAttribute("http.auth.target-scope");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute("http.auth.credentials-provider");

				HttpHost targetHost = (HttpHost) context
						.getAttribute("http.target_host");

				if (authState.getAuthScheme() == null) {
					AuthScope authScope = new AuthScope(targetHost
							.getHostName(), targetHost.getPort());
					Credentials creds = credsProvider.getCredentials(authScope);
					if (creds != null) {
						authState.setAuthScheme(new BasicScheme());
						authState.setCredentials(creds);
					}
				}
			}

		}, 0);

		this.httpClient.setHttpRequestRetryHandler(new RetryHandler(1, 1500));
	}

	public static void allowRetryExceptionClass(Class<?> cls) {

		if (cls != null)
			RetryHandler.addClassToWhitelist(cls);
	}

	public static void blockRetryExceptionClass(Class<?> cls) {

		if (cls != null)
			RetryHandler.addClassToBlacklist(cls);
	}

	public HttpClient getHttpClient() {

		return this.httpClient;
	}

	public HttpContext getHttpContext() {

		return this.httpContext;
	}

	public void setCookieStore(CookieStore cookieStore) {

		this.httpContext.setAttribute("http.cookie-store", cookieStore);
	}

	public void setThreadPool(ExecutorService threadPool) {

		this.threadPool = threadPool;
	}

	public ExecutorService getThreadPool() {

		return this.threadPool;
	}

	protected ExecutorService getDefaultThreadPool() {

		return Executors.newCachedThreadPool();
	}

	public void setEnableRedirects(boolean enableRedirects,
			boolean enableRelativeRedirects, boolean enableCircularRedirects) {

		this.httpClient.getParams().setBooleanParameter(
				"http.protocol.reject-relative-redirect",
				!enableRelativeRedirects);
		this.httpClient.getParams().setBooleanParameter(
				"http.protocol.allow-circular-redirects",
				enableCircularRedirects);
		this.httpClient.setRedirectHandler(new MyRedirectHandler(
				enableRedirects));
	}

	public void setEnableRedirects(boolean enableRedirects,
			boolean enableRelativeRedirects) {

		setEnableRedirects(enableRedirects, enableRelativeRedirects, true);
	}

	public void setEnableRedirects(boolean enableRedirects) {

		setEnableRedirects(enableRedirects, enableRedirects, enableRedirects);
	}

	public void setRedirectHandler(RedirectHandler customRedirectHandler) {

		this.httpClient.setRedirectHandler(customRedirectHandler);
	}

	public void setUserAgent(String userAgent) {

		HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
	}

	public int getMaxConnections() {

		return this.maxConnections;
	}

	public void setMaxConnections(int maxConnections) {

		if (maxConnections < 1)
			maxConnections = 10;
		this.maxConnections = maxConnections;
		HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(this.maxConnections));
	}

	@Deprecated
	public int getTimeout() {

		return this.connectTimeout;
	}

	public void setTimeout(int value) {

		value = value < 1000 ? 10000 : value;
		setConnectTimeout(value);
		setResponseTimeout(value);
	}

	public int getConnectTimeout() {

		return this.connectTimeout;
	}

	public void setConnectTimeout(int value) {

		this.connectTimeout = (value < 1000 ? 10000 : value);
		HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, this.connectTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams,
				this.connectTimeout);
	}

	public int getResponseTimeout() {

		return this.responseTimeout;
	}

	public void setResponseTimeout(int value) {

		this.responseTimeout = (value < 1000 ? 10000 : value);
		HttpParams httpParams = this.httpClient.getParams();
		HttpConnectionParams.setSoTimeout(httpParams, this.responseTimeout);
	}

	public void setProxy(String hostname, int port) {

		HttpHost proxy = new HttpHost(hostname, port);
		HttpParams httpParams = this.httpClient.getParams();
		httpParams.setParameter("http.route.default-proxy", proxy);
	}

	public void setProxy(String hostname, int port, String username,
			String password) {

		this.httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(hostname, port),
				new UsernamePasswordCredentials(username, password));

		HttpHost proxy = new HttpHost(hostname, port);
		HttpParams httpParams = this.httpClient.getParams();
		httpParams.setParameter("http.route.default-proxy", proxy);
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {

		this.httpClient.getConnectionManager().getSchemeRegistry()
				.register(new Scheme("https", sslSocketFactory, 443));
	}

	public void setMaxRetriesAndTimeout(int retries, int timeout) {
		// for ip retry, we just retry once here
		this.httpClient
				.setHttpRequestRetryHandler(new RetryHandler(1, timeout));
	}

	public void removeAllHeaders() {

		this.clientHeaderMap.clear();
	}

	public void addHeader(String header, String value) {

		this.clientHeaderMap.put(header, value);
	}

	public void removeHeader(String header) {

		this.clientHeaderMap.remove(header);
	}

	public void setBasicAuth(String username, String password) {

		setBasicAuth(username, password, false);
	}

	public void setBasicAuth(String username, String password, boolean preemtive) {

		setBasicAuth(username, password, null, preemtive);
	}

	public void setBasicAuth(String username, String password, AuthScope scope) {

		setBasicAuth(username, password, scope, false);
	}

	public void setBasicAuth(String username, String password, AuthScope scope,
			boolean preemtive) {

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		setCredentials(scope, credentials);
		setAuthenticationPreemptive(preemtive);
	}

	public void setCredentials(AuthScope authScope, Credentials credentials) {

		if (credentials == null) {
			Log.d("AsyncHttpClient",
					"Provided credentials are null, not setting");
			return;
		}
		this.httpClient.getCredentialsProvider().setCredentials(
				authScope == null ? AuthScope.ANY : authScope, credentials);
	}

	public void setAuthenticationPreemptive(boolean isPreemtive) {

		if (isPreemtive)
			this.httpClient.addRequestInterceptor(
					new PreemtiveAuthorizationHttpRequestInterceptor(), 0);
		else
			this.httpClient
					.removeRequestInterceptorByClass(PreemtiveAuthorizationHttpRequestInterceptor.class);
	}

	@Deprecated
	public void clearBasicAuth() {

		clearCredentialsProvider();
	}

	public void clearCredentialsProvider() {

		this.httpClient.getCredentialsProvider().clear();
	}

	public void cancelRequests(final Context context,
			final boolean mayInterruptIfRunning) {

		if (context == null) {
			Log.e("AsyncHttpClient", "Passed null Context to cancelRequests");
			return;
		}
		Runnable r = new Runnable() {

			@Override
			public void run() {

				List<RequestHandle> requestList = AsyncHttpClient.this.requestMap
						.get(context);
				if (requestList != null) {
					for (RequestHandle requestHandle : requestList) {
						requestHandle.cancel(mayInterruptIfRunning);
					}
					AsyncHttpClient.this.requestMap.remove(context);
				}
			}

		};
		if (Looper.myLooper() == Looper.getMainLooper())
			new Thread(r).start();
		else
			r.run();
	}

	public void cancelAllRequests(boolean mayInterruptIfRunning) {

		for (List<RequestHandle> requestList : this.requestMap.values()) {
			if (requestList != null) {
				for (RequestHandle requestHandle : requestList) {
					requestHandle.cancel(mayInterruptIfRunning);
				}
			}
		}
		this.requestMap.clear();
	}

	public RequestHandle head(String url,
			ResponseHandlerInterface responseHandler) {

		return head(null, url, null, responseHandler);
	}

	public RequestHandle head(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {

		return head(null, url, params, responseHandler);
	}

	public RequestHandle head(Context context, String url,
			ResponseHandlerInterface responseHandler) {

		return head(context, url, null, responseHandler);
	}

	public RequestHandle head(Context context, String url,
			RequestParams params, ResponseHandlerInterface responseHandler) {

		return sendRequest(this.httpClient, this.httpContext, new HttpHead(
				getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)),
				null, responseHandler, context);
	}

	public RequestHandle head(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler,LogRecord record) {

		HttpUriRequest request = new HttpHead(getUrlWithQueryString(
				this.isUrlEncodingEnabled, url, params));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.httpClient, this.httpContext, request, null,
				responseHandler, context,record);
	}

	public RequestHandle get(String url,
			ResponseHandlerInterface responseHandler) {

		return get(null, url, null, responseHandler);
	}

	public RequestHandle get(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {

		return get(null, url, params, responseHandler);
	}

	public RequestHandle get(Context context, String url,
			ResponseHandlerInterface responseHandler) {

		return get(context, url, null, responseHandler);
	}

	public RequestHandle get(Context context, String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {

		return sendRequest(this.httpClient, this.httpContext, new HttpGet(
				getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)),
				null, responseHandler, context);
	}

	public RequestHandle get(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler,LogRecord record) {

		HttpUriRequest request = new HttpGet(getUrlWithQueryString(
				this.isUrlEncodingEnabled, url, params));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.httpClient, this.httpContext, request, null,
				responseHandler, context,record);
	}

	public RequestHandle post(String url,
			ResponseHandlerInterface responseHandler) {

		return post(null, url, null, responseHandler);
	}

	public RequestHandle post(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {

		return post(null, url, params, responseHandler);
	}

	public RequestHandle post(Context context, String url,
			RequestParams params, ResponseHandlerInterface responseHandler) {

		return post(context, url, paramsToEntity(params, responseHandler),
				null, responseHandler);
	}

	public RequestHandle post(Context context, String url, HttpEntity entity,
			String contentType, ResponseHandlerInterface responseHandler) {

		return sendRequest(
				this.httpClient,
				this.httpContext,
				addEntityToRequestBase(
						new HttpPost(URI.create(url).normalize()), entity),
				contentType, responseHandler, context);
	}

	public RequestHandle post(Context context, String url, Header[] headers,
			RequestParams params, String contentType,
			ResponseHandlerInterface responseHandler) {

		HttpEntityEnclosingRequestBase request = new HttpPost(URI.create(url)
				.normalize());
		if (params != null)
			request.setEntity(paramsToEntity(params, responseHandler));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.httpClient, this.httpContext, request,
				contentType, responseHandler, context);
	}

	public RequestHandle post(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			ResponseHandlerInterface responseHandler,LogRecord record) {

		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPost(URI.create(url).normalize()), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.httpClient, this.httpContext, request,
				contentType, responseHandler, context,record);
	}

	public RequestHandle put(String url,
			ResponseHandlerInterface responseHandler) {

		return put(null, url, null, responseHandler);
	}

	public RequestHandle put(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {

		return put(null, url, params, responseHandler);
	}

	public RequestHandle put(Context context, String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {

		return put(context, url, paramsToEntity(params, responseHandler), null,
				responseHandler);
	}

	public RequestHandle put(Context context, String url, HttpEntity entity,
			String contentType, ResponseHandlerInterface responseHandler) {

		return sendRequest(
				this.httpClient,
				this.httpContext,
				addEntityToRequestBase(
						new HttpPut(URI.create(url).normalize()), entity),
				contentType, responseHandler, context);
	}

	public RequestHandle put(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			ResponseHandlerInterface responseHandler, LogRecord record) {

		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPut(URI.create(url).normalize()), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.httpClient, this.httpContext, request,
				contentType, responseHandler, context, record);
	}

	public RequestHandle delete(String url,
			ResponseHandlerInterface responseHandler) {

		return delete(null, url, responseHandler);
	}

	public RequestHandle delete(Context context, String url,
			ResponseHandlerInterface responseHandler) {

		HttpDelete delete = new HttpDelete(URI.create(url).normalize());
		return sendRequest(this.httpClient, this.httpContext, delete, null,
				responseHandler, context);
	}

	public RequestHandle delete(Context context, String url, Header[] headers,
			ResponseHandlerInterface responseHandler,LogRecord record) {

		HttpDelete delete = new HttpDelete(URI.create(url).normalize());
		if (headers != null)
			delete.setHeaders(headers);
		return sendRequest(this.httpClient, this.httpContext, delete, null,
				responseHandler, context,record);
	}

	public RequestHandle delete(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler) {

		HttpDelete httpDelete = new HttpDelete(getUrlWithQueryString(
				this.isUrlEncodingEnabled, url, params));
		if (headers != null)
			httpDelete.setHeaders(headers);
		return sendRequest(this.httpClient, this.httpContext, httpDelete, null,
				responseHandler, context);
	}

	protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType, ResponseHandlerInterface responseHandler,
			Context context, LogRecord record) {

		return new AsyncHttpRequest(client, httpContext, uriRequest,
				responseHandler,record);
	}

	protected RequestHandle sendRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType, ResponseHandlerInterface responseHandler,
			Context context) {
		return this.sendRequest(client, httpContext, uriRequest, contentType,
				responseHandler, context, null);
	}

	protected RequestHandle sendRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType, ResponseHandlerInterface responseHandler,
			Context context, LogRecord record) {

		if (uriRequest == null) {
			throw new IllegalArgumentException(
					"HttpUriRequest must not be null");
		}

		if (responseHandler == null) {
			throw new IllegalArgumentException(
					"ResponseHandler must not be null");
		}

		if (responseHandler.getUseSynchronousMode()) {
			throw new IllegalArgumentException(
					"Synchronous ResponseHandler used in AsyncHttpClient. You should create your response handler in a looper thread or use SyncHttpClient instead.");
		}

		if (contentType != null) {
			if (((uriRequest instanceof HttpEntityEnclosingRequestBase))
					&& (((HttpEntityEnclosingRequestBase) uriRequest)
							.getEntity() != null))
				Log.w("AsyncHttpClient",
						"Passed contentType will be ignored because HttpEntity sets content type");
			else {
				uriRequest.setHeader("Content-Type", contentType);
			}
		}

		responseHandler.setRequestHeaders(uriRequest.getAllHeaders());
		responseHandler.setRequestURI(uriRequest.getURI());

		AsyncHttpRequest request = newAsyncHttpRequest(client, httpContext,
				uriRequest, contentType, responseHandler, context,record);
		this.threadPool.submit(request);
		RequestHandle requestHandle = new RequestHandle(request);

		if (context != null) {
			List<RequestHandle> requestList = this.requestMap.get(context);
			synchronized (this.requestMap) {
				if (requestList == null) {
					requestList = Collections
							.synchronizedList(new LinkedList<RequestHandle>());
					this.requestMap.put(context, requestList);
				}
			}

			if ((responseHandler instanceof RangeFileAsyncHttpResponseHandler)) {
				((RangeFileAsyncHttpResponseHandler) responseHandler)
						.updateRequestHeaders(uriRequest);
			}
			requestList.add(requestHandle);

			Iterator<RequestHandle> iterator = requestList.iterator();
			while (iterator.hasNext()) {
				if (((RequestHandle) iterator.next())
						.shouldBeGarbageCollected()) {
					iterator.remove();
				}
			}
		}

		return requestHandle;
	}

	public void setURLEncodingEnabled(boolean enabled) {

		this.isUrlEncodingEnabled = enabled;
	}

	public static String getUrlWithQueryString(boolean shouldEncodeUrl,
			String url, RequestParams params) {

		if (url == null) {
			return null;
		}
		if (shouldEncodeUrl) {
			url = url.replace(" ", "%20");
		}
		if (params != null) {
			String paramString = params.getParamString().trim();

			if ((!paramString.equals("")) && (!paramString.equals("?"))) {
				url = new StringBuilder().append(url)
						.append(url.contains("?") ? "&" : "?").toString();
				url = new StringBuilder().append(url).append(paramString)
						.toString();
			}
		}

		return url;
	}

	public static boolean isInputStreamGZIPCompressed(
			PushbackInputStream inputStream) throws IOException {

		if (inputStream == null) {
			return false;
		}
		byte[] signature = new byte[2];
		int readStatus = inputStream.read(signature);
		inputStream.unread(signature);
		int streamHeader = signature[0] & 0xFF | signature[1] << 8 & 0xFF00;
		return (readStatus == 2) && (35615 == streamHeader);
	}

	public static void silentCloseInputStream(InputStream is) {

		try {
			if (is != null)
				is.close();
		} catch (IOException e) {
			Log.w("AsyncHttpClient", "Cannot close input stream", e);
		}
	}

	public static void silentCloseOutputStream(OutputStream os) {

		try {
			if (os != null)
				os.close();
		} catch (IOException e) {
			Log.w("AsyncHttpClient", "Cannot close output stream", e);
		}
	}

	private HttpEntity paramsToEntity(RequestParams params,
			ResponseHandlerInterface responseHandler) {

		HttpEntity entity = null;
		try {
			if (params != null)
				entity = params.getEntity(responseHandler);
		} catch (IOException e) {
			if (responseHandler != null)
				responseHandler.sendFailureMessage(0, null, null, e);
			else {
				e.printStackTrace();
			}
		}

		return entity;
	}

	public boolean isUrlEncodingEnabled() {

		return this.isUrlEncodingEnabled;
	}

	private HttpEntityEnclosingRequestBase addEntityToRequestBase(
			HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {

		if (entity != null) {
			requestBase.setEntity(entity);
		}

		return requestBase;
	}

	public static void endEntityViaReflection(HttpEntity entity) {

		if ((entity instanceof HttpEntityWrapper))
			try {
				Field f = null;
				Field[] fields = HttpEntityWrapper.class.getDeclaredFields();
				for (Field ff : fields) {
					if (ff.getName().equals("wrappedEntity")) {
						f = ff;
						break;
					}
				}
				if (f != null) {
					f.setAccessible(true);
					HttpEntity wrapped = (HttpEntity) f.get(entity);
					if (wrapped != null)
						wrapped.consumeContent();
				}
			} catch (Throwable t) {
				Log.e("AsyncHttpClient", "wrappedEntity consume", t);
			}
	}

	private static class InflatingEntity extends HttpEntityWrapper {

		InputStream wrappedStream;
		PushbackInputStream pushbackStream;
		GZIPInputStream gzippedStream;

		public InflatingEntity(HttpEntity wrapped) {

			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {

			this.wrappedStream = this.wrappedEntity.getContent();
			this.pushbackStream = new PushbackInputStream(this.wrappedStream, 2);
			if (AsyncHttpClient
					.isInputStreamGZIPCompressed(this.pushbackStream)) {
				this.gzippedStream = new GZIPInputStream(this.pushbackStream);
				return this.gzippedStream;
			}
			return this.pushbackStream;
		}

		@Override
		public long getContentLength() {

			return this.wrappedEntity == null ? 0L : this.wrappedEntity
					.getContentLength();
		}

		@Override
		public void consumeContent() throws IOException {

			AsyncHttpClient.silentCloseInputStream(this.wrappedStream);
			AsyncHttpClient.silentCloseInputStream(this.pushbackStream);
			AsyncHttpClient.silentCloseInputStream(this.gzippedStream);
			super.consumeContent();
		}

	}

}
