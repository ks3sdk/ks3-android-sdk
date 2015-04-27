package com.ksyun.ks3.services.crypto;

import android.content.Context;

import com.ksyun.ks3.model.Ks3CryptoModule;
import com.ksyun.ks3.model.crypto.CryptoConfiguration;
import com.ksyun.ks3.model.crypto.CryptoMode;
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

public class CryptoModuleDispatcher implements Ks3CryptoModule {

	private CryptoMode defaultCryptoMode;
	private Ks3CryptoModuleAE ae;
	private Ks3CryptoModuleEO eo;

	public CryptoModuleDispatcher(
			Ks3DirectImpl ks3DirectImpl, EncryptionMaterialsProvider encryptionMaterialsProvider,
			CryptoConfiguration cryptoConfiguration, Context context) {
		CryptoMode cryptoMode = cryptoConfiguration.getCryptoMode();
		this.defaultCryptoMode = cryptoMode == null ? CryptoMode.EncryptionOnly
				: cryptoMode;
		switch (defaultCryptoMode) {
		case StrictAuthenticatedEncryption:
			this.ae = new Ks3CryptoModuleAEStrict(ks3DirectImpl,encryptionMaterialsProvider,
					cryptoConfiguration, context);
			this.eo = null;
			break;
		case AuthenticatedEncryption:
			this.ae = new Ks3CryptoModuleAE(ks3DirectImpl,encryptionMaterialsProvider,
					cryptoConfiguration, context);
			this.eo = null;
			break;
		default:
			this.eo = new Ks3CryptoModuleEO(ks3DirectImpl,encryptionMaterialsProvider,
					cryptoConfiguration, context);
			this.ae = new Ks3CryptoModuleAE(ks3DirectImpl,encryptionMaterialsProvider,
					cryptoConfiguration, context);
			break;
		}
	}

	@Override
	public Ks3HttpRequest putObjectSecurely(PutObjectRequest request,
			PutObjectResponseHandler handler, boolean isAsyncMode) {
		if (defaultCryptoMode == CryptoMode.EncryptionOnly) {
			return eo.putObjectSecurely(request, handler, isAsyncMode);
		} else {
			return ae.putObjectSecurely(request, handler, isAsyncMode);
		}
	}

	@Override
	public Ks3HttpRequest getObjectSecurely(GetObjectRequest request,
			GetObjectResponseHandler handler, boolean isAsyncMode) {
		// AE module can handle S3 objects encrypted in either AE or EO format
		return ae.getObjectSecurely(request, handler, isAsyncMode);
	}

	@Override
	public void completeMultipartUploadSecurely(
			CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler, boolean isAsyncMode) {
		if (defaultCryptoMode == CryptoMode.EncryptionOnly) {
			eo.completeMultipartUploadSecurely(request, handler, isAsyncMode);
		} else {
			ae.completeMultipartUploadSecurely(request, handler, isAsyncMode);
		}
	}

	@Override
	public void initiateMultipartUploadSecurely(
			InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler,
			boolean isAsyncMode) {
		if (defaultCryptoMode == CryptoMode.EncryptionOnly) {
			eo.initiateMultipartUploadSecurely(request, resultHandler,
					isAsyncMode);
		} else {
			ae.initiateMultipartUploadSecurely(request, resultHandler,
					isAsyncMode);
		}
	}

	@Override
	public void uploadPartSecurely(UploadPartRequest request,
			UploadPartResponceHandler resultHandler, boolean isAsyncMode) {
		if (defaultCryptoMode == CryptoMode.EncryptionOnly) {
			eo.uploadPartSecurely(request, resultHandler, isAsyncMode);
		} else {
			ae.uploadPartSecurely(request, resultHandler, isAsyncMode);
		}
	}

	@Override
	public void abortMultipartUploadSecurely(
			AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler, boolean isAsyncMode) {
		if (defaultCryptoMode == CryptoMode.EncryptionOnly) {
			eo.abortMultipartUploadSecurely(request, handler, isAsyncMode);
		} else {
			ae.abortMultipartUploadSecurely(request, handler, isAsyncMode);
		}
	}

}
