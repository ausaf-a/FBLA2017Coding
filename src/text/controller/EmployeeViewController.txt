package me.ausaf.fbla.app.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.ausaf.fbla.app.Employee;
import me.ausaf.fbla.app.Main;
import me.ausaf.fbla.app.SQLiteManager;
import me.ausaf.fbla.app.ui.AlertMessage;

/**
 * This class manages the logic behind the Employee screen. 
 * The table view is managed through this class's refreshTable method, 
 * which implements SQLiteManager.getEmployeesAsList() to retrieve database entries
 * 
 * The layout for this view can be found at 'res/EmployeeView.fxml's
 * 
 * @author Ausaf Ahmed
 */
public class EmployeeViewController implements Initializable {
	
	//components from gui
	@FXML private JFXTextField searchField;
    @FXML private JFXComboBox<String> searchType;
    @FXML private JFXButton addButton, editButton, removeButton, resetButton, scheduleButton;
    @FXML private TableView<Employee> tableView;
    @FXML private TableColumn<Employee,String> fname, lname, id, title, dob, email, phone;

    private FXMLLoader employeeCreator, employeeEditor, scheduleView; //hold the layouts for the employee creation/edit/schedule forms
    private Scene creator, editor, schedule;
    
    private static ObservableList<Employee> data; //tied to table cells, editing this allows us to manipulate the table
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//load FXML layouts into Java components
		employeeCreator = new FXMLLoader(getClass().getClassLoader().getResource("EmployeeForm.fxml"));
		employeeEditor = new FXMLLoader(getClass().getClassLoader().getResource("EmployeeEditor.fxml"));
		scheduleView = new FXMLLoader(getClass().getClassLoader().getResource("ScheduleView.fxml"));
		try {
			creator = new Scene(employeeCreator.load());
			editor = new Scene(employeeEditor.load());
			schedule = new Scene(scheduleView.load());
		} catch (IOException e2) {
			new AlertMessage("Error loading interface: \n" + e2.getMessage()).showError();
			e2.printStackTrace();
		}
		
		//assign table column values to String properties from each Employee object
		fname.setCellValueFactory(new PropertyValueFactory<Employee, String>("fname"));
		lname.setCellValueFactory(new PropertyValueFactory<Employee, String>("lname"));
		id.setCellValueFactory(new PropertyValueFactory<Employee, String>("id"));
		title.setCellValueFactory(new PropertyValueFactory<Employee, String>("title"));
		dob.setCellValueFactory(new PropertyValueFactory<Employee, String>("dob"));
		email.setCellValueFactory(new PropertyValueFactory<Employee, String>("email"));
		phone.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));
		
		//route events to handler methods
		resetButton.setOnAction(e->handleReset());
		addButton.setOnAction(e->handleAdd());
		
		data = FXCollections.observableArrayList();
		
		//search options
		ObservableList<String> searchTypes = FXCollections.observableArrayList(
				"First Name", "Last Name", "ID", "Title", "Date of Birth", "Phone", "Email"
				);
		//maps visible choices to table columns
		String[] cols = {"fname", "lname", "id", "title", "dob", "phone", "email"};
		searchType.setItems(searchTypes);
		searchType.getSelectionModel().select(0); //select First Name by default
		
		
		//Dynamic table filtering updates whenever text in searchField changes
		searchField.textProperty().addListener((observable, oldValue, newValue)->{
			try {
				String col = cols[searchType.getSelectionModel().getSelectedIndex()];
				//prevent possible SQL injection
				for(int i = 0; i < newValue.length(); i++){
					if("!@#$%^&*_=;\"'".contains(""+newValue.charAt(i))){
						setInvalid(searchField);
						return;
					}
				}
				setValid(searchField);
				//if search box is empty, show all
				if(newValue.trim().isEmpty()){
					refreshTable();
					return;
				}
				//else query database with search term 
				data.clear();
				String sql = "SELECT * FROM employees WHERE " + col + " LIKE '" + newValue + "%' ORDER BY " + col;
				ResultSet r = SQLiteManager.getConnection().prepareStatement(sql).executeQuery();
				while(r.next()){
					Employee e = new Employee(r.getString("fname"), r.getString("lname"), ""+r.getString("id"), r.getString("title"));
					e.setDob(r.getString("dob"));
					e.setEmail(r.getString("email"));
					e.setPhone(r.getString("phone"));
					data.add(e);
				}
			} catch (SQLException e1) {
				//print to stderr stream
				System.err.println(e1.getMessage());
			}
		});
		
		//disable these buttons until an employee is selected
		removeButton.setDisable(true);
		editButton.setDisable(true);
		scheduleButton.setDisable(true);
		
		/* when user clicks on table, send event to handleClick 
		 * (this gives the method more details about the click 
		 * such as click count*/ 
		tableView.setOnMouseClicked(e->handleClick(e));
		//populate table on startup
		refreshTable();
	}
	
	/**
	 * Handle tableView clicks
	 */
	private void handleClick(MouseEvent e){
		Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
		if(selectedEmployee != null){
			removeButton.setDisable(false);
			editButton.setDisable(false);
			scheduleButton.setDisable(false);
			editButton.setOnAction(event->{
				handleEdit(selectedEmployee, true);
			});
			scheduleButton.setOnAction(event->handleSchedule());
			
			//define remove action
			removeButton.setOnAction(event->{
				//confirm deletion
				AlertMessage am = new AlertMessage("Confirm removing employee '" 
						+ selectedEmployee.getFname() + " " + selectedEmployee.getLname() + "'");
				if(!am.showConfirm()) 
					return;
				data.remove(selectedEmployee);
				//if table becomes empty, disable al
				if(data.size() == 0){
					removeButton.setDisable(true);
					editButton.setDisable(true);
					scheduleButton.setDisable(true);
				}
				try { 
					SQLiteManager.removeEmployee(Integer.parseInt(selectedEmployee.getId()));
				} catch (NumberFormatException | SQLException exception) {
					System.err.println("Error deleting employee");
				}
			});
		}else{
			removeButton.setDisable(true);
			editButton.setDisable(true);
			scheduleButton.setDisable(true);
		}
		if(e != null && e.getClickCount() == 2){
			handleEdit(selectedEmployee, false);
		}
	}
	
	/* Resets table to show all employees and clears search bar
	 * */
	private void handleReset(){
		searchField.clear();
		refreshTable();
		handleClick(null); //disable edit and schedule buttons if no employee is selected
	}
	
	/* Shows employee form and refreshes table to show new Employee
	 * */
	private void handleAdd(){
		Stage popup = new Stage();
		popup.setTitle("Add a new employee");
		popup.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
		popup.setScene(creator);
		popup.setOnCloseRequest(event->{((EmployeeFormController)employeeCreator.getController()).handleClose();
		});
		
		//lock focus
		popup.initOwner(Main.getStage());
		popup.initModality(Modality.WINDOW_MODAL);
		
		popup.showAndWait();
		refreshTable();
	}
	
	/**
	 * Open new editor window. Create the window with 
	 * @param e
	 * @param edit
	 */
	private void handleEdit(Employee e, boolean edit){
		EmployeeEditorController eec = employeeEditor.getController(); 
		eec.setEmployee(e, edit);
		Stage popup = new Stage();
		popup.setTitle("Employee Editor");
		popup.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("img/icon.png")));
		popup.setScene(editor);
		
		popup.setOnCloseRequest(event->{
			eec.handleClose(event);
		});
		popup.initOwner(Main.getStage());
		popup.initModality(Modality.WINDOW_MODAL);
		popup.showAndWait();
		handleClick(null); //enable
	}

	/**
	 * Open the selected employee's schedule
	 */
	private void handleSchedule(){
		ScheduleController sc = scheduleView.getController();
		Employee selected = tableView.getSelectionModel().getSelectedItem();
		sc.setSchedule(Integer.parseInt(selected.getId()));
		Stage popup = new Stage();
		popup.setTitle("Schedule Viewer");
		popup.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("img/icon.png")));
		popup.setScene(schedule);
		//lock focus
		popup.initOwner(scheduleButton.getScene().getWindow());
		popup.initModality(Modality.WINDOW_MODAL);
		popup.showAndWait();
	}
	
	/* Attempt to load employees from database into ObservableArrayList of Employee objects
	 * and populate the TableView with this data 
	 */
	public void refreshTable(){
		try {
			data.clear();
			data.addAll(SQLiteManager.getEmployeesAsList());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.err.println("Error retrieving employees");
		}
		tableView.setItems(data);		
	}
	
	/**
	 * Flag the given field as invalid
	 * @param f
	 */
	private void setInvalid(JFXTextField f){
		f.setFocusColor(Color.RED);
		f.setUnFocusColor(Color.RED);
	}
	
	/**
	 * Flag the given field as valid
	 * @param f
	 */
	private void setValid(JFXTextField f){
		f.setFocusColor(Color.web("#4059a9"));
		f.setUnFocusColor(Color.web("4d4d4d"));
	}

}
