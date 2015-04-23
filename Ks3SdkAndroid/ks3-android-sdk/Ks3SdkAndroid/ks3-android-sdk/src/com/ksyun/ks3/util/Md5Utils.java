/*
 * Copyright 2012-2014 Amazon Technologies, Inc.
 *
 * Portions copyright 2006-2009 James Murty. Please see LICENSE.txt
 * for applicable license terms and NOTICE.txt for applicable notices.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ksyun.ks3.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;
import android.util.Log;

public class Md5Utils {
    private static final int SIXTEEN_K = 1 << 14;
    public static byte[] computeMD5Hash(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[SIXTEEN_K];
            int bytesRead;
            while ( (bytesRead = bis.read(buffer, 0, buffer.length)) != -1 ) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            // should never get here
            throw new IllegalStateException(e);
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
            	Log.e("ks3", "Unable to close input stream of hash candidate: " + e);
            }
        }
    }

    /**
     * Returns the MD5 in base64 for the data from the given input stream.
     * Note this method closes the given input stream upon completion.
     */
    public static String md5AsBase64(InputStream is) throws IOException {
    	return Base64.encodeToString(computeMD5Hash(is), Base64.DEFAULT).trim();
    }

    /**
     * Computes the MD5 hash of the given data and returns it as an array of
     * bytes.
     */
    public static byte[] computeMD5Hash(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            // should never get here
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the MD5 in base64 for the given byte array.
     */
    public static String md5AsBase64(byte[] input) {
    	return Base64.encodeToString(computeMD5Hash(input), Base64.DEFAULT).trim();    	
    }

    /**
     * Computes the MD5 of the given file.
     */
    public static byte[] computeMD5Hash(File file) throws FileNotFoundException, IOException {
        return computeMD5Hash(new FileInputStream(file));
    }

    /**
     * Returns the MD5 in base64 for the given file.
     */
    public static String md5AsBase64(File file) throws FileNotFoundException, IOException {
    	return Base64.encodeToString(computeMD5Hash(file), Base64.DEFAULT).trim();
    }
    
    
    public static String MD52ETag(String md5)
	{
		String etag = String.format("\"%s\"", Hex.encodeHexString(Base64.decode(md5, Base64.DEFAULT)));
		Log.i("ks3","md5 we calculated is :"+md5+",convert to etag is :"+etag);
		return etag;
	}
	
	//将eTag转换成md5
	public static String ETag2MD5(String eTag)
	{
		String md5 = null;
		if(eTag.length()>=2){
			if(eTag.charAt(0)=='"'){
				eTag = eTag.substring(1, eTag.length()-1);
			}
			try {
				
				md5 = new String(Base64.encode(Hex.decodeHex(eTag.toCharArray()), Base64.DEFAULT) , "UTF-8");
			} catch(Exception e) {
				Log.e("ks3","Something Wrong when converter eTag to md5 :" + eTag);
			}
		}
		Log.i("ks3","etag we calculated is :"+eTag+",convert to md5 is :"+md5);
		return md5;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
