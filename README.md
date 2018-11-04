#### Coding and Programming 
#### 2017 FBLA National Leadership Conference

#### README.md

## Development/Source Code info:
------------------------------------------------------------------------------------------------------------------------
This application was developed using Java and JavaFX version 8 along with SQLite3. Software used during the development process includes: Eclipse Java Neon IDE, JDK 8, Gluon Scene Builder 8.0.0, Adobe Photoshop, and SqliteManager. The layouts for the user interface are defined in separate files written in a markup language (FXML), and are loaded and given functionality at runtime. Each 'View' or scene has a corresponding controller class, which gives functionality to the layouts; for example, a button's graphics would be defined in a *.fxml file, and the corresponding Controller class would implement the functionality of the button. 

To view source code, navigate to 'src\text' The main class is defined in Main.txt, the controller folder contains controller classes, and the layouts folder contains the FXML layouts. 

Alternatively, the folder 'src\eclipse' can be loaded into Eclipse Java IDE to view the full structured project along with syntax highlighting and other features. 

# Execution Instructions:

### Requirements:
- Windows XP or later (64-bit)
- Can also run on 32 bit machines as long as Java 8 is installed 

### Download Java: 
(32 bit): http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jre-8u131-windows-i586.exe
(64 bit): http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jre-8u131-windows-x64.exe


### Execute (32 bit/64 bit with Java installed): 
Double click on the file 'FunzoneManager.jar' in 'Execute (32 or 64 bit with java)' folder

### Execute (64 bit no Java): 
Double click on FunzoneManagerFBLA2017.exe in 'Execute (64 bit)'

------------------------------------------------------------------------------------------------------------------------

Usage Instructions:
------------------------------------------------------------------------------------------------------------------------
### Employee View:
Click the 'Employees' button in the top left hand corner. This will bring up a table of all employees in the database, as well as a top bar with search fields and buttons, referred to as the 'Employees View'. 

#### Searching: 
To search the database, click the drop down menu labeled 'First Name' to select the filter parameter. Type your search into the text field to see only the employees who match the criteria. To reset the search, either clear the text in the search bar, or click the 'Reset Search' button. 

#### Add:
If you would like to add a new employee, click on the 'Add' button located in the top bar. This will display a employee creation form. The fields 'ID', 'Job Title', 'First Name', and 'Last Name' must be completed. The additional fields, 'Phone Number', 'E-mail', and the Employee Photo are optional. To set the photo, click 'Browse' and open an image file in the file chooser. To reset the photo back to default, click 'Reset'. When you are done completing the information, click 'Submit' to add the new employee. Note: If your input contains illegal characters, the field will turn red to indicate that it needs correction, and the employee will not be created until the fields are valid.  

#### Remove:
To remove an employee, click on the employee you wish to remove to select. Next, click on the 'Remove' button in the top bar. This will prompt you for a confirmation, click OK to confirm. The employee will then be removed from the database.

#### Edit: 
To edit an employee, click on the employee you wish to edit to select. Next, either click the 'Edit' button in the top bar, or double-click on the employee and press 'Edit' in the resulting pop up window.  When you are done editing, click the 'Done' button in the left corner to save changes and close the window.  

#### View: 
You can view the basic information about all of the employees in the database on this screen. To see additional information such as work schedule or photo for a single employee, double-click on the employee in the table view. This will display an Employee Viewer window with all of the employee's information as well as their photo. To view the employee's schedule, click the 'View Schedule' button in either the Employee Viewer or the 'Employees View' screen. This will display a window with the schedule. To view the full schedule, maximize the window. When you are done viewing or editing the schedule, click 'Save Changes'. If you do not wish to save changes, click 'Discard Changes' and confirm. For more information, see the Schedule section.

-------------------------------------------------------------------------------------------------------------------
### Schedule View: 
Click on an employee in the employees table, then click 'View Schedule' in the top bar. This will show the an agenda for the current week. To add a new appointment, fill out the fields in 'New Appointment', and click create. Note: The Schedule View will only display appointments in the current scope, so adding an appointment to a date not in the current week will result in it not being displayed. Alternatively you can click anywhere on the week view to create a new appointment, and drag the top and bottom edges to resize. Right click on the appointment to view or set additional information, such as location or description. When you are finished, click 'Save Changes' to save and close the window. If you want to export the schedule as a PDF or print it, click 'Print or Export to PDF'. In the resulting printer popup, select 'Microsoft Print to PDF' to export, otherwise select your printer of choice. Click OK to confirm. When the schedule has finished printing or saving, click 'Save Changes' to save and close the Schedule View.
 
 
------------------------------------------------------------------------------------------------------------------------

### Attendance View: 
To open, click the 'Attendance' Button in the top left. This will display a customer check-in view with tabs for each day of the week. To keep track of attendance, first create a new week by clicking the 'New Week' button, or select an existing week in the drop down. When a customer arrives, select the current day, enter his/her information in the section titled 'Customer Check-in', and click 'Check-in' to add the entry. You can view all of the entries for a given day by clicking on the corresponding tab. If you would like to export the attendance for the selected week, click 'Generate Report' to create a log of attendance numbers through the week. In the popup box, click 'Print' to open a print dialog. Select 'Print to PDF' for export otherwise select a printer. Click OK to export/print the attendance report. 

------------------------------------------------------------------------------------------------------------------------
