package com.loopj.android.http;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public class JsonHttpResponseHandler extends TextHttpResponseHandler
{

	private static final String LOG_TAG = "JsonHttpResponseHandler";

	public JsonHttpResponseHandler()
	{

		super("UTF-8");
	}

	public JsonHttpResponseHandler(String encoding)
	{

		super(encoding);
	}

	public void onSuccess(int statusCode, Header[] headers, JSONObject response)
	{

		Log.w("JsonHttpResponseHandler", "onSuccess(int, Header[], JSONObject) was not overriden, but callback was received");
	}

	public void onSuccess(int statusCode, Header[] headers, JSONArray response)
	{

		Log.w("JsonHttpResponseHandler", "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
	}

	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
	{

		Log.w("JsonHttpResponseHandler", "onFailure(int, Header[], Throwable, JSONObject) was not overriden, but callback was received", throwable);
	}

	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
	{

		Log.w("JsonHttpResponseHandler", "onFailure(int, Header[], Throwable, JSONArray) was not overriden, but callback was received", throwable);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
	{

		Log.w("JsonHttpResponseHandler", "onFailure(int, Header[], String, Throwable) was not overriden, but callback was received", throwable);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String responseString)
	{

		Log.w("JsonHttpResponseHandler", "onSuccess(int, Header[], String) was not overriden, but callback was received");
	}

	@Override
	public final void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes)
	{

		if (statusCode != 204) {
			Runnable parser = new Runnable()
			{

				@Override
				public void run() {

					try {
						final Object jsonResponse = JsonHttpResponseHandler.this.parseResponse(responseBytes);
						JsonHttpResponseHandler.this.postRunnable(new Runnable()
						{

							@Override
							public void run() {

								if ((jsonResponse instanceof JSONObject))
									JsonHttpResponseHandler.this.onSuccess(statusCode, headers, (JSONObject) jsonResponse);
								else if ((jsonResponse instanceof JSONArray))
									JsonHttpResponseHandler.this.onSuccess(statusCode, headers, (JSONObject) jsonResponse);
								else if ((jsonResponse instanceof String))
									JsonHttpResponseHandler.this.onFailure(statusCode, headers, (String) jsonResponse, new JSONException("Response cannot be parsed as JSON data"));
								else
									JsonHttpResponseHandler.this.onFailure(statusCode, headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject) null);
							}
						});
					}
					catch (final JSONException ex) {
						JsonHttpResponseHandler.this.postRunnable(new Runnable()
						{

							@Override
							public void run() {

								JsonHttpResponseHandler.this.onFailure(statusCode, headers, ex, (JSONObject) null);
							}
						});
					}
				}
			};
			if (!getUseSynchronousMode()) {
				new Thread(parser).start();
			}
			else
				parser.run();
		}
		else {
			onSuccess(statusCode, headers, new JSONObject());
		}
	}

	@Override
	public final void onFailure(final int statusCode, final Header[] headers, final byte[] responseBytes, final Throwable throwable)
	{

		if (responseBytes != null) {
			Runnable parser = new Runnable()
			{

				@Override
				public void run() {

					try {
						final Object jsonResponse = JsonHttpResponseHandler.this.parseResponse(responseBytes);
						JsonHttpResponseHandler.this.postRunnable(new Runnable()
						{

							@Override
							public void run() {

								if ((jsonResponse instanceof JSONObject))
									JsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, (JSONObject) jsonResponse);
								else if ((jsonResponse instanceof JSONArray))
									JsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, (JSONArray) jsonResponse);
								else if ((jsonResponse instanceof String))
									JsonHttpResponseHandler.this.onFailure(statusCode, headers, (String) jsonResponse, throwable);
								else
									JsonHttpResponseHandler.this.onFailure(statusCode, headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject) null);
							}
						});
					}
					catch (final JSONException ex) {
						JsonHttpResponseHandler.this.postRunnable(new Runnable()
						{

							@Override
							public void run() {

								JsonHttpResponseHandler.this.onFailure(statusCode, headers, ex, (JSONObject) null);
							}
						});
					}
				}
			};
			if (!getUseSynchronousMode()) {
				new Thread(parser).start();
			}
			else
				parser.run();
		}
		else {
			Log.v("JsonHttpResponseHandler", "response body is null, calling onFailure(Throwable, JSONObject)");
			onFailure(statusCode, headers, throwable, (JSONObject) null);
		}
	}

	protected Object parseResponse(byte[] responseBody)
			throws JSONException
	{

		if (null == responseBody)
			return null;
		Object result = null;

		String jsonString = getResponseString(responseBody, getCharset());
		if (jsonString != null) {
			jsonString = jsonString.trim();
			if (jsonString.startsWith("﻿")) {
				jsonString = jsonString.substring(1);
			}
			if ((jsonString.startsWith("{")) || (jsonString.startsWith("["))) {
				result = new JSONTokener(jsonString).nextValue();
			}
		}
		if (result == null) {
			result = jsonString;
		}
		return result;
	}
}
