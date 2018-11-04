package me.ausaf.fbla.app.ui;

import java.awt.Toolkit;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertMessage{

	private Alert info, error, confirm; 
	private String message;
	private Image icon = new Image(getClass().getClassLoader().getResourceAsStream("img/icon.png"));
	
	/**
	 * Wrapper for javafx.scene.control.Alert to allow for simple, clean alert messages without 
	 * having to prepare Alert object each time and set icon
	 * @param msg
	 */
	public AlertMessage(String msg){
		message = msg;
		
		info = new Alert(AlertType.INFORMATION);
		error = new Alert(AlertType.ERROR);
		confirm = new Alert(AlertType.CONFIRMATION);
		
		info.setHeaderText(null);
		error.setHeaderText(null);
		confirm.setHeaderText(null);
		
		//set icons
		((Stage)info.getDialogPane().getScene().getWindow()).getIcons().add(icon);
		((Stage)error.getDialogPane().getScene().getWindow()).getIcons().add(icon);
		((Stage)confirm.getDialogPane().getScene().getWindow()).getIcons().add(icon);
	}
	
	//show info dialog
	public void showInformation(String msg){
		info.setContentText(msg);
		info.showAndWait();
	}
	
	public void showInformation(){
		showInformation(this.message);
	}
	
	//show error dialog
	public void showError(String msg){
		error.setContentText(msg);
		Toolkit.getDefaultToolkit().beep();
		error.showAndWait();
	}
	
	public void showError(){
		showError(this.message);
	}
	
	public void setMessage(String msg){
		this.message = msg;
	}
	
	//show confirm dialog, and return boolean with result
	public boolean showConfirm(String msg){
		confirm.setContentText(msg);
		Optional<ButtonType> result = confirm.showAndWait();
		if(result.get() == ButtonType.OK)
			return true;
		else
			return false;
	}

	public boolean showConfirm(){
		return showConfirm(this.message);
	}
}
