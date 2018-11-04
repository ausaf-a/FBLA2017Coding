package me.ausaf.fbla.app.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTimePicker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import me.ausaf.fbla.app.Employee;
import me.ausaf.fbla.app.SQLiteManager;
import me.ausaf.fbla.app.ui.AlertMessage;

/**
 * This class holds the logic behind the employees' schedules. 
 * The schedule system is an implementation of the JFXtras Agenda node:
 * <br>
 * http://jfxtras.org/ 
 * <br>
 * Schedules are stored in the SQLite database as binary 'blobs'
 * and read into ArrayLists by the SQLiteManager.getSchedule() method. 
 * 
 * The layout for this view can be found at 'res/ScheduleView.fxml
 * 
 * @author Ausaf Ahmed
 */
public class ScheduleController implements Initializable{

	 @FXML private Agenda agenda;
	 @FXML private JFXDatePicker date;
	 @FXML private JFXTimePicker start, end;
	 @FXML private JFXTextArea description;
	 @FXML private JFXButton create, print, save, discard;

	 
	 private int currentID = -1; //the id of the selected employee, set to invalid value

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//enable dragging to resize
		agenda.allowResizeProperty().set(true);
		agenda.allowDraggingProperty().set(true);
		
		//create a new Appointment when user clicks on schedule 
		agenda.newAppointmentCallbackProperty().set( (localDateTimeRange) -> {
	          return new Agenda.AppointmentImplLocal()
	                  .withStartLocalDateTime(localDateTimeRange.getStartLocalDateTime())
	                  .withEndLocalDateTime(localDateTimeRange.getEndLocalDateTime())
	                  .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
	      });
		
		//add actions to buttons
		create.setOnAction(event->{handleCreate();});
		save.setOnAction(event->{handleSave();});
		discard.setOnAction(event->handleDiscard());
		print.setOnAction(event->handlePrint());
	}
	
	
	/**
	 * Generates report and displays print dialog, allowing the user to 
	 * export to a PDF or print directly to a printer
	 */
	private void handlePrint() {
		PrinterJob job = PrinterJob.createPrinterJob();
		//show printer selection menu and prompt the user with printer choices
		if(job.showPrintDialog(null)){
			agenda.print(job);
			//show error or success message based on job result
			if(job.endJob()){
				new AlertMessage("The report was printed successfully.").showInformation();
			}else{
				new AlertMessage("An error occurred when printing the report. "
						+ "Please try again or select a different printer").showError();
			}
		}
		return;
	}



	/**
	 * Loads and displays the schedule information for the given employee
	 * @param e - Employee to load
	 */
	public void setSchedule(int id){
		this.currentID = id; //update current id 
		
		ArrayList<Appointment> appts = null;
		
		
		try {
			appts = SQLiteManager.getSchedule(id);
		} catch (SQLException e1) {
			new AlertMessage("Failed to load schedule. Error: \n" + e1.getMessage()).showError();;
		}
		
		if(appts != null)
			agenda.appointments().setAll(appts);
		else System.out.println("Appointments are null");
	}
	
	/**
	 * Saves changes to the table and inserts schedule into database entry for corresponding employee
	 */
	public void handleSave(){
		ArrayList<Appointment> buffer = null;
		buffer = new ArrayList<Appointment>(agenda.appointments().stream().collect(Collectors.toList()));
		try {
			SQLiteManager.insertSchedule(currentID, buffer);
		} catch (IOException | SQLException e) {
			new AlertMessage("Error saving schedule: \n" + e.getMessage()).showError();
		}
		
		//clear fields
		start.setValue(null);
		end.setValue(null);
		date.setValue(null);
		description.clear();
		
		//close window 
		((Stage)save.getScene().getWindow()).close();
	}
	
	/**
	 * Prompt user for discard confirmation. If confirmed, then clear fields and exit
	 * without saving
	 */
	public void handleDiscard(){
		if(new AlertMessage("Are you sure? All unsaved changes will be lost.").showConfirm()){
			//clear values 
			start.setValue(null);
			end.setValue(null);
			date.setValue(null);
			description.clear();
			
			//close window without saving 
			((Stage)discard.getScene().getWindow()).close();
		}
	}
	
	/**
	 * Create new Appointment with entered parameters and add to schedule 
	 */
	private void handleCreate(){
		if(date.getValue() != null && start.getValue() != null && end.getValue() != null){
			LocalDate d = date.getValue();
			LocalDateTime s = d.atTime(start.getValue());
			LocalDateTime e = d.atTime(end.getValue());
				agenda.appointments().add(new Agenda.AppointmentImplLocal()
						.withStartLocalDateTime(s)
						.withEndLocalDateTime(e)
						.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1")));
		}else{
			new AlertMessage("Please complete all fields").showError();
		}
	}
}
