package com.ksyun.ks3.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.loopj.android.http.AsyncHttpClient;

public class AsyncHttpClientFactory {
	private static AsyncHttpClient instance;

	private AsyncHttpClientFactory() {
	}

	static AsyncHttpClient getInstance() {
		if (instance == null) {
			instance = new AsyncHttpClient();
		}
		return instance;
	}

	static AsyncHttpClient getInstance(Ks3ClientConfiguration configuration) {
		if (instance == null) {
			instance = new AsyncHttpClient();
			instance.setConnectTimeout(configuration.getConnectionTimeout());
			instance.setTimeout(configuration.getSocketTimeout());
			instance.setUserAgent(configuration.getUserAgent());
			instance.setMaxConnections(configuration.getMaxConnections());
			instance.setThreadPool(configuration.getThreadPool());
			instance.setMaxRetriesAndTimeout(configuration.getMaxRetrytime(),
					configuration.getRetryTimeOut());
			if (configuration.getProxyUsername() != null
					&& configuration.getProxyPort() > 0) {
				instance.setProxy(configuration.getProxyHost(),
						configuration.getProxyPort(),
						configuration.getProxyUsername(),
						configuration.getProxyPassword());
			}
		}
		return instance;
	}

}
