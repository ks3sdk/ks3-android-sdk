package com.ksyun.ks3.services;

import com.ksyun.ks3.util.StringUtils;

public class AuthResult {

	private String mAuthStr;
	private String mDateStr;

	public AuthResult(String auth, String date) {

		this.mAuthStr = auth;
		this.mDateStr = date;
	}

	public String getAuthStr() {

		return mAuthStr;
	}

	public void setAuthStr(String mAuthStr) {

		this.mAuthStr = mAuthStr;
	}

	public String getDateStr() {

		return mDateStr;
	}

	public void setDateStr(String mDateStr) {

		this.mDateStr = mDateStr;
	}

	public boolean validateAuth() {

		if (StringUtils.isBlank(mAuthStr))
			return false;

		return true;

	}

	public boolean validateDate() {

		return true;
	}

}
