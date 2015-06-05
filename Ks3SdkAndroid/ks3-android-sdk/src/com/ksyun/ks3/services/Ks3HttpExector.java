package com.ksyun.ks3.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.LogRecord;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.PhoneInfoUtils;
import com.ksyun.ks3.util.PhoneInfoUtils.PhoneInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

public class Ks3HttpExector {
	private AsyncHttpClient client;

	public void invoke(Authorization auth, final Ks3HttpRequest request,
			final AsyncHttpResponseHandler resultHandler,
			Ks3ClientConfiguration clientConfiguration, final Context context,
			String endpoint, AuthListener authListener, Boolean isUseAsyncMode,
			final LogRecord record) {
		/* Configure AsyncHttpClient */
		if (clientConfiguration != null) {
			client = AsyncHttpClientFactory.getInstance(clientConfiguration);
		} else {
			client = AsyncHttpClientFactory.getInstance();
		}
		request.setAuthorization(auth);
		if (request.getBucketname() != null) {
			request.setEndpoint(request.getBucketname() + "." + endpoint);
		} else {
			request.setEndpoint(endpoint);
		}
		// 异步
		if (isUseAsyncMode) {
			// Token形式
			if (authListener != null) {
				request.setAuthListener(authListener);
				setUpRequsetInBackground(request, resultHandler, record,
						context);
			}
			// AK&SK形式
			else {
				try {
					request.completeRequset(resultHandler);
				} catch (Ks3ClientException e) {
					resultHandler.onFailure(0, null, null, e);
					return;
				}

				doRequset(request, context, resultHandler, record);
			}
		}
		// 同步
		else {
			// Token形式
			if (authListener != null) {
				request.setAuthListener(authListener);
				setUpRequsetInBackground(request, resultHandler, record,
						context);
				// AK&SK形式
			} else {
				try {
					request.completeRequset(resultHandler);
				} catch (Ks3ClientException e) {
					resultHandler.onFailure(0, null, null, e);
					return;
				}
				doRequset(request, context, resultHandler, record);
			}
		}

	}

	protected void doRequset(Ks3HttpRequest request, Context context,
			AsyncHttpResponseHandler resultHandler, LogRecord record) {
		// For test
		LogShow(request);
		RequestHandle handler = null;
		PhoneInfo info = PhoneInfoUtils.getPhoneInfo(context);
		info.makeBasicRecord(record);
		Log.i(Constants.LOG_TAG, "requset url => " + request.getUrl()
				+ "\nmethod => " + request.getClass().getName());
		switch (request.getHttpMethod()) {
		case GET:
			handler = client.get(context, request.getAsyncHttpRequestParam()
					.getUrl(), request.getAsyncHttpRequestParam().getHeader(),
					null, resultHandler, record);
			break;
		case POST:
			handler = client.post(context, request.getAsyncHttpRequestParam()
					.getUrl(), request.getAsyncHttpRequestParam().getHeader(),
					request.getEntity(), request.getContentType(),
					resultHandler, record);
			break;
		case PUT:
			handler = client.put(context, request.getAsyncHttpRequestParam()
					.getUrl(), request.getAsyncHttpRequestParam().getHeader(),
					request.getEntity(), request.getContentType(),
					resultHandler, record);
			break;
		case DELETE:
			handler = client.delete(context, request.getAsyncHttpRequestParam()
					.getUrl(), request.getAsyncHttpRequestParam().getHeader(),
					resultHandler, record);
			break;
		case HEAD:
			handler = client.head(context, request.getAsyncHttpRequestParam()
					.getUrl(), request.getAsyncHttpRequestParam().getHeader(),
					null, resultHandler, record);
			break;
		default:
			Log.e(Constants.LOG_TAG, "unsupport http method ! ");
			break;
		}
		request.setRequestHandler(handler);
	}

	private void setUpRequsetInBackground(final Ks3HttpRequest request,
			final AsyncHttpResponseHandler resultHandler,
			final LogRecord record, Context context) {
		SetUpRequestAsyncTask task = new SetUpRequestAsyncTask(request,
				resultHandler, record, context);
		task.executeOnExecutor(Executors.newCachedThreadPool(), "");
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
		Log.i(Constants.LOG_TAG, sb.toString());
	}

	public void cancel(Context context) {
		client.cancelRequests(context, true);
	}

	public void pause(Context context) {
		client.cancelRequests(context, true);
	}

	public class SetUpRequestAsyncTask extends
			AsyncTask<String, Integer, Boolean> {

		private Ks3HttpRequest request;
		private AsyncHttpResponseHandler resultHandler;
		private LogRecord record;
		private InetAddress x;
		private Context context;
		private Throwable throwable;

		public SetUpRequestAsyncTask(Ks3HttpRequest request,
				AsyncHttpResponseHandler resultHandler, LogRecord record,
				Context context) {
			this.request = request;
			this.resultHandler = resultHandler;
			this.record = record;
			this.context = context;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = true;
			try {
				x = java.net.InetAddress.getByName(request.getEndpoint());
				record.setTarget_ip(x.getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
				Log.i(Constants.LOG_TAG,
						"Get host address failed,reason:" + e.getMessage());
			}
			try {
				request.completeRequset(resultHandler);
			} catch (Ks3ClientException e) {
				throwable.initCause(e);
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				doRequset(request, context, resultHandler, record);
			} else {
				resultHandler.onFailure(0, null, null, throwable);
			}
		}

	}

}
