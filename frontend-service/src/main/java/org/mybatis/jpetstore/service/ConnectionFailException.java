package org.mybatis.jpetstore.service;

public class ConnectionFailException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4173930512372209712L;
	

	public ConnectionFailException(String message, Exception e) {
		super(message, e);
	}
	
	public ConnectionFailException(Exception e) {
		super(e);
	}

}
