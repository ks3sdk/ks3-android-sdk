package com.ksyun.ks3.model.crypto;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class EncryptionMaterials {
	private final KeyPair keyPair;
	private final SecretKey symmetricKey;

	public EncryptionMaterials(KeyPair keyPair) {
		this(keyPair, null);
	}

	public EncryptionMaterials(SecretKey symmetricKey) {
		this(null, symmetricKey);
	}

	private EncryptionMaterials(KeyPair keyPair, SecretKey symmetricKey) {
		this.keyPair = keyPair;
		this.symmetricKey = symmetricKey;
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public SecretKey getSymmetricKey() {
		return this.symmetricKey;
	}

	public Map<String, String> getMaterialsDescription() {
		return new HashMap<String, String>();
	}
}
