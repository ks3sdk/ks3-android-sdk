package com.loopj.android.http;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

public class SyncHttpClient extends AsyncHttpClient
{

	public SyncHttpClient()
	{

		super(false, 80, 443);
	}

	public SyncHttpClient(int httpPort)
	{

		super(false, httpPort, 443);
	}

	public SyncHttpClient(int httpPort, int httpsPort)
	{

		super(false, httpPort, httpsPort);
	}

	public SyncHttpClient(boolean fixNoHttpResponseException, int httpPort, int httpsPort)
	{

		super(fixNoHttpResponseException, httpPort, httpsPort);
	}

	public SyncHttpClient(SchemeRegistry schemeRegistry)
	{

		super(schemeRegistry);
	}

	@Override
	protected RequestHandle sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context)
	{

		if (contentType != null) {
			uriRequest.addHeader("Content-Type", contentType);
		}

		responseHandler.setUseSynchronousMode(true);

		newAsyncHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context,null).run();

		return new RequestHandle(null);
	}
}
