package org.avr.epfapi;


/**
 * Represents the individual products available from EPF website. 
 * @author axviareque
 *
 */
public class EPFProductCode {
	
	private String code;
	private String id;
	private String name;
	
	
	public EPFProductCode(String n, String c , String i) {
		this.name = n;
		this.code = c;
		this.id = i;
	}
	
	
	public String toString() {
		return name;
	}
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
