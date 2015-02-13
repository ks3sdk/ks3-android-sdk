package com.ks3.demo.main;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.util.ByteUtil;

import android.text.TextUtils;
import android.util.Base64;

/**
 * 
 * 签名工具类
 * 
 */
public class AuthUtils {
	public static String calcAuthorization(Authorization auth,
			Ks3HttpRequest request) throws SignatureException {
		String signature = calcSignature(auth.getAccessKeySecret(), request);
		String value = "KSS " + auth.getAccessKeyId() + ":" + signature;
		return value;  
	}

	private static String calcSignature(String accessKeySecret,
			Ks3HttpRequest request) throws SignatureException {
		String resource = CanonicalizedKSSResource(request);
		String requestMethod = request.getHttpMethod().toString();
		String contentMd5 = request.getContentMD5();
		String contentType = request.getContentType();
		String _signDate = request.getDate();
		List<String> signList = new ArrayList<String>();
		signList.addAll(Arrays.asList(new String[] { requestMethod, contentMd5,
				contentType, _signDate }));
		String _headers = CanonicalizedKSSHeaders(request);
		if (_headers != null && !_headers.equals("")) {
			signList.add(_headers);
		}
		signList.add(resource);
		String signStr = TextUtils.join("\n", signList.toArray());
		String serverSignature = calculateRFC2104HMAC(signStr, accessKeySecret);
		return serverSignature;
	}

	// AuthListener方式，签名计算方法
	public static String calcAuthToken(String httpMethod, String contentType,
			String date, String contentMD5, String resource, String Headers,
			String accessKeyId, String accessKeySecret)
			throws SignatureException {
		List<String> signList = new ArrayList<String>();
		signList.addAll(Arrays.asList(new String[] { httpMethod, contentMD5,
				contentType, date }));
		String _headers = Headers;
		if (_headers != null && !_headers.equals("")) {
			signList.add(_headers);
		}
		signList.add(resource);
		String signStr = TextUtils.join("\n", signList.toArray());
		String serverSignature = calculateRFC2104HMAC(signStr, accessKeySecret);
		String value = "KSS " + accessKeyId + ":" + serverSignature;
		return value;
	}

	public static String CanonicalizedKSSResource(Ks3HttpRequest request) {
		boolean escapeDoubleSlash = true;

		String bucketName = request.getBucketname();
		String objectKey = request.getObjectkey();

		StringBuffer buffer = new StringBuffer();
		buffer.append("/");
		if (!TextUtils.isEmpty(bucketName)) {
			buffer.append(bucketName).append("/");
		}

		if (!TextUtils.isEmpty(objectKey)) {
			String encodedPath = null;
			try {
				encodedPath = URLEncoder.encode(objectKey, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (escapeDoubleSlash) {
				encodedPath = encodedPath.replace("//", "/%2F");
			}
			buffer.append(encodedPath);
		}

		String resource = buffer.toString();

		String queryParams = request.getParamsToSign();
		if (queryParams != null && !queryParams.equals(""))
			resource = resource + "?" + queryParams;
		return resource;
	}

	public static String CanonicalizedKSSHeaders(Ks3HttpRequest request) {
		String prefix = "x-kss";
		Map<String, String> headers = request.getHeader();

		List<String> headList = new ArrayList<String>();

		for (String _header : headers.keySet()) {
			if (_header.toLowerCase().startsWith(prefix)) {
				headList.add(_header);
			}
		}

		Collections.sort(headList, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return ByteUtil.compareTo(o1.getBytes(), o2.toString()
						.getBytes());
			}
		});
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < headList.size(); i++) {
			String _key = headList.get(i);
			buffer.append(headList.get(i) + ":" + headers.get(_key));
			if (i < (headList.size() - 1))
				buffer.append("\n");
		}
		return buffer.toString();
	}

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public static String calculateRFC2104HMAC(String data, String key)
			throws java.security.SignatureException {
		String result;
		try {
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
					HMAC_SHA1_ALGORITHM);
			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());
			// base64-encode the hmac
			// result = new String(Base64.encodeBase64(rawHmac), "GBK");
			result = Base64.encodeToString(rawHmac, Base64.DEFAULT);
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e);
		}
		return result;
	}

}
