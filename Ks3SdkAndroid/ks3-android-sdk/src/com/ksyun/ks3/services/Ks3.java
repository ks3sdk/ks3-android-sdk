package com.ksyun.ks3.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import android.content.Context;

import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.handler.AbortMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CopyObjectResponseHandler;
import com.ksyun.ks3.services.handler.CreateBucketResponceHandler;
import com.ksyun.ks3.services.handler.DeleteBucketResponceHandler;
import com.ksyun.ks3.services.handler.DeleteObjectRequestHandler;
import com.ksyun.ks3.services.handler.GetBucketACLResponceHandler;
import com.ksyun.ks3.services.handler.GetObjectACLResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.HeadBucketResponseHandler;
import com.ksyun.ks3.services.handler.HeadObjectResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.ListBucketsResponceHandler;
import com.ksyun.ks3.services.handler.ListObjectsResponseHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.PutBucketACLResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectACLResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.services.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.services.request.CopyObjectRequest;
import com.ksyun.ks3.services.request.CreateBucketRequest;
import com.ksyun.ks3.services.request.DeleteBucketRequest;
import com.ksyun.ks3.services.request.DeleteObjectRequest;
import com.ksyun.ks3.services.request.GetBucketACLRequest;
import com.ksyun.ks3.services.request.GetObjectACLRequest;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.HeadBucketRequest;
import com.ksyun.ks3.services.request.HeadObjectRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.services.request.ListBucketsRequest;
import com.ksyun.ks3.services.request.ListObjectsRequest;
import com.ksyun.ks3.services.request.ListPartsRequest;
import com.ksyun.ks3.services.request.PutBucketACLRequest;
import com.ksyun.ks3.services.request.PutObjectACLRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;

public abstract interface Ks3 {

	public void listBuckets(ListBucketsResponceHandler resultHandler);

	public void listBuckets(ListBucketsRequest request,
			ListBucketsResponceHandler resultHandler);

	public void getBucketACL(String bucketName,
			GetBucketACLResponceHandler resultHandler);

	public void getBucketACL(GetBucketACLRequest request,
			GetBucketACLResponceHandler resultHandler);

	public void putBucketACL(String bucketName,
			AccessControlList accessControlList,
			PutBucketACLResponseHandler resultHandler);

	public void putBucketACL(String bucketName,
			CannedAccessControlList CannedAcl,
			PutBucketACLResponseHandler resultHandler);

	public void putBucketACL(PutBucketACLRequest request,
			PutBucketACLResponseHandler resultHandler);

	public void putObjectACL(String bucketName, String objectName,
			CannedAccessControlList accessControlList,
			PutObjectACLResponseHandler resultHandler);

	public void putObjectACL(String bucketName, String objectName,
			AccessControlList accessControlList,
			PutObjectACLResponseHandler resultHandler);

	public void putObjectACL(PutObjectACLRequest request,
			PutObjectACLResponseHandler resultHandler);

	public void getObjectACL(String bucketName, String ObjectName,
			GetObjectACLResponseHandler resultHandler);

	public void getObjectACL(GetObjectACLRequest request,
			GetObjectACLResponseHandler resultHandler);

	public void headBucket(String bucketname,
			HeadBucketResponseHandler resultHandler);

	public void headBucket(HeadBucketRequest request,
			HeadBucketResponseHandler resultHandler);

	public boolean bucketExists(String bucketname);

	public void createBucket(String bucketname,
			CreateBucketResponceHandler handlhandler);

	public void createBucket(String bucketname, AccessControlList list,
			CreateBucketResponceHandler handlhandler);

	public void createBucket(String bucketname, CannedAccessControlList list,
			CreateBucketResponceHandler handlhandler);

	public void createBucket(CreateBucketRequest request,
			CreateBucketResponceHandler handlhandler);

	public void deleteBucket(String bucketname,
			DeleteBucketResponceHandler handler);

	public void deleteBucket(DeleteBucketRequest request,
			DeleteBucketResponceHandler resultHandler);

	public void listObjects(String bucketname,
			ListObjectsResponseHandler resultHandler);

	public void listObjects(String bucketname, String prefix,
			ListObjectsResponseHandler resultHandler);

	public void listObjects(ListObjectsRequest request,
			ListObjectsResponseHandler resultHandler);

	public void deleteObject(String bucketname, String objectKey,
			DeleteObjectRequestHandler handler);

	public void deleteObject(DeleteObjectRequest request,
			DeleteObjectRequestHandler handler);

	public Ks3HttpRequest getObject(Context context, String bucketname, String key,
			GetObjectResponseHandler getObjectResponceHandler);

	public Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler getObjectResponceHandler);

	public Ks3HttpRequest putObject(String bucketname, String objectkey, File file,
			PutObjectResponseHandler handler);

	public Ks3HttpRequest putObject(String bucketname, String objectkey,
			InputStream inputstream, ObjectMetadata objectmeta,
			PutObjectResponseHandler handler);

	public Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler);

	public void headObject(String bucketname, String objectkey,
			HeadObjectResponseHandler resultHandler);

	public void headObject(HeadObjectRequest request,
			HeadObjectResponseHandler resultHandler);

	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CopyObjectResponseHandler handler);

	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CannedAccessControlList cannedAcl, CopyObjectResponseHandler handler);

	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			AccessControlList accessControlList,
			CopyObjectResponseHandler handler);

	public void copyObject(CopyObjectRequest request,
			CopyObjectResponseHandler handler);

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

	public void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler);

	public void completeMultipartUpload(ListPartsResult result,
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

	public void pause(Context context);

	public void cancel(Context context);

	public Context getContext();

}
