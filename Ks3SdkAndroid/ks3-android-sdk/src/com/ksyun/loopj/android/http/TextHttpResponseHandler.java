package com.ksyun.loopj.android.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import android.util.Log;

public abstract class TextHttpResponseHandler extends AsyncHttpResponseHandler
{

	private static final String LOG_TAG = "TextHttpResponseHandler";

	public TextHttpResponseHandler()
	{

		this("UTF-8");
	}

	public TextHttpResponseHandler(String encoding)
	{

		setCharset(encoding);
	}

	public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, String paramString, Throwable paramThrowable);

	public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, String paramString);

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes)
	{

		onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable)
	{

		onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), throwable);
	}

	public static String getResponseString(byte[] stringBytes, String charset)
	{

		try
		{
			String toReturn = stringBytes == null ? null : new String(stringBytes, charset);
			if ((toReturn != null) && (toReturn.startsWith("﻿"))) {
				return toReturn.substring(1);
			}
			return toReturn;
		} catch (UnsupportedEncodingException e) {
			Log.e("TextHttpResponseHandler", "Encoding response into string failed", e);
		}
		return null;
	}
}
