package me.ausaf.fbla.app;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import jfxtras.scene.control.agenda.Agenda.Appointment;

/** Handles SQLite queries with statically accessible wrapper methods, 
 * 	instead of having to manually write queries and use  
 * 	connection.createStatement(q).execute();
 * 	every time, we can use:
 * 	SQLiteManager.getEmployee(id);
 *  Alternatively, we can retrieve the connection using
 	SQLiteManager.getConnection();
 *  to perform special case actions manually.
 */
public class SQLiteManager {
	
	//Database connection
	private static Connection connection;
	private static boolean connected = false;
	
	//static initialization block, executed on application startup
	static {
		//establish connection with database
		try{
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:data.sqlite");
			connected = true;
		}catch(Exception e){
			System.err.println("[!] Error getting database connection");
			connection = null; 
		}		
	}
	
	/**
	 * Searches SQLite database for row given ID, and returns Employee object with row data   
	 * @param id - ID of employee to retrieve
	 * @return Employee object if ID exists in database, otherwise returns null
	 * @throws SQLException
	 */
	public static Employee getEmployee(int id) throws SQLException{
		String query = "SELECT * FROM employees WHERE id="+id;
		ResultSet r = connection.prepareStatement(query).executeQuery();
		if(r.next()){
			Employee e = new Employee(r.getString("fname"), r.getString("lname"), ""+id, r.getString("title"));
			e.setDob(r.getString("dob"));
			e.setEmail(r.getString("email"));
			e.setPhone(r.getString("phone"));
			return e;
		}else{
			//System.err.println("[!] No employee with id:"+id+" exists");
			return null;
		}
	}
	
	
	
	/**
	 * Reads entire database of employees into an ordered ArrayList of Employee POJOs 
	 * @param order - column to order by, must be a valid column or SQLException will be thrown
	 * @return an ArrayList of Employee POJOs
	 * @throws SQLException if order parameter results in query invalidation
	 * */
	public static ArrayList<Employee> getEmployeesAsList(String order) throws SQLException{
		ArrayList<Employee> list = new ArrayList<>();
		String query = "SELECT * FROM employees ORDER BY " + order.trim();
		ResultSet r = connection.prepareStatement(query).executeQuery();
		while(r.next()){
			Employee e = new Employee(r.getString("fname"), r.getString("lname"), ""+r.getString("id"), r.getString("title"));
			e.setDob(r.getString("dob"));
			e.setEmail(r.getString("email"));
			e.setPhone(r.getString("phone"));
			list.add(e);
		}
		return list;
	}
	
	/**
	 * Returns employees ordered by first name 
	 * @return ArrayList of employees ordered by first name
	 * @throws SQLException
	 */
	public static ArrayList<Employee> getEmployeesAsList() throws SQLException{
		return getEmployeesAsList("fname");
	}
	
	
	/** 
	 *	Remove employee, image, and schedule with given ID from database
	 *	@param id - ID of employee to remove
	 * @throws SQLException 
	 */
	public static void removeEmployee(int id) throws SQLException{
		connection.createStatement().execute("DELETE FROM employees WHERE id=" + id);
		connection.createStatement().execute("DELETE FROM images WHERE id=" + id);
		connection.createStatement().execute("DELETE FROM schedules WHERE id=" + id);
	}

	 /** 
	 *	Insert given employee into database table
	 *	@param e: Employee to insert
	 */
	public static void insertEmployee(Employee e) throws SQLException{
		String sql = "INSERT OR REPLACE INTO "
				+ "employees"
				+ "(id, fname, lname, title, dob, phone, email) VALUES(?,?,?,?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setInt(1, Integer.parseInt(e.getId()));
		ps.setString(2, e.getFname());
		ps.setString(3, e.getLname());
		ps.setString(4, e.getTitle());
		ps.setString(5, e.getDob());
		ps.setString(6, e.getPhone());
		ps.setString(7, e.getEmail());
		ps.executeUpdate();
	}
	
	
	/**
	 * Retrieves image with given employee id from database
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static Image getImage(int id) throws SQLException{
		String sql = "SELECT * FROM images WHERE id = " + id;
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			return new Image(rs.getBinaryStream("img"));
		}else{
			return null;
		}
	}
	
	/**
	 * Converts image to byte array and writes it to images table in data.sqlite.
	 * The stored image can be retrieved with the correct ID using getImage()
	 * @param id - Employee ID of image
	 * @param img - Image to write
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void insertImage(int id, Image img) throws IOException, SQLException{
		
		//a new thread is used to handle the image processing, shortens delay on submit
		Task<Void> storeImage = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				BufferedImage buf = null;
				buf = SwingFXUtils.fromFXImage(img, null);
				ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
				//writing the image in memory
				ImageIO.write(buf, "png", bytesOut);				
				byte[] bytes = bytesOut.toByteArray();				
				String sql = "INSERT OR REPLACE INTO images(id, img) VALUES(?,?)";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, id);
				ps.setBytes(2, bytes);
				ps.executeUpdate();
				
				return null;
			}
		};
		
		new Thread(storeImage).start();
		
	}
	
	/**
	 * Uses Kryo framework to retrieve schedule from database as binary blob and 
	 * deserialize the data into an ArrayList of Appointments which can be 
	 * directly added to the schedule
	 * @param schedule
	 * @param id
	 * @throws IOException
	 * @throws SQLException 
	 */
	public static ArrayList<Appointment> getSchedule(int id) throws SQLException{
		String sql = "SELECT * FROM schedules WHERE id="+id;
		ResultSet rs = connection.createStatement().executeQuery(sql);
		if(rs.next()){
			System.out.println("Found schedule for " + id + " , loading...");
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(rs.getBytes("schedule"));
			Input input = new Input(bytesIn);
			Kryo k = new Kryo();
			ArrayList<Appointment> schedule = (k.readObject(input, ArrayList.class));
			return schedule;
		}else{	
			System.out.println("A schedule for " + id + " was not found");
			return new ArrayList<Appointment>();
		}
	}
	
	/**
	 * Uses Kryo framework to write schedule to a byte array and stores it in the 
	 * "schedules" table in the database with id as primary key 
	 * @param schedule
	 * @param id
	 * @throws IOException
	 * @throws SQLException 
	 */
	public static void insertSchedule(int id, ArrayList<Appointment> schedule) throws IOException, SQLException{
		/*Kryo is an open-source, third-party graph-based serialization framework which allows 
		 * serialization of objects without needing to implement Serializable. In this case it 
		 * is needed since Agenda.AppointmentImpl is final and doesn't implement Serializable, so it becomes 
		 * very difficult to serialize manually. */
		Kryo k = new Kryo();
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		Output output = new Output(bytesOut);
		k.writeObject(output, schedule);
		output.close();
		String sql = "INSERT OR REPLACE INTO schedules(id, schedule) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setInt(1, id);
		ps.setBytes(2, bytesOut.toByteArray());
		ps.executeUpdate();
	}
	
	/**
	 * Return all of the Customers who attended on a particular day of the week, 
	 * note that there is no primary key, so any changes are persistent. 
	 * @param week
	 * @param day
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<Customer> getAttendance(int week, String day) throws SQLException{
		ArrayList<Customer> list = new ArrayList<>();
		
		String sql = "SELECT * FROM attendance WHERE week="+week + " AND upper(day) = '" + day.toUpperCase() + "'";
		ResultSet rs = connection.createStatement().executeQuery(sql);
		while(rs.next() && !rs.getString("fname").equals("weekholder")){//weekholder prevents the week placeholder from being counted
			list.add(new Customer(rs.getString("fname"), rs.getString("lname"), rs.getString("time")));
		}
		
		
		return list;
	}
	
	public static void main(String[] args) throws SQLException{
		String[] fnames = {"James","Albert","Richard","Adam","Eric","Mary","Susan","Laura","Sarah","Julia","Alan", "Monty", "James"};
		String[] lnames = {"Smith","Adams","Jacobs","Richardson","Turing","White","Foster", "Rhodes","Einstein", "Oconnor"};
		String[] titles = {"Accountant", "Engineer", "Clerk", "CEO", "Physicist", "Waiter", "Doctor", "CTO", "Manager"};
		String[] words = {"apple", "water", "pencil", "magnet", "marker", "gyroscope", "math", "wire"};
		String[] adjectives = {"curious", "nice", "mean", "good", "happy", "round", "alive", "bright", "red", "blue", "lead", "sorry"};
		String[] mails = {"gmail", "yahoo", "aol", "icloud", "outlook"};
		for(int i = 0; i < 200; i++){
			int f = (int)(Math.random()*fnames.length);
			int l = (int)(Math.random()*lnames.length);
			int t = (int)(Math.random()*titles.length);
			int w = (int)(Math.random()*words.length);
			int a = (int)(Math.random()*adjectives.length);
			int m = (int)(Math.random()*mails.length);
			
			Employee e = new Employee(fnames[f],lnames[l],""+i,titles[t]);
			int y = (int)(Math.random()*12+1), d=(int)(Math.random()*28+1);
			String yr = (y<10)?"0"+y:""+y, dy= (d<10)?"0"+d:""+d;
			e.setDob(yr+"/"+dy+"/"+(int)(Math.random()*100+1917));
			e.setEmail(adjectives[a] + words[w] + "@"+mails[m]+".com");
			e.setPhone(""+(int)(Math.random()*100000000+1000000000));
			SQLiteManager.insertEmployee(e);
			System.out.println(i/2.0 + "% Done");
		}
	}
	
	/**
	 * Allows access to connection for manual database operation
	 * @return connection - SQLite Database connection
	 */
	public static Connection getConnection(){
		return connection;
	}
	
	/**
	 * Returns whether connection has been established with the database
	 * @return connected - connection status
	 */
	public static boolean isConnected(){
		return connected;
	}
	
}
