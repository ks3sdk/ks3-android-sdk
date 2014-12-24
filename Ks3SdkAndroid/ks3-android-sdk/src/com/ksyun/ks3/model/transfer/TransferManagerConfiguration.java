package com.ksyun.ks3.model.transfer;

public class TransferManagerConfiguration {
	private static final int DEFAULT_MINIMUM_UPLOAD_PART_SIZE = 5242880;
	private static final long DEFAULT_MULTIPART_UPLOAD_THRESHOLD = 16777216L;
	private static final long DEFAULT_MULTIPART_COPY_THRESHOLD = 5368709120L;
	private static final long DEFAULT_MINIMUM_COPY_PART_SIZE = 104857600L;
	// private long minimumUploadPartSize = 5242880L;
	private long minimumUploadPartSize = 8388608L;
	// private long multipartUploadThreshold = 16777216L;
	private long multipartUploadThreshold = 10 * 1024 * 1024L;
	private long multipartCopyThreshold = 5368709120L;
	private long multipartCopyPartSize = 104857600L;

	public long getMinimumUploadPartSize() {
		return this.minimumUploadPartSize;
	}

	public void setMinimumUploadPartSize(long paramLong) {
		this.minimumUploadPartSize = paramLong;
	}

	public long getMultipartUploadThreshold() {
		return this.multipartUploadThreshold;
	}

	public void setMultipartUploadThreshold(long paramLong) {
		this.multipartUploadThreshold = paramLong;
	}

	public long getMultipartCopyPartSize() {
		return this.multipartCopyPartSize;
	}

	public void setMultipartCopyPartSize(long paramLong) {
		this.multipartCopyPartSize = paramLong;
	}

	public long getMultipartCopyThreshold() {
		return this.multipartCopyThreshold;
	}

	public void setMultipartCopyThreshold(long paramLong) {
		this.multipartCopyThreshold = paramLong;
	}
}
