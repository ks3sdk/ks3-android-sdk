package com.ksyun.loopj.android.http;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import android.util.Log;

public abstract class BinaryHttpResponseHandler extends AsyncHttpResponseHandler
{

	private static final String LOG_TAG = "BinaryHttpResponseHandler";
	private String[] mAllowedContentTypes = { "application/octet-stream", "image/jpeg", "image/png", "image/gif" };

	public String[] getAllowedContentTypes()
	{

		return this.mAllowedContentTypes;
	}

	public BinaryHttpResponseHandler()
	{

	}

	public BinaryHttpResponseHandler(String[] allowedContentTypes)
	{

		if (allowedContentTypes != null)
			this.mAllowedContentTypes = allowedContentTypes;
		else
			Log.e("BinaryHttpResponseHandler", "Constructor passed allowedContentTypes was null !");
	}

	@Override
	public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte);

	@Override
	public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, byte[] paramArrayOfByte, Throwable paramThrowable);

	@Override
	public final void sendResponseMessage(HttpResponse response)
			throws IOException
	{

		StatusLine status = response.getStatusLine();
		Header[] contentTypeHeaders = response.getHeaders("Content-Type");
		if (contentTypeHeaders.length != 1)
		{
			sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), null, new HttpResponseException(status.getStatusCode(), "None, or more than one, Content-Type Header found!"));

			return;
		}
		Header contentTypeHeader = contentTypeHeaders[0];
		boolean foundAllowedContentType = false;
		for (String anAllowedContentType : getAllowedContentTypes()) {
			try {
				if (Pattern.matches(anAllowedContentType, contentTypeHeader.getValue()))
					foundAllowedContentType = true;
			} catch (PatternSyntaxException e) {
				Log.e("BinaryHttpResponseHandler", "Given pattern is not valid: " + anAllowedContentType, e);
			}
		}
		if (!foundAllowedContentType)
		{
			sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), null, new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!"));

			return;
		}
		super.sendResponseMessage(response);
	}
}
