package com.ksyun.ks3.model;

public class IpModel {
	private String CHINA_MOBILE_SERVER_IP = null;
	private String CHINA_UNICOM_SERVER_IP = null;
	private String CHINA_TELECOM_SERVER_IP = null;

	public IpModel(String china_mobile_ip, String china_unicom_ip,
			String china_telecom_ip) {
		setCHINA_MOBILE_SERVER_IP(china_mobile_ip);
		setCHINA_UNICOM_SERVER_IP(china_unicom_ip);
		setCHINA_TELECOM_SERVER_IP(china_telecom_ip);
	}

	public String getCHINA_MOBILE_SERVER_IP() {
		return CHINA_MOBILE_SERVER_IP;
	}

	public void setCHINA_MOBILE_SERVER_IP(String cHINA_MOBILE_SERVER_IP) {
		CHINA_MOBILE_SERVER_IP = cHINA_MOBILE_SERVER_IP;
	}

	public String getCHINA_UNICOM_SERVER_IP() {
		return CHINA_UNICOM_SERVER_IP;
	}

	public void setCHINA_UNICOM_SERVER_IP(String cHINA_UNICOM_SERVER_IP) {
		CHINA_UNICOM_SERVER_IP = cHINA_UNICOM_SERVER_IP;
	}

	public String getCHINA_TELECOM_SERVER_IP() {
		return CHINA_TELECOM_SERVER_IP;
	}

	public void setCHINA_TELECOM_SERVER_IP(String cHINA_TELECOM_SERVER_IP) {
		CHINA_TELECOM_SERVER_IP = cHINA_TELECOM_SERVER_IP;
	}

}
