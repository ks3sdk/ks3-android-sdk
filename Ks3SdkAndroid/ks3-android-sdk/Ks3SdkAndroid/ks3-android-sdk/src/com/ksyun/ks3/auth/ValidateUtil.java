package com.ksyun.ks3.auth;

public class ValidateUtil {
	public static final int MIN_BUCKET_NAME_LENGTH = 3;
	public static final int MAX_BUCKET_NAME_LENGTH = 63;

	public static String validateBucketName(String bname) {
		if (bname == null) {
			return null;
		}

		if (bname.length() < MIN_BUCKET_NAME_LENGTH
				|| bname.length() > MAX_BUCKET_NAME_LENGTH) {
			return null;
		}

		char previous = '\0';

		for (int i = 0; i < bname.length(); ++i) {
			char next = bname.charAt(i);

			if (next >= 'A' && next <= 'Z') {
				return null;
			}

			if (next == ' ' || next == '\t' || next == '\r' || next == '\n') {
				return null;
			}

			if (next == '.') {
				if (previous == '.') {
					return null;
				}
				if (previous == '-') {
					return null;
				}
			} else if (next == '-') {
				if (previous == '.') {
					return null;
				}
			} else if ((next < '0') || (next > '9' && next < 'a')
					|| (next > 'z')) {
				return null;
			}

			previous = next;
		}
		if (previous == '.' || previous == '-') {
			return null;
		}
		return bname;
	}
}
