package com.ksyun.ks3.services.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.CipherInputStream;
import org.apache.http.Header;
import android.database.CursorJoiner.Result;
import android.os.Environment;
import android.util.Log;

import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.ObjectMetadata.Meta;
import com.ksyun.ks3.model.crypto.CryptoConfiguration;
import com.ksyun.ks3.model.crypto.EncryptionMaterialsProvider;
import com.ksyun.ks3.model.crypto.algorithm.CipherLiteInputStream;
import com.ksyun.ks3.model.crypto.algorithm.ContentCryptoMaterial;
import com.ksyun.ks3.model.crypto.algorithm.KS3ObjectInputStream;
import com.ksyun.ks3.model.result.GetObjectResult;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.Md5Utils;

public abstract class GetObjectResponseHandler extends
		com.ksyun.ks3.asynchttpclient.FileAsyncHttpResponseHandler {
	private String mBucketName;
	private String mObjectKey;
	public boolean isCryptoMode;
	public EncryptionMaterialsProvider encryptionMaterialsProvider;
	public CryptoConfiguration cryptoConfiguration;
	protected static final int DEFAULT_BUFFER_SIZE = 1024 * 2;

	public GetObjectResponseHandler(File file, boolean append) {
		super(file, append);
	}

	public GetObjectResponseHandler(File file, String buckName, String objectKey) {
		this(file, false);
		this.mBucketName = buckName;
		this.mObjectKey = objectKey;
	}

	public abstract void onTaskProgress(double progress);

	public abstract void onTaskStart();

	public abstract void onTaskFinish();

	public abstract void onTaskCancel();

	public abstract void onTaskSuccess(int paramInt,
			Header[] paramArrayOfHeader, GetObjectResult getObjectResult);

	public abstract void onTaskFailure(int paramInt, Ks3Error error,
			Header[] paramArrayOfHeader, Throwable paramThrowable,
			File paramFile);

	@Override
	public final void onFailure(int statesCode, Header[] paramArrayOfHeader,
			Throwable throwable, byte[] response, File paramFile) {
		Ks3Error error = new Ks3Error(statesCode, response, throwable);
		this.onTaskFailure(statesCode, error, paramArrayOfHeader, throwable,
				paramFile);
	}

	@Override
	public final void onSuccess(int paramInt, Header[] paramArrayOfHeader,
			File paramFile) {
		GetObjectResult result = parse(paramInt, paramArrayOfHeader, paramFile);
		ObjectMetadata metadata = result.getObject().getObjectMetadata();
		if (hasEncryptionInfo(metadata)) {
			decipherWithMetadata(metadata, result);
		}
		this.onTaskSuccess(paramInt, paramArrayOfHeader, result);
	}

	private boolean hasEncryptionInfo(ObjectMetadata metadata) {
		return true;
	}

	private void decipherWithMetadata(ObjectMetadata metadata,
			GetObjectResult result) {
		ContentCryptoMaterial cekMaterial = ContentCryptoMaterial
				.fromObjectMetadata(metadata, encryptionMaterialsProvider,
						cryptoConfiguration.getCryptoProvider());
		decrypt(cekMaterial, result);
	}

	private void decrypt(ContentCryptoMaterial cekMaterial,
			GetObjectResult result) {
		try {
			KS3ObjectInputStream objectContent = new KS3ObjectInputStream(
					new BufferedInputStream(new FileInputStream(result
							.getObject().getFile())));
			decryptContent(objectContent, cekMaterial, result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void decryptContent(KS3ObjectInputStream objectContent,
			ContentCryptoMaterial cekMaterial, GetObjectResult result) {
		KS3ObjectInputStream inputStream = new KS3ObjectInputStream(
				new CipherLiteInputStream(objectContent,
						cekMaterial.getCipherLite(), DEFAULT_BUFFER_SIZE));
		// inputstreamtofile(inputStream, result.getObject().getFile());
		File file = result.getObject().getFile();
		String filename = file.getAbsolutePath();
		Log.d(Constants.LOG_TAG, "filename = " + filename);
		file.delete();
		Log.d(Constants.LOG_TAG, "file delete " );
		File desFile = new File("/storage/emulated/0/ksyun_download/liekkas1.java");
		Log.d(Constants.LOG_TAG, "file create " );
		inputstreamtofile(inputStream, desFile);
		result.getObject().setFile(desFile);
	}

	public void inputstreamtofile(InputStream ins, File file) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
				os.write(buffer, 0, bytesRead);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (ins != null) {
					ins.close();
				}

			} catch (Exception e2) {

			}

		}

	}

	public static boolean isFileExist(String director) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + director);
		return file.exists();
	}

	public static boolean createFile(String director) {
		if (isFileExist(director)) {
			return true;
		} else {
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + director);
			if (!file.mkdirs()) {
				return false;
			}
			return true;
		}
	}

	public File writeToSDCardFromInput(String directory, String fileName,
			InputStream input) {
		File file = null;
		OutputStream os = null;
		try {
			if (createFile(directory)) {
				return file;
			}
			file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + directory + fileName);
			os = new FileOutputStream(file);
			byte[] data = new byte[1024];
			int length = -1;
			while ((length = input.read(data)) != -1) {
				os.write(data, 0, length);
			}
			// clear cache
			os.flush();
		} catch (Exception e) {
			Log.e("FileUtil", "" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	@Override
	public final void onProgress(int bytesWritten, int totalSize) {
		double progress = Double.valueOf(totalSize > 0 ? bytesWritten * 1.0D
				/ totalSize * 100.0D : -1.0D);
		onTaskProgress(progress);
	}

	@Override
	public final void onStart() {
		onTaskStart();
	}

	@Override
	public final void onFinish() {
		onTaskFinish();
	}

	@Override
	public final void onCancel() {
		onTaskCancel();
	}

	@Override
	public final boolean deleteTargetFile() {
		return (getTargetFile() != null) && (getTargetFile().delete());
	}

	@Override
	protected final File getTargetFile() {
		assert (this.mFile != null);
		return this.mFile;
	}

	private GetObjectResult parse(int statesCode, Header[] responceHeaders,
			File file) {
		GetObjectResult result = new GetObjectResult();
		ObjectMetadata metaData = new ObjectMetadata();

		result.getObject().setBucketName(mBucketName);
		result.getObject().setKey(mObjectKey);
		if (statesCode == 200 || statesCode == 206) {
			result.getObject().setFile(file);
			Header[] headers = responceHeaders;
			for (int i = 0; i < headers.length; i++) {
				String name = headers[i].getName();
				String value = headers[i].getValue();
				if (HttpHeaders.XKssWebsiteRedirectLocation.toString()
						.equalsIgnoreCase(name)) {
					result.getObject().setRedirectLocation(value);
				} else if (name.startsWith(ObjectMetadata.userMetaPrefix)) {
					metaData.addOrEditUserMeta(headers[i].getName(), value);
				} else {
					if (name.equalsIgnoreCase(HttpHeaders.LastModified
							.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.LastModified, value);

					} else if (name.equalsIgnoreCase(HttpHeaders.ETag
							.toString())) {

						metaData.addOrEditMeta(ObjectMetadata.Meta.Etag,
								value.replace("\"", ""));
						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentMD5,
								Md5Utils.ETag2MD5(value));

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.CacheControl
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.CacheControl, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentDisposition
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.ContentDisposition, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentEncoding
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.ContentEncoding, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentLength
									.toString())) {

						metaData.addOrEditMeta(
								ObjectMetadata.Meta.ContentLength, value);

					} else if (name
							.equalsIgnoreCase(ObjectMetadata.Meta.ContentType
									.toString())) {

						metaData.addOrEditMeta(ObjectMetadata.Meta.ContentType,
								value);

					} else if (name.equalsIgnoreCase(Meta.Expires.toString())) {

						metaData.setExpires(value);

					}
				}
			}
			result.getObject().setObjectMetadata(metaData);
		} else if (statesCode == 304) {
			result.setIfModified(false);
		} else if (statesCode == 412) {
			result.setIfPreconditionSuccess(false);
		}
		return result;
	}

}
