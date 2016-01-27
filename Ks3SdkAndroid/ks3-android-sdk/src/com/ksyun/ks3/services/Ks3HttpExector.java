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
import com.ksyun.ks3.util.ExceptionUtil;
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
			final LogRecord record, StringBuffer traceBuffer) {
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
						traceBuffer, context);
			}
			// AK&SK形式
			else {
				try {
					request.completeRequset(resultHandler);
				} catch (Ks3ClientException e) {
					resultHandler.appendTraceBuffer("Step ==> Client request set up error");
					resultHandler.appendTraceBuffer(ExceptionUtil.getStackMsg(e));
					resultHandler.onFailure(0, null, null, e);
					return;
				}
				doRequset(request, context, resultHandler, record, traceBuffer);
			}
		}
		// 同步
		else {
			// Token形式
			if (authListener != null) {
				request.setAuthListener(authListener);
				setUpRequsetInBackground(request, resultHandler, record,
						traceBuffer, context);
				// AK&SK形式
			} else {
				try {
					request.completeRequset(resultHandler);
				} catch (Ks3ClientException e) {
					resultHandler.appendTraceBuffer("Step ==> Client request set up error");
					resultHandler.appendTraceBuffer(ExceptionUtil.getStackMsg(e));
					resultHandler.onFailure(0, null, null, e);
					return;
				}
				doRequset(request, context, resultHandler, record, traceBuffer);
			}
		}

	}

	protected void doRequset(Ks3HttpRequest request, Context context,
			AsyncHttpResponseHandler resultHandler, LogRecord record,
			StringBuffer traceBuffer) {
		if (traceBuffer != null) {
			traceBuffer.append(LogShow(request));
		}
		RequestHandle handler = null;
		PhoneInfo info = PhoneInfoUtils.getPhoneInfo(context);
		info.makeBasicRecord(record);

		switch (request.getHttpMethod()) {
		case GET:
			handler = client.get(context, request.getAsyncHttpRequestParam()
					.getUrl(), request.getAsyncHttpRequestParam().getHeader(),
					null, resultHandler, record, traceBuffer, request
							.getBucketname());
			break;
		case POST:
			handler = client
					.post(context, request.getAsyncHttpRequestParam().getUrl(),
							request.getAsyncHttpRequestParam().getHeader(),
							request.getEntity(), request.getContentType(),
							resultHandler, record, traceBuffer,
							request.getBucketname());
			break;
		case PUT:
			handler = client
					.put(context, request.getAsyncHttpRequestParam().getUrl(),
							request.getAsyncHttpRequestParam().getHeader(),
							request.getEntity(), request.getContentType(),
							resultHandler, record, traceBuffer,
							request.getBucketname());
			break;
		case DELETE:
			handler = client
					.delete(context, request.getAsyncHttpRequestParam()
							.getUrl(), request.getAsyncHttpRequestParam()
							.getHeader(), resultHandler, record, traceBuffer,
							request.getBucketname());
			break;
		case HEAD:
			handler = client.head(context, request.getAsyncHttpRequestParam()
					.getUrl(), request.getAsyncHttpRequestParam().getHeader(),
					null, resultHandler, record, traceBuffer, request
							.getBucketname());
			break;
		default:
			Log.e(Constants.LOG_TAG, "unsupport http method ! ");
			break;
		}
		request.setRequestHandler(handler);
	}

	private void setUpRequsetInBackground(final Ks3HttpRequest request,
			final AsyncHttpResponseHandler resultHandler,
			final LogRecord record, StringBuffer traceBuffer, Context context) {
		SetUpRequestAsyncTask task = new SetUpRequestAsyncTask(request,
				resultHandler, record, traceBuffer, context);
		task.executeOnExecutor(Executors.newCachedThreadPool(), "");
	}

	private String LogShow(Ks3HttpRequest request) {
		request.getAsyncHttpRequestParam().getUrl();
		request.getAsyncHttpRequestParam().getHeader();
		request.getAsyncHttpRequestParam().getParams();
		StringBuffer sb = new StringBuffer();
		String className = request.getClass().getName();
		sb.append("Step ==> Make equest").append("\n");
		sb.append(
				"Method ==> "
						+ className.substring(className.lastIndexOf(".") + 1))
				.append("\n");
		sb.append("Requset Url ==> " + request.getUrl()).append("\n");
		sb.append("Heads Begin ==> ").append("\n");
		for (int i = 0; i < request.getAsyncHttpRequestParam().getHeader().length; i++) {
			sb.append("    ")
					.append(request.getAsyncHttpRequestParam().getHeader()[i]
							.getName())
					.append("=>")
					.append(request.getAsyncHttpRequestParam().getHeader()[i]
							.getValue()).append("\n");
		}
		sb.append("Heads End ==> ").append("\n");
		sb.append("Step ==> Execut async-http-client request").append("\n");
		Log.i(Constants.LOG_TAG, sb.toString());
		return sb.toString();
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
		private StringBuffer traceBuffer;

		public SetUpRequestAsyncTask(Ks3HttpRequest request,
				AsyncHttpResponseHandler resultHandler, LogRecord record,
				StringBuffer traceBuffer, Context context) {
			this.request = request;
			this.resultHandler = resultHandler;
			this.record = record;
			this.context = context;
			this.traceBuffer = traceBuffer;
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
				throwable = new Throwable();
				throwable.initCause(e);
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				doRequset(request, context, resultHandler, record, traceBuffer);
			} else {
				resultHandler.appendTraceBuffer("Step ==> Client request set up error");
				resultHandler.appendTraceBuffer(ExceptionUtil.getStackMsg(throwable.getCause()));
				resultHandler.onFailure(0, null, null, throwable);
			}
		}

	}

}
