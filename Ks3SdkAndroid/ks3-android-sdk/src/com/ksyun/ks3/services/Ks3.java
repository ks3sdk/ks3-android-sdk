package com.ksyun.ks3.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import android.content.Context;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.exception.Ks3ServiceException;
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
import com.ksyun.ks3.services.handler.GetObjectResponceHandler;
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
import com.ksyun.ks3.services.request.ListBucketsRequest;
import com.ksyun.ks3.services.request.ListObjectsRequest;
import com.ksyun.ks3.services.request.ListPartsRequest;
import com.ksyun.ks3.services.request.PutBucketACLRequest;
import com.ksyun.ks3.services.request.PutObjectACLRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;

public abstract interface Ks3 {

	public void listBuckets(ListBucketsResponceHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void listBuckets(ListBucketsRequest request,
			ListBucketsResponceHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void getBucketACL(String bucketName,
			GetBucketACLResponceHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void getBucketACL(GetBucketACLRequest request,
			GetBucketACLResponceHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void putBucketACL(String bucketName,
			AccessControlList accessControlList,
			PutBucketACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void putBucketACL(String bucketName,
			CannedAccessControlList CannedAcl,
			PutBucketACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void putBucketACL(PutBucketACLRequest request,
			PutBucketACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void putObjectACL(String bucketName, String objectName,
			CannedAccessControlList accessControlList,
			PutObjectACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void putObjectACL(String bucketName, String objectName,
			AccessControlList accessControlList,
			PutObjectACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void putObjectACL(PutObjectACLRequest request,
			PutObjectACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void getObjectACL(String bucketName, String ObjectName,
			GetObjectACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void getObjectACL(GetObjectACLRequest request,
			GetObjectACLResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void headBucket(String bucketname,
			HeadBucketResponseHandler resultHandler) throws Ks3ClientException,
			Ks3ServiceException;

	public void headBucket(HeadBucketRequest request,
			HeadBucketResponseHandler resultHandler) throws Ks3ClientException,
			Ks3ServiceException;

	public boolean bucketExists(String bucketname) throws Ks3ClientException;

	public void createBucket(String bucketname,
			CreateBucketResponceHandler handlhandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void createBucket(String bucketname, AccessControlList list,
			CreateBucketResponceHandler handlhandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void createBucket(String bucketname, CannedAccessControlList list,
			CreateBucketResponceHandler handlhandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void createBucket(CreateBucketRequest request,
			CreateBucketResponceHandler handlhandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void deleteBucket(String bucketname,
			DeleteBucketResponceHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void deleteBucket(DeleteBucketRequest request,
			DeleteBucketResponceHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void listObjects(String bucketname,
			ListObjectsResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void listObjects(String bucketname, String prefix,
			ListObjectsResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void listObjects(ListObjectsRequest request,
			ListObjectsResponseHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void deleteObject(String bucketname, String objectKey,
			DeleteObjectRequestHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void deleteObject(DeleteObjectRequest request,
			DeleteObjectRequestHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void getObject(Context context, String bucketname, String key,
			GetObjectResponceHandler getObjectResponceHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void getObject(GetObjectRequest request,
			GetObjectResponceHandler getObjectResponceHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void putObject(String bucketname, String objectkey, File file,
			PutObjectResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void putObject(String bucketname, String objectkey,
			InputStream inputstream, ObjectMetadata objectmeta,
			PutObjectResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void putObject(PutObjectRequest request,
			PutObjectResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void headObject(String bucketname, String objectkey,
			HeadObjectResponseHandler resultHandler) throws Ks3ClientException,
			Ks3ServiceException;

	public void headObject(HeadObjectRequest request,
			HeadObjectResponseHandler resultHandler) throws Ks3ClientException,
			Ks3ServiceException;

	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CopyObjectResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CannedAccessControlList cannedAcl, CopyObjectResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			AccessControlList accessControlList,
			CopyObjectResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void copyObject(CopyObjectRequest request,
			CopyObjectResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;
	
	public void initiateMultipartUpload(String bucketname, String objectkey,
			InitiateMultipartUploadResponceHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void initiateMultipartUpload(InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler)
			throws Ks3ClientException, Ks3ServiceException;

	public void uploadPart(String bucketName, String key, String uploadId,
			File file, long offset, int partNumber, long partSize,
			UploadPartResponceHandler resultHandler) throws Ks3ClientException,
			Ks3ServiceException;

	public void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler) throws Ks3ClientException,
			Ks3ServiceException;

	public void completeMultipartUpload(String bucketname, String objectkey,
			String uploadId, List<PartETag> partETags,
			CompleteMultipartUploadResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void completeMultipartUpload(ListPartsResult result,
			CompleteMultipartUploadResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void abortMultipartUpload(String bucketname, String objectkey,
			String uploadId, AbortMultipartUploadResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void listParts(String bucketname, String objectkey, String uploadId,
			ListPartsResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, ListPartsResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, int partNumberMarker, ListPartsResponseHandler handler)
			throws Ks3ClientException, Ks3ServiceException;

	public void listParts(ListPartsRequest request,
			ListPartsResponseHandler handler) throws Ks3ClientException,
			Ks3ServiceException;

	public void pause(Context context);

	public void cancel(Context context);

	public Context getContext();

}
