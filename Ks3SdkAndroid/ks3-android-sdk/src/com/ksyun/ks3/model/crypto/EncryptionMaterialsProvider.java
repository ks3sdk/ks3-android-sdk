package com.ksyun.ks3.model.crypto;

public interface EncryptionMaterialsProvider extends EncryptionMaterialsAccessor{
    public EncryptionMaterials getEncryptionMaterials();
    public void refresh();
}
