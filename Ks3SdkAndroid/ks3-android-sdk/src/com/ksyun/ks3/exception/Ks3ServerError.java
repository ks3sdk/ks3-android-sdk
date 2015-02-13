package com.ksyun.ks3.exception;

public class Ks3ServerError {
	private String serverErrorCode;
	private String serverErrorMessage;
	private String serverErrorResource;
	private String serverErrorRequsetId;

	public String getServerErrorCode() {
		return serverErrorCode;
	}

	public void setServerErrorCode(String serverErrorCode) {
		this.serverErrorCode = serverErrorCode;
	}

	public String getServerErrorMessage() {
		return serverErrorMessage;
	}

	public void setServerErrorMessage(String serverErrorMessage) {
		this.serverErrorMessage = serverErrorMessage;
	}

	public String getServerErrorResource() {
		return serverErrorResource;
	}

	public void setServerErrorResource(String serverErrorResource) {
		this.serverErrorResource = serverErrorResource;
	}

	public String getServerErrorRequsetId() {
		return serverErrorRequsetId;
	}

	public void setServerErrorRequsetId(String serverErrorRequsetId) {
		this.serverErrorRequsetId = serverErrorRequsetId;
	}

}
