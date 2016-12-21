package org.avr.epfapi.json;

public abstract class CommonResponse extends CommonRequest {
	
	private String response;
	private String messages;
	
	
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
}
