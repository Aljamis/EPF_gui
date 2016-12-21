package org.avr.epfapi.exceptions;

/**
 * Unable to login to the EPF website
 * @author axviareque
 *
 */
public class EPFloginException extends Exception {
	private static final long serialVersionUID = 8445777853354889426L;
	
	
	public EPFloginException() { super(); }
	public EPFloginException(String msg) {
		super(msg);
	}
}
