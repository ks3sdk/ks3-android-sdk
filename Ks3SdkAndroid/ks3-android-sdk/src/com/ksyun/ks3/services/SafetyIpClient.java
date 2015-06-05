package com.ksyun.ks3.services;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.ksyun.ks3.model.IpModel;
import com.ksyun.ks3.util.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SafetyIpClient {
	private static final String HTTP = "http://";
	public static IpModel ipModel;
	private static AsyncHttpClient client;

	public static void setUpSafetyModel() {
		client = AsyncHttpClientFactory.getInstance(Ks3ClientConfiguration
				.getDefaultConfiguration());
		client.get(Constants.Client_SATEFY_IP_URL_NORMAL,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int paramInt,
							Header[] paramArrayOfHeader, byte[] paramArrayOfByte) {
						String result = new String(paramArrayOfByte).trim();
						Log.i(Constants.LOG_TAG, "get ip ok");
						parse(result);
					}

					@Override
					public void onFailure(int paramInt,
							Header[] paramArrayOfHeader,
							byte[] paramArrayOfByte, Throwable paramThrowable) {
						Log.i(Constants.LOG_TAG, "get ip failure");
						client.get(Constants.Client_SATEFY_IP_URL_UNNORMAL,
								new AsyncHttpResponseHandler() {

									@Override
									public void onSuccess(int paramInt,
											Header[] paramArrayOfHeader,
											byte[] paramArrayOfByte) {
										String result = new String(
												paramArrayOfByte).trim();
										Log.i(Constants.LOG_TAG, "retry ip ok");
										parse(result);
									}

									@Override
									public void onFailure(int paramInt,
											Header[] paramArrayOfHeader,
											byte[] paramArrayOfByte,
											Throwable paramThrowable) {
										Log.i(Constants.LOG_TAG,
												"retry ip failure");
									}
								});
					}
				});
	}

	private static void parse(String json_str) {
		try {
			JSONObject object = new JSONObject(json_str);
			String ct_array = object.getString("ct");

			ipModel = new IpModel(ct_array.substring(0, ct_array.indexOf(",")),
					ct_array.substring(ct_array.indexOf(",")+1),
					ct_array.substring(0, ct_array.indexOf(",")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
			String ipPathUrl = appendString(ip, removeHostStr);
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

	public static String getRealPath(String ipUrl) {
		String ipUrlWithoutHttpStr = ipUrl.replace(HTTP, "");
		String result = ipUrlWithoutHttpStr.trim().substring(
				ipUrlWithoutHttpStr.indexOf("/"));
		if (result.contains("?")) {
			result = result.substring(0, result.indexOf("?"));
		}
		return result;
	}

}
