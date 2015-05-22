package com.ksyun.ks3.services;

import org.apache.http.Header;

import android.util.Log;

import com.ksyun.ks3.model.IpModel;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.PhoneInfoUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SafetyIpClient {
	private static final String HTTP = "http://";
	public static IpModel ipModel;
	private static AsyncHttpClient client;
	
	public static void setUpSafetyModel(){
		// Request Ip Interface
		Log.d(Constants.LOG_TAG, "setUpSafetyModel");
		client = AsyncHttpClientFactory.getInstance();
		ipModel = new IpModel("192.168.1.1","192.168.2.1","192.168.3.1");

//		client.get("http://www.baidu.com", new AsyncHttpResponseHandler() {
//			
//			@Override
//			public void onSuccess(int paramInt, Header[] paramArrayOfHeader,
//					byte[] paramArrayOfByte) {
//				ipModel = new IpModel("192.168.1.1","192.168.2.1","192.168.3.1");
//				Log.d(Constants.LOG_TAG, "ip get completed");
//			}
//			
//			@Override
//			public void onFailure(int paramInt, Header[] paramArrayOfHeader,
//					byte[] paramArrayOfByte, Throwable paramThrowable) {
//				Log.d(Constants.LOG_TAG, "ip get failed");
//				ipModel = new IpModel("192.168.1.1","192.168.2.1","192.168.3.1");
//			}
//		});
	}
	
	// For URL transform
	public static String VhostToPath(String vhostUrl, String ip, boolean isIp) {
		String hostSrt = vhostUrl.replace(HTTP, "");
		int firstDotIndex = hostSrt.indexOf(".");
		String bucketAndDotStr = hostSrt.substring(0, firstDotIndex + 1);
		String removeBucketAndDotStr = hostSrt.substring(firstDotIndex + 1);
		int insertIndex = removeBucketAndDotStr.indexOf("/") + 1;
		String bucketAndSlash = bucketAndDotStr.replace(".", "/");
		String result = insertString(insertIndex, removeBucketAndDotStr,
				bucketAndSlash);
		if (isIp) {
			int replaceIndex = result.indexOf("/");
			String removeHostStr = result.substring(replaceIndex);
			String ipPathUrl = appendString("192.168.1.1", removeHostStr);
			return appendString(HTTP, ipPathUrl);
		} else {
			return appendString(HTTP, result);
		}
	}
	

	public static String PathToVhost(String pathUrl, String ip, boolean isIp) {
		String pathStr = pathUrl.replace(HTTP, "");
		int firstSlashIndex = pathStr.indexOf("/");
		int secondSlashIndex = pathStr.indexOf("/", firstSlashIndex + 1);
		String removeBucketAndSlash;
		if (isIp) {
			String removeHost = pathStr.substring(firstSlashIndex);
			String replaceIpToHostUrl = appendString(
					Constants.ClientConfig_END_POINT, removeHost);
			removeBucketAndSlash = removeStringBetween(firstSlashIndex,
					secondSlashIndex, replaceIpToHostUrl);
		} else {
			removeBucketAndSlash = removeStringBetween(firstSlashIndex,
					secondSlashIndex, pathStr);
		}

		String bucketAndSlash = pathStr.substring(firstSlashIndex + 1,
				secondSlashIndex + 1);
		String bucketAndDot = bucketAndSlash.replace("/", ".");
		String result = appendString(HTTP,
				appendString(bucketAndDot, removeBucketAndSlash));
		return result;
	}

	private static String appendString(String bucketAndSlash,
			String removeBucketAndSlash) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(bucketAndSlash);
		buffer.append(removeBucketAndSlash);
		return buffer.toString();
	}

	private static String removeStringBetween(int firstSlashIndex,
			int secondSlashIndex, String pathUrl) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(pathUrl.substring(0, firstSlashIndex));
		buffer.append(pathUrl.substring(secondSlashIndex));
		return buffer.toString();
	}

	public static String insertString(int index, String originStr,
			String insertStr) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(originStr.subSequence(0, index));
		buffer.append(insertStr);
		buffer.append(originStr.substring(index));
		return buffer.toString();
	}
	
	// Request IpModel
	

}
