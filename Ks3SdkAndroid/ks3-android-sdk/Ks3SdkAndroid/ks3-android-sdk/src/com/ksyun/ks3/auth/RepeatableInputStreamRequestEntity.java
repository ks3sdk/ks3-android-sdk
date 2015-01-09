package com.ksyun.ks3.auth;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;

import com.ksyun.ks3.model.transfer.RequestProgressListener;

public class RepeatableInputStreamRequestEntity extends BasicHttpEntity {
	private boolean firstAttempt = true;

	private InputStreamEntity inputStreamRequestEntity;

	private InputStream content;

	private IOException originalException;
	
	private RequestProgressListener progressLisener;
	
	private long contentLength;
	public RepeatableInputStreamRequestEntity(InputStream content,String length) {
		setChunked(false);

		long contentLength = -1;
		try {
			String contentLengthString = length;
			if (contentLengthString != null) {
				contentLength = Long.parseLong(contentLengthString);
			}
		} catch (NumberFormatException nfe) {
			
		}

		inputStreamRequestEntity = new InputStreamEntity(content, contentLength);
		inputStreamRequestEntity.setContentType(contentType);
		this.content = content;
		this.contentLength = contentLength;

		setContent(content);
		setContentType(contentType);
		setContentLength(contentLength);
	}
	
	public void setProgressLisener(RequestProgressListener progressLisener){
		this.progressLisener = progressLisener;
	}
	
	
	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public boolean isRepeatable() {
		return content.markSupported()
				|| inputStreamRequestEntity.isRepeatable();
	}

	@Override
	public void writeTo(OutputStream output) throws IOException {
		try {
			if (!firstAttempt && isRepeatable())
				content.reset();

			firstAttempt = false;
			if(this.progressLisener != null){
				inputStreamRequestEntity.writeTo(output instanceof CountingOutputStream? output : new CountingOutputStream(output,this.contentLength,this.progressLisener));
			}else{
				inputStreamRequestEntity.writeTo(output);
			}
			
			
			
		} catch (IOException ioe) {
			if (originalException == null)
				originalException = ioe;
			throw originalException;
		}
	}
	
	public static class CountingOutputStream extends FilterOutputStream {

        private final RequestProgressListener listener;
        private long uploaded;
        private long length ;

        CountingOutputStream(final OutputStream out,long length, final RequestProgressListener listener) {
            super(out);
            this.listener = listener;
            this.length = length;
            this.uploaded = 0;
        }

        @Override
        public void write(final byte[] b, final int off, final int len)
                throws IOException {
            out.write(b, off, len);
            this.uploaded += len;
            if(this.length > 0 ){
            	 double progress = Double.valueOf(length > 0 ? uploaded * 1.0D / length * 100.0D : -1.0D);
            	 this.listener.onTaskProgress(progress);
            }
            
            
           
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            this.uploaded++;
            if(this.length > 0 ){
            	double progress = Double.valueOf(length > 0 ? uploaded * 1.0D / length * 100.0D : -1.0D);
           	 	this.listener.onTaskProgress(progress);
           }
        }
    }
}
