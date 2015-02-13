package com.ksyun.ks3.exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ksyun.ks3.util.Constants;

import android.util.Log;

public class Ks3Error {
	public static final int ERROR_CODE_UNKNOWN_ERROR = -1;
	public static final int ERROR_CODE_BAD_DIGEST = 0;
	public static final int ERROR_CODE_INVALID_ACL_STR = 1;
	public static final int ERROR_CODE_INVALID_AUTHORIZATION_STR = 2;
	public static final int ERROR_CODE_INVALID_BUCKET_NAME = 3;
	public static final int ERROR_CODE_INVALID_DATE_FORMAT = 4;
	public static final int ERROR_CODE_INVALID_DIGEST = 5;
	public static final int ERROR_CODE_INVALID_ENCYPTION_ALGORITHM = 6;
	public static final int ERROR_CODE_INVALID_HOST_HEADER = 7;
	public static final int ERROR_CODE_INVALID_PARAMETER = 8;
	public static final int ERROR_CODE_INVALID_PATH = 9;
	public static final int ERROR_CODE_INVALID_QUERY_STR = 10;
	public static final int ERROR_CODE_META_DATA_TOO_LARGE = 11;
	public static final int ERROR_CODE_MISSING_DATA_HEADER = 12;
	public static final int ERROR_CODE_MISSING_HOST_HEADER = 13;
	public static final int ERROR_CODE_TOO_MANY_BUCKETS = 14;
	public static final int ERROR_CODE_BAD_PARAMS = 15;
	public static final int ERROR_CODE_IMAGE_TYPE_NOT_SUPPORT = 16;
	public static final int ERROR_CODE_MISSING_FROM_ARGS = 17;
	public static final int ERROR_CODE_CONTENT_RANGE_ERROR = 18;
	public static final int ERROR_CODE_CONTENT_LENGTH_OUT_OF_RANGE = 19;
	public static final int ERROR_CODE_POLICY_ERROR = 20;
	public static final int ERROR_CODE_EXPIRATION_ERROR = 21;
	public static final int ERROR_CODE_FORM_UNMATCH_POLICY = 22;
	public static final int ERROR_CODE_ACCESS_DENIED = 23;
	public static final int ERROR_CODE_INVALID_ACCESS_KEY = 24;
	public static final int ERROR_CODE_REQUEST_TIME_TOO_SKEWED = 25;
	public static final int ERROR_CODE_SIGNATURE_DOES_NOT_MATCH = 26;
	public static final int ERROR_CODE_URL_EXPIRED = 27;
	public static final int ERROR_CODE_NO_SUCH_BUCKET = 28;
	public static final int ERROR_CODE_NO_SUCH_KEY = 29;
	public static final int ERROR_CODE_METHOD_NOT_ALLOWED = 30;
	public static final int ERROR_CODE_BUCKET_ALREADY_EXISTS = 31;
	public static final int ERROR_CODE_BUCKET_ALREADY_OWNED_BY_YOU = 32;
	public static final int ERROR_CODE_BUCKET_NOT_EMPTY = 33;
	public static final int ERROR_CODE_INVALID_RANGE = 34;
	public static final int ERROR_CODE_INTERNAL_ERROR = 35;
	public static final int ERROR_CODE_NOT_IMPLEMENTED = 36;

	private int errorCode;
	private String errorMessage;
	private Ks3ServerError ks3ServerError;

	public Ks3Error(int statesCode, byte[] response, Throwable throwable) {
		if (response!=null) {
			Log.e(Constants.GLOBLE_LOG_TAG, new String(response));
		} else {
			Log.e(Constants.GLOBLE_LOG_TAG, "response string is null");
		}
		if (parseServerError(response)) {
			this.errorCode = judgeErrorCode(statesCode, throwable);
			this.errorMessage = ks3ServerError.getServerErrorMessage();
		} else {
			Log.e(Constants.GLOBLE_LOG_TAG, "Parse Ks3Error Failed");
		}
	}

	private int judgeErrorCode(int statesCode, Throwable throwable) {
		String serverErrorCode = ks3ServerError.getServerErrorCode();
		if (serverErrorCode != null) {
			switch (statesCode) {
			case 0:
				
				break;
			case 400:
				if (serverErrorCode.equals("BadDigest")) {
					return Ks3Error.ERROR_CODE_BAD_DIGEST;
				} else if (serverErrorCode.equals("InvalidACLString")) {
					return Ks3Error.ERROR_CODE_INVALID_ACL_STR;

				} else if (serverErrorCode.equals("InvalidAuthorizationString")) {
					return Ks3Error.ERROR_CODE_INVALID_AUTHORIZATION_STR;

				} else if (serverErrorCode.equals("InvalidBucketName")) {
					return Ks3Error.ERROR_CODE_INVALID_BUCKET_NAME;

				} else if (serverErrorCode.equals("InvalidDateFormat")) {
					return Ks3Error.ERROR_CODE_INVALID_DATE_FORMAT;

				} else if (serverErrorCode.equals("InvalidDigest")) {
					return Ks3Error.ERROR_CODE_INVALID_DIGEST;

				} else if (serverErrorCode.equals("InvalidEncryptionAlgorithm")) {
					return Ks3Error.ERROR_CODE_INVALID_ENCYPTION_ALGORITHM;

				} else if (serverErrorCode.equals("InvalidHostHeader")) {
					return Ks3Error.ERROR_CODE_INVALID_HOST_HEADER;

				} else if (serverErrorCode.equals("InvalidParameter")) {
					return Ks3Error.ERROR_CODE_INVALID_PARAMETER;

				} else if (serverErrorCode.equals("InvalidPath")) {
					return Ks3Error.ERROR_CODE_INVALID_PATH;

				} else if (serverErrorCode.equals("InvalidQueryString")) {
					return Ks3Error.ERROR_CODE_INVALID_QUERY_STR;

				} else if (serverErrorCode.equals("MetadataTooLarge")) {
					return Ks3Error.ERROR_CODE_META_DATA_TOO_LARGE;

				} else if (serverErrorCode.equals("MissingDateHeader")) {
					return Ks3Error.ERROR_CODE_MISSING_DATA_HEADER;

				} else if (serverErrorCode.equals("MissingHostHeader")) {
					return Ks3Error.ERROR_CODE_MISSING_HOST_HEADER;

				} else if (serverErrorCode.equals("TooManyBuckets")) {
					return Ks3Error.ERROR_CODE_TOO_MANY_BUCKETS;

				} else if (serverErrorCode.equals("BadParams")) {
					return Ks3Error.ERROR_CODE_BAD_PARAMS;

				} else if (serverErrorCode.equals("ImageTypeNotSupport")) {
					return Ks3Error.ERROR_CODE_IMAGE_TYPE_NOT_SUPPORT;

				} else if (serverErrorCode.equals("MissingFormArgs")) {
					return Ks3Error.ERROR_CODE_MISSING_FROM_ARGS;

				} else if (serverErrorCode.equals("ContentRangeError")) {
					return Ks3Error.ERROR_CODE_CONTENT_RANGE_ERROR;

				} else if (serverErrorCode.equals("ContentLengthOutOfRange")) {
					return Ks3Error.ERROR_CODE_CONTENT_LENGTH_OUT_OF_RANGE;

				} else if (serverErrorCode.equals("PolicyError")) {
					return Ks3Error.ERROR_CODE_POLICY_ERROR;

				} else if (serverErrorCode.equals("ExpirationError")) {
					return Ks3Error.ERROR_CODE_EXPIRATION_ERROR;

				} else if (serverErrorCode.equals("FormUnmatchPolicy")) {
					return Ks3Error.ERROR_CODE_FORM_UNMATCH_POLICY;
				}
				break;
			case 403:
				if (serverErrorCode.equals("AccessDenied")) {
					return Ks3Error.ERROR_CODE_ACCESS_DENIED;

				} else if (serverErrorCode.equals("InvalidAccessKey")) {
					return Ks3Error.ERROR_CODE_INVALID_ACCESS_KEY;

				} else if (serverErrorCode.equals("RequestTimeTooSkewed")) {
					return Ks3Error.ERROR_CODE_REQUEST_TIME_TOO_SKEWED;

				} else if (serverErrorCode.equals("SignatureDoesNotMatch")) {
					return Ks3Error.ERROR_CODE_SIGNATURE_DOES_NOT_MATCH;

				} else if (serverErrorCode.equals("URLExpired")) {
					return Ks3Error.ERROR_CODE_URL_EXPIRED;

				}
				break;
			case 404:
				if (serverErrorCode.equals("NoSuchBucket")) {
					return Ks3Error.ERROR_CODE_NO_SUCH_BUCKET;

				} else if (serverErrorCode.equals("NoSuchKey")) {
					return Ks3Error.ERROR_CODE_NO_SUCH_KEY;

				}
				break;
			case 405:
				if (serverErrorCode.equals("MethodNotAllowed")) {
					return Ks3Error.ERROR_CODE_METHOD_NOT_ALLOWED;

				}
				break;
			case 409:
				if (serverErrorCode.equals("BucketAlreadyExists")) {
					return Ks3Error.ERROR_CODE_BUCKET_ALREADY_EXISTS;

				} else if (serverErrorCode.equals("BucketAlreadyOwnedByYou")) {
					return Ks3Error.ERROR_CODE_BUCKET_ALREADY_OWNED_BY_YOU;

				} else if (serverErrorCode.equals("BucketNotEmpty")) {
					return Ks3Error.ERROR_CODE_BUCKET_NOT_EMPTY;

				}
				break;
			case 416:
				if (serverErrorCode.equals("InvalidRange")) {
					return Ks3Error.ERROR_CODE_INVALID_RANGE;

				}
				break;
			case 500:
				if (serverErrorCode.equals("InternalError")) {
					return Ks3Error.ERROR_CODE_INTERNAL_ERROR;

				}
				break;
			case 501:
				if (serverErrorCode.equals("NotImplemented")) {
					return Ks3Error.ERROR_CODE_NOT_IMPLEMENTED;

				}
				break;
			default:
				break;
			}
		}
		return Ks3Error.ERROR_CODE_UNKNOWN_ERROR;

	}

	private boolean parseServerError(byte[] response) {
		if (response != null) {
			XmlPullParserFactory factory;
			try {
				factory = XmlPullParserFactory.newInstance();
				XmlPullParser parse = factory.newPullParser();
				parse.setInput(new ByteArrayInputStream(response), "UTF-8");
				int eventType = parse.getEventType();
				while (XmlPullParser.END_DOCUMENT != eventType) {
					String nodeName = parse.getName();
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						ks3ServerError = new Ks3ServerError();
						break;
					case XmlPullParser.END_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:
						if (nodeName.equalsIgnoreCase("Code")) {
							ks3ServerError.setServerErrorCode(parse.nextText());
						}
						if (nodeName.equalsIgnoreCase("Message")) {
							ks3ServerError.setServerErrorMessage(parse
									.nextText());
						}
						if (nodeName.equalsIgnoreCase("Resource")) {
							ks3ServerError.setServerErrorResource(parse
									.nextText());
						}

						if (nodeName.equalsIgnoreCase("RequestId")) {
							ks3ServerError.setServerErrorRequsetId(parse
									.nextText());
						}
						break;
					case XmlPullParser.END_TAG:

						break;
					case XmlPullParser.TEXT:

						break;
					default:
						break;
					}
					eventType = parse.next();
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else {
			return false;
		}

	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Ks3ServerError getKs3ServerError() {
		return ks3ServerError;
	}

	public void setKs3ServerError(Ks3ServerError ks3ServerError) {
		this.ks3ServerError = ks3ServerError;
	}

}
