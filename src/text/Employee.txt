package me.ausaf.fbla.app;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;

/**
 * Representation of an Employee. Instances of this class can be inserted into database 
 * through SQLiteManager.insertEmployee(e) and retrieved using SQLiteManager.getEmployee(ID)
 * 
 * @author Ausaf Ahmed
 */
public final class Employee implements Serializable{

	private static final long serialVersionUID = 1L;

	
	//SimpleStringProperties allow these variables to be attached to a JavaFX TableColumn
	private SimpleStringProperty fname, lname, id, title, dob, email, phone;
	
	/**
	 * Create employee with given information
	 * @param fname
	 * @param lname
	 * @param id
	 * @param title
	 */
	public Employee(String fname, String lname, String id, String title){
		//initialize class vars
		this.fname = new SimpleStringProperty("");
		this.lname = new SimpleStringProperty("");
		this.id = new SimpleStringProperty("");
		this.title = new SimpleStringProperty("");
		this.dob = new SimpleStringProperty("");
		this.email = new SimpleStringProperty("");
		this.phone = new SimpleStringProperty("");
		this.fname.set(fname);
		this.lname.set(lname);
		this.id.set(id);;
		this.title.set(title);
	}

	//Setters and getters for private variables 
	public String getFname() {
		return fname.get();
	}

	public String getLname() {
		return lname.get();
	}

	public String getId() {
		return id.get();
	}

	public String getPhone() {
		return phone.get();
	}
	
	
	public String getTitle() {
		return title.get();
	}

	public String getDob() {
		return dob.get();
	}

	public String getEmail() {
		return email.get();
	}
	
	public void setPhone(String phone) {
		this.phone.set(phone);
	}
	public void setFname(String fname) {
		this.fname.set(fname);
	}

	public void setLname(String lname) {
		this.lname.set(lname);
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public void setDob(String dob) {
		this.dob.set(dob);
	}

	public void setEmail(String email) {
		this.email.set(email);
	}
	
}

	
		