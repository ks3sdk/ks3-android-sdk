package com.ksyun.ks3.exception;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;

import com.ksyun.ks3.util.Constants;

public class Ks3ServiceException extends Ks3ClientException {
	private static final long serialVersionUID = 5225806336951827450L;
	private String errorCode;
	private int statueCode;
	private String expectedStatueCode;
	private String errorMessage;
	/* Client request resource */
	private String resource;
	private String requestId;

	public Ks3ServiceException() {
		super("");
	}

	public Ks3ServiceException(HttpResponse response, String expected) {
		super("");
		this.expectedStatueCode = expected;
		this.statueCode = response.getStatusLine().getStatusCode();
		/*try {
			Document document = new XmlReader(response.getEntity().getContent())
					.getDocument();
			try {
				errorMessage = document.getElementsByTagName("Message").item(0)
						.getTextContent();
			} catch (Exception e) {
				this.errorMessage = "unknow";
			}
			try {
				errorCode = document.getElementsByTagName("Code").item(0)
						.getTextContent();
			} catch (Exception e) {
				this.errorCode = "unknow";
			}
			try {
				resource = document.getElementsByTagName("Resource").item(0)
						.getTextContent();
			} catch (Exception e) {
				this.resource = "unknow";
			}
			try {
				requestId = document.getElementsByTagName("RequestId").item(0)
						.getTextContent();
			} catch (Exception e) {
				this.requestId = "unknow";
			}
		} catch (Exception e) {
		} finally {
			try {
				if (response.getEntity().getContent() != null)
					response.getEntity().getContent().close();
			} catch (Exception e) {
			}
		}*/
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ":" + "[RequestId:" + this.requestId
				+ ",Resource:" + resource + ",Statue code:" + this.statueCode
				+ ",Expected statue code:" + this.expectedStatueCode
				+ ",Error code:" + this.errorCode + ",Error message:"
				+ this.errorMessage + "]";
	}

	public String getErrorCode() {
		return errorCode;
	}

	public int getStatueCode() {
		return statueCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getResource() {
		return this.resource;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getExpectedStatueCode() {
		return expectedStatueCode;
	}

	public void setExpectedStatueCode(String expectedStatueCode) {
		this.expectedStatueCode = expectedStatueCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setStatueCode(int statueCode) {
		this.statueCode = statueCode;
	}

	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	// 将当前异常转化为com.ksyun.ks3.exception.serviceside.*下的异常
	public <X extends Ks3ServiceException> RuntimeException convert() {
		String classString = Constants.KS3_PACAKAGE + ".exception.serviceside."
				+ this.getErrorCode() + "Exception";
		try {
			@SuppressWarnings("unchecked")
			X e = (X) Class.forName(classString).newInstance();
			e.setErrorMessage(this.getErrorMessage());
			e.setErrorCode(this.getErrorCode());
			e.setExpectedStatueCode(this.getExpectedStatueCode());
			e.setRequestId(this.getRequestId());
			e.setResource(this.getResource());
			e.setStatueCode(this.getStatueCode());
			e.setStackTrace(this.getStackTrace());
			return e;
		} catch (Exception e) {
			return this;
		}
	}
}
