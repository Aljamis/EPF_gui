package org.avr.epfapi.json;

public class ListReq extends CommonRequest {
	
	private String productcode;
	private String productid;
	private String status;
	private String fulfilled;
	
	
	
	public String getProductcode() {
		return productcode;
	}
	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}
	public String getProductid() {
		return productid;
	}
	public void setProductid(String productid) {
		this.productid = productid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFulfilled() {
		return fulfilled;
	}
	public void setFulfilled(String fulfilled) {
		this.fulfilled = fulfilled;
	}
}
