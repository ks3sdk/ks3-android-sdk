package com.ksyun.ks3.model.result;
import java.util.Date;
import com.ksyun.ks3.util.StringUtils;

public class CopyResult {
	/**
	 * last modified date
	 */
	private Date lastModified;
	private String ETag;
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public String getETag() {
		return ETag;
	}
	public void setETag(String eTag) {
		ETag = eTag;
	}
	public String toString(){
		return StringUtils.object2string(this);
	}
}
