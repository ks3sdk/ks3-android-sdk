package com.ksyun.ks3.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import android.net.ParseException;

import com.ksyun.ks3.exception.Ks3ClientException;

public class DateUtil {
	// date to string
	public static String ConverToString(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);

		return df.format(date);
	}

	// string to date
	public static Date ConverToDate(String strDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.UK);
		sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
		if (strDate.contains("-") && strDate.contains("T")) {
			if (strDate.endsWith("Z")) {
				strDate = strDate.replace("Z", " GMT");
				sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z", Locale.UK);
			} else if(strDate.endsWith("+08:00")){
				strDate = strDate.replace("+08:00", " GMT");
				sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z", Locale.UK);
			}
		}
		try {
			return sdf.parse(strDate);
		} catch (ParseException e) {
			throw new Ks3ClientException(
						"The server did not return the expected value,it is "
								+ strDate, e);
		}

	
		
		
	}
	public static String GetUTCTime() {
		SimpleDateFormat df = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
		
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return df.format(cal.getTime());
	}
}
