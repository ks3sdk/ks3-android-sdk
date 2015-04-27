package com.ksyun.ks3.model;

import com.ksyun.ks3.services.handler.AbortMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.services.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;

public interface Ks3CryptoModule {
	public Ks3HttpRequest putObjectSecurely(PutObjectRequest request,
			PutObjectResponseHandler handler, boolean b) ;
 
	public Ks3HttpRequest getObjectSecurely(GetObjectRequest request,
			GetObjectResponseHandler handler, boolean b);

	public void completeMultipartUploadSecurely(
			CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler, boolean b);

	public void initiateMultipartUploadSecurely(
			InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler, boolean b);

	public void uploadPartSecurely(UploadPartRequest request,
			UploadPartResponceHandler resultHandler, boolean b);

	public void abortMultipartUploadSecurely(
			AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler, boolean b);
}
