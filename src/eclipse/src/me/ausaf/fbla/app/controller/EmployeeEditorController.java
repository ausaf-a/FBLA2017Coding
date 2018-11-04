package me.ausaf.fbla.app.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.ausaf.fbla.app.Employee;
import me.ausaf.fbla.app.SQLiteManager;
import me.ausaf.fbla.app.ui.AlertMessage;
import me.ausaf.fbla.app.ui.InputValidator;

/**
 * This class handles the logic behind the EmployeeEditor view, 
 * such as editing or deleting user info, or modifying/viewing an
 * employee's schedule. This class looks almost the same as EmployeeFormController, 
 * but it is slightly different as they provide different functionalities.
 * 
 * The layout for this view can be found at 'res/EmployeeEditor.fxml'
 * 
 * @author Ausaf Ahmed
 *
 */
public class EmployeeEditorController implements Initializable{

	
	@FXML private JFXTextField id, title, fname, lname, phone, email;
	@FXML private JFXButton edit, close, delete, browse, reset, schedule;
	@FXML private JFXDatePicker dob;
	@FXML private ImageView image;
	
	
	private static HashMap<JFXTextField, Boolean> fields = new HashMap<>();
	
	private static boolean editable = false;
	
	private Scene scheduleView; 
	private FXMLLoader scheduleLoader = new FXMLLoader(getClass().getClassLoader().getResource("ScheduleView.fxml")); 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try{
			scheduleView = new Scene(scheduleLoader.load());
		}catch(IOException ioe){
			new AlertMessage("").showError("Error occurred when loading component: \n" + ioe.getMessage());
		}

		fields.put(title, true);
		fields.put(fname, true);
		fields.put(lname, true);
		fields.put(phone, true);
		fields.put(email, true);
		
		for(JFXTextField f : fields.keySet()){
			f.setOnAction(e->edit.fire());
		}
		
		setEditable(false);
		
		edit.setOnAction(event->{
			if(!editable){
				setEditable(true);
			}else{
				modifyEmployee();
			}
		});
		
		dob.getEditor().setOnAction(event->edit.fire());
		close.setOnAction(event->handleClose(event));
		delete.setOnAction(event->handleDelete());
		browse.setOnAction(event->selectImage());
		reset.setOnAction(event->resetImage());
		
		image.setOnMouseClicked(event->{
			Stage popup = new Stage();
			BorderPane layout = new BorderPane();
			Scene scene = new Scene(layout, 300, 400);
			ImageView imgView = new ImageView();
			imgView.setPreserveRatio(true);
			imgView.fitHeightProperty().bind(popup.heightProperty());
			imgView.setImage(image.getImage());
			layout.setCenter(imgView);
			popup.setTitle("Image Viewer");
			popup.setScene(scene);
			popup.showAndWait();
		});
		
		schedule.setOnAction(event->openSchedule());
		
		initValidator();
	}
	
	/**
	 * Open the schedule view for the selected employee
	 */
	private void openSchedule() {
		ScheduleController sc = scheduleLoader.getController();
		sc.setSchedule(Integer.parseInt(id.getText()));
		Stage popup = new Stage();
		popup.setTitle("Schedule Viewer");
		popup.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("img/icon.png")));
		popup.setScene(scheduleView);
		
		//lock focus
		popup.initOwner(schedule.getScene().getWindow());
		popup.initModality(Modality.WINDOW_MODAL);
		popup.showAndWait();
	}
	
	/**
	 * Fills text fields with selected employee's data
	 * @param e - Employee
	 * @param edit - boolean used to enter editing mode directly
	 */
	public void setEmployee(Employee e, boolean edit){
		id.setText(e.getId());
		title.setText(e.getTitle());
		fname.setText(e.getFname());
		lname.setText(e.getLname());
		phone.setText(e.getPhone());
		email.setText(e.getEmail());
		
		Image temp = null;
		try {
			temp = SQLiteManager.getImage(Integer.parseInt(e.getId().trim()));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		//if image read failed then set image to default icon
		if(temp != null){
			image.setImage(temp);
		}else{
			image.setImage(
					new Image(EmployeeEditorController.class.getClassLoader().getResourceAsStream("img/employee_icon.png")));
		}
		
		
		if(!e.getDob().trim().isEmpty()){
			DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/uuuu");
			LocalDate date = LocalDate.parse(e.getDob(), df);
			dob.setValue(date);
		}
		
		if(edit) this.edit.fire();
	}
	
	/**
	 * select image 
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
			Image img = SwingFXUtils.toFXImage(buf, null);
			image.setImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * populate ImageView with default employee icon when reset is pressed
	 */
	private void resetImage() {
		image.setImage(new Image(EmployeeEditorController.class.getClassLoader().getResourceAsStream("img/employee_icon.png")));
	}
	
	/**
	 * Changes the state of text fields and buttons to disabled/enabled 
	 * @param state
	 */
	public void setEditable(boolean state){
		editable = state;
		if(state){
			edit.setText("Done");
			dob.setDisable(false);
			browse.setDisable(false);
			reset.setDisable(false);
			for(JFXTextField f : fields.keySet())
				if(f != id) f.setDisable(false);
		}else{
			edit.setText("Edit");
			dob.setDisable(true);;
			browse.setDisable(true);
			reset.setDisable(true);
			id.setDisable(true);
			for(JFXTextField f : fields.keySet())
				f.setDisable(true);
		}
	}
	
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
	
	private void handleDelete(){
		AlertMessage alert = new AlertMessage("Confirm deletion");
		if(alert.showConfirm()){
			try {	
				int i = Integer.parseInt(id.getText().trim());
				SQLiteManager.removeEmployee(i);
				//refresh table after removing employee
				((EmployeeViewController)new FXMLLoader(
		 				getClass().getClassLoader().getResource("EmployeeView.fxml")).getController()).refreshTable();
				alert.setMessage("Successfully removed employee.");
				alert.showInformation();
				handleClose(null); //close, since user didn't close manually, pass null parameter for Event
			} catch (NumberFormatException | SQLException e) {
				alert.setMessage("Error deleting employee");
				alert.showError();
			}
		}
	}
	
	/**
	 * Safe close method clears fields and exits cleanly
	 */
	public void handleClose(Event e){
		if(editable){
			AlertMessage alert = new AlertMessage("Are you sure you want to close the window? All unsaved changes will be lost");
			//check if this method was called by user event or by delete press
			if(e != null && !alert.showConfirm()){
				e.consume();
				return;
			}
		}
		setEditable(false);
		id.clear();
		dob.setValue(null);
		resetImage();
		for(JFXTextField f : fields.keySet())
			f.clear();
		((Stage)close.getScene().getWindow()).close();
	}
	
	/**
	 * Parse input from fields into an Employee object and insert into database
	 */
	private void modifyEmployee(){
		if(handleSubmit()){
			setEditable(false);
			String fname, lname, id, title, email, phone, dob;
			
			//Capitalize first letter
			fname = this.fname.getText().trim();
			fname = fname.substring(0,1).toUpperCase() + fname.substring(1);
			
			lname = this.lname.getText().trim();
			lname = lname.substring(0,1).toUpperCase() + lname.substring(1);
			
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
				SQLiteManager.insertImage(Integer.parseInt(em.getId().trim()), image.getImage());
				
				//refresh table after modifying employee
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("EmployeeView.fxml"));
				loader.load();
				((EmployeeViewController)loader.getController()).refreshTable();
			} catch (SQLException e1) {
				AlertMessage alert = new AlertMessage("Exception occurred when inserting employee:\n"
						+ e1.getMessage());
				alert.showError();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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

}
