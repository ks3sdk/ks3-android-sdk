package com.ksyun.ks3.model.result;

import com.ksyun.ks3.model.Ks3Object;
import com.ksyun.ks3.util.StringUtils;

public class GetObjectResult {
	private Ks3Object object = new Ks3Object();
	private boolean ifModified = true;
	private boolean ifPreconditionSuccess = true;
	public String toString()
	{
		return StringUtils.object2string(this);
	}
	public Ks3Object getObject() {
		return object;
	}
	public void setObject(Ks3Object object) {
		this.object = object;
	}
	public boolean isIfModified() {
		return ifModified;
	}
	public void setIfModified(boolean ifModified) {
		this.ifModified = ifModified;
	}
	public boolean isIfPreconditionSuccess() {
		return ifPreconditionSuccess;
	}
	public void setIfPreconditionSuccess(boolean ifPreconditionSuccess) {
		this.ifPreconditionSuccess = ifPreconditionSuccess;
	}
	
}
