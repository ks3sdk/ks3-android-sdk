package com.ksyun.ks3.services.handler;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.util.Log;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.result.InitiateMultipartUploadResult;
import com.ksyun.ks3.services.LogClient;
import com.ksyun.ks3.services.LogUtil;
import com.ksyun.ks3.util.Constants;

public abstract class InitiateMultipartUploadResponceHandler extends Ks3HttpResponceHandler {
	
	public abstract void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,InitiateMultipartUploadResult result);	
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		InitiateMultipartUploadResult initialUploadResult = parseXml(responceHeaders, response);
		LogUtil.setSuccessLog(statesCode, response,responceHeaders,record);
		LogClient.getInstance().insertAndSendLog(record);
		onSuccess(statesCode, responceHeaders, initialUploadResult);
	}

	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		Log.i(Constants.LOG_TAG, "error code: "+error.getErrorCode()+",error message:"+error.getErrorMessage());
		LogUtil.setFailureLog(statesCode, response, throwable, error,record);
		LogClient.getInstance().insertAndSendLog(record);
		onFailure(statesCode,error, responceHeaders, response == null ? "":new String(response), throwable);
	}
	
	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}

	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}
	
	@Override
	public final void onCancel() {}

	private InitiateMultipartUploadResult parseXml(Header[] responceHeaders, byte[] response) {
		XmlPullParserFactory factory;
		InitiateMultipartUploadResult result = null ;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parse = factory.newPullParser();
			parse.setInput(new ByteArrayInputStream(response), "UTF-8");
			int eventType = parse.getEventType();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String nodeName = parse.getName();
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					result = new InitiateMultipartUploadResult();
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("Bucket".equalsIgnoreCase(nodeName)){
						result.setBucket(parse.nextText());
					}
					if("Key".equalsIgnoreCase(nodeName)){
						result.setKey(parse.nextText());
					}
					if("UploadId".equalsIgnoreCase(nodeName)){
						result.setUploadId(parse.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
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
		return result;
	}
}
