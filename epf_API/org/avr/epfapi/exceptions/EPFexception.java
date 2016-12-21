package org.avr.epfapi.exceptions;



/**
 * A Generic EPF Failure
 * @author axviareque
 *
 */
public class EPFexception extends Exception {
	private static final long serialVersionUID = -2159220577117541141L;
	private Exception originalException;
	
	
	public EPFexception() { super(); }
	
	public EPFexception(String message) {
		super(message);
	}
	
	public EPFexception(Exception exc , String message) {
		super(message);
		this.originalException = exc;
	}
	
	
	public Exception getOriginalException() {
		if ( this.originalException == null )
			return this;
		return this.originalException;
	}
	public void setOriginalException(Exception ex) {
		this.originalException = ex;
	}
}
