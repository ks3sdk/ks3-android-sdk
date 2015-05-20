package com.ksyun.ks3.services;

import java.io.File;
import java.util.List;

import android.content.Context;

import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.handler.AbortMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.ListObjectsResponseHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.services.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.services.request.ListObjectsRequest;
import com.ksyun.ks3.services.request.ListPartsRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;
import com.ksyun.ks3.util.Constants;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Ks3Client implements Ks3 {
	private Ks3ClientConfiguration clientConfiguration;
	private String endpoint;
	private Authorization auth;
	private Ks3HttpExector client = new Ks3HttpExector();
	private Context context = null;
	public AuthListener authListener = null;


	public Ks3Client(String accesskeyid, String accesskeysecret, Context context) {
		this(accesskeyid, accesskeysecret, Ks3ClientConfiguration
				.getDefaultConfiguration(), context);
	}

	public Ks3Client(String accesskeyid, String accesskeysecret,
			Ks3ClientConfiguration clientConfiguration, Context context) {
		this.auth = new Authorization(accesskeyid, accesskeysecret);
		this.clientConfiguration = clientConfiguration;
		this.context = context;
		init();
	}

	public Ks3Client(Authorization auth, Context context) {
		this(auth, Ks3ClientConfiguration.getDefaultConfiguration(), context);
	}

	public Ks3Client(Authorization auth,
			Ks3ClientConfiguration clientConfiguration, Context context) {
		this.auth = auth;
		this.clientConfiguration = clientConfiguration;
		this.context = context;
		init();
	}

	public Ks3Client(AuthListener listener, Context context) {
		this(listener, Ks3ClientConfiguration.getDefaultConfiguration(),
				context);
	}

	public Ks3Client(AuthListener listener,
			Ks3ClientConfiguration clientConfiguration, Context context) {
		this.authListener = listener;
		this.clientConfiguration = clientConfiguration;
		this.context = context;
		init();
	}

	private void init() {
		setEndpoint(Constants.ClientConfig_END_POINT);
	}

	public void setConfiguration(Ks3ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public AuthListener getAuthListener() {
		return authListener;
	}

	public void setAuthListener(AuthListener authListener) {
		this.authListener = authListener;
	}

	/* Service */
	@Override
	public Ks3HttpRequest getObject(Context context, String bucketname, String key,
			GetObjectResponseHandler handler) {
		this.context = context;
		return this.getObject(new GetObjectRequest(bucketname, key), handler);
	}

	@Override
	public Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler handler) {
		return this.getObject(request, handler, true);
	}

	private Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler handler,boolean isUseAsyncMode) {
		return this.invoke(auth, request, handler,isUseAsyncMode);
	}
	
	@Override
	public Ks3HttpRequest putObject(String bucketname, String objectkey, File file,
			PutObjectResponseHandler handler) {
		return this.putObject(new PutObjectRequest(bucketname, objectkey, file),
				handler);
	}
	
	@Override
	public Ks3HttpRequest putObject(String bucketname, String objectkey,
			File file, ObjectMetadata objectmeta,
			PutObjectResponseHandler handler) {
		return this.putObject(new PutObjectRequest(bucketname, objectkey, file,
				objectmeta), handler);
	}
	
	@Override
	public Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler) {
		return this.putObject(request, handler, true);
	}

	private Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler,boolean isUseAsyncMode) {
		return this.invoke(auth, request, handler,isUseAsyncMode);
	}
	
	/* MultiUpload */
	@Override
	public void initiateMultipartUpload(String bucketname, String objectkey,
			InitiateMultipartUploadResponceHandler resultHandler) {
		this.initiateMultipartUpload(new InitiateMultipartUploadRequest(
				bucketname, objectkey), resultHandler);
	}

	@Override
	public void initiateMultipartUpload(InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler) {
		this.initiateMultipartUpload(request, resultHandler, true);
	}
	
	private void initiateMultipartUpload(InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler,boolean isUseAsyncMode) {
		this.invoke(auth, request, resultHandler,isUseAsyncMode);
	}
	
	@Override
	public void uploadPart(String bucketName, String key, String uploadId,
			File file, long offset, int partNumber, long partSize,
			UploadPartResponceHandler resultHandler) {
		this.uploadPart(new UploadPartRequest(bucketName, key, uploadId, file,
				offset, partNumber, partSize), resultHandler);
	}

	@Override
	public void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler) {
		this.uploadPart(request, resultHandler, true);
	}

	private void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler,boolean isUseAsyncMode) {
		this.invoke(auth, request, resultHandler,isUseAsyncMode);
	}

	@Override
	public void completeMultipartUpload(String bucketname, String objectkey,
			String uploadId, List<PartETag> partETags,
			CompleteMultipartUploadResponseHandler handler) {
		this.completeMultipartUpload(new CompleteMultipartUploadRequest(
				bucketname, objectkey, uploadId, partETags), handler);
	}
	
	@Override
	public void completeMultipartUpload(ListPartsResult result,
			CompleteMultipartUploadResponseHandler handler) {
		this.completeMultipartUpload(
				new CompleteMultipartUploadRequest(result), handler);
	}
	
	@Override
	public void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler) {
		this.completeMultipartUpload(request, handler, true);
	}

	private void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler,boolean isUseAsyncMode) {
		this.invoke(auth, request, handler,isUseAsyncMode);
	}
	
	@Override
	public void abortMultipartUpload(String bucketname, String objectkey,
			String uploadId, AbortMultipartUploadResponseHandler handler) {
		this.abortMultipartUpload(new AbortMultipartUploadRequest(bucketname,
				objectkey, uploadId), handler);
	}

	@Override
	public void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler) {
		this.abortMultipartUpload(request, handler, true);
	}

	private void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler,boolean isUseAsyncMode) {
		this.invoke(auth, request, handler,isUseAsyncMode);
	}
	
	@Override
	public void listParts(String bucketname, String objectkey, String uploadId,
			ListPartsResponseHandler handler) {
		this.listParts(new ListPartsRequest(bucketname, objectkey, uploadId),
				handler);
	}

	@Override
	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, ListPartsResponseHandler handler) {
		this.listParts(new ListPartsRequest(bucketname, objectkey, uploadId,
				maxParts), handler);
	}

	@Override
	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, int partNumberMarker, ListPartsResponseHandler handler) {
		this.listParts(new ListPartsRequest(bucketname, objectkey, uploadId,
				maxParts, partNumberMarker), handler);
	}

	@Override
	public void listParts(ListPartsRequest request,
			ListPartsResponseHandler handler) {
		this.listParts(request, handler, true);
	}

	private void listParts(ListPartsRequest request,
			ListPartsResponseHandler handler,boolean isUseAsyncMode) {
		this.invoke(auth, request, handler,isUseAsyncMode);
	}
	
	/* Invoke asnyc http client */
	private Ks3HttpRequest invoke(Authorization auth, Ks3HttpRequest request,
			AsyncHttpResponseHandler resultHandler,boolean isUseAsyncMode) {
		client.invoke(auth, request, resultHandler, clientConfiguration,
				context, endpoint, authListener, isUseAsyncMode);
		return request;
	}

	@Override
	public void pause(Context context) {
		client.pause(context);
	}

	@Override
	public void cancel(Context context) {
		client.cancel(context);
	}

	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void listObjects(String bucketname,
			ListObjectsResponseHandler resultHandler) {
		this.listObjects(new ListObjectsRequest(bucketname), resultHandler);
	}

	@Override
	public void listObjects(String bucketname, String prefix,
			ListObjectsResponseHandler resultHandler) {
		this.listObjects(new ListObjectsRequest(bucketname, prefix),
				resultHandler);
	}

	@Override
	public void listObjects(ListObjectsRequest request,
			ListObjectsResponseHandler resultHandler) {
		this.listObjects(request, resultHandler, true);
	}

	private void listObjects(ListObjectsRequest request,
			ListObjectsResponseHandler resultHandler,boolean isUseAsyncMode) {
		this.invoke(auth, request, resultHandler,isUseAsyncMode);
	}

}
