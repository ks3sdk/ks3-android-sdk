package com.ksyun.ks3.services.crypto;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.Ks3CryptoModule;
import com.ksyun.ks3.model.crypto.CryptoConfiguration;
import com.ksyun.ks3.model.crypto.EncryptionMaterials;
import com.ksyun.ks3.model.crypto.EncryptionMaterialsProvider;
import com.ksyun.ks3.model.crypto.StaticEncryptionMaterialsProvider;
import com.ksyun.ks3.services.AuthListener;
import com.ksyun.ks3.services.Ks3Client;
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
import android.content.Context;

public class Ks3EncryptionClient extends Ks3Client {
	private Ks3CryptoModule crypto;
	private static Ks3EncryptionClient client;

	public static Ks3EncryptionClient getInstance() throws Ks3ClientException {
		if (client == null) {
			throw new Ks3ClientException(
					"Client first setup must use another getInstance() method with init params");
		}
		return client;
	}

	public static Ks3EncryptionClient getInstance(String accesskeyid,
			String accesskeysecret, EncryptionMaterials encryptionMaterials,
			CryptoConfiguration cryptoConfiguration, Context context) {
		client = new Ks3EncryptionClient(accesskeyid, accesskeysecret,
				encryptionMaterials, cryptoConfiguration, context);
		return client;
	}

	public static Ks3EncryptionClient getInstance(AuthListener listener,
			EncryptionMaterials encryptionMaterials,
			CryptoConfiguration cryptoConfiguration, Context context) {
		client = new Ks3EncryptionClient(listener, encryptionMaterials,
				cryptoConfiguration, context);
		return client;
	}

	// AK&SK

	public Ks3EncryptionClient(String accesskeyid, String accesskeysecret,
			EncryptionMaterials encryptionMaterials,
			CryptoConfiguration cryptoConfiguration, Context context) {
		this(accesskeyid, accesskeysecret,
				new StaticEncryptionMaterialsProvider(encryptionMaterials),
				cryptoConfiguration, context);
	}

	public Ks3EncryptionClient(String accesskeyid, String accesskeysecret,
			EncryptionMaterialsProvider encryptionMaterialsProvider,
			CryptoConfiguration cryptoConfiguration, Context context) {
		super(accesskeyid, accesskeysecret, context);
		client = this;
		crypto = new CryptoModuleDispatcher(new Ks3DirectImpl(),
				encryptionMaterialsProvider, cryptoConfiguration, context);

	}

	// AuthListener

	public Ks3EncryptionClient(AuthListener listener,
			EncryptionMaterials encryptionMaterials,
			CryptoConfiguration cryptoConfiguration, Context context) {
		this(listener, new StaticEncryptionMaterialsProvider(
				encryptionMaterials), cryptoConfiguration, context);

	}

	public Ks3EncryptionClient(AuthListener listener,
			EncryptionMaterialsProvider encryptionMaterialsProvider,
			CryptoConfiguration cryptoConfiguration, Context context) {
		super(listener, context);
		crypto = new CryptoModuleDispatcher(new Ks3DirectImpl(),
				encryptionMaterialsProvider, cryptoConfiguration, context);
	}

	// Replaceable Encryption Method
	@Override
	public Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler) {
		return crypto.putObjectSecurely(request, handler, true);
	}

	@Override
	public Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler handler) {
		return crypto.getObjectSecurely(request, handler, true);
	}

	@Override
	public void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler) {
		crypto.completeMultipartUploadSecurely(request, handler, true);
	}

	@Override
	public void initiateMultipartUpload(InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler) {
		crypto.initiateMultipartUploadSecurely(request, resultHandler, true);
	}

	@Override
	public void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler) {
		crypto.uploadPartSecurely(request, resultHandler, true);
	}

	@Override
	public void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler) {
		crypto.abortMultipartUploadSecurely(request, handler, true);
	}

	public final class Ks3DirectImpl extends Ks3Direct {

		@Override
		public Ks3HttpRequest putObject(PutObjectRequest request,
				PutObjectResponseHandler handler, boolean b) {

			return Ks3EncryptionClient.super.putObject(request, handler);
		}

		@Override
		public Ks3HttpRequest getObject(GetObjectRequest request,
				GetObjectResponseHandler handler, boolean b) {

			return Ks3EncryptionClient.super.getObject(request, handler);
		}

		@Override
		public void completeMultipartUpload(
				CompleteMultipartUploadRequest request,
				CompleteMultipartUploadResponseHandler handler, boolean b) {
			Ks3EncryptionClient.super.completeMultipartUpload(request, handler);
		}

		@Override
		public void initiateMultipartUpload(
				InitiateMultipartUploadRequest request,
				InitiateMultipartUploadResponceHandler resultHandler, boolean b) {
			Ks3EncryptionClient.super.initiateMultipartUpload(request,
					resultHandler);
		}

		@Override
		public void uploadPart(UploadPartRequest request,
				UploadPartResponceHandler resultHandler, boolean b) {
			Ks3EncryptionClient.super.uploadPart(request, resultHandler);
		}

		@Override
		public void abortMultipartUpload(AbortMultipartUploadRequest request,
				AbortMultipartUploadResponseHandler handler, boolean b) {
			Ks3EncryptionClient.super.abortMultipartUpload(request, handler);
		}

	}

}
