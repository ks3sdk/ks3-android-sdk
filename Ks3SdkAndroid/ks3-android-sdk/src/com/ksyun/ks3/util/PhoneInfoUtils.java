package com.ksyun.ks3.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;



public class PhoneInfoUtils {

	private static  TelephonyManager telephonyManager;
	public static final int TYPE_CHINA_MOBILE = 0;
	public static final int TYPE_CHINA_UNICOM = 1;
	public static final int TYPE_CHINA_TELCOM = 2;

	/**
	 * 国际移动用户识别码
	 */
	private String IMSI;
	
	private static TelephonyManager getTelephonyManager(Context context) {
		if (telephonyManager == null) {
			telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		}
		
		return telephonyManager;
	}
	
	/**
	 * 获取手机设备号
	 * @return
	 */
	public static String getImei(Context context){
		return Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
	}

	/**
	 * 获取电话号码,不一定获取的到
	 */
	public static String  getNativePhoneNumber(Context context) {
		String NativePhoneNumber = null;
		NativePhoneNumber = getTelephonyManager(context).getLine1Number();
		return NativePhoneNumber;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取手机服务商信息
	 */
	public String getProvidersName() {
		String ProvidersName = "N/A";
		try{
		IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		System.out.println(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ProvidersName;
	}
	
	public static int getProvidersType() {
		int ProviderType = PhoneInfoUtils.TYPE_CHINA_MOBILE;
		try{
		String IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProviderType = PhoneInfoUtils.TYPE_CHINA_MOBILE;
		} else if (IMSI.startsWith("46001")) {
			ProviderType = PhoneInfoUtils.TYPE_CHINA_UNICOM;
		} else if (IMSI.startsWith("46003")) {
			ProviderType = PhoneInfoUtils.TYPE_CHINA_TELCOM;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ProviderType;
	}
	
	public static String getDeviceModel(Context context) {
		return Build.MODEL;
	}
	
	public static String getManufacturer(Context context) {
		return Build.MANUFACTURER;
	}
	
	public static String getBuildVersion(Context context) {
		return Build.VERSION.RELEASE;
	}
	
	public static String getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				switch (mNetworkInfo.getType()) {
				case ConnectivityManager.TYPE_MOBILE:
					return "Mobile Network";
				case ConnectivityManager.TYPE_WIFI:
					return "WIFI Network";
				default:
					break;
				}
				return "Unknown";
			}
		}
		return "Unknown";
	}
	
	public static String  getPhoneInfo(Context context){
		TelephonyManager tm = getTelephonyManager(context);
        StringBuilder sb = new StringBuilder();
        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        sb.append("\nManufacurer = "+ getManufacturer(context));
        sb.append("\nDeviceModel = "+getDeviceModel(context));
        sb.append("\nBuildVersion = "+getBuildVersion(context));
        sb.append("\nNetWorkType = "+getConnectedType(context));
        sb.append("\ntype = "+getProvidersType());
       return  sb.toString();
	}
}