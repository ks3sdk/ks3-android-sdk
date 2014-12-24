package com.ksyun.ks3.services.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.impl.cookie.DateParseException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ksyun.ks3.model.Bucket;
import com.ksyun.ks3.model.Owner;
import com.ksyun.ks3.util.DateUtil;
import com.ksyun.ks3.util.StringUtils;

public abstract class ListBucketsResponceHandler extends Ks3HttpResponceHandler {

	private ArrayList<Bucket> result;
	private Owner owner;
	private Bucket bucket;

	public abstract void onFailure(int statesCode, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,ArrayList<Bucket> resultList);

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		onFailure(statesCode, responceHeaders, response == null ? "" :new String(response), throwable);
	}
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		ArrayList<Bucket> buckets = parseXml(responceHeaders, response);
		onSuccess(statesCode, responceHeaders, buckets);
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}

	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}
	
	
	private ArrayList<Bucket> parseXml(Header[] responceHeaders, byte[] response) {
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parse = factory.newPullParser();
			parse.setInput(new ByteArrayInputStream(response), "UTF-8");
			int eventType = parse.getEventType();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String nodeName = parse.getName();
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					result = new ArrayList<Bucket>();
					break;
				case XmlPullParser.END_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					if (nodeName.equalsIgnoreCase("Owner")) {
						owner = new Owner();
					}
					if (nodeName.equalsIgnoreCase("ID")) {
						owner.setId(parse.nextText());
					}
					if (nodeName.equalsIgnoreCase("DisplayName")) {
						owner.setDisplayName(parse.nextText());
					}

					if (nodeName.equalsIgnoreCase("Bucket")) {
						bucket = new Bucket();
					}
					if (nodeName.equalsIgnoreCase("Name")) {
						bucket.setName(parse.nextText());
					}
					if (nodeName.equalsIgnoreCase("CreationDate")) {
						String dateStr = parse.nextText();
						if(!StringUtils.isBlank(dateStr))
							bucket.setCreationDate(DateUtil.ConverToDate(dateStr));
					}
					break;
				case XmlPullParser.END_TAG:
					if (nodeName.equalsIgnoreCase("Bucket")) {
						bucket.setOwner(owner);
						result.add(bucket);
					}

					break;
				case XmlPullParser.TEXT:

					break;
				default:
					break;
				}
				eventType = parse.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DateParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
