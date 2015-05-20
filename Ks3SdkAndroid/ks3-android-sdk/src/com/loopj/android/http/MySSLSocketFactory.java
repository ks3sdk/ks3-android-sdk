package com.loopj.android.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class MySSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory
{

	SSLContext sslContext = SSLContext.getInstance("TLS");

	public MySSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
	{

		super(truststore);

		X509TrustManager tm = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {

				return null;
			}
		};
		this.sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException
	{

		return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException
	{

		return this.sslContext.getSocketFactory().createSocket();
	}

	public void fixHttpsURLConnection()
	{

		HttpsURLConnection.setDefaultSSLSocketFactory(this.sslContext.getSocketFactory());
	}

	public static KeyStore getKeystoreOfCA(InputStream cert)
	{

		InputStream caInput = null;
		Certificate ca = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			caInput = new BufferedInputStream(cert);
			ca = cf.generateCertificate(caInput);
		} catch (CertificateException e) {
			e.printStackTrace();
		} finally {
			try {
				if (caInput != null)
					caInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyStore;
	}

	public static KeyStore getKeystore()
	{

		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return trustStore;
	}

	public static org.apache.http.conn.ssl.SSLSocketFactory getFixedSocketFactory()
	{

		org.apache.http.conn.ssl.SSLSocketFactory socketFactory;
		try
		{
			socketFactory = new MySSLSocketFactory(getKeystore());
			socketFactory.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Throwable t) {
			t.printStackTrace();
			socketFactory = org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory();
		}
		return socketFactory;
	}

	public static DefaultHttpClient getNewHttpClient(KeyStore keyStore)
	{

		try
		{
			org.apache.http.conn.ssl.SSLSocketFactory sf = new MySSLSocketFactory(keyStore);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "UTF-8");

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
		}
		return new DefaultHttpClient();
	}
}
