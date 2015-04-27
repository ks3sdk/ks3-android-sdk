package com.ksyun.ks3.model;

public enum HttpHeaders {
	RequestId("x-kss-request-id"), Authorization("Authorization"), Date("Date"), Host(
			"Host"), ContentMD5("Content-MD5"),UserAgent("User-Agent"),IfMatch("If-Match"),
			IfNoneMatch("If-None-Match"),
			IfModifiedSince("If-Modified-Since"),
			IfUnmodifiedSince("If-Unmodified-Since"),
	/* Put object metadata */
	ContentLength("Content-Length"), CacheControl("Cache-Control"), ContentType(
			"Content-Type"), ContentDisposition("Content-Disposition"), ContentEncoding(
			"Content-Encoding"), Expires("Expires"),Range("Range"),
	/* Acl */
	CannedAcl("x-kss-acl"), AclPrivate("x-kss-acl-private"), AclPubicRead(
			"x-kss-acl-public-read"), AclPublicReadWrite(
			"x-kss-acl-public-write"), AclPublicAuthenticatedRead(
			"x-kss-acl-public-authenticated-read"), GrantFullControl(
			"x-kss-grant-full-control"), GrantRead("x-kss-grant-read"), GrantWrite(
			"x-kss-grant-write"), ServerSideEncryption(
			"x-kss-server-side-encryption"), ETag("ETag"), LastModified(
			"Last-Modified"),
	/* Get object response */
	/* Default false */
	XKssDeleteMarker("x-kss-delete-marker"), XKssExpiration("x-kss-expiration"),
	/* Default None */
	XKssRestore("x-kss-restore"),
	/* Default None */
	XKssWebsiteRedirectLocation("x-kss-website-redirect-location"),
	XKssCopySource("x-kss-copy-source"),
	
	/*Call back */
	XKssCallBackUrl("x-kss-callbackurl"),
	XKssCallBackBody("x-kss-callbackbody"),
	CRYPTO_KEY("x-amz-key"),
	CRYPTO_KEY_V2("x-amz-key-v2"),
	CRYPTO_IV("x-amz-iv"),
	MATERIALS_DESCRIPTION("x-amz-matdesc"),
	UNENCRYPTED_CONTENT_LENGTH("x-amz-unencrypted-content-length"),
	UNENCRYPTED_CONTENT_MD5("UNENCRYPTED_CONTENT_MD5"),
	CRYPTO_KEYWRAP_ALGORITHM("x-amz-wrap-alg"),
	CRYPTO_CEK_ALGORITHM("x-amz-cek-alg"),
	CRYPTO_TAG_LENGTH("x-amz-tag-len");
	private String value;

	HttpHeaders(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
