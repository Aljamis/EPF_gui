package org.avr.epfapi.exceptions;

public class SecurityTokenException extends EPFexception {
	private static final long serialVersionUID = 1120705637983727259L;
	
	public SecurityTokenException() { }
	
	public SecurityTokenException(String msg) {
		super(msg);
	}
}
