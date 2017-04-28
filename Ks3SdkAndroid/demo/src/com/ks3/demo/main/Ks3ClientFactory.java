package com.ks3.demo.main;

import android.content.Context;

import com.ksyun.ks3.services.Ks3Client;
import com.ksyun.ks3.services.Ks3ClientConfiguration;

public class Ks3ClientFactory {
	private static Ks3Client ks3Client;
	private static Ks3ClientConfiguration configuration;

	private Ks3ClientFactory() {

	};

	public static Ks3Client getDefaultClient(Context context) {
		if (ks3Client == null) {
			configuration = Ks3ClientConfiguration.getDefaultConfiguration();
			ks3Client = new Ks3Client(Constants.ACCESS_KEY__ID,
					Constants.ACCESS_KEY_SECRET,context);
			ks3Client.setConfiguration(configuration);
			ks3Client.setEndpoint(Constants.END_POINT);
		}
		return ks3Client;
	}

}
