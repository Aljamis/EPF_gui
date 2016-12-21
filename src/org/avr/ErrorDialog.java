package org.avr;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


/**
 * Display an Alert Dialog with the Exception that was thrown.
 * @author axviareque
 *
 */
public class ErrorDialog extends Alert {

	public ErrorDialog(AlertType alertType , Exception ex) {
		super(alertType);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		
		String exceptionText = sw.toString();
		
		Label lbl = new Label("Exception Stack Trace");
		this.setContentText( ex.getMessage() );
		
		TextArea txtArea = new TextArea(exceptionText);
		txtArea.setEditable(false);
		txtArea.setWrapText(true);
		txtArea.setMaxWidth(Double.MAX_VALUE);
		txtArea.setMaxHeight(Double.MAX_VALUE);
		
		GridPane.setVgrow(txtArea, Priority.ALWAYS);
		GridPane.setHgrow(txtArea, Priority.ALWAYS);
		
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(lbl , 0,0);
		expContent.add(txtArea , 0,1);
		
		this.getDialogPane().setExpandableContent(expContent);
	}

}
