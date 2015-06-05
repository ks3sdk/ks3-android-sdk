package com.loopj.android.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.spec.EncodedKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.LogRecord;
import com.ksyun.ks3.services.SafetyIpClient;
import com.ksyun.ks3.services.request.ListObjectsRequest;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.PhoneInfoUtils;
import com.ksyun.ks3.util.StringUtils;

public class AsyncHttpRequest implements Runnable {

	private final AbstractHttpClient client;
	private final HttpContext context;
	private final HttpUriRequest request;
	private final ResponseHandlerInterface responseHandler;
	private int executionCount;
	private boolean isCancelled;
	private boolean cancelIsNotified;
	private boolean isFinished;
	private boolean isRequestPreProcessed;
	private LogRecord record = null;

	public AsyncHttpRequest(AbstractHttpClient client, HttpContext context,
			HttpUriRequest request, ResponseHandlerInterface responseHandler,
			LogRecord record) {
		this.client = client;
		this.context = context;
		this.request = request;
		this.responseHandler = responseHandler;
		this.context.setAttribute("http.request", this.request);
		this.record = record;
	}

	public void onPreProcessRequest(AsyncHttpRequest request) {

	}

	public void onPostProcessRequest(AsyncHttpRequest request)
			throws IOException {

	}

	@Override
	public void run() {

		if (isCancelled()) {
			return;
		}

		if (!this.isRequestPreProcessed) {
			this.isRequestPreProcessed = true;
			onPreProcessRequest(this);
		}

		if (isCancelled()) {
			return;
		}

		if (this.responseHandler != null) {
			this.responseHandler.sendStartMessage();
		}

		if (isCancelled()) {
			return;
		}
		try {
			makeRequestWithRetries();
		} catch (IOException e) {
			if ((!isCancelled()) && (this.responseHandler != null))
				this.responseHandler.sendFailureMessage(0, null, null, e);
			else {
				Log.e(Constants.LOG_TAG,
						"makeRequestWithRetries returned error, but handler is null",
						e);
			}
		} finally {
			// modified , fixed steam close problem
//			if (this.request != null
//					&& this.request instanceof HttpEntityEnclosingRequestBase) {
//				try {
//					HttpEntity entity = ((HttpEntityEnclosingRequestBase) this.request)
//							.getEntity();
//					if (entity != null && entity.isStreaming())
//						entity.consumeContent();
//				} catch (IOException e) {
//					Log.e(Constants.LOG_TAG,
//							"consume stream entity failed , cause exception :"
//									+ e);
//				}
//			}
		}

		if (isCancelled()) {
			return;
		}

		if (this.responseHandler != null) {
			this.responseHandler.sendFinishMessage();
		}

		if (isCancelled()) {
			return;
		}

		try {
			onPostProcessRequest(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.isFinished = true;
	}

	private void makeRequest() throws IOException {

		if (isCancelled()) {
			return;
		}

		if (this.request.getURI().getScheme() == null) {
			throw new MalformedURLException("No valid URI scheme was provided");
		}
		long send_before_time = System.currentTimeMillis();
		if (record != null) {
			record.setSend_before_time(send_before_time);
		}
		HttpResponse response = this.client.execute(this.request, this.context);
		long send_first_data_time = System.currentTimeMillis();
		if (record != null) {
			record.setSend_first_data_time(send_first_data_time);
			this.responseHandler.sendLogRecordMessage(record);
		}
		if ((isCancelled()) || (this.responseHandler == null)) {
			return;
		}
		this.responseHandler.onPreProcessResponse(this.responseHandler,
				response);

		if (isCancelled()) {
			return;
		}
		this.responseHandler.sendResponseMessage(response);
		if (isCancelled()) {
			return;
		}

		this.responseHandler.onPostProcessResponse(this.responseHandler,
				response);
	}

	private void makeRequestWithRetries() throws IOException {

		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = this.client
				.getHttpRequestRetryHandler();
		try {
			while (retry) {
				try {
					makeRequest();
					return;
				} catch (UnknownHostException e) {
					// For deal with DNS fail
					Log.i(Constants.LOG_TAG, "DNS parsed failure");
					String originUrl = this.request.getURI().toString();
					int networkOperatorType = PhoneInfoUtils.getProvidersType();
					String ipStr = null;
					if (SafetyIpClient.ipModel != null) {
						switch (networkOperatorType) {
						case PhoneInfoUtils.TYPE_CHINA_MOBILE:
							ipStr = SafetyIpClient.ipModel
									.getCHINA_MOBILE_SERVER_IP();
							break;
						case PhoneInfoUtils.TYPE_CHINA_UNICOM:
							ipStr = SafetyIpClient.ipModel
									.getCHINA_MOBILE_SERVER_IP();
							break;
						case PhoneInfoUtils.TYPE_CHINA_TELCOM:
							ipStr = SafetyIpClient.ipModel
									.getCHINA_TELECOM_SERVER_IP();
							break;
						default:
							break;
						}

						String ipUrl = SafetyIpClient.VhostToPath(originUrl,
								ipStr, true);
						URI ipUri = new URI(ipUrl);
						// String vhostUrl =
						// SafetyIpClient.PathToVhost(originUrl,
						// "192.168.1.1", true);
						 Log.d(Constants.LOG_TAG, ipUrl);
						cause = e;
						// cause = new
						// IOException("UnknownHostException exception: " +
						// e.getMessage());
						// modify request
						HttpRequestBase base = (HttpRequestBase) this.request;
						base.setHeader(HttpHeaders.Host.toString(),
								"kss.ksyun.com");
						String pathStr = SafetyIpClient.getRealPath(ipUrl);

						base.setURI(URIUtils.createURI(base.getURI()
								.getScheme(), ipUri.getHost(), base.getURI()
								.getPort(), pathStr, base.getURI()
								.getQuery(), base.getURI().getFragment()));
						// set resend request
						Log.d(Constants.LOG_TAG, "dns failed, changed url = "
								+ base.getURI().toString() + ", host = "
								+ "kss.ksyun.com");
						this.context.setAttribute("http.request", base);
						retry = retryHandler.retryRequest(cause,
								++this.executionCount, this.context);
					} else {
						Log.d(Constants.LOG_TAG, "ip list is null");
						retry = false;
						return;
					}
				} catch (NullPointerException e) {
					cause = new IOException("NPE in HttpClient: "
							+ e.getMessage());
					retry = retryHandler.retryRequest(cause,
							++this.executionCount, this.context);
				} catch (IOException e) {
					if (isCancelled()) {
						return;
					}
					cause = e;
					retry = retryHandler.retryRequest(cause,
							++this.executionCount, this.context);
				}
				if ((retry) && (this.responseHandler != null))
					this.responseHandler.sendRetryMessage(this.executionCount);
			}
		} catch (Exception e) {
			Log.e("AsyncHttpRequest", "Unhandled exception origin cause", e);
			cause = new IOException("Unhandled exception: " + e.getMessage());
		}

		throw cause;
	}

	public boolean isCancelled() {

		if (this.isCancelled) {
			sendCancelNotification();
		}
		return this.isCancelled;
	}

	private synchronized void sendCancelNotification() {

		if ((!this.isFinished) && (this.isCancelled)
				&& (!this.cancelIsNotified)) {
			this.cancelIsNotified = true;
			if (this.responseHandler != null)
				this.responseHandler.sendCancelMessage();
		}
	}

	public boolean isDone() {
		return (isCancelled()) || (this.isFinished);
	}

	public boolean cancel(boolean mayInterruptIfRunning) {

		this.isCancelled = true;
		this.request.abort();
		return isCancelled();
	}

}
