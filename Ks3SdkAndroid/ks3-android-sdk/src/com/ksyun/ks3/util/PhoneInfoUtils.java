package com.ksyun.ks3.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ksyun.ks3.model.LogRecord;

public class PhoneInfoUtils {

	private static TelephonyManager telephonyManager;
	public static final int TYPE_CHINA_MOBILE = 0;
	public static final int TYPE_CHINA_UNICOM = 1;
	public static final int TYPE_CHINA_TELCOM = 2;

	/**
	 * 国际移动用户识别码
	 */
	private String IMSI;

	private static TelephonyManager getTelephonyManager(Context context) {
		if (telephonyManager == null) {
			telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
		}

		return telephonyManager;
	}

	/**
	 * 获取手机设备号
	 * 
	 * @return
	 */
	public static String getImei(Context context) {
		return Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	/**
	 * 获取电话号码,不一定获取的到
	 */
	/*public static String getNativePhoneNumber(Context context) {
		String NativePhoneNumber = null;
		NativePhoneNumber = getTelephonyManager(context).getLine1Number();
		return NativePhoneNumber;
	}*/

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ProvidersName;
	}

	public static int getProvidersType() {
		int ProviderType = PhoneInfoUtils.TYPE_CHINA_MOBILE;
		try {
			String IMSI = telephonyManager.getSubscriberId();
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				ProviderType = PhoneInfoUtils.TYPE_CHINA_MOBILE;
			} else if (IMSI.startsWith("46001")) {
				ProviderType = PhoneInfoUtils.TYPE_CHINA_UNICOM;
			} else if (IMSI.startsWith("46003")) {
				ProviderType = PhoneInfoUtils.TYPE_CHINA_TELCOM;
			}
		} catch (Exception e) {
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
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
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

	public static PhoneInfo getPhoneInfo(Context context) {
		TelephonyManager tm = getTelephonyManager(context);
		PhoneInfo info = new PhoneInfo();
		info.setBuild_version(getBuildVersion(context));
		info.setConnect_type(getConnectedType(context));
		info.setModel(getDeviceModel(context));
		info.setManufacturer(getManufacturer(context));
		info.setId(tm.getDeviceId());
		info.setLocal_ip(getLocalHostIp(context));
		info.setNetwork_type(tm.getNetworkOperatorName());
		StringBuilder sb = new StringBuilder();
		sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
//		sb.append("\nLine1Number = " + tm.getLine1Number());
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
		sb.append("\nManufacurer = " + getManufacturer(context));
		sb.append("\nDeviceModel = " + getDeviceModel(context));
		sb.append("\nBuildVersion = " + getBuildVersion(context));
		sb.append("\nNetWorkType = " + getConnectedType(context));
		sb.append("\ntype = " + getProvidersType());
		Log.d(Constants.LOG_SERVER_URL, sb.toString());
		return info;
	}

	public static String getLocalHostIp(Context context) {

		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); // 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();
				// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> inet = nif.getInetAddresses(); // 遍历每一个接口绑定的所有ip
				while (inet.hasMoreElements()) {
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ip
									.getHostAddress())) {
						return ipaddress = ip.getHostAddress();
					}
				}

			}
		} catch (SocketException e) {
			Log.e("feige", "获取本地ip地址失败");
			e.printStackTrace();
		}
		return ipaddress;
	}

	public static final class PhoneInfo {
		private String model;
		private String manufacturer;
		private String id;
		private String network_type;
		private String build_version;
		private String connect_type;
		private String local_ip;

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public String getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getNetwork_type() {
			return network_type;
		}

		public void setNetwork_type(String network_type) {
			this.network_type = network_type;
		}

		public String getBuild_version() {
			return build_version;
		}

		public void setBuild_version(String build_version) {
			this.build_version = build_version;
		}

		public String getConnect_type() {
			return connect_type;
		}

		public void setConnect_type(String connect_type) {
			this.connect_type = connect_type;
		}

		public String getLocal_ip() {
			return local_ip;
		}

		public void setLocal_ip(String local_ip) {
			this.local_ip = local_ip;
		}

		public void makeBasicRecord(LogRecord record) {
			record.setSource_ip(getLocal_ip());
//			record.setBuild_version(getBuild_version());
			record.setConnect_type(getConnect_type());
			record.setNetwork_type(getNetwork_type());
			record.setId(getId());
//			record.setManufacturer(getManufacturer());
//			record.setModel(getModel());
		}
	}
}