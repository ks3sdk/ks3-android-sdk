package com.loopj.android.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public abstract class SaxAsyncHttpResponseHandler<T extends DefaultHandler> extends AsyncHttpResponseHandler
{

	private T handler = null;
	private static final String LOG_TAG = "SaxAsyncHttpResponseHandler";

	public SaxAsyncHttpResponseHandler(T t)
	{

		if (t == null) {
			throw new Error("null instance of <T extends DefaultHandler> passed to constructor");
		}
		this.handler = t;
	}

	@Override
	protected byte[] getResponseData(HttpEntity entity, int statusCode)
			throws IOException
	{

		if (entity != null) {
			InputStream instream = entity.getContent();
			InputStreamReader inputStreamReader = null;
			if (instream != null)
				try {
					SAXParserFactory sfactory = SAXParserFactory.newInstance();
					SAXParser sparser = sfactory.newSAXParser();
					XMLReader rssReader = sparser.getXMLReader();
					rssReader.setContentHandler(this.handler);
					inputStreamReader = new InputStreamReader(instream, "UTF-8");
					rssReader.parse(new InputSource(inputStreamReader));
				} catch (SAXException e) {
					Log.e("SaxAsyncHttpResponseHandler", "getResponseData exception", e);
				} catch (ParserConfigurationException e) {
					Log.e("SaxAsyncHttpResponseHandler", "getResponseData exception", e);
				} finally {
					AsyncHttpClient.silentCloseInputStream(instream);
					if (inputStreamReader != null)
						try {
							inputStreamReader.close();
						} catch (IOException e) {
						}
				}
		}
		return null;
	}

	public abstract void onSuccess(int paramInt, Header[] paramArrayOfHeader, T paramT);

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
	{

		onSuccess(statusCode, headers, this.handler);
	}

	public abstract void onFailure(int paramInt, Header[] paramArrayOfHeader, T paramT);

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
	{

		onSuccess(statusCode, headers, this.handler);
	}
}
