package com.ksyun.ks3.services.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import android.content.Context;
import android.util.Log;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.crypto.CryptoConfiguration;
import com.ksyun.ks3.model.crypto.EncryptedUploadContext;
import com.ksyun.ks3.model.crypto.EncryptionMaterials;
import com.ksyun.ks3.model.crypto.EncryptionMaterialsProvider;
import com.ksyun.ks3.model.crypto.algorithm.ByteRangeCapturingInputStream;
import com.ksyun.ks3.model.crypto.algorithm.CipherFactory;
import com.ksyun.ks3.model.crypto.algorithm.EncryptionInstruction;
import com.ksyun.ks3.model.crypto.algorithm.EncryptionUtils;
import com.ksyun.ks3.model.crypto.algorithm.JceEncryptionConstants;
import com.ksyun.ks3.model.result.CompleteMultipartUploadResult;
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
import com.ksyun.ks3.util.Constants;

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
		Log.d(Constants.LOG_TAG, "encryption completeMultipartUploadSecurely");
	        String uploadId = request.getUploadId();
	        EncryptedUploadContext encryptedUploadContext = multipartUploadContexts.get(uploadId);

	        if (encryptedUploadContext.hasFinalPartBeenSeen() == false) {
	            Log.d(Constants.LOG_TAG, "Unable to complete an encrypted multipart upload without being told which part was the last.  "
                        +
                        "Without knowing which part was the last, the encrypted data in Amazon S3 is incomplete and corrupt.");
	        }

	        ks3DirectImpl.completeMultipartUpload(request,handler,true);
	        // In InstructionFile mode, we want to write the instruction file only
	        // after the whole upload has completed correctly.
//	        if (cryptoConfig.getStorageMode() == CryptoStorageMode.InstructionFile) {
//	            Cipher symmetricCipher = createSymmetricCipher(
//	                    encryptedUploadContext.getEnvelopeEncryptionKey(),
//	                    Cipher.ENCRYPT_MODE, cryptoConfig.getCryptoProvider(),
//	                    encryptedUploadContext.getFirstInitializationVector());
//
//	            EncryptionMaterials encryptionMaterials;
//	            if (encryptedUploadContext.getMaterialsDescription() != null) {
//	                encryptionMaterials = kekMaterialsProvider
//	                        .getEncryptionMaterials(encryptedUploadContext.getMaterialsDescription());
//	            } else {
//	                encryptionMaterials = kekMaterialsProvider.getEncryptionMaterials();
//	            }
//
//	            // Encrypt the envelope symmetric key
//	            byte[] encryptedEnvelopeSymmetricKey = getEncryptedSymmetricKey(
//	                    encryptedUploadContext.getEnvelopeEncryptionKey(), encryptionMaterials,
//	                    cryptoConfig.getCryptoProvider());
//	            EncryptionInstruction instruction = new EncryptionInstruction(
//	                    encryptionMaterials.getMaterialsDescription(), encryptedEnvelopeSymmetricKey,
//	                    encryptedUploadContext.getEnvelopeEncryptionKey(), symmetricCipher);
//
//	            // Put the instruction file into S3
//	            s3.putObject(EncryptionUtils.createInstructionPutRequest(
//	                    encryptedUploadContext.getBucketName(), encryptedUploadContext.getKey(),
//	                    instruction));
//	        }
	        multipartUploadContexts.remove(uploadId);
	}

	@Override
	public void initiateMultipartUploadSecurely(
			InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler, boolean b) {
		// Generate a one-time use symmetric key and initialize a cipher to
		// encrypt object data
		Log.d(Constants.LOG_TAG, "encryption initiateMultipartUploadSecurely");
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
//		 multipartUploadContexts.put(result.getUploadId(),
//		 encryptedUploadContext);

		// return null;
	}

	@Override
	public void uploadPartSecurely(UploadPartRequest request,
			UploadPartResponceHandler resultHandler, boolean b) {
		Log.d(Constants.LOG_TAG, "encryption uploadPartSecurely");

		boolean isLastPart = request.isLastPart();
		String uploadId = request.getUploadId();

		boolean partSizeMultipleOfCipherBlockSize = request.getPartSize()
				% JceEncryptionConstants.SYMMETRIC_CIPHER_BLOCK_SIZE == 0;
		if (!isLastPart && !partSizeMultipleOfCipherBlockSize) {
			Log.d(Constants.LOG_TAG,
					"Invalid part size: part sizes for encrypted multipart uploads must be multiples "
							+ "of the cipher block size ("
							+ JceEncryptionConstants.SYMMETRIC_CIPHER_BLOCK_SIZE
							+ ") with the exception of the last part.  "
							+ "Otherwise encryption adds extra padding that will corrupt the final object.");

		}

		// Generate the envelope symmetric key and initialize a cipher to
		// encrypt the object's data
		EncryptedUploadContext encryptedUploadContext = multipartUploadContexts
				.get(uploadId);
		Log.d(Constants.LOG_TAG, "upload id = "+uploadId);
		if (encryptedUploadContext == null)
			Log.d(Constants.LOG_TAG,
					"No client-side information available on upload ID "
							+ uploadId);

		SecretKey envelopeSymmetricKey = encryptedUploadContext
				.getEnvelopeEncryptionKey();
		byte[] iv = encryptedUploadContext.getNextInitializationVector();
		CipherFactory cipherFactory = new CipherFactory(envelopeSymmetricKey,
				Cipher.ENCRYPT_MODE, iv, this.cryptoConfig.getCryptoProvider());

		// Create encrypted input stream
		ByteRangeCapturingInputStream encryptedInputStream = EncryptionUtils
				.getEncryptedInputStream(request, cipherFactory);
		request.setRequestBody(encryptedInputStream);
//		request.setInputStream(encryptedInputStream);

		// The last part of the multipart upload will contain extra padding from
		// the encryption process
		if (request.isLastPart()) {
			// We only change the size of the last part
			long cryptoContentLength = EncryptionUtils
					.calculateCryptoContentLength(cipherFactory.createCipher(),
							request);
			if (cryptoContentLength > 0)
				request.setPartSize(cryptoContentLength);

			if (encryptedUploadContext.hasFinalPartBeenSeen()) {
				Log.d(Constants.LOG_TAG,
						"This part was specified as the last part in a multipart upload, but a previous part was already marked as the last part.  "
								+ "Only the last part of the upload should be marked as the last part, otherwise it will cause the encrypted data to be corrupted.");
			}

			encryptedUploadContext.setHasFinalPartBeenSeen(true);
		}

		// Treat all encryption requests as input stream upload requests, not as
		// file upload requests.
//		request.setFile(null);
		request.setFileOffset(0);
		request.setEncrypt(true);
		ks3DirectImpl.uploadPart(request, resultHandler, true);
		encryptedUploadContext.setNextInitializationVector(encryptedInputStream
				.getBlock());
	}

	@Override
	public void abortMultipartUploadSecurely(
			AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler, boolean b) {
		ks3DirectImpl.abortMultipartUpload(request, handler, true);
	}

}
