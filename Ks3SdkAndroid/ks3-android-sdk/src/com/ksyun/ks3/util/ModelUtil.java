package com.ksyun.ks3.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

import com.ksyun.loopj.android.http.RequestParams;

public class ModelUtil {

	public static Header[] convertHeaderArray(Map<String, String> sourceMap) {
		Iterator<Entry<String, String>> iterator = sourceMap.entrySet()
				.iterator();
		Header[] headers = new Header[sourceMap.size()];
		int num = 0;
		while (iterator.hasNext()) {
			final Map.Entry<String, String> entry = iterator.next();
			Header header = new Header() {
				@Override
				public String getValue() {
					return entry.getValue();
				}

				@Override
				public String getName() {
					return entry.getKey();
				}

				@Override
				public HeaderElement[] getElements() throws ParseException {
					return null;
				}
			};
			headers[num] = header;
			num++;
		}
		return headers;
	}

	public static RequestParams convertRequsetParams(
			Map<String, String> sourceMap) {
		return new RequestParams(sourceMap);
	}
}
