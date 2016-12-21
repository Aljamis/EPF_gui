package org.avr.epfapi;

import java.util.List;

import org.avr.epfapi.exceptions.EPFexception;
import org.avr.epfapi.exceptions.EPFloginException;
import org.avr.epfapi.json.ListReq;
import org.avr.epfapi.json.Product;

public interface EPF {
	
	
	public String getVersion() throws EPFexception;
	public void login(String user, CharSequence pwd) throws EPFloginException;
	public void logout() throws EPFloginException;
	public List<Product> listFiles(ListReq product) throws EPFexception;
	public void changeStatus(Product prod , String newStatus) throws EPFexception;
	public void requestFile_POST(String epfFileID) throws EPFexception;
	public void requestFile_GET(String epfFileID) throws EPFexception;
	
	public void setProxy(String host , int port);
	public void setProxyUser(String usr);
	public void setProxyPwd(CharSequence pwd);
	
}
