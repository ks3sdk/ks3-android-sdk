package com.ksyun.loopj.android.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class PersistentCookieStore
		implements CookieStore
{

	private static final String LOG_TAG = "PersistentCookieStore";
	private static final String COOKIE_PREFS = "CookiePrefsFile";
	private static final String COOKIE_NAME_STORE = "names";
	private static final String COOKIE_NAME_PREFIX = "cookie_";
	private boolean omitNonPersistentCookies = false;
	private final ConcurrentHashMap<String, Cookie> cookies;
	private final SharedPreferences cookiePrefs;

	public PersistentCookieStore(Context context)
	{

		this.cookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);
		this.cookies = new ConcurrentHashMap();

		String storedCookieNames = this.cookiePrefs.getString("names", null);
		if (storedCookieNames != null) {
			String[] cookieNames = TextUtils.split(storedCookieNames, ",");
			for (String name : cookieNames) {
				String encodedCookie = this.cookiePrefs.getString(new StringBuilder().append("cookie_").append(name).toString(), null);
				if (encodedCookie != null) {
					Cookie decodedCookie = decodeCookie(encodedCookie);
					if (decodedCookie != null) {
						this.cookies.put(name, decodedCookie);
					}
				}

			}

			clearExpired(new Date());
		}
	}

	@Override
	public void addCookie(Cookie cookie)
	{

		if ((this.omitNonPersistentCookies) && (!cookie.isPersistent()))
			return;
		String name = new StringBuilder().append(cookie.getName()).append(cookie.getDomain()).toString();

		if (!cookie.isExpired(new Date()))
			this.cookies.put(name, cookie);
		else {
			this.cookies.remove(name);
		}

		SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
		prefsWriter.putString("names", TextUtils.join(",", this.cookies.keySet()));
		prefsWriter.putString(new StringBuilder().append("cookie_").append(name).toString(), encodeCookie(new SerializableCookie(cookie)));
		prefsWriter.commit();
	}

	@Override
	public void clear()
	{

		SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
		for (String name : this.cookies.keySet()) {
			prefsWriter.remove(new StringBuilder().append("cookie_").append(name).toString());
		}
		prefsWriter.remove("names");
		prefsWriter.commit();

		this.cookies.clear();
	}

	@Override
	public boolean clearExpired(Date date)
	{

		boolean clearedAny = false;
		SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();

		for (ConcurrentHashMap.Entry<String, Cookie> entry : cookies.entrySet()) {
			String name = entry.getKey();
			Cookie cookie = entry.getValue();
			if (cookie.isExpired(date))
			{
				this.cookies.remove(name);

				prefsWriter.remove(new StringBuilder().append("cookie_").append(name).toString());

				clearedAny = true;
			}

		}

		if (clearedAny) {
			prefsWriter.putString("names", TextUtils.join(",", this.cookies.keySet()));
		}
		prefsWriter.commit();

		return clearedAny;
	}

	@Override
	public List<Cookie> getCookies()
	{

		return new ArrayList(this.cookies.values());
	}

	public void setOmitNonPersistentCookies(boolean omitNonPersistentCookies)
	{

		this.omitNonPersistentCookies = omitNonPersistentCookies;
	}

	public void deleteCookie(Cookie cookie)
	{

		String name = new StringBuilder().append(cookie.getName()).append(cookie.getDomain()).toString();
		this.cookies.remove(name);
		SharedPreferences.Editor prefsWriter = this.cookiePrefs.edit();
		prefsWriter.remove(new StringBuilder().append("cookie_").append(name).toString());
		prefsWriter.commit();
	}

	protected String encodeCookie(SerializableCookie cookie)
	{

		if (cookie == null)
			return null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(os);
			outputStream.writeObject(cookie);
		} catch (IOException e) {
			Log.d("PersistentCookieStore", "IOException in encodeCookie", e);
			return null;
		}

		return byteArrayToHexString(os.toByteArray());
	}

	protected Cookie decodeCookie(String cookieString)
	{

		byte[] bytes = hexStringToByteArray(cookieString);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		Cookie cookie = null;
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			cookie = ((SerializableCookie) objectInputStream.readObject()).getCookie();
		} catch (IOException e) {
			Log.d("PersistentCookieStore", "IOException in decodeCookie", e);
		} catch (ClassNotFoundException e) {
			Log.d("PersistentCookieStore", "ClassNotFoundException in decodeCookie", e);
		}

		return cookie;
	}

	protected String byteArrayToHexString(byte[] bytes)
	{

		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte element : bytes) {
			int v = element & 0xFF;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase(Locale.US);
	}

	protected byte[] hexStringToByteArray(String hexString)
	{

		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[(i / 2)] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}
}
