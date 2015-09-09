package com.ks3.demo.main;

import java.io.File;

import android.os.Environment;

/**
 * 
 * Demo中一些常量
 * 
 */
public class Constants {
	// public static final String ACCESS_KEY__ID = "YOUR_ACCESS_KEY_ID";
	// public static final String ACCESS_KEY_SECRET = "YOUR_ACCESS_KEY_SECRET";
	public static final String TEST_TXT = "YOUR_TEST_TXT_IN_SDCARD_ROOT_PATH";
	public static final String TEST_IMG = "YOUR_TEST_IMG_IN_SDCARD_ROOT_PATH";
	protected static final String UPLOAD_ID = "YOUR_UPLOAD_ID";
	// public static final String APP_SERTVER_HOST =
	// "http://192.168.231.49:11911";
	public static final String APP_SERTVER_HOST = "YOUR_APP_SERVER_HOST";
	// 测试用AK&SK
	public static final String ACCESS_KEY__ID = "lMQTr0hNlMpB0iOk/i+x";
	public static final String ACCESS_KEY_SECRET = "D4CsYLs75JcWEjbiI22zR3P7kJ/+5B1qdEje7A7I";
	public static final long MULTI_UPLOAD_THREADHOLD = 16 * 1024 * 1024;
	public final static File LOCAL_FILE_FORDER_DOWNLOAD = new File(Environment.getExternalStorageDirectory(), "KS3DownloadTest");
	public static final String LOCAL_M1_FILE_NAME = "L1.txt";
	public static final String LOCAL_M20_FILE_NAME = "L20.txt";
}
