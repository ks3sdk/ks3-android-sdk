package com.ksyun.ks3.model;

public class LogBean {
	private final int mLogId;
	private final String mLogContent;

	public LogBean(final int logId, final String logContent) {
		mLogId = logId;
		mLogContent = logContent;
	}

	public int getId() {
		return mLogId;
	}

	public String getContent() {
		return mLogContent;
	}
}
