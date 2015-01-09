package com.ksyun.ks3.model.transfer;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputSubStream extends FilterInputStream{
	private long currentPosition;
	private final long requestedOffset;
	private final long requestedLength;
	private final boolean closeSourceStream;
	private long markedPosition = 0;

    public InputSubStream(InputStream in, long offset, long length, boolean closeSourceStream) {
    	super(in);
    	this.currentPosition = 0;
    	this.requestedLength = length;
    	this.requestedOffset = offset;
    	this.closeSourceStream = closeSourceStream;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int bytesRead = read(b, 0, 1);

        if (bytesRead == -1) return bytesRead;
        return b[0];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        while (currentPosition < requestedOffset) {
            long skippedBytes = super.skip(requestedOffset - currentPosition);
            currentPosition += skippedBytes;
        }

        long bytesRemaining = (requestedLength + requestedOffset) - currentPosition;
		if (bytesRemaining <= 0) return -1;

        len = (int) Math.min(len, bytesRemaining);
        int bytesRead = super.read(b, off, len);
        currentPosition += bytesRead;

        return bytesRead;
    }

	@Override
	public synchronized void mark(int readlimit) {
		markedPosition = currentPosition;
		super.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		currentPosition = markedPosition;
		super.reset();
	}

	@Override
	public void close() throws IOException {
		// Only close the wrapped input stream if we're at the end of
		// the wrapped stream.  We don't want to close the wrapped input
		// stream just because we've reached the end of one subsection.
		if (closeSourceStream) super.close();
	}

	@Override
    public int available() throws IOException {
		long bytesRemaining;
		if (currentPosition < requestedOffset) bytesRemaining = requestedLength;
		else bytesRemaining = (requestedLength + requestedOffset) - currentPosition;

		return (int)Math.min(bytesRemaining, super.available());
    }
}
