package application;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.avr.ErrorDialog;
import org.avr.ProxyDialog;
import org.avr.epfapi.EPF;
import org.avr.epfapi.EPFProductCode;
import org.avr.epfapi.EPF_impl;
import org.avr.epfapi.exceptions.EPFexception;
import org.avr.epfapi.exceptions.EPFloginException;
import org.avr.epfapi.json.ListReq;
import org.avr.epfapi.json.Product;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;



/**
 * Controller for EPF front end.
 *  - handle...(ActionEvent event) :  Any actions from the screen.
 * @author axviareque
 *
 */
public class EPFcontroller implements Initializable {
	
	EPF epf = new EPF_impl();
	
	@FXML private TextField txtUser;
	@FXML private PasswordField txtPwd;
	@FXML private Button btnLogin;
	@FXML private Button btnLogout;
	@FXML private Label epfVersion = new Label();
	
	@FXML private CheckBox chkbxNew;
	@FXML private CheckBox chkbxStarted;
	@FXML private CheckBox chkbxCompleted;
	@FXML private ComboBox<EPFProductCode> cmbbxProductCodes;
	
	@FXML private TableView<Product> tblProducts;
	@FXML private TableColumn<Product, String> fileIdColumn;
	@FXML private TableColumn<Product, String> fileStatusColumn;
	@FXML private TableColumn<Product, String> filePathColumn;
	
	@FXML private MenuItem changeStatusNew;
	@FXML private MenuItem changeStatusCompleted;
	
	@FXML private Button btnUpdate;
	
	
	
	@Override
	public void initialize(URL location , ResourceBundle res) {
		fileIdColumn.setCellValueFactory( new PropertyValueFactory<Product , String>("fileid"));
		fileStatusColumn.setCellValueFactory( new PropertyValueFactory<Product , String>("status"));
		filePathColumn.setCellValueFactory( new PropertyValueFactory<Product , String>("filepath"));
	}
	
	
	@FXML protected void handleAppClose(ActionEvent event ) {
		Platform.exit();
	}
	
	@FXML protected void handleProxySetup(ActionEvent event ) {
		ProxyDialog proxy = new ProxyDialog();
		Optional<String> v = proxy.showAndWait();
		
		v.ifPresent( version -> {
			epfVersion.setText( version );
			epfVersion.setVisible(true);
			return;
		});
	}
	
	
	@FXML protected void handleLoginButtonAction(ActionEvent event) {
		try {
			epf.login(txtUser.getText(), txtPwd.getCharacters());
			
			buildProductCodes();
			displayFields(true);
		} catch (EPFloginException loginEx) {
			loginEx.printStackTrace();
			ErrorDialog err = new ErrorDialog(AlertType.ERROR, loginEx );
			err.showAndWait();
		}
	}

	@FXML protected void handleLogoutButtonAction(ActionEvent event) {
		try {
			epf.logout();
			displayFields(false);
			clearFields();
		}catch (EPFloginException loginEx) {
			loginEx.printStackTrace();
			/* An error here is not a big deal */
		}
	}
	
	@FXML protected void handleUpdateButtonAction(ActionEvent event) {
		EPFProductCode prod = cmbbxProductCodes.getValue();
		
		StringBuffer status = new StringBuffer();
		if (chkbxCompleted.isSelected())
			status.append("C");
		if (chkbxNew.isSelected())
			status.append("N");
		if (chkbxStarted.isSelected())
			status.append("S");
		
		ListReq req = new ListReq();
		req.setProductcode( prod.getCode() );
		req.setProductid( prod.getId() );
		req.setStatus( status.toString() );
		
		try {
			ObservableList<Product> data = FXCollections.observableArrayList( epf.listFiles(req));
			tblProducts.getItems().setAll(data);
		} catch (EPFexception epfEx) {
			epfEx.printStackTrace();
			ErrorDialog err = new ErrorDialog(AlertType.ERROR, epfEx );
			err.showAndWait();
		}
	}
	
	@FXML protected void handleChangeStatusNew(ActionEvent event) {
		Product prod = tblProducts.getSelectionModel().getSelectedItem();
		try {
			epf.changeStatus(prod, "N");
		} catch (EPFexception e) {
			e.printStackTrace();
		}
		/* reset the screen with changes */
		handleUpdateButtonAction(event);
	}
	
	@FXML protected void handleChangeStatusCompleted(ActionEvent event) {
		Product prod = tblProducts.getSelectionModel().getSelectedItem();
		try {
			epf.changeStatus(prod, "C");
		} catch (EPFexception e) {
			e.printStackTrace();
		}
		/* reset the screen with changes */
		handleUpdateButtonAction(event);
	}

		
	
	
	/**
	 * Display or hide the EPF specific fields.
	 * @param dispVal
	 */
	private void displayFields(boolean dispVal) {
		btnLogin.setVisible(!dispVal);
		btnLogout.setVisible(dispVal);
		chkbxCompleted.setVisible(dispVal);
		chkbxNew.setVisible(dispVal);
		chkbxStarted.setVisible(dispVal);
		cmbbxProductCodes.setVisible(dispVal);
		btnUpdate.setVisible(dispVal);
		tblProducts.setVisible(dispVal);
	}
	
	
	
	
	private void clearFields() {
		tblProducts.getItems().clear();
		chkbxCompleted.setSelected(false);
		chkbxNew.setSelected(false);
		chkbxStarted.setSelected(false);
	}
	
	
	
	
	/**
	 * Populate ComboBox with distinct EPF Products and codes
	 */
	private void buildProductCodes() {
		ObservableList<EPFProductCode> products = FXCollections.observableArrayList();
		products.add( new EPFProductCode("NCOA Weekly file", "NCAW", "NCL18H"));
		products.add( new EPFProductCode("Daily Delete file", "NCAD", "TEXTFILE"));
		products.add( new EPFProductCode("NCOA Weekly file 256", "NCAW1", "NCL18H"));
		products.add( new EPFProductCode("Daily Delete file 256", "NCADX", "TEXTFILE"));
		
		cmbbxProductCodes.setItems(products);
	}

}
