package org.avr.epfapi.json;

import java.util.List;

public class ListResp extends CommonResponse {
	
	private int reccount;
	private List<Product> filelist;
	
	
	
	public int getReccount() {
		return reccount;
	}
	public void setReccount(int reccount) {
		this.reccount = reccount;
	}
	public List<Product> getFilelist() {
		return filelist;
	}
	public void setFilelist(List<Product> filelist) {
		this.filelist = filelist;
	}
}
