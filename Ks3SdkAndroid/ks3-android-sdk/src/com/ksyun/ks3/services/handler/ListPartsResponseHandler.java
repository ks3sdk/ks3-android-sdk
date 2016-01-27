package com.ksyun.ks3.services.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.Owner;
import com.ksyun.ks3.model.Part;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.LogClient;
import com.ksyun.ks3.services.LogUtil;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.DateUtil;
import com.ksyun.ks3.util.StringUtils;

public abstract class ListPartsResponseHandler extends Ks3HttpResponceHandler {

	private static String RESPONSE_TAG_PRIFIX = "ns2:";

	public abstract void onFailure(int statesCode, Ks3Error error,
			Header[] responceHeaders, String response, Throwable paramThrowable, StringBuffer stringBuffer);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,
			ListPartsResult listPartsResult, StringBuffer stringBuffer);

	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,
			byte[] response) {
		Log.i(Constants.LOG_TAG, "ListParts Request Success");
		LogUtil.setSuccessLog(statesCode, response, responceHeaders, record);
		try {
			LogClient.getInstance().put(record.toString());
		} catch (Ks3ClientException e) {
			e.printStackTrace();
		}
		this.onSuccess(statesCode, responceHeaders,
				parseXml(responceHeaders, response), getTraceBuffer());
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,
			byte[] response, Throwable throwable) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		Log.e(Constants.LOG_TAG,
				"ListParts Request Failed, Error Code: " + error.getErrorCode()
						+ ",Error Message:" + error.getErrorMessage());
		LogUtil.setFailureLog(statesCode, response, throwable, error, record);
		try {
			LogClient.getInstance().put(record.toString());
		} catch (Ks3ClientException e) {
			e.printStackTrace();
		}
		this.onFailure(statesCode, error, responceHeaders,
				response == null ? "" : new String(response), throwable, getTraceBuffer());
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {
	}

	@Override
	public final void onStart() {
	}

	@Override
	public final void onFinish() {
	}

	@Override
	public final void onCancel() {
	}

	private ListPartsResult parseXml(Header[] responceHeaders, byte[] response) {
		XmlPullParserFactory factory = null;
		ListPartsResult partsResult = null;
		Owner owner = null;
		Owner initiator = null;
		Part part = null;
		boolean isOwner = false;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parse = factory.newPullParser();
			parse.setInput(new ByteArrayInputStream(response), "UTF-8");
			int eventType = parse.getEventType();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String nodeName = parse.getName();
				if (!StringUtils.isBlank(nodeName)
						&& nodeName.startsWith(RESPONSE_TAG_PRIFIX)) {
					nodeName = nodeName.substring(4);
				}
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("ListPartsResult".equalsIgnoreCase(nodeName)) {
						partsResult = new ListPartsResult();
					}
					if ("Bucket".equalsIgnoreCase(nodeName)) {
						partsResult.setBucketname(parse.nextText());
					}
					if ("Key".equalsIgnoreCase(nodeName)) {
						partsResult.setKey(parse.nextText());
					}
					if ("UploadId".equalsIgnoreCase(nodeName)) {
						String upid = parse.nextText();
						partsResult.setUploadId(upid);
					}
					if ("Initiator".equalsIgnoreCase(nodeName)) {
						initiator = new Owner();
					}
					if ("Owner".equalsIgnoreCase(nodeName)) {
						isOwner = true;
						owner = new Owner();
					}
					if ("ID".equalsIgnoreCase(nodeName)) {
						if (isOwner) {
							owner.setId(parse.nextText());
						} else {
							initiator.setId(parse.nextText());
						}
					}
					if ("DisplayName".equalsIgnoreCase(nodeName)) {
						if (isOwner) {
							owner.setDisplayName(parse.nextText());
						} else {
							initiator.setDisplayName(parse.nextText());
						}
					}
					if ("StorageClass".equalsIgnoreCase(nodeName)) {
					}
					if ("PartNumberMarker".equalsIgnoreCase(nodeName)) {
						partsResult.setPartNumberMarker(parse.nextText());
					}
					if ("NextPartNumberMarker".equalsIgnoreCase(nodeName)) {
						partsResult.setNextPartNumberMarker(parse.nextText());
					}
					if ("MaxParts".equalsIgnoreCase(nodeName)) {
						partsResult.setMaxParts(parse.nextText());
					}
					if ("IsTruncated".equalsIgnoreCase(nodeName)) {
						partsResult.setTruncated(Boolean.valueOf(parse
								.nextText()));
					}
					if ("Part".equalsIgnoreCase(nodeName)) {
						part = new Part();
					}
					if ("PartNumber".equalsIgnoreCase(nodeName)) {
						part.setPartNumber(Integer.valueOf(parse.nextText()));
					}
					if ("LastModified".equalsIgnoreCase(nodeName)) {
						String dateStr = parse.nextText();
						if (!StringUtils.isBlank(dateStr))
							part.setLastModified(DateUtil.ConverToDate(dateStr));
					}
					if ("ETag".equalsIgnoreCase(nodeName)) {
						part.setETag(parse.nextText());
					}
					if ("Size".equalsIgnoreCase(nodeName)) {
						part.setSize(Long.valueOf(parse.nextText()));
					}
					if ("Encoding-Type".equalsIgnoreCase(nodeName)) {
						partsResult.setEncodingType(parse.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("ListPartsResult".equalsIgnoreCase(nodeName)) {

					}
					if ("Initiator".equalsIgnoreCase(nodeName)) {
						partsResult.setInitiator(initiator);
					}
					if ("Owner".equalsIgnoreCase(nodeName)) {
						isOwner = false;
						partsResult.setOwner(owner);
					}
					if ("Part".equalsIgnoreCase(nodeName)) {
						partsResult.getParts().add(part);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partsResult;
	}

}
