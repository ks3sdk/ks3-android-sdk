package com.ksyun.ks3.services;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.ksyun.ks3.model.IpModel;
import com.ksyun.ks3.util.Constants;
import com.loopj.android.http.AsyncHttpClient;

public class SafetyIpClient {
	private static final int STATUS_OK = 200;
	private static final String HTTP = "http://";
	public static IpModel ipModel;

	public static void setUpSafetyModel() {
		HttpGet getMethod = new HttpGet(Constants.Client_SATEFY_IP_URL_NORMAL);

		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(getMethod);
			if (response.getStatusLine().getStatusCode() == STATUS_OK) {
				Log.i(Constants.LOG_TAG, "get ip success");
				String result = EntityUtils.toString(response.getEntity());
				parse(result);
			} else {
				Log.i(Constants.LOG_TAG, "get ip failure");
				HttpGet failureGetMethod = new HttpGet(
						Constants.Client_SATEFY_IP_URL_UNNORMAL);
				HttpResponse failurResponsee = httpClient
						.execute(failureGetMethod);
				if (failurResponsee.getStatusLine().getStatusCode() == STATUS_OK) {
					Log.i(Constants.LOG_TAG, "get ip second success");
					String result = EntityUtils.toString(failurResponsee
							.getEntity());
					parse(result);
				} else {
					Log.i(Constants.LOG_TAG, "get ip second failure");

				}

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void parse(String json_str) {
		try {
			JSONObject object = new JSONObject(json_str);
			String ct_array = object.getString("ct");

			ipModel = new IpModel(ct_array.substring(0, ct_array.indexOf(",")),
					ct_array.substring(ct_array.indexOf(",") + 1),
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
