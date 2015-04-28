package com.ksyun.ks3.model.crypto;

import java.util.Map;


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

	@Override
	public EncryptionMaterials getEncryptionMaterials(
			Map<String, String> materialsDescription) {
		// TODO Auto-generated method stub
		return null;
	}

}
