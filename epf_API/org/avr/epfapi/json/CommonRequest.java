package org.avr.epfapi.json;

public abstract class CommonRequest {
	
	private String logonKey;
	private String logonToken;
	
	
	
	public String getLogonKey() {
		return logonKey;
	}
	public void setLogonKey(String logonKey) {
		this.logonKey = logonKey;
	}
	public String getLogonToken() {
		return logonToken;
	}
	public void setLogonToken(String logonToken) {
		this.logonToken = logonToken;
	}
}
