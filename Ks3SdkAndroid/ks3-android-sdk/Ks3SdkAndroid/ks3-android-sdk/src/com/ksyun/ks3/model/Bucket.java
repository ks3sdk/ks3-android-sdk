package com.ksyun.ks3.model;

import java.util.Date;

public class Bucket {
	private String name = null;
	private Owner owner = null;
	private Date creationDate = null;
	private static final long serialVersionUID = -8646831898339939580L;

	public Bucket() {
	}

	public Bucket(String name) {
		this.name = name;
	}

	public String toString() {
		return "S3Bucket [name=" + getName() + ", creationDate="
				+ getCreationDate() + ", owner=" + getOwner() + "]";
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
