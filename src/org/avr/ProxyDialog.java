package org.avr;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.Optional;

import org.avr.epfapi.EPF;
import org.avr.epfapi.EPF_impl;
import org.avr.epfapi.exceptions.EPFexception;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

public class ProxyDialog extends Dialog<String> {
	Optional<String> result = null;
	private String version = null;
	
	private TextField proxyHost = new TextField();
	private TextField proxyPort = new TextField();
	private TextField username = new TextField();
	private PasswordField password = new PasswordField();
	
	private Label errMessage = new Label();
	
	private EPF epf = new EPF_impl(); 
	
	public ProxyDialog() {
		this.setTitle("Proxy Setup");
		ButtonType btnOK = new ButtonType("Okay", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll( ButtonType.CANCEL , btnOK );
		this.getDialogPane().setMaxWidth(300);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(2);
		grid.setPadding( new Insets(10, 10, 10, 10) );
		
		username.setPromptText("username");
		password.setPromptText("password");
		
		grid.add(new Label("Proxy Host : "), 0, 0);
		proxyHost.setText("proxy.aetna.com");
		grid.add(proxyHost, 1, 0);
		grid.add(new Label("Proxy Port : "), 0, 1);
		proxyPort.setText("9119");
		grid.add(proxyPort, 1, 1);
		grid.add(new Label("Username : "), 0, 2);
		grid.add(username, 1, 2);
		grid.add(new Label("Password : "), 0, 3);
		grid.add(password, 1, 3);
		
		
		errMessage.setWrapText(true);
		errMessage.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(errMessage, Priority.ALWAYS);
		
		grid.add( errMessage, 0, 4, 2, 1);
		this.getDialogPane().setContent( grid );
		
		Button btn = (Button)getDialogPane().lookupButton( btnOK );
		btn.addEventFilter( ActionEvent.ACTION, ae -> {
			if (!isValid()) {
				ae.consume();
			}
		});
		
		this.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType param) {
				if (param == btnOK) {
					return version;
				}
				return null;
			}
		});
	}
	
	
	
	
	/**
	 * Make sure fields have been filled in
	 * @return
	 */
	private boolean isValid() {
		StringBuffer missingFields = new StringBuffer();
		if (proxyHost.getText() == null  ||  proxyHost.getText().trim().length() == 0) 
			missingFields.append("- Missing Proxy Host\n");
		
		if (proxyPort.getText() == null  ||  proxyPort.getText().trim().length() == 0) 
			missingFields.append("- Missing Proxy Port\n");
		
		try {
			Integer.parseInt( proxyPort.getText() );
		} catch (NumberFormatException nfEx) {
			missingFields.append("- Proxy Port not a number\n");
		}
		
		if (username.getText() == null  ||  username.getText().trim().length() == 0)
			missingFields.append("- Missing User name\n");
		
		if (password.getText() == null  ||  password.getText().trim().length() == 0)
			missingFields.append("- Missing Password\n");
		
		if (missingFields.length() > 0) {
			missingFields.insert(0, "Fix these errors\n");
			errMessage.setText( missingFields.toString() );
			errMessage.setTextFill(Color.RED);
			return false;
		}
		
		epf.setProxyUser(username.getText());
		epf.setProxyPwd( password.getCharacters() );
		epf.setProxy( proxyHost.getText() , Integer.parseInt( proxyPort.getText() ) );
		try {
			this.version = epf.getVersion();
		} catch (EPFexception epfEx) {
			epfEx.printStackTrace();
			errMessage.setText( epfEx.getMessage() );
			errMessage.setTextFill(Color.RED);
			return false;
		}
		return true;
	}
}