package com.ks3.demo.main;

import java.io.File;
import android.util.Log;
import com.ksyun.ks3.model.transfer.TransferManagerConfiguration;
import com.ksyun.ks3.services.request.PutObjectRequest;

/**
 *
 * 上传下载工具类
 *
 */
public class TransferUtils {
	// 判断当前上传是否需要使用分块上传
	public static boolean needUseMultipartUpload(File mFile,
			TransferManagerConfiguration configuration) {
		if (mFile != null) {
			return mFile.length() > configuration.getMultipartUploadThreshold();
		}
		return false;
	}
	
	// 获取最优分块大小
	public static long getOptimalPartSize(File file,
			TransferManagerConfiguration configuration) {
		long resultSize = calculateOptimalPartSize(file, configuration);
		return resultSize;
	}

	public static long calculateOptimalPartSize(File file,
			TransferManagerConfiguration paramTransferManagerConfiguration) {
		double d1 = file.length();
		double d2 = d1 / 10000.0D;
		d2 = Math.ceil(d2);
		return (long) Math.max(d2,
				paramTransferManagerConfiguration.getMinimumUploadPartSize());
	}

	public static File getRequestFile(PutObjectRequest paramPutObjectRequest)
	  {
	    if (paramPutObjectRequest.getFile() != null)
	      return paramPutObjectRequest.getFile();
	    return null;
	  }
	
	 public static long getContentLength(PutObjectRequest paramPutObjectRequest)
	  {
	    File localFile = getRequestFile(paramPutObjectRequest);
	    if (localFile != null)
	      return localFile.length();
	    if ((paramPutObjectRequest.getRequestBody() != null) && (paramPutObjectRequest.getObjectMeta().getContentLength() > 0L))
	      return paramPutObjectRequest.getObjectMeta().getContentLength();
	    return -1L;
	  }
}
