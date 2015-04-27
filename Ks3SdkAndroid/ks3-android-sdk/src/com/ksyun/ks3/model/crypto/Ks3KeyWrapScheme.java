package com.ksyun.ks3.model.crypto;

import java.security.Key;

import com.ksyun.ks3.model.crypto.algorithm.CryptoRuntime;

public class Ks3KeyWrapScheme  {
	static final Ks3KeyWrapScheme NONE = new Ks3KeyWrapScheme() {
        @Override
        String getKeyWrapAlgorithm(Key key) {
            return null;
        }

        @Override
        public String toString() {
            return "NONE";
        }
    };
    public static final String AESWrap = "AESWrap";
    public static final String RSA_ECB_OAEPWithSHA256AndMGF1Padding = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    String getKeyWrapAlgorithm(Key key) {
        String algorithm = key.getAlgorithm();
        if (Ks3CryptoScheme.AES.equals(algorithm)) {
            return AESWrap;
        }
        if (Ks3CryptoScheme.RSA.equals(algorithm)) {
            if (CryptoRuntime.isRsaKeyWrapAvailable())
                return RSA_ECB_OAEPWithSHA256AndMGF1Padding;
        }
        return null;
    }
}
