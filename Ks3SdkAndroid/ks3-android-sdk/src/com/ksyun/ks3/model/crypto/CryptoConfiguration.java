package com.ksyun.ks3.model.crypto;

import java.security.Provider;

public class CryptoConfiguration {
	private CryptoMode cryptoMode;
	private Provider cryptoProvider;

	public CryptoConfiguration() {
		this(CryptoMode.EncryptionOnly); // default to Encryption Only (EO) for
											// backward compatibility
	}

	public CryptoConfiguration(CryptoMode cryptoMode) {
		// By default, store encryption info in metadata
		// A null value implies that the default JCE crypto provider will be
		// used
		this.cryptoProvider = null;
		this.cryptoMode = cryptoMode;
	}

	public void setCryptoProvider(Provider cryptoProvider) {
		this.cryptoProvider = cryptoProvider;
	}

	public CryptoConfiguration withCryptoProvider(Provider cryptoProvider) {
		this.cryptoProvider = cryptoProvider;
		return this;
	}

	public Provider getCryptoProvider() {
		return this.cryptoProvider;
	}

	public CryptoMode getCryptoMode() {
		return cryptoMode;
	}

	public void setCryptoMode(CryptoMode cryptoMode) {
		this.cryptoMode = cryptoMode;
	}

	public CryptoConfiguration withCryptoMode(CryptoMode cryptoMode) {
		this.cryptoMode = cryptoMode;
		return this;
	}

}
