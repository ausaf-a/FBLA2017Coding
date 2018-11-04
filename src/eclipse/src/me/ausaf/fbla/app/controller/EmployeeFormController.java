package me.ausaf.fbla.app.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.ausaf.fbla.app.Employee;
import me.ausaf.fbla.app.Main;
import me.ausaf.fbla.app.SQLiteManager;
import me.ausaf.fbla.app.ui.AlertMessage;
import me.ausaf.fbla.app.ui.InputValidator;

/**
 * This class holds the logic behind the employee creator view. Tasks
 * such as input validation are implemented here, while the SQLite
 * add/remove entries are defined in SQLiteManager.java
 * 
 * The layout for this view can be found at 'res/EmployeeForm'
 * 
 * @author Ausaf Ahmed
 */
public class EmployeeFormController implements Initializable {
	
	@FXML private JFXTextField fname, lname, id, title, email, phone; 
	@FXML private JFXDatePicker dob; 
	@FXML private JFXButton submit, cancel, browse, reset; 
	@FXML private ImageView img;
	
	//array holding text fields mapped to their validation status, makes it easier to clear/change properties of all fields
	private static HashMap<JFXTextField, Boolean> fields = new HashMap<>();
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//set required fields invalid until completed
		setInvalid(fname);
		setInvalid(lname);
		setInvalid(id);
		setInvalid(title);
		
		//initialize the fields array with text fields and their respective validation states
		fields.clear();
		fields.put(fname, false);
		fields.put(lname, false);
		fields.put(id, false);
		fields.put(title, false);
		fields.put(email, true);
		fields.put(phone, true);
		
		//clear fields and close form on x or close press
		cancel.setOnAction(e->handleClose());
		
		//create a new employee object and add it to the database
		submit.setOnAction(e->createEmployee());
		
		//assign action methods to image control buttons
		browse.setOnAction(e->selectImage());
		reset.setOnAction(e->resetImage());
		
		for(JFXTextField field : fields.keySet())
			field.setOnAction(e->submit.fire());
		initValidator();
	}


	/**
	 * Show FileChooser so user can select image
	 */
	private void selectImage() {
		FileChooser f = new FileChooser();
		f.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (png, jpeg, jpg)", "*.png", "*.jpg", "*.jpeg"));
		
		Stage stage = new Stage();
		stage.setTitle("Select an image");
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("img/icon.png")));
		
		File file = f.showOpenDialog(stage);
		BufferedImage buf = null;
		if(file == null)
			return;
		try {
			buf = ImageIO.read(file);
			Image image = SwingFXUtils.toFXImage(buf, null);
			img.setImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * reset image to default employee icon
	 */
	private void resetImage() {
		img.setImage(new Image(getClass().getClassLoader().getResourceAsStream("img/employee_icon.png")));
	}
	
	/**
	 * Parse input from fields into an Employee object and insert into database
	 */
	private void createEmployee(){
		if(handleSubmit()){
			String fname, lname, id, title, email, phone, dob;
			
			//Capitalize first letter
			fname = this.fname.getText().trim();
			fname = fname.substring(0,1).toUpperCase() + fname.substring(1).toLowerCase();
			
			lname = this.lname.getText().trim();
			lname = lname.substring(0,1).toUpperCase() + lname.substring(1).toLowerCase();
			
			//format textfield input
			id = this.id.getText().trim();
			title = this.title.getText().trim();
			email = this.email.getText().trim();
			phone = this.phone.getText().trim();
			
			//Format date to month/day/year format
			if(this.dob.getValue() != null)
				dob = this.dob.getValue().format(DateTimeFormatter.ofPattern("MM/dd/uuuu"));
			else dob = "";
			Employee em = new Employee(fname, lname, id, title);
			em.setEmail(email);
			em.setPhone(phone);
			em.setDob(dob);
			try {
				SQLiteManager.insertEmployee(em);
				SQLiteManager.insertImage(Integer.parseInt(id.trim()), img.getImage());
				AlertMessage alert = new AlertMessage("Employee '"+fname+" "+lname+"' was added successfully.");
				alert.showInformation();
				handleClose();
			} catch (SQLException | NumberFormatException | IOException e1) {
				AlertMessage alert = new AlertMessage("Exception occurred when inserting employee");
				alert.showError();
			}
		}
	}
	
	/**
	 * Flag the given field as invalid
	 * @param f
	 */
	private void setInvalid(JFXTextField f){
		f.setFocusColor(Color.RED);
		f.setUnFocusColor(Color.RED);
		fields.put(f, false);
	}
	
	/**
	 * Flag the given field as valid
	 * @param f
	 */
	private void setValid(JFXTextField f){
		f.setFocusColor(Color.web("#4059a9"));
		f.setUnFocusColor(Color.web("4d4d4d"));
		fields.put(f, true);
	}
	
	/**
	 * Called when submit is pressed, validates input and shows appropriate error message
	 * @return
	 */
	private boolean handleSubmit(){
		Object[] arr = fields.values().toArray();
		String err = "";
		//loop through required fields
		for(int i = 0; i < arr.length; i++){
			if(!(Boolean)arr[i]){
				err += ((JFXTextField)fields.keySet().toArray()[i]).getPromptText() + "\n";
			}
		}
		if(!err.isEmpty()){
			AlertMessage alert = new AlertMessage("Please check the following fields:\n"+err);
			alert.showError();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Initialize dynamic input validation using ChangeListeners
	 */
	private void initValidator(){
		InputValidator iv = new InputValidator();
		fname.textProperty().addListener((obs, old, val)->{
			if(iv.checkText(val))
				setValid(fname);
			else
				setInvalid(fname);
		});
		lname.textProperty().addListener((obs, old, val)->{
			if(iv.checkText(val))
				setValid(lname);
			else
				setInvalid(lname);
		});
		title.textProperty().addListener((obs, old, val)->{
			if(iv.checkText(val))
				setValid(title);
			else
				setInvalid(title);
		});
		id.textProperty().addListener((obs, old, val)->{
			if(iv.checkID(val))
				setValid(id);
			else
				setInvalid(id);
		});
		email.textProperty().addListener((obs, old, val)->{
			if(iv.checkEmail(val))
				setValid(email);
			else
				setInvalid(email);
		});
		phone.textProperty().addListener((obs, old, val)->{
			if(iv.checkPhone(val))
				setValid(phone);
			else
				setInvalid(phone);
		});
		
	}
	
	/**
	 * Handle window close by clearing fields and resetting image
	 */
	public void handleClose(){
		for(JFXTextField f : fields.keySet())
			f.clear();
		setInvalid(fname);
		setInvalid(lname);
		setInvalid(id);
		setInvalid(title);
		dob.setValue(null);
		img.setImage(new Image("img/employee_icon.png"));
		id.requestFocus();
		((Stage)cancel.getScene().getWindow()).close();	
	}

}


