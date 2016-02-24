package com.ksyun.loopj.android.http;

import org.apache.http.Header;

import android.util.Log;

public abstract class BaseJsonHttpResponseHandler<JSON_TYPE> extends
		TextHttpResponseHandler
{

	private static final String LOG_TAG = "BaseJsonHttpResponseHandler";

	public BaseJsonHttpResponseHandler()
	{

		this("UTF-8");
	}

	public BaseJsonHttpResponseHandler(String encoding)
	{

		super(encoding);
	}

	public abstract void onSuccess(int paramInt,
			Header[] paramArrayOfHeader, String paramString,
			JSON_TYPE paramJSON_TYPE);

	public abstract void onFailure(int paramInt,
			Header[] paramArrayOfHeader, Throwable paramThrowable,
			String paramString, JSON_TYPE paramJSON_TYPE);

	@Override
	public final void onSuccess(final int statusCode, final Header[] headers,
			final String responseString)
	{

		if (statusCode != 204) {
			Runnable parser = new Runnable()
			{

				@Override
				public void run() {

					try {
						final JSON_TYPE jsonResponse = BaseJsonHttpResponseHandler.this
								.parseResponse(responseString, false);
						BaseJsonHttpResponseHandler.this
								.postRunnable(new Runnable()
								{

									@Override
									public void run() {

										BaseJsonHttpResponseHandler.this
												.onSuccess(statusCode, headers,
														responseString,
														jsonResponse);
									}
								});
					} catch (final Throwable t) {
						Log.d("BaseJsonHttpResponseHandler",
								"parseResponse thrown an problem", t);
						BaseJsonHttpResponseHandler.this
								.postRunnable(new Runnable()
								{

									@Override
									public void run() {

										BaseJsonHttpResponseHandler.this
												.onFailure(statusCode, headers,
														t, responseString, null);
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
			onSuccess(statusCode, headers, null, null);
		}
	}

	@Override
	public final void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable)
	{

		if (responseString != null) {
			Runnable parser = new Runnable()
			{

				@Override
				public void run() {

					try {
						final JSON_TYPE jsonResponse = BaseJsonHttpResponseHandler.this.parseResponse(responseString, true);
						BaseJsonHttpResponseHandler.this.postRunnable(new Runnable()
						{

							@Override
							public void run() {

								BaseJsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, responseString, jsonResponse);
							}
						});
					} catch (Throwable t) {
						Log.d("BaseJsonHttpResponseHandler", "parseResponse thrown an problem", t);
						BaseJsonHttpResponseHandler.this.postRunnable(new Runnable()
						{

							@Override
							public void run() {

								BaseJsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, responseString, null);
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
			onFailure(statusCode, headers, throwable, null, null);
		}
	}

	protected abstract JSON_TYPE parseResponse(String paramString,
			boolean paramBoolean)
			throws Throwable;

}
