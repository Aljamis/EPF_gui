package org.avr.epfapi.json;

public class Product {
	
	private String fileid = "";
	private String status = "";
	private String filepath = "";
	private String fulfilled = "";
	
	
	
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getFulfilled() {
		return fulfilled;
	}
	public void setFulfilled(String fulfilled) {
		this.fulfilled = fulfilled;
	}
}
