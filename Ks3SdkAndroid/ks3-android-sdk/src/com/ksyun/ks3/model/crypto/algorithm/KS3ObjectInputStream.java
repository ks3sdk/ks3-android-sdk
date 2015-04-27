/*
 * Copyright 2012-2015 Amazon Technologies, Inc.
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

package com.ksyun.ks3.model.crypto.algorithm;

import java.io.InputStream;

/**
 * Input stream representing the content of an {@link S3Object}. In addition to
 * the methods supplied by the {@link InputStream} class,
 * {@link S3ObjectInputStream} supplies the abort() method, which will terminate
 * an HTTP connection to the S3 object.
 */
public class KS3ObjectInputStream extends SdkFilterInputStream {

	protected KS3ObjectInputStream(InputStream in) {
		super(in);
		// TODO Auto-generated constructor stub
	}

}
