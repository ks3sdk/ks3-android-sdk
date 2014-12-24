package com.ksyun.ks3.model;

public enum HttpMethod {
	GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), HEAD("HEAD");

	private String value;

	HttpMethod(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
