/**
 * FBLA 2016-17 Coding and Programming Submission
 * 
 * 
 */

package me.ausaf.fbla.app;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

/**
 * This class is the entry point of the application. 
 * The main method is invoked, and launches the JavaFX
 * Application.start() method.  
 */
public class Main extends Application {
	
	private static Stage stage;
	
	/*
	 * The start() method launches the JavaFX Application and creates a new Stage (window) instance
	 * 
	 * (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage window) {
		try { 
			stage = window;
			Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("MainView.fxml")));
			window.setTitle("FunZone FEC Manager");
			window.setScene(scene);
			window.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
			window.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Entry-point
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/*
	 * returns window/stage of MainView
	 */
	public static Stage getStage(){
		return stage;
	}
}
