package com.ksyun.ks3.services;

import android.content.Context;
import android.util.Log;

import com.ksyun.ks3.auth.AuthEvent;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Ks3HttpExector {
	private AsyncHttpClient client;

	public void invoke(Authorization auth, final Ks3HttpRequest request,
			final AsyncHttpResponseHandler resultHandler,
			Ks3ClientConfiguration clientConfiguration, final Context context,
			String endpoint, AuthListener authListener, Boolean isUseAsyncMode) {
		/* Configure AsyncHttpClient */
		if (clientConfiguration != null) {
			if (isUseAsyncMode) {
				client = AsyncHttpClientFactory
						.getInstance(clientConfiguration);
			} else {
				client = SyncHttpClientFactory.getInstance(clientConfiguration);
			}
		} else {
			if (isUseAsyncMode) {
				client = AsyncHttpClientFactory.getInstance();
			} else {
				client = SyncHttpClientFactory.getInstance();
			}
		}
		request.setAuthorization(auth);
		if (request.getBucketname() != null) {
			request.setEndpoint(request.getBucketname() + "." + endpoint);
		} else {
			request.setEndpoint(endpoint);
		}
		if (authListener != null) {
			request.setAuthListener(authListener);
			setUpRequsetInBackground(request, new Ks3AuthHandler() {

				@Override
				public void onSuccess(AuthEvent event) {
					doRequset(request, context, resultHandler);
				}

				@Override
				public void onFailure(AuthEvent event) {
					Log.d("eflake", event.getContent());
					throw new Ks3ClientException("Make requset failed");
				}
			}, resultHandler);
		} else {
			request.completeRequset(null, resultHandler);
			doRequset(request, context, resultHandler);
		}

	}

	protected void doRequset(Ks3HttpRequest request, Context context,
			AsyncHttpResponseHandler resultHandler) {
		// For test
		LogShow(request);

		switch (request.getHttpMethod()) {
		case GET:
			client.get(context, request.getAsyncHttpRequestParam().getUrl(),
					request.getAsyncHttpRequestParam().getHeader(), null,
					resultHandler);
			break;
		case POST:
			client.post(context, request.getAsyncHttpRequestParam().getUrl(),
					request.getAsyncHttpRequestParam().getHeader(),
					request.getEntity(), request.getContentType(),
					resultHandler);
			break;
		case PUT:
			client.put(context, request.getAsyncHttpRequestParam().getUrl(),
					request.getAsyncHttpRequestParam().getHeader(),
					request.getEntity(), request.getContentType(),
					resultHandler);
			break;
		case DELETE:
			client.delete(context, request.getAsyncHttpRequestParam().getUrl(),
					request.getAsyncHttpRequestParam().getHeader(),
					resultHandler);
			break;
		case HEAD:
			client.head(context, request.getAsyncHttpRequestParam().getUrl(),
					request.getAsyncHttpRequestParam().getHeader(), null,
					resultHandler);
			break;
		default:
			break;
		}
	}

	private void setUpRequsetInBackground(final Ks3HttpRequest request,
			final Ks3AuthHandler ks3AuthHandler,
			final AsyncHttpResponseHandler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				request.completeRequset(ks3AuthHandler, handler);
			}
		}).start();
	}

	private void LogShow(Ks3HttpRequest request) {
		request.getAsyncHttpRequestParam().getUrl();
		request.getAsyncHttpRequestParam().getHeader();
		request.getAsyncHttpRequestParam().getParams();
		StringBuffer sb = new StringBuffer();
		sb.append("**url** " + request.getAsyncHttpRequestParam().getUrl())
				.append("\n");
		sb.append("**heads**").append("\n");
		for (int i = 0; i < request.getAsyncHttpRequestParam().getHeader().length; i++) {
			sb.append(
					request.getAsyncHttpRequestParam().getHeader()[i].getName())
					.append("=>")
					.append(request.getAsyncHttpRequestParam().getHeader()[i]
							.getValue()).append("\n");
		}
		Log.e("eflake", sb.toString());
	}

	public void cancel(Context context) {
		client.cancelRequests(context, true);
	}

	public void pause(Context context) {
		client.cancelRequests(context, true);
	}

}
