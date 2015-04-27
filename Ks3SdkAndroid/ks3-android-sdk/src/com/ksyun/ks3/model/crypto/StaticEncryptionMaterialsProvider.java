package com.ksyun.ks3.model.crypto;


public class StaticEncryptionMaterialsProvider implements
		EncryptionMaterialsProvider {

	private final EncryptionMaterials encryptionMaterials;

	public StaticEncryptionMaterialsProvider(
			EncryptionMaterials encryptionMaterials) {
		this.encryptionMaterials = encryptionMaterials;
	}

	@Override
	public EncryptionMaterials getEncryptionMaterials() {
		return encryptionMaterials;
	}

	@Override
	public void refresh() {
		
	}

}
