package com.ksyun.ks3.services.handler;

import org.apache.http.Header;

import android.util.Log;

import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.transfer.RequestProgressListener;

public abstract class UploadPartResponceHandler extends Ks3HttpResponceHandler implements RequestProgressListener{

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,PartETag result);

	public abstract void onFailure(int statesCode, Header[] responceHeaders,String response, Throwable throwable);

	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {
		onSuccess(statesCode, responceHeaders, parse(responceHeaders));
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		StringBuffer sb = new StringBuffer("fail code :").append(statesCode).append("\n");
		
		if(responceHeaders != null && responceHeaders.length > 0){
			sb.append("Fail headers==>");
			for(Header header : responceHeaders){
				sb.append("[").append(header.toString()).append("]");
			}
			sb.append("\n");
		}
		if(response != null){
			sb.append("response ==>").append(new String(response)).append("\n");
		}
		if(throwable != null){
			sb.append("throwable message =>").append(throwable.getMessage()).append(",cause by :").append(throwable.getCause());
		}
		
		Log.e(com.ksyun.ks3.util.Constants.LOG_TAG,"get object failed , traces :" + sb.toString());
		onFailure(statesCode, responceHeaders, response == null?"":new String(response), throwable);
	}
	
	
	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}
	
	@Override
	public final void onCancel() {}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}
	
	private PartETag parse(Header[] responceHeaders) {
		PartETag result = new PartETag();
		for (int i = 0; i < responceHeaders.length; i++) {
			Header header = responceHeaders[i];
			if (header.getName().equals(HttpHeaders.ETag.toString())) {
				result.seteTag(header.getValue());
			}
		}
		return result;
	}	

	
	
	
	
}
