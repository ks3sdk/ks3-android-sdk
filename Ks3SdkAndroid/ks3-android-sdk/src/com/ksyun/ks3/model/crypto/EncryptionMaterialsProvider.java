package com.ksyun.ks3.model.crypto;

public interface EncryptionMaterialsProvider {
    public EncryptionMaterials getEncryptionMaterials();
    public void refresh();
}
