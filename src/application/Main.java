package application;
	
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	private Stage primaryStage;
	private AnchorPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		try {
//			BorderPane root = new BorderPane();
//			Scene scene = new Scene(root,400,400);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
			
			this.primaryStage = primaryStage;
//			this.primaryStage.setTitle("USPS EPF Website Access");
			
			FXMLLoader loader = new FXMLLoader();
			
			/*  Load Resources */
//			Locale loc = Locale.FRENCH;
//			Locale loc = new Locale("es", "ES");
			Locale loc = Locale.getDefault();
//			Locale loc = new Locale("xx", "XX");
			loader.setResources(ResourceBundle.getBundle("resources.EPF_gui" , loc) );
			
			
			/* Window Title getting version from package */
			String title = loader.getResources().getString("Screen.Title") 
					+" - v "
					+ EPF_guiVersion.getVersion()
					+" ("+ loc.getDisplayLanguage() +")"
					;
			this.primaryStage.setTitle( title );
			
			loader.setLocation( Main.class.getResource("/EPF.fxml") );
			rootLayout = (AnchorPane)loader.load();
			
			Scene scene = new Scene(rootLayout);
			this.primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
