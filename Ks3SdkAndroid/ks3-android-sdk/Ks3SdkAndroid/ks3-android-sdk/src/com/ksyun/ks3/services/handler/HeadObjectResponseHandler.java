package com.ksyun.ks3.services.handler;

import org.apache.http.Header;

import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.ObjectMetadata.Meta;
import com.ksyun.ks3.model.result.HeadObjectResult;
import com.ksyun.ks3.util.DateUtil;
import com.ksyun.ks3.util.StringUtils;

public abstract class HeadObjectResponseHandler extends Ks3HttpResponceHandler {

	public abstract void onFailure(int statesCode, Header[] responceHeaders,String response, Throwable paramThrowable);

	public abstract void onSuccess(int statesCode, Header[] responceHeaders,HeadObjectResult headObjectResult);
	
	@Override
	public final void onSuccess(int statesCode, Header[] responceHeaders,byte[] response) {

		this.onSuccess(statesCode, responceHeaders, parseHeaders(responceHeaders));
	}

	@Override
	public final void onFailure(int statesCode, Header[] responceHeaders,byte[] response, Throwable throwable) {
		this.onFailure(statesCode, responceHeaders, response==null?"":new String(response), throwable);
	}
	
	@Override
	public final void onProgress(int bytesWritten, int totalSize) {}

	@Override
	public final void onStart() {}

	@Override
	public final void onFinish() {}
	
	@Override
	public final void onCancel() {}
	
	private HeadObjectResult parseHeaders(Header[] responceHeaders) {
		
		HeadObjectResult result = new HeadObjectResult();
		ObjectMetadata meta = new ObjectMetadata();
		try {
			for(int i = 0;i<responceHeaders.length;i++)
			{
				Header h = responceHeaders[i];
				if (HttpHeaders.ETag.toString().equalsIgnoreCase(h.getName())) {
					result.setETag(h.getValue());
				}
				if(HttpHeaders.LastModified.toString().equalsIgnoreCase(h.getName())){
					String dateStr = h.getValue();
					if(!StringUtils.isBlank(dateStr))
					    result.setLastmodified(DateUtil.ConverToDate(dateStr));
				}
				if(h.getName().startsWith(ObjectMetadata.userMetaPrefix))
				{
				    meta.addOrEditUserMeta(h.getName(), h.getValue());
				}
				else if(h.getName().equalsIgnoreCase(Meta.CacheControl.toString()))
				{
					meta.setCacheControl(h.getValue());
				}
				else if(h.getName().equalsIgnoreCase(Meta.ContentDisposition.toString()))
				{
					meta.setContentDisposition(h.getValue());
				}
				else if(h.getName().equalsIgnoreCase(Meta.ContentEncoding.toString()))
				{
					meta.setContentEncoding(h.getValue());
				}
				else if(h.getName().equalsIgnoreCase(Meta.ContentLength.toString()))
				{
					meta.setContentLength(h.getValue());
				}
				else if(h.getName().equalsIgnoreCase(Meta.ContentType.toString()))
				{
					meta.setContentType(h.getValue());
				}
				else if(h.getName().equalsIgnoreCase(Meta.Expires.toString()))
				{
					meta.setExpires(h.getValue());
				}
				result.setObjectMetadata(meta);
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		
		return result;
	}
	
}
