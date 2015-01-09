package com.ksyun.ks3.exception;

public class Ks3ClientException extends Exception{
 
	private static final long serialVersionUID = -2503345001841814995L;
	
	public Ks3ClientException(String message, Throwable t) {
        super(message, t);
    }
    public Ks3ClientException(String message) {
        super(message);
    }

    public Ks3ClientException(Throwable t) {
        super(t);
    }
}
