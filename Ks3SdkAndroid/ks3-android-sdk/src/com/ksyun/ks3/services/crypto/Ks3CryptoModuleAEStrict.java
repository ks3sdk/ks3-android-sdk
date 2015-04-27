package com.ksyun.ks3.services.crypto;

import android.content.Context;

import com.ksyun.ks3.model.crypto.CryptoConfiguration;
import com.ksyun.ks3.model.crypto.EncryptionMaterialsProvider;
import com.ksyun.ks3.services.crypto.Ks3EncryptionClient.Ks3DirectImpl;
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

public class Ks3CryptoModuleAEStrict extends Ks3CryptoModuleAE {

	public Ks3CryptoModuleAEStrict(
			Ks3DirectImpl ks3DirectImpl, EncryptionMaterialsProvider encryptionMaterialsProvider,
			CryptoConfiguration cryptoConfiguration, Context context) {
		super(ks3DirectImpl, encryptionMaterialsProvider, cryptoConfiguration, context);
	}

	@Override
	public Ks3HttpRequest putObjectSecurely(PutObjectRequest request,
			PutObjectResponseHandler handler, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ks3HttpRequest getObjectSecurely(GetObjectRequest request,
			GetObjectResponseHandler handler, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void completeMultipartUploadSecurely(
			CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initiateMultipartUploadSecurely(
			InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uploadPartSecurely(UploadPartRequest request,
			UploadPartResponceHandler resultHandler, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void abortMultipartUploadSecurely(
			AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler, boolean b) {
		// TODO Auto-generated method stub

	}

}
