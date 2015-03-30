package com.ksyun.ks3.util;

public class Constants {
	/* Global log tag */
	public static final String GLOBLE_LOG_TAG = "ks3_sdk_android";
	public final static String KS3_SDK_USER_AGENT = "ks3-kss-android-sdk";
	public final static String LOG_TAG = "ks3_android_sdk";
	/* Xml namespace */
	public final static String KS3_XML_NAMESPACE = "http://s3.amazonaws.com/doc/2006-03-01/";
	public final static String KS3_PACAKAGE = "com.ksyun.ks3";
	public static final String KS3_PROTOCOL = "http";
	public static final String KS3_HOSTNAME = null;
	public static final String KS3_UPLOAD_HOSTNAME = null;
	public static final String KS3_DOWNLOAD_HOSTNAME = null;
	/* Default Configue */
	public static final String ClientConfig_CONNECTION_TIMEOUT = "50000";
	public static final String ClientConfig_SOCKET_TIMEOUT = String
			.valueOf(Integer.MAX_VALUE);
	public static final String ClientConfig_SOCKET_SEND_BUFFER_SIZE_HINT = "0";
	public static final String ClientConfig_SOCKET_RECEIVE_BUFFER_SIZE_HINT = "0";
	public static final String ClientConfig_CONNECTION_TTL = "-1";
	public static final String ClientConfig_MAX_CONNECTIONS = "50";
	public static final String ClientConfig_PROXY_HOST = null;
	public static final String ClientConfig_PROXY_PORT = "-1";
	public static final String ClientConfig_PROXY_DAMAIN = null;
	public static final String ClientConfig_PROXY_PASSWORD = null;
	public static final String ClientConfig_PROXY_USER_NAME = null;
	public static final String ClientConfig_PROXY_WORKSTATION = null;
	public static final String ClientConfig_IS_PREEMPTIVE_BASIC_PROXY_AUTH = "false";
	public static final String ClientConfig_END_POINT = "kss.ksyun.com";
	public static final String ClientConfig_CLIENT_SIGNER = "com.ksyun.ks3.signer.DefaultSigner";
	// uploadPart
	public static final int minPartNumber = 1;
	public static final int maxPartNumber = 10000;
	public static final int minPartSize = 0 ;
	public static final int maxPartSize = 5 * 1024 * 1024 * 1024;
	public static final int EXCEPTION = 0;
	
	// call back 
	public static final String CALL_BACK_CUSTOM_PREFIX = "kss-";
	
	
	
	
	
}
