package com.ksyun.ks3.services.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ksyun.ks3.services.crypto.MaterialsDescriptionProvider;

public class EncryptedInitiateMultipartUploadRequest extends
		InitiateMultipartUploadRequest implements MaterialsDescriptionProvider {

	private static final long serialVersionUID = -8206951471518456797L;
	private Map<String, String> materialsDescription;

	public EncryptedInitiateMultipartUploadRequest(String bucketName, String key) {
		super(bucketName, key);
	}

	@Override
	public Map<String, String> getMaterialsDescription() {
		return materialsDescription;
	}

	  /**
     * sets the materials description for the encryption materials to be used
     * with the current Multi Part Upload Request.
     *
     * @param materialsDescription the materialsDescription to set
     */
    public void setMaterialsDescription(Map<String, String> materialsDescription) {
        this.materialsDescription = materialsDescription == null
                ? null
                : Collections.unmodifiableMap(new HashMap<String, String>(materialsDescription));
    }

    /**
     * sets the materials description for the encryption materials to be used
     * with the current Multi Part Upload Request.
     *
     * @param materialsDescription the materialsDescription to set
     */
    public EncryptedInitiateMultipartUploadRequest withMaterialsDescription(
            Map<String, String> materialsDescription) {
        setMaterialsDescription(materialsDescription);
        return this;
    }
}
