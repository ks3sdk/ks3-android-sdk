package com.ksyun.ks3.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import android.util.Log;
import com.ksyun.ks3.util.Constants;

public class Mimetypes {
	// The default XML mimetype: application/xml
	public static final String MIMETYPE_XML = "application/xml";
	// The default HTML mimetype: text/html
	public static final String MIMETYPE_HTML = "text/html";
	// The default binary mimetype: application/octet-stream
	public static final String MIMETYPE_OCTET_STREAM = "application/octet-stream";
	// The default gzip mimetype: application/x-gzip
	public static final String MIMETYPE_GZIP = "application/x-gzip";
	private static Mimetypes mimetypes = null;
	private HashMap<String, String> extensionToMimetypeMap = new HashMap<String, String>();

	private Mimetypes() {
	}

	public synchronized static Mimetypes getInstance() {
		if (mimetypes != null)
			return mimetypes;

		mimetypes = new Mimetypes();
		InputStream is = mimetypes.getClass()
				.getResourceAsStream("/assets/mime.types");
		if (is != null) {
//			Log.d(Constants.GLOBLE_LOG_TAG,
//					"Loading mime types from file in the classpath: mime.types");
			try {
				mimetypes.loadAndReplaceMimetypes(is);
			} catch (IOException e) {
//				Log.e(Constants.GLOBLE_LOG_TAG,
//						"Failed to load mime types from file in the classpath: mime.types");
			} finally {
				try {
					is.close();
				} catch (IOException ex) {
					Log.d(Constants.GLOBLE_LOG_TAG,
							"Failed to close inputstream");
				}
			}
		} else {
			Log.w(Constants.GLOBLE_LOG_TAG,
					"Unable to find 'mime.types' file in classpath");
		}
		return mimetypes;
	}

	public void loadAndReplaceMimetypes(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;

		while ((line = br.readLine()) != null) {
			line = line.trim();

			if (line.startsWith("#") || line.length() == 0) {
				// Ignore comments and empty lines.
			} else {
				StringTokenizer st = new StringTokenizer(line, " \t");
				if (st.countTokens() > 1) {
					String mimetype = st.nextToken();
					while (st.hasMoreTokens()) {
						String extension = st.nextToken();
						extensionToMimetypeMap.put(extension.toLowerCase(),
								mimetype);
//						Log.d(Constants.GLOBLE_LOG_TAG,
//								"Setting mime type for extension '"
//										+ extension.toLowerCase() + "' to '"
//										+ mimetype + "'");
					}
				} else {
//					Log.d(Constants.GLOBLE_LOG_TAG,
//							"Ignoring mimetype with no associated file extensions: '"
//									+ line + "'");
				}
			}
		}
	}

	public String getMimetype(String fileName) {
		int lastPeriodIndex = fileName.lastIndexOf(".");
		if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length()) {
			String ext = fileName.substring(lastPeriodIndex + 1).toLowerCase();
			if (extensionToMimetypeMap.keySet().contains(ext)) {
				String mimetype = (String) extensionToMimetypeMap.get(ext);
//				Log.d(Constants.GLOBLE_LOG_TAG, "Recognised extension '" + ext
//						+ "', mimetype is: '" + mimetype + "'");

				return mimetype;
			} else {
//				Log.d(Constants.GLOBLE_LOG_TAG, "Extension '" + ext
//						+ "' is unrecognized in mime type listing"
//						+ ", using default mime type: '"
//						+ MIMETYPE_OCTET_STREAM + "'");
			}
		} else {
//			Log.d(Constants.GLOBLE_LOG_TAG,
//					"File name has no extension, mime type cannot be recognised for: "
//							+ fileName);
		}
		return MIMETYPE_OCTET_STREAM;
	}

	public String getMimetype(File file) {
		return getMimetype(file.getName());
	}
}
