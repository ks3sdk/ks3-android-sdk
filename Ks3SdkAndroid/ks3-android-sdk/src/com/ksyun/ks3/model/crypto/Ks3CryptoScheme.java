package com.ksyun.ks3.model.crypto;

import java.security.SecureRandom;

import com.ksyun.ks3.model.crypto.algorithm.ContentCryptoScheme;

public class Ks3CryptoScheme {
	static final String AES = "AES";
    static final String RSA = "RSA";
    private static final SecureRandom srand = new SecureRandom();
    private final Ks3KeyWrapScheme kwScheme;
    private final ContentCryptoScheme contentCryptoScheme;
    
    Ks3CryptoScheme(ContentCryptoScheme contentCryptoScheme) {
        this.contentCryptoScheme = contentCryptoScheme;
        this.kwScheme = new Ks3KeyWrapScheme();
    }
    
    private Ks3CryptoScheme(ContentCryptoScheme contentCryptoScheme,
    		Ks3KeyWrapScheme kwScheme) {
        this.contentCryptoScheme = contentCryptoScheme;
        this.kwScheme = kwScheme;
    }
    
    SecureRandom getSecureRandom() {
        return srand;
    }

    ContentCryptoScheme getContentCryptoScheme() {
        return contentCryptoScheme;
    }

    Ks3KeyWrapScheme getKeyWrapScheme() {
        return kwScheme;
    }

    /**
     * Convenient method.
     */
    public static boolean isAesGcm(String cipherAlgorithm) {
        return ContentCryptoScheme.AES_GCM.getCipherAlgorithm().equals(cipherAlgorithm);
    }

    static Ks3CryptoScheme from(CryptoMode mode) {
        switch (mode) {
            case EncryptionOnly:
                return new Ks3CryptoScheme(ContentCryptoScheme.AES_CBC,
                        Ks3KeyWrapScheme.NONE);
            case AuthenticatedEncryption:
            case StrictAuthenticatedEncryption:
                return new Ks3CryptoScheme(ContentCryptoScheme.AES_GCM,
                        new Ks3KeyWrapScheme());
            default:
                throw new IllegalStateException();
        }
    }
}
