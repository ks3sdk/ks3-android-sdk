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
			Map<String, String> materialDescIn) {
//		 final Map<String, String> materialDesc =
//				 encryptionMaterials.getMaterialsDescription();
//	        if (materialDescIn != null
//	                && materialDescIn.equals(materialDesc)) {
//	            return encryptionMaterials; // matching description
//	        }
//	        EncryptionMaterialsAccessor accessor = encryptionMaterials.getAccessor();
//	        if (accessor != null) {
//	            EncryptionMaterials accessorMaterials =
//	                    accessor.getEncryptionMaterials(materialDescIn);
//	            if (accessorMaterials != null)
//	                return accessorMaterials; // accessor decided materials
//	        }
	        // The condition that there are
	        // 1) no input materials description (typically from S3); and
	        // 2) no materials description for the current client-side materials;
	        // and
	        // 3) the client's material accessor has no corresponding materials
	        // for null or empty materials description;
	        // implies that the only sensible materials is
	        // the current client-side materials (which has no description).
//	        boolean noMaterialDescIn = materialDescIn == null || materialDescIn.size() == 0;
//	        boolean noMaterialDesc = materialDesc == null || materialDesc.size() == 0;
//	        return noMaterialDescIn && noMaterialDesc ? encryptionMaterials : null;
		return encryptionMaterials;
	}

}
