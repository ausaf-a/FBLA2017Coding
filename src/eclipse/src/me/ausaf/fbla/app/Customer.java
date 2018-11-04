package me.ausaf.fbla.app;

import javafx.beans.property.SimpleStringProperty;

/*
 * Representation of a customer, contains very basic properties as customers are not tracked. 
 * Instances of this class are displayed in AttendanceView
 */
public class Customer {

	
	private SimpleStringProperty fname, lname, time;
	public Customer(String fname, String lname, String time) {
		this.fname = new SimpleStringProperty(fname);
		this.lname = new SimpleStringProperty(lname);
		this.time = new SimpleStringProperty(time);
	}
	public String getFname() {
		return fname.get();
	}
	public String getLname() {
		return lname.get();
	}
	public String getTime() {
		return time.get();
	}
	

}
