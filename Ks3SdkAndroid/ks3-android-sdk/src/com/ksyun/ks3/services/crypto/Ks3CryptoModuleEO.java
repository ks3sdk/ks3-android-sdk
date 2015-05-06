package com.ksyun.ks3.services.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import android.content.Context;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.crypto.CryptoConfiguration;
import com.ksyun.ks3.model.crypto.EncryptedUploadContext;
import com.ksyun.ks3.model.crypto.EncryptionMaterials;
import com.ksyun.ks3.model.crypto.EncryptionMaterialsProvider;
import com.ksyun.ks3.model.crypto.algorithm.EncryptionInstruction;
import com.ksyun.ks3.model.crypto.algorithm.EncryptionUtils;
import com.ksyun.ks3.model.result.InitiateMultipartUploadResult;
import com.ksyun.ks3.services.crypto.Ks3EncryptionClient.Ks3DirectImpl;
import com.ksyun.ks3.services.handler.AbortMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.services.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.services.request.EncryptedInitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;

public class Ks3CryptoModuleEO extends Ks3CryptoModuleBase {

	public Ks3CryptoModuleEO(Ks3DirectImpl ks3DirectImpl,
			EncryptionMaterialsProvider encryptionMaterialsProvider,
			CryptoConfiguration cryptoConfiguration, Context context) {
		super(ks3DirectImpl, encryptionMaterialsProvider, cryptoConfiguration,
				context);
	}

	@Override
	public Ks3HttpRequest putObjectSecurely(PutObjectRequest request,
			PutObjectResponseHandler handler, boolean b) {
		return putObjectUsingMetadata(request, handler);
	}

	private Ks3HttpRequest putObjectUsingMetadata(PutObjectRequest request,
			PutObjectResponseHandler handler) {
		// Create instruction
		EncryptionInstruction instruction = encryptionInstructionOf(request);

		// Remove old meta data ,and encrypt the object data with the
		// instruction
		PutObjectRequest encryptedObjectRequest = EncryptionUtils
				.encryptRequestUsingInstruction(request, instruction);

		// Update the meta data
		EncryptionUtils.updateMetadataWithEncryptionInstruction(request,
				instruction);

		// Put the encrypted object into S3
		return ks3DirectImpl.putObject(encryptedObjectRequest, handler, true);
	}

	private EncryptionInstruction encryptionInstructionOf(Ks3HttpRequest req) {
		EncryptionInstruction instruction = EncryptionUtils
				.generateInstruction(this.encryptionMaterialsProvider,
						this.cryptoConfig.getCryptoProvider());
		return instruction;
	}

	@Override
	public Ks3HttpRequest getObjectSecurely(GetObjectRequest request,
			GetObjectResponseHandler handler, boolean b) {
		// Adjust the crypto range to retrieve all of the cipher blocks needed
		// to contain the user's desired
		// range of bytes.
		// long[] desiredRange = request.getRange();

		// range is now not support
		// long[] desiredRange = new long[]{0,100};
		// if (isStrict() && desiredRange != null)
		// throw new
		// SecurityException("Range get is not allowed in strict crypto mode");
		// long[] adjustedCryptoRange =
		// EncryptionUtils.getAdjustedCryptoRange(desiredRange);
		// if (adjustedCryptoRange != null)
		// request.setRange(adjustedCryptoRange[0], adjustedCryptoRange[1]);

		// Get the object from S3
		// S3Object retrieved = s3.getObject(req);
		handler.isCryptoMode = true;
		handler.encryptionMaterialsProvider = encryptionMaterialsProvider;
		handler.cryptoConfiguration = cryptoConfig;
		Ks3HttpRequest sendRequest = ks3DirectImpl.getObject(request, handler,
				true);
		return sendRequest;
		// If the caller has specified constraints, it's possible that
		// super.getObject(...)
		// would return null, so we simply return null as well.
	}

	private boolean isStrict() {
		return false;
	}

	@Override
	public void completeMultipartUploadSecurely(
			CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler, boolean b) {

	}

	@Override
	public void initiateMultipartUploadSecurely(
			InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler, boolean b) {
		// Generate a one-time use symmetric key and initialize a cipher to
		// encrypt object data
		SecretKey envelopeSymmetricKey = EncryptionUtils
				.generateOneTimeUseSymmetricKey();
		Cipher symmetricCipher = EncryptionUtils.createSymmetricCipher(
				envelopeSymmetricKey, Cipher.ENCRYPT_MODE,
				cryptoConfig.getCryptoProvider(), null);

		EncryptionMaterials encryptionMaterials = null;
		if (request instanceof EncryptedInitiateMultipartUploadRequest) {
			encryptionMaterials = encryptionMaterialsProvider
					.getEncryptionMaterials(((EncryptedInitiateMultipartUploadRequest) request)
							.getMaterialsDescription());
		} else {
			encryptionMaterials = encryptionMaterialsProvider
					.getEncryptionMaterials();
		}
		// Encrypt the envelope symmetric key
		byte[] encryptedEnvelopeSymmetricKey = EncryptionUtils
				.getEncryptedSymmetricKey(envelopeSymmetricKey,
						encryptionMaterials, cryptoConfig.getCryptoProvider());

		// Store encryption info in metadata
		ObjectMetadata metadata = EncryptionUtils
				.updateMetadataWithEncryptionInfo(request,
						encryptedEnvelopeSymmetricKey, symmetricCipher,
						encryptionMaterials.getMaterialsDescription());

		// Update the request's metadata to the updated metadata
		request.setObjectMeta(metadata);

		EncryptedUploadContext encryptedUploadContext = new EncryptedUploadContext(
				request.getBucketname(), request.getObjectkey(),
				envelopeSymmetricKey);
		encryptedUploadContext.setNextInitializationVector(symmetricCipher
				.getIV());
		encryptedUploadContext.setFirstInitializationVector(symmetricCipher
				.getIV());
		if (request instanceof EncryptedInitiateMultipartUploadRequest) {
			encryptedUploadContext
					.setMaterialsDescription(((EncryptedInitiateMultipartUploadRequest) request)
							.getMaterialsDescription());
		}
		resultHandler.setEncryptedUploadContext = encryptedUploadContext;
		// move here
		ks3DirectImpl.initiateMultipartUpload(request, resultHandler, true);
		// multipartUploadContexts.put(result.getUploadId(),
		// encryptedUploadContext);

		// return null;
	}

	@Override
	public void uploadPartSecurely(UploadPartRequest request,
			UploadPartResponceHandler resultHandler, boolean b) {

	}

	@Override
	public void abortMultipartUploadSecurely(
			AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler, boolean b) {

	}

}
