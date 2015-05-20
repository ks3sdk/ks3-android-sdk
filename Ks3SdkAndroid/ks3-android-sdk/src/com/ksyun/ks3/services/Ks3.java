package com.ksyun.ks3.services;

import java.io.File;
import java.util.List;

import android.content.Context;

import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.PartETag;
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

public abstract interface Ks3 {

	public Ks3HttpRequest getObject(Context context, String bucketname,
			String key, GetObjectResponseHandler getObjectResponceHandler);

	public Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler getObjectResponceHandler);

	public Ks3HttpRequest putObject(String bucketname, String objectkey,
			File file, PutObjectResponseHandler handler);

	public Ks3HttpRequest putObject(String bucketname, String objectkey,
			File file, ObjectMetadata objectmeta,
			PutObjectResponseHandler handler);

	public Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler);

	public void initiateMultipartUpload(String bucketname, String objectkey,
			InitiateMultipartUploadResponceHandler resultHandler);

	public void initiateMultipartUpload(InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler);

	public void uploadPart(String bucketName, String key, String uploadId,
			File file, long offset, int partNumber, long partSize,
			UploadPartResponceHandler resultHandler);

	public void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler);

	public void completeMultipartUpload(String bucketname, String objectkey,
			String uploadId, List<PartETag> partETags,
			CompleteMultipartUploadResponseHandler handler);

	public void completeMultipartUpload(ListPartsResult result,
			CompleteMultipartUploadResponseHandler handler);

	public void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler);

	public void abortMultipartUpload(String bucketname, String objectkey,
			String uploadId, AbortMultipartUploadResponseHandler handler);

	public void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler);

	public void listParts(String bucketname, String objectkey, String uploadId,
			ListPartsResponseHandler handler);

	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, ListPartsResponseHandler handler);

	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, int partNumberMarker, ListPartsResponseHandler handler);

	public void listParts(ListPartsRequest request,
			ListPartsResponseHandler handler);

	public void listObjects(String bucketname,
			ListObjectsResponseHandler resultHandler);
	
	public void listObjects(String bucketname, String prefix,
			ListObjectsResponseHandler resultHandler);
	
	public void listObjects(ListObjectsRequest request,
			ListObjectsResponseHandler resultHandler);

	public void pause(Context context);

	public void cancel(Context context);

	public Context getContext();

}
