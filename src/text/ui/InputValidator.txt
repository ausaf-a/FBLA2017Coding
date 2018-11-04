package me.ausaf.fbla.app.ui;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.Tooltip;
import javafx.util.Pair;
import me.ausaf.fbla.app.SQLiteManager;


/**
 * Simple input validator to handle logic for search, employee form, and editor
 * ensures there is no possibility for SQL injection errors and other errors by 
 * parsing types of input and checking their validity 
 * @author Ausaf Ahmed
 */
public class InputValidator {
	
	public InputValidator() {
	}
	
	/**
	 * Whether or not id is a valid, unique ID
	 * @param id
	 * @return <b>true</b> if valid, else <b>false</b> 
	 */
	public boolean checkID(String id){
		try{
			//check if id is valid integer, if not, this will throw a NumberFormatException
			int x = Integer.parseInt(id.trim());
			//check if id is in use
			if(SQLiteManager.getEmployee(x) != null)
				return false;
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	/**
	 * Check if text contains illegal characters
	 * @param text
	 * @return
	 */
	public boolean checkText(String text){
		for(int i = 0; i < text.trim().length(); i++)
			if("!@#$%^&*_={}[]\\|\"';:<>,.?".contains(""+text.trim().toLowerCase().charAt(i)))
				return false;
		
		if(text.trim().isEmpty())
			return false;
		else
			return true;
	}
	
	/**
	 * Crudely check if email is valid (contains '@' and '.') and no invalid characters
	 * @param email
	 * @return
	 */
	public boolean checkEmail(String email){
		for(int i = 0; i < email.trim().length(); i++){
			if("!#$%^&*_={}\\|\"';:<>,?".contains(""+email.trim().toLowerCase().charAt(i)))
				return false;
		}
		
		if(email.contains("@") && email.contains(".") || email.trim().isEmpty())
			return true;
		else 
			return false;
	}
	
	/**
	 * Crudely check if phone is valid (contains only numbers, +, and () )
	 * @param phone
	 * @return
	 */
	public boolean checkPhone(String phone){
		for(int i = 0; i < phone.trim().length(); i++)
			if("abcdefghijklmnopqrstuvwxyz!@#$%^&*_={}[]\\|\"';:<>,./?".contains(""+phone.trim().toLowerCase().charAt(i)))
				return false;
		return true;
	}

}
