package com.ksyun.ks3.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ks3ClientConfiguration {
	public static final int DEFAULT_SOCKET_TIMEOUT = 50000;
	public static final int DEFAULT_MAX_CONNECTIONS = 50;
	public static final boolean DEFAULT_USE_REAPER = true;
	public static final String DEFAULT_USER_AGENT = "ks3-android-sdk";
	private ExecutorService threadPool;
	private String userAgent = null;
	private String proxyHost = null;
	private String proxyUsername = null;
	private String proxyPassword = null;
	private String proxyDomain = null;
	private String proxyWorkstation = null;
	private int proxyPort = -1;
	private int maxConnections = 0;
	private int socketTimeout = 0;
	private int connectionTimeout = 0;
	private int socketSendBufferSizeHint = 0;
	private int socketReceiveBufferSizeHint = 0;
	private boolean useReaper = true;
	private int maxRetrytime;
	private int retryTimeOut;
	private static Ks3ClientConfiguration instantce = null;
	//添加代码
	private Boolean domainMode = false;  //true for customized, false for predefined
	public Boolean getDomainMode() {
		return domainMode;
	}

	public void setDomainMode(Boolean domainMode) {
		this.domainMode = domainMode;
	}


	private Ks3ClientConfiguration() {
	}

	public static Ks3ClientConfiguration getDefaultConfiguration() {
		if (instantce == null) {
			instantce = new Ks3ClientConfiguration();
			instantce.setConnectionTimeout(20000);
			instantce.setSocketTimeout(50000);
			instantce.setMaxConnections(10);
			instantce.setProxyHost(null);
			instantce.setProxyUsername(null);
			instantce.setProxyPassword(null);
			instantce.setProxyPort(-1);
			instantce.setMaxRetrytime(0);
			instantce.setRetryTimeOut(5000);
			instantce.setUserAgent(DEFAULT_USER_AGENT);
			instantce.setThreadPool(Executors.newCachedThreadPool());
		}
		return instantce;
	}

	public Ks3ClientConfiguration(Ks3ClientConfiguration other) {
		this.connectionTimeout = other.connectionTimeout;
		this.maxConnections = other.maxConnections;
		this.proxyDomain = other.proxyDomain;
		this.proxyHost = other.proxyHost;
		this.proxyPassword = other.proxyPassword;
		this.proxyPort = other.proxyPort;
		this.proxyUsername = other.proxyUsername;
		this.proxyWorkstation = other.proxyWorkstation;
		this.socketTimeout = other.socketTimeout;
		this.useReaper = other.useReaper;
		this.socketReceiveBufferSizeHint = other.socketReceiveBufferSizeHint;
		this.socketSendBufferSizeHint = other.socketSendBufferSizeHint;
		this.maxRetrytime = other.maxRetrytime;
		this.userAgent = other.userAgent;
		this.threadPool = other.threadPool;
	}

	public int getMaxRetrytime() {
		return maxRetrytime;
	}

	public void setMaxRetrytime(int maxRetrytime) {
		this.maxRetrytime = maxRetrytime;
	}

	public int getRetryTimeOut() {
		return retryTimeOut;
	}

	public void setRetryTimeOut(int retryTimeOut) {
		this.retryTimeOut = retryTimeOut;
	}

	public int getMaxConnections() {
		return this.maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public Ks3ClientConfiguration withMaxConnections(int maxConnections) {
		setMaxConnections(maxConnections);
		return this;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void withUserAgent(String userAgent) {
		setUserAgent(userAgent);
	}

	public String getProxyHost() {
		return this.proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public Ks3ClientConfiguration withProxyHost(String proxyHost) {
		setProxyHost(proxyHost);
		return this;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public Ks3ClientConfiguration withProxyPort(int proxyPort) {
		setProxyPort(proxyPort);
		return this;
	}

	public String getProxyUsername() {
		return this.proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public Ks3ClientConfiguration withProxyUsername(String proxyUsername) {
		setProxyUsername(proxyUsername);
		return this;
	}

	public String getProxyPassword() {
		return this.proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public Ks3ClientConfiguration withProxyPassword(String proxyPassword) {
		setProxyPassword(proxyPassword);
		return this;
	}

	public String getProxyDomain() {
		return this.proxyDomain;
	}

	public void setProxyDomain(String proxyDomain) {
		this.proxyDomain = proxyDomain;
	}

	public Ks3ClientConfiguration withProxyDomain(String proxyDomain) {
		setProxyDomain(proxyDomain);
		return this;
	}

	public String getProxyWorkstation() {
		return this.proxyWorkstation;
	}

	public void setProxyWorkstation(String proxyWorkstation) {
		this.proxyWorkstation = proxyWorkstation;
	}

	public Ks3ClientConfiguration withProxyWorkstation(String proxyWorkstation) {
		setProxyWorkstation(proxyWorkstation);
		return this;
	}

	public int getSocketTimeout() {
		return this.socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public Ks3ClientConfiguration withSocketTimeout(int socketTimeout) {
		setSocketTimeout(socketTimeout);
		return this;
	}

	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Ks3ClientConfiguration withConnectionTimeout(int connectionTimeout) {
		setConnectionTimeout(connectionTimeout);
		return this;
	}

	public boolean useReaper() {
		return this.useReaper;
	}

	public void setUseReaper(boolean use) {
		this.useReaper = use;
	}

	public Ks3ClientConfiguration withReaper(boolean use) {
		setUseReaper(use);
		return this;
	}

	public int[] getSocketBufferSizeHints() {
		return new int[] { this.socketSendBufferSizeHint,
				this.socketReceiveBufferSizeHint };
	}

	public void setSocketBufferSizeHints(int socketSendBufferSizeHint,
			int socketReceiveBufferSizeHint) {
		this.socketSendBufferSizeHint = socketSendBufferSizeHint;
		this.socketReceiveBufferSizeHint = socketReceiveBufferSizeHint;
	}

	public Ks3ClientConfiguration withSocketBufferSizeHints(
			int socketSendBufferSizeHint, int socketReceiveBufferSizeHint) {
		setSocketBufferSizeHints(socketSendBufferSizeHint,
				socketReceiveBufferSizeHint);
		return this;
	}

	public ExecutorService getThreadPool() {
		return threadPool;
	}

	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}
	
}
