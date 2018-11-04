package me.ausaf.fbla.app.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * This class handles the logic for the toolbar buttons on the 
 * left side of the program, including switching between different views
 * 
 * The layout for this view can be found at 'res/MainView.fxml
 * 
 * @author Ausaf Ahmed
 */
public class MainViewController implements Initializable {
    @FXML private JFXButton employeesButton;
    @FXML private JFXButton scheduleButton;
    @FXML private JFXButton attendanceButton;
    @FXML private JFXButton helpButton;
    @FXML private BorderPane borderPane;
	private VBox employeeView;
	private BorderPane attendanceView, helpView;
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		
    	/* load all FXML layouts into JavaFX objects for faster scene switching
    	 * not exactly scene switching, just changing the  
    	 * center element of the borderPane
    	 */
    	try {
			employeeView = new VBox((VBox)FXMLLoader.load(getClass().getClassLoader().getResource("EmployeeView.fxml")));
			attendanceView = new FXMLLoader(getClass().getClassLoader().getResource("AttendanceView.fxml")).load();
			helpView = new FXMLLoader(getClass().getClassLoader().getResource("HelpView.fxml")).load();
    	} catch (IOException e) {
			e.printStackTrace();
		}
		
		//assign actions to buttons
		employeesButton.setOnAction(e->openEmployee()); //lambda expressions used to simplify function definitions
		attendanceButton.setOnAction(e->openAttendance());
		helpButton.setOnAction(e->openHelp());
		
		//start out on employee screen
		employeesButton.fire();
	}
    
    //the following methods are used to simplify the scene switching process
    private void openEmployee(){
    	//lighten all of the other buttons and darken this button
    	attendanceButton.setStyle("-fx-background-color: #ffffff");
    	helpButton.setStyle("-fx-background-color: #ffffff");
    	employeesButton.setStyle("-fx-background-color: #bbbbbb");
    	borderPane.setCenter(employeeView); //sets the center element of the BorderPane to the desired scene
    }
    private void openAttendance(){
    	employeesButton.setStyle("-fx-background-color: #ffffff");
    	helpButton.setStyle("-fx-background-color: #ffffff");
    	attendanceButton.setStyle("-fx-background-color: #bbbbbb");
    	borderPane.setCenter(attendanceView);
    }

    private void openHelp(){
    	employeesButton.setStyle("-fx-background-color: #ffffff");
    	attendanceButton.setStyle("-fx-background-color: #ffffff");
    	helpButton.setStyle("-fx-background-color: #bbbbbb");
    	borderPane.setCenter(helpView);
    }

    
}
