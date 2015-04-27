package com.ksyun.ks3.services.crypto;

import com.ksyun.ks3.model.result.PutObjectResult;
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

public abstract class Ks3Direct {
	public abstract Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler, boolean b);
 
	public abstract Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler handler, boolean b);

	public abstract  void completeMultipartUpload(
			CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler, boolean b);

	public abstract  void initiateMultipartUpload(
			InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler, boolean b);

	public abstract  void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler, boolean b);

	public abstract void abortMultipartUpload(
			AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler, boolean b);
}
