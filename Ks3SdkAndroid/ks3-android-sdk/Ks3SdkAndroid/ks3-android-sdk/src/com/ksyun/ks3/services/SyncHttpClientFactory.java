package com.ksyun.ks3.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

public class SyncHttpClientFactory {
	private static SyncHttpClient instance;

	private SyncHttpClientFactory() {
	}

	static SyncHttpClient getInstance() {
		if (instance == null) {
			instance = new SyncHttpClient();
		}
		return instance;
	}

	static SyncHttpClient getInstance(Ks3ClientConfiguration configuration) {
		if (instance == null) {
			instance = new SyncHttpClient();
			instance.setConnectTimeout(configuration.getConnectionTimeout());
			instance.setTimeout(configuration.getSocketTimeout());
			instance.setUserAgent(configuration.getUserAgent());
			instance.setMaxConnections(configuration.getMaxConnections());
			instance.setMaxRetriesAndTimeout(configuration.getMaxRetrytime(), configuration.getRetryTimeOut());
			  if (configuration.getProxyUsername() != null && configuration.getProxyPort() > 0) {
				  instance.setProxy(configuration.getProxyHost(),
							configuration.getProxyPort(),
							configuration.getProxyUsername(),
							configuration.getProxyPassword());
			  }
		}
		return instance;
	}

	private static ExecutorService getDefaultThreadPool() {
		return Executors.newCachedThreadPool();
	}

}
