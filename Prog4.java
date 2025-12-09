/* Names: Daniel Reynaldo, Advik Bargoti, Elliott Cepin
 * Course: CSc 460
 * Assignment: Program 4
 * 
 * Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
 * 
 * The Goal of Program4 is to design a DBMS to manage a Pet Cafe. We practiced normalization,
 * building our schema, and interacting with the DBMS via a jdbc interface
 * 
 *  This program prompts the user to insert, modify, or delete records from the database.
 *  They also have the option of choosing from 4 separate queries to view results.
 *  
 *  
 * you must run this on lectura before running this:
 * 
 * 		export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
 * 
 * 
 * This Program relies on:
 * The Oracle database, and the tables we have created under
 * dreynaldo.TABLENAME 
 * 
 * This program was made with Java 16. We tested on lectura, compiled with the unix command: 'javac Prog4.java'
 * and ran with 'java Prog4 {username} {password}'. We granted select, insert, and delete on all tables, but
 * in case that does not work, you can run with 'java Prog4 dreynaldo a7463'
 * 
 * 
 * No known errors currently exist.
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class Prog4 {
	
	private static Connection dbconn = null;

	// Declares the types for each field
	enum types{
		t_int,
		t_float,
		t_string,
		t_date,
		t_time
	}
	
	// For insertion on Customer
	private static final String[] customerPrompts = {"Customer Id", "Customer Name", "Phone Number", "Email", 
			"Date of Birth", "Emergency Contact Name", "Emergency Contact Phone Number",
			"tier", "Date of Registration"};
	private static final types[] customerTypes = {types.t_int, types.t_string, types.t_string, types.t_string,
			types.t_date, types.t_string, types.t_string, types.t_string, types.t_date};
	
	
	// For insertion on Pet
	private static final String[] petPrompts = {"Pet Id", "Pet Name", "Age", 
			"Species", "Breed", "Arrival Date", "Status", "Adoption Fee", "Is Resident? (0/1)",
			"Special Needs", "Temperment"};
	private static final types[] petTypes = {types.t_int, types.t_string, types.t_int, types.t_string,
			types.t_string, types.t_date, types.t_string, types.t_float, types.t_int, types.t_string, types.t_string};
	
	
	// For insertion on Food Order
	private static final String[] orderPrompts = {"Order Id", "Customer Id Of Orderer", "Reservation Id", 
			"Order Time", "Payment Status (0/1)"};
	private static final types[] orderTypes = {types.t_int, types.t_int, types.t_int, types.t_time, types.t_int};
	
	
	// For insertion on order item
	private static final String[] ordItemPrompts = {"Order Item Id", "Order Id", "Menu Item Id", 
			"Quantity"};
	private static final types[] ordItemTypes = {types.t_int, types.t_int, types.t_int, types.t_int};
	
	
	// For insertion on reservation
	private static final String[] reservationPrompts = {"Reservation Id", "Customer Id", "Room Number", 
			"Reservation Time", "Duration", "Current Tier", "Checkin Status(0/1)", "Checkout Status(0/1)"};
	private static final types[] reservationTypes = {types.t_int, types.t_int, types.t_int, types.t_time, types.t_float, 
			types.t_string, types.t_int, types.t_int};
	
	
	// For insertion on healthRecord
	private static final String[] recordPrompts = {"Record Id", "Pet Id", "Employee Id", 
			"Record Date", "Record Type", "Description", "Next Due Date", "Record Status"};
	private static final types[] recordTypes = {types.t_int, types.t_int, types.t_int, types.t_date, types.t_string, types.t_string,
			types.t_date, types.t_string};
		
	
	// For insertion on adoption application
	private static final String[] adoptPrompts = {"Application Id", "Customer Id", "Pet Id", 
			"Employee Id", "Application Status", "Application Date"};
	private static final types[] adoptTypes = {types.t_int, types.t_int, types.t_int, types.t_int, types.t_string, types.t_date};
	
	
	// For insertion on event booking
	private static final String[] eventPrompts = {"Booking Id", "Customer Id", "Event Id", 
			"Booking Date", "Payment Status(0/1)", "Attendance Status(0/1)"};
	private static final types[] eventTypes = {types.t_int, types.t_int, types.t_int, types.t_date, types.t_int, types.t_int};
	
	// For insertion on record change audit
	private static final String[] auditPrompts = {"audit id", "record id", "desc"};
	private static final types[] auditTypes = {types.t_int, types.t_int, types.t_string};
	
	
	
	/* Method nextUniqueKey(String tableName, String pkFieldName)
	 * 
	 * Purpose: For a given tablename and name of the primary key attibute field name, this
	 * ensures that the integer that returned is an unused primary key from the database.
	 * This is used for automatically assigning a pk to a new record.
	 * 
	 * 
	 * Pre-condition: tablename must be valid name of a table under
	 * dreynaldo.___ and pkFieldName must be a primary key from that table.
	 * 
	 * Post-condition: An unused pk will be returned
	 * 
	 * Parameters: 	tableName -- name of table in oracle (in)
	 * 				pkFieldName -- name of field in that table (in)
	 * 
	 * 
	 * Returns: integer.
	 */
	public static int nextUniqueKey(String tableName, String pkFieldName) {
		// Send the query to the DBMS, and get and display the results
        Statement stmt = null;
        ResultSet answer = null;

        try {
        	
        	// This query gets the max primary key + 1
        	String query = "SELECT max(" + pkFieldName+") "
        			+ "FROM dreynaldo."+tableName;

        	// Execute and print result for first query
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);


              
        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            return 1;
        }
        
        int nextPrimaryKey = 0;
        try {
        	answer.next();
			nextPrimaryKey = answer.getInt(1);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error getting the next Primary Key");
		}
        
        return nextPrimaryKey+1;
        
	}
	
	/* Method currentReserveTier(int resId)
	 * 
	 * Purpose: Given a reservation id, this returns the current tier of the 
	 * customer who made the reservation
	 * 
	 * 
	 * Pre-condition: resId must be a valid reservation id
	 * 
	 * Post-condition: the tier as a string will be returned
	 * 
	 * Parameters: 	resId -- pk of reservation (in)
	 * 
	 * 
	 * Returns: String.
	 */
	public static String currentReserveTier(int resId) {
		// Send the query to the DBMS, and get and display the results
        Statement stmt = null;
        ResultSet answer = null;

        try {
        	
        	// This gets the current tier of the customer who booked a reservation
        	String query = "SELECT tier FROM "
        			+ " dreynaldo.reserveBooking join dreynaldo.customer"
        			+ " on dreynaldo.reserveBooking.custId = dreynaldo.customer.custId "
        			+ " where resid = "+resId;

        	// Execute and print result for first query
            stmt = dbconn.createStatement();
            answer = stmt.executeQuery(query);


              
        } catch (SQLException e) {

            System.err.println("*** SQLException:  "
                + "Could not fetch query results.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            return "";
        }
        
        String tier = "";
        try {
        	answer.next();
			tier = answer.getString(1);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error getting the next Primary Key");
		}
        
        return tier;
        
	}
	
	/* Method insertFields(String tablename, String[] fieldnames, types[] fieldtypes, String[] overrides)
	 * 
	 * Purpose: Based on a list of prompts, the types of those prompts, and the name of the
	 * table, this allows attributes to be entered into an INSERTION command, which will then
	 * add the values to the table under dreynaldo.tablename. The corresponding indices in 
	 * override[] normally will be set to null. However, if an index in the override index is
	 * not set to null, it will manually fill in that attribute rather than prompting the user
	 * to type it in from the console.
	 * 
	 * 
	 * Pre-condition: fieldnames, fieldtypes, and overrides must all be of the same length, corresponding to the 
	 * number of fields in the table under dreynaldo.tablename in oracle.
	 * 
	 * Post-condition: the table under dreynaldo.tablename will be inserted a new record with
	 * inputs deriving from either System.in or the override list.
	 * 
	 * Parameters: 	tablename -- name of table (in)
	 * 				fieldnames -- Prompts for the user (so they know what to type) (in)
	 * 				fieldtypes -- types of the fields, so it is formatted correctly in insertion command (in)
	 * 				overrides -- override list, which will replace system.in if the current field index is not null (in)
	 * 
	 * Returns: String[] -- the inputs that were entered.
	 */
	public static String[] insertFields(String tablename, String[] fieldnames, types[] fieldtypes, String[] overrides) {
		Scanner scan = new Scanner(System.in);
		String build = "INSERT into dreynaldo."+tablename+" values (";
		String[] inputs = new String[fieldnames.length];
		
		// For each field
		for (int i = 0; i < fieldnames.length; i++) {
			
			if (overrides == null || overrides[i] == null) {
				// Print header for each field prompt
				System.out.print(fieldnames[i]);
				switch (fieldtypes[i]) {
					case t_int:
						System.out.print("(Integer)");
						break;
					case t_float:
						System.out.print("(Decimal)");
						break;
					case t_string:
						System.out.print("(String)");
						break;
					case t_date:
						System.out.print("(Date: 'YYYY-MM-DD')");
						break;
					case t_time:
						System.out.print("(Date + Time: 'YYYY-MM-DD HH24:MI:SS')");
						break;
				}
				System.out.print("?: ");
				
				
				// Get input from scanner
				String input = scan.nextLine().trim();
				
				// format for insert command 
				switch (fieldtypes[i]) {
					case t_int:
						break;
					case t_float:
						break;
					case t_string:
						input = "'"+input+"'";
						break;
					case t_date:
						input = "DATE '"+input+"'";
						break;
					case t_time:
						input = "TIMESTAMP '"+input+"'";
						break;
				}
				
				// append to building string
				build+=input;
				inputs[i]=input;
			}
			else {
				build+= overrides[i];	// use override string to force enter
				inputs[i]=overrides[i];
			}
			
			// Add commas in between
			if (i < fieldnames.length-1) build+= ",";
		}
		// Final parenthesis
		build+=")";
		
		//System.out.println(build);
		
		// SQL statement string, adds to corresponding table
		Statement stmt = null;
		String query = build;
		try {
			
			// Execute insert statement
		    stmt = dbconn.createStatement();
		    stmt.executeQuery(query);
		    
		    stmt.close();
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        return null;
		}
		
		
		return inputs;
	}
	
	/* Method printResults(ResultSet res) throws SQLException
	 * 
	 * Purpose: Prints the results of a SQL query.
	 * 
	 * 
	 * Pre-condition: res must have been assigned to a stmt.executeQuery()
	 * 
	 * Post-condition: Each record will be printed as a block with the field
	 * names and values.
	 * 
	 * Parameters: 	res -- the ResultSet from the query (in/out)
	 * 
	 * 
	 * Returns: none.
	 */
	private static void printResults(ResultSet res) throws SQLException{
		ResultSetMetaData meta = res.getMetaData();
		int columns = meta.getColumnCount();
		
		int count = 0;
		while (res.next()) {
			count++;
			
			System.out.println("  [RESULT "+count+"]");
			
			for (int i = 1; i <= columns; i++) {
				System.out.print("    "+meta.getColumnName(i)+": ");
				System.out.println(res.getString(i));
			}
			System.out.println();
		}
		
		System.out.println(count+" records returned");
	}
	
	/* Method auditPetApplications(int petID)
	 * 
	 * Purpose: This query shows all applications made for a certain
	 * pet id. it prints the customer name, pet name, application status, 
	 * application date, and supervising employee name. Results are printed
	 * to console.
	 * 
	 * 
	 * Pre-condition: petId should be a valid pet PK
	 * 
	 * Post-condition: Each record will be printed as a block with the field
	 * names and values.
	 * 
	 * Parameters: 	petID -- PK of pet from pet table (in)
	 * 
	 * 
	 * Returns: none.
	 */
	public static void auditPetApplications(int petID) {
		String query = "SELECT customerName AS \"Applicant\", "
				+ "pet.name AS \"Pet Name\", "
				+ "appStatus AS \"Application Status\", "
				+ "appDate AS \"Application Date\", "
				+ "emp.name AS \"Adoption Coordinator \" "
				+ " FROM (dreynaldo.adoptApplication adopt JOIN dreynaldo.customer cust ON adopt.custID=cust.custID) "
				+ " JOIN dreynaldo.pet pet ON pet.petId = adopt.petId "
				+ " JOIN dreynaldo.employee emp ON adopt.empID=emp.empID "
				+ " WHERE adopt.petID=" + petID;
		
		
		Statement stmt = null;
		
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query);
		
				
			
			printResults(result);
			
			stmt.close();
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		}	
		
		return;
	}
	
	/* Method auditCustomer(int custID) 
	 * 
	 * Purpose: This query shows the reservation/order history of a customer, along
	 * with their current tier at the time, the orders made, item quantity, total
	 * cost of order, and room number.
	 * 
	 * 
	 * Pre-condition: custId should be a valid customer PK
	 * 
	 * Post-condition: Each record will be printed as a block with the field
	 * names and values.
	 * 
	 * Parameters: 	custId -- PK of pet from customer table (in)
	 * 
	 * 
	 * Returns: none.
	 */
	public static void auditCustomer(int custID) {
		String query = "SELECT "
				+ " reserveTime AS \"Reservation Time\","
				+ " roomId AS \"Room Number\","
				+ " ord.orderId AS \"Meal Order #\","
				+ " SUM(orditem.qty) AS \"Qty Items Ordered\","
				+ " SUM(orditem.qty*menu.price) AS \"Total Price\", "
				+ " currentTier AS \"Tier At Time\" "
				
				+ " FROM dreynaldo.reserveBooking res LEFT JOIN dreynaldo.foodOrder ord"
				+ " ON res.resId = ord.reservationId "
				
				+ " LEFT JOIN dreynaldo.orderItem orditem"
				+ " ON orditem.orderID = ord.orderId"
				
				+ " LEFT JOIN dreynaldo.menu menu ON orditem.menuItemId = menu.menuItemId"
				+ " WHERE res.custId = "+custID
				+ " AND res.checkinStatus = 1"
				 + " GROUP BY "
				 + " res.reserveTime, "
				 + " res.roomId,"
				 + " ord.orderId,"
				 + " res.currentTier" ;
		
		Statement stmt = null;
		
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query);
		
			printResults(result);
			
			stmt.close();
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		}	
		
		return;
	}
	
	public static void auditUpcomingEvents() {
		String date = LocalDate.now().toString();
		String query = "SELECT eventName, eventDate, eventStartTime, roomNo, eventCapacity, empID, eventID FROM dreynaldo.event \"EVNT\" WHERE eventCapacity > (SELECT count(*) from dreynaldo.eventBooking \"BKING\" where \"EVNT\".eventID=\"BKING\".eventID) AND eventDate > SYSDATE"; // saw SYSDATE in one of Daniel's queries, hope it works.

		Statement stmt = null;
		
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query);
		
			printResults(result);

			stmt.close();	
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		}	
		
		return;
	}
	
	static String[] recordType = {"vaccination", "checkup", "feeding schedule", "grooming", "behavioral note"};
	// retrieves the health records of all pets adopted by the given customer of a specific type
	public static void auditAllHealthRecords(int custID, String type) {
		boolean a = false;
		for (int i=0; i<recordType.length; i++) {
			if (recordType[i].equals(type)) {
				a = true;
				break;
			}
		}

		if (!a) {
			System.out.println("Invalid record type");
			return;
		}
		
		String date = LocalDate.now().toString();
		String query = "SELECT HR.petID, recordID, empID, recordDate, recordType, description, nextDueDate, recordStatus FROM (select custID, petID, appStatus FROM dreynaldo.adoptApplication WHERE appStatus='approved' and custID=" + custID + ") AA JOIN dreynaldo.healthRecord HR on HR.petID=AA.petID WHERE HR.recordType='" + type + "'";
		Statement stmt = null;
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query);
		
			printResults(result);

			stmt.close();	
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		}	
		
		return;
	}	
	
	/* Method updateEntity(String tablename, int pk, String pkName, String fieldName, types fType, Scanner scan, String override
	 * 
	 * Purpose: This updates a record from a table, setting the field to a new value.
	 * 
	 * 
	 * Pre-condition: tablename should be a table under dreynaldo.tablename
	 * 	pk is a valid pk, pkName is the name of the PK field. fieldname is the name of
	 * the field to modify. ftype is the correlating type of the field. scan
	 * is the System.in to capture used input. And if override is not null, it will be used
	 * as input instead of System.in
	 * 
	 * Post-condition: the records' field will be changed to the input by user. their input is returned
	 * 
	 * Parameters: 	tablename -- PK of pet from customer table (in)
	 * 				pk -- pk of record (in)
	 * 				pkName -- name of PK field (in)
	 * 				fieldName -- name of field to modify (in)
	 * 				fType -- corresponding type of field to modify (in)
	 * 				scan -- System.in for user input (in)
	 * 				override -- if not null, used as override instead of scan (in)
	 * Returns: String.
	 */
	private static String updateEntity(String tablename, int pk, String pkName, String fieldName, types fType, Scanner scan, String override) {

		switch (fType) {
			case t_int:
				System.out.print("(Integer)");
				break;
			case t_float:
				System.out.print("(Decimal)");
				break;
			case t_string:
				System.out.print("(String)");
				break;
			case t_date:
				System.out.print("(Date YYYY-MM-DD)");
				break;
			case t_time:
				System.out.print("(Date + Time: 'YYYY-MM-DD HH24:MI:SS')");
				break;
		}
		System.out.print("?: ");
		
		String value;
		if (override == null) {
			value = scan.nextLine().trim();
		}
		else {
			value = override;
		}
		
		String toReturn = value;
		toReturn.replaceAll("'", "");	// remove quotes for return value
		
		switch (fType) {
			case t_int:
				break;
			case t_float:
				break;
			case t_string:
				value = "'"+value+"'";
				break;
			case t_date:
				value = "DATE '"+value+"'";
				break;
			case t_time:
				value = "TIMESTAMP '"+value+"'";
				break;
		}
		
		
		// SQL statement string, updates to corresponding table
		Statement stmt = null;
		String query = "UPDATE "+ tablename + " SET " + fieldName + " = " + value + 
				" WHERE " + pkName + " = " + pk;
		try {
			
			// Execute insert statement
		    stmt = dbconn.createStatement();
		    stmt.executeQuery(query);
		    
		    stmt.close();
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		
		}
		
		System.out.println("  SET field '"+fieldName+"' to '"+ value+"'");
		
		return toReturn;
	}
	
	
	
	/* Method selectInsert(int input, Scanner scan)
	 * 
	 * Purpose: Switches between insertion between 7 different tables, depending
	 * on 'input'. Will prompt the user about which fields to insert into the data base.
	 * 
	 * 
	 * Pre-condition: input should be an int 1-7, scan is set to System.in
	 * 
	 * Post-condition: The corresponding table will have a record added from the inputs
	 * provided by the user.
	 * 
	 * Parameters:  input -- selects a table 1-7 to insert into (in)
	 * 				scan -- System.in used for input (in)
	 * Returns: void.
	 */
	private static void selectInsert(int input, Scanner scan) {
		
		
		String[] overrides;
		String[] inputs;
		int pk;
		
		switch (input) {
		case 1:
			pk = nextUniqueKey("customer","custId");	// find next primary key
			overrides = new String[customerPrompts.length];	// set override input list to primary key
			overrides[0] = Integer.toString(pk);
			overrides[7] = "'basic'";
			
			inputs = insertFields("customer", customerPrompts, customerTypes, overrides);
			if (inputs == null) break;
			
			System.out.println("  Inserted " + inputs[1] + " as member with PRIMARY KEY = " + pk);
			break;
		case 2:
			pk = nextUniqueKey("pet","petID");	// find next primary key
			overrides = new String[petPrompts.length];
			overrides[0] = Integer.toString(pk);
			inputs = insertFields("pet", petPrompts, petTypes, overrides);
			if (inputs == null) break;
			
			System.out.println("  Inserted " + inputs[1] + " as pet with PRIMARY KEY = " + pk);
			break;
		case 3:
			pk = nextUniqueKey("foodOrder","orderId");	// find next primary key
			overrides = new String[orderPrompts.length];
			overrides[0] = Integer.toString(pk);
			// first, user enters the order information
			System.out.println("Please enter the information for the Order:");
			inputs = insertFields("foodOrder", orderPrompts, orderTypes, overrides);
			
			if (inputs == null) break;
			
			System.out.println("  Inserted " + inputs[1] + " as order with PRIMARY KEY = " + pk);
			
			
			// Then User enters the individual order items. We want the foreign key to be the same
			// as what the user just enters, so we override the insertion command with that orderId
			System.out.println("Please add the items that are apart of this order:");
			overrides = new String[ordItemPrompts.length];
			overrides[1] = inputs[0];
		
			while (true) {
				pk = nextUniqueKey("orderItem","orderItemID");	// find next primary key
				overrides[0] = Integer.toString(pk);
				
				
				inputs = insertFields("orderItem", ordItemPrompts, ordItemTypes, overrides);
				System.out.println("  Inserted Item to Order. This item is assigned PRIMARY KEY = " + pk);
				System.out.println("Add more items? (Y/N) ");
				String moreItems = scan.nextLine().trim();
				if (!moreItems.equalsIgnoreCase("y")) break;
			}
			
			break;
		case 4:
			pk = nextUniqueKey("reserveBooking","resId");	// find next primary key
			overrides = new String[reservationPrompts.length];
			overrides[0] = Integer.toString(pk);
			overrides[5] = "'none'";	// set tier to none (haven't checked in yet)
			
			inputs = insertFields("reserveBooking", reservationPrompts, reservationTypes, overrides);
			if (inputs == null) break;
			
			System.out.println("  Inserted Reservation Booking with PRIMARY KEY = " + pk);
			break;
		case 5:
			pk = nextUniqueKey("healthRecord","recordID");	// find next primary key
			overrides = new String[recordPrompts.length];
			overrides[0] = Integer.toString(pk);
			inputs = insertFields("healthRecord", recordPrompts, recordTypes, overrides);
			if (inputs == null) break;
			
			System.out.println("  Inserted Health Record with PRIMARY KEY = " + pk);
			break;
		case 6:
			pk = nextUniqueKey("adoptApplication","appID");	// find next primary key
			overrides = new String[adoptPrompts.length];
			overrides[0] = Integer.toString(pk);
			overrides[4] = "'unreviewed'";
			inputs = insertFields("adoptApplication", adoptPrompts, adoptTypes, overrides);
			if (inputs == null) break;
			
			System.out.println("  Inserted Adoption Application with PRIMARY KEY = " + pk);
			break;
		case 7:
			pk = nextUniqueKey("eventBooking","bookingID");	// find next primary key
			overrides = new String[eventPrompts.length];
			overrides[0] = Integer.toString(pk);
			inputs = insertFields("eventBooking", eventPrompts, eventTypes, overrides);
			if (inputs == null) break;
			
			System.out.println("  Inserted Event Booking with PRIMARY KEY = " + pk);
			break;
		default:
			break;
		}
	}
	
	/* Method selectModify(int input, Scanner scan)
	 * 
	 * Purpose: Switches between modification between 7 different tables, depending
	 * on 'input'. Will prompt the user about which fields to modify from the table
	 * 
	 * 
	 * Pre-condition: input should be an int 1-7, scan is set to System.in
	 * 
	 * Post-condition: The corresponding table will have a field modified from the input
	 * provided by the user.
	 * 
	 * Parameters:  input -- selects a table 1-7 to modify (in)
	 * 				scan -- System.in used for input (in)
	 * Returns: void.
	 */
	private static void selectModify(int input, Scanner scan) {
		
		String tableName;
		String pkName;
		int pk = -1;
		int selectField = -1;
		
		System.out.println("Please enter the PRIMARY KEY (int) of the entity in the table");
		if (scan.hasNextInt()) {
			pk = scan.nextInt();
			scan.nextLine();
		}
		else {
			scan.nextLine();
			return;
		}
		
		switch (input) {
		case 1:
			// Prompt and choices
			tableName = "customer";
			pkName = "custId";
			System.out.println("What Member Data do you need to modify?:");
			System.out.println("Phone(1), Email(2), Tier(3)");
			if (scan.hasNextInt()) {
				selectField = scan.nextInt();
				scan.nextLine();
			}
			else {
				scan.nextLine();
				break;
			}
			
			// Update entity based on choice
			if (selectField == 1) {
				updateEntity(tableName,pk,pkName, "phone", types.t_string, scan,null);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "email", types.t_string, scan,null);
			}
			else if (selectField == 3) {
				System.out.println("Select Tier: basic(1)/plus(2)/premium(3)");
				if (scan.hasNextInt()) {
					selectField = scan.nextInt();
					scan.nextLine();
				}
				else {
					scan.nextLine();
					break;
				}
				
				if (selectField==2) {
					updateEntity(tableName,pk,pkName, "tier", types.t_string, scan,"plus");
				}
				else if (selectField == 3) {
					updateEntity(tableName,pk,pkName, "tier", types.t_string, scan,"premium");
				}
				else {
					updateEntity(tableName,pk,pkName, "tier", types.t_string, scan,"basic");
				}
				
			}
			break;
		case 2:
			// Prompt and choices
			tableName = "pet";
			pkName = "petId";
			System.out.println("What Pet Data do you need to modify?:");
			System.out.println("Age(1), Status(2), Temperment(3)");
			if (scan.hasNextInt()) {
				selectField = scan.nextInt();
				scan.nextLine();
			}
			else {
				scan.nextLine();
				break;
			}
			
			// Update entity based on choice
			if (selectField == 1) {
				updateEntity(tableName,pk,pkName, "age", types.t_int, scan,null);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "status", types.t_string, scan,null);
			}
			else if (selectField == 3) {
				updateEntity(tableName,pk,pkName, "temperment", types.t_string, scan,null);
			}
			break;
		case 3:
			// Prompt and choices
			tableName = "foodOrder";
			pkName = "orderId";
			System.out.println("What Order Data do you need to modify?:");
			System.out.println("Add Items(1), Payment Status(2)");
			if (scan.hasNextInt()) {
				selectField = scan.nextInt();
				scan.nextLine();
			}
			else {
				scan.nextLine();
				break;
			}
			
			// Update entity based on choice
			if (selectField == 1) {
				
				// Add more items to order
				while (true) {
					int itemPk = nextUniqueKey("orderItem","orderItemID");	// find next primary key
					String[] orderInputs = new String[ordItemPrompts.length];
					orderInputs[0] = Integer.toString(itemPk);	// order item pk
					orderInputs[1] = Integer.toString(pk);	// fk to order
					
					
					insertFields("orderItem", ordItemPrompts, ordItemTypes, orderInputs);
					System.out.println("  Inserted Item to Order. This item is assigned PRIMARY KEY = " + itemPk);
					System.out.println("Add more items? (Y/N) ");
					String moreItems = scan.nextLine().trim();
					if (!moreItems.equalsIgnoreCase("y")) break;
				}
			}
			else if (selectField == 2) {
				System.out.println("Enter a status: 0/1");
				updateEntity(tableName,pk,pkName, "paymentStatus", types.t_int, scan,null);
			}

			break;
		case 4:
			// Prompt and choices
			tableName = "reserveBooking";
			pkName = "resId";
			System.out.println("What Reservation Booking Data do you need to modify?:");
			System.out.println("Date+Time(1), Duration(2), Check-In Status(3), Check-Out Status(4)");
			if (scan.hasNextInt()) {
				selectField = scan.nextInt();
				scan.nextLine();
			}
			else {
				scan.nextLine();
				break;
			}
			
			// Update entity based on choice
			if (selectField == 1) {
				updateEntity(tableName,pk,pkName, "reserveTime", types.t_time, scan,null);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "duration", types.t_float, scan,null);
			}
			else if (selectField == 3) {
				System.out.println("Enter a status: 0/1");
				updateEntity(tableName,pk,pkName, "checkinStatus", types.t_int, scan,null);
				
				// Grab the current tier of customer, update the reservebookings current tier for customer history
				String updateTier = currentReserveTier(pk);
				updateEntity(tableName,pk,pkName, "currentTier", types.t_string, scan,updateTier);
			}
			else if (selectField == 4) {
				System.out.println("Enter a status: 0/1");
				updateEntity(tableName,pk,pkName, "checkoutStatus", types.t_int, scan,null);
			}
			break;
		case 5:
			// Prompt and choices
			tableName = "healthRecord";
			pkName = "recordID";
			System.out.println("What Health Record Data do you need to modify?:");
			System.out.println("Type(1), Description(2), Next Due Date(3), Status(4)");
			if (scan.hasNextInt()) {
				selectField = scan.nextInt();
				scan.nextLine();
			}
			else {
				scan.nextLine();
				break;
			}
			
			String changed;
			String value;
			// Update entity based on choice
			if (selectField == 1) {
				value = updateEntity(tableName,pk,pkName, "recordType", types.t_string, scan,null);
				changed = "Record Type";
			}
			else if (selectField == 2) {
				value = updateEntity(tableName,pk,pkName, "description", types.t_string, scan,null);
				changed = "Description";
			}
			else if (selectField == 3) {
				value = updateEntity(tableName,pk,pkName, "nextDueDate", types.t_date, scan,null);
				changed = "Next Due Date";
			}
			else if (selectField == 4) {
				value = updateEntity(tableName,pk,pkName, "recordStatus", types.t_string, scan,null);
				changed = "Status";
			}
			else {
				break;
			}
			
			int auditPk = nextUniqueKey("recordAudit","auditId");	// find next primary key
			String[] overrides = {Integer.toString(auditPk), Integer.toString(pk), "'Changed "+changed+" to \""+value+"\"'"};
			insertFields("recordAudit", auditPrompts, auditTypes, overrides);
			
			break;
		case 6:
			// Prompt and choices
			tableName = "adoptApplication";
			pkName = "appID";

			System.out.println("Enter a new status for the application(pending/approved/rejected/withdrawn):");
			updateEntity(tableName,pk,pkName, "appStatus", types.t_string, scan,null);
			
			break;
		case 7:
			// Prompt and choices
			tableName = "eventBooking";
			pkName = "bookingID";
			System.out.println("What Event Booking Data do you need to modify?:");
			System.out.println("Attendance Status(1), Payment Status(2)");
			if (scan.hasNextInt()) {
				selectField = scan.nextInt();
				scan.nextLine();
			}
			else {
				scan.nextLine();
				break;
			}
			
			System.out.println("Enter a status: 0/1");
			// Update entity based on choice
			if (selectField == 1) {
				updateEntity(tableName,pk,pkName, "attendanceStatus", types.t_int, scan,null);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "paymentStatus", types.t_int, scan,null);
			}

			break;
		default:
			break;
		}
	}
	
	/* Method deleteWithPk(String tablename, int pk, String pkName)
	 * 
	 * Purpose: deletes a record from dreynaldo.tablename with primary key
	 * = pk.
	 * 
	 * 
	 * Pre-condition: pkName should be the name of the Primary key field, pk is 
	 * an existing pk, and tablename exists under dreynaldo.tablename
	 * 
	 * Post-condition: The corresponding table will have a record deleted.
	 * 
	 * Parameters:  tablename -- name of table(in)
	 * 				scan -- System.in used for input
	 * Returns: void.
	 */
	private static void deleteWithPk(String tablename, int pk, String pkName) {
		String query = "DELETE FROM dreynaldo."+ tablename
				+ " WHERE " + pkName + " = " + pk;
		
		Statement stmt = null;
		try {
			// Execute delete statement
		    stmt = dbconn.createStatement();
		    stmt.executeQuery(query);
		    
		    stmt.close();
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		
		}
		
		System.out.println("  DELETED all records with "+pkName+"="+pk+" from " + tablename);

	}
	
	/* Method queryResponseEmpty(ResultSet result)
	 * 
	 * Purpose: Checks if a query result is empty, returns true or false
	 * 
	 * 
	 * Pre-condition: result should come from executing a query
	 * 
	 * Post-condition: returns true if empty, false otherwise
	 * 
	 * Parameters:  tablename -- name of table(in)
	 * 				scan -- System.in used for input
	 * Returns: void.
	 */
	private static boolean queryResponseEmpty(ResultSet result) {
		try {
			if (!result.next()) {
				return true;
			}
			else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}
	
	/* Method checkCustomerDelete(int custId)
	 * 
	 * Purpose: Checks the requirements that a customer
	 * may be deleted. They must have no incomplete bookings, no pending
	 * adoption applications, and no unpaid orders.
	 * 
	 * 
	 * Pre-condition: custid should be a valid pk for customer
	 * 
	 * Post-condition: returns true if able to delete, false otherwise
	 * 
	 * Parameters:  custId -- pk of customer (in)
	 * Returns: boolean.
	 */
	private static boolean checkCustomerDelete(int custId) {
		
		// check reserve bookings which are not complete (customer hasn't checked 
		String query1 = "SELECT * FROM dreynaldo.reserveBooking"
				+ " WHERE custId = " + custId
				+ " AND (checkinStatus = 0 OR checkoutStatus = 0)";
		// check adoption apps which are not complete (customer hasn't checked 
		String query2 = "SELECT * FROM dreynaldo.adoptApplication"
				+ " WHERE custId = " + custId
				+ " AND appStatus = 'pending'";
		// check customer has no unpaid orders
		String query3 = "SELECT * FROM dreynaldo.foodOrder"
				+ " WHERE custId = " + custId
				+ " AND paymentStatus = 0";

		
		Statement stmt = null;
		
		try { 
			// CHECK QUERY 1, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query1);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, customer has active reservations");
				return false;
			}
			
			stmt.close();
			
			// CHECK QUERY 2, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			result = stmt.executeQuery(query2);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, customer has pending adoption appplications");
				return false;
			}
			
			stmt.close();
			
			// CHECK QUERY 3, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			result = stmt.executeQuery(query3);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, customer has unpaid food orders");
				return false;
			}
			
			stmt.close();
			
		
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		        return false;
		}	
		
		return true;
	}
	
	/* Method checkPetDelete(int petId)
	 * 
	 * Purpose: Checks the requirements that a pet
	 * may be deleted. They must either be dead, or not have pending
	 * applications and have completed follow up schedules
	 * 
	 * 
	 * Pre-condition: petId should be a valid pk for pet
	 * 
	 * Post-condition: returns true if able to delete, false otherwise
	 * 
	 * Parameters:  petId -- pk of pet (in)
	 * Returns: boolean.
	 */
	private static boolean checkPetDelete(int petId) {
		
		// check pets dead
		String query1 = "SELECT * FROM dreynaldo.pet"
				+ " WHERE petId = " + petId
				+ " AND UPPER(status) = UPPER('dead')";
		
		// check adoption apps which are pending
		String query2 = "SELECT * FROM dreynaldo.adoptApplication"
				+ " WHERE petId = " + petId
				+ " AND appStatus = 'pending'";
		
		// check adoptions before follow up schedule
		String query3 = "SELECT * FROM dreynaldo.adoption JOIN dreynaldo.adoptApplication"
				+ " ON dreynaldo.adoption.appId = dreynaldo.adoptApplication.appId"
				+ " WHERE petId = " + petId
				+ " AND SYSDATE < followUpSchedule";

		
		Statement stmt = null;
		
		try { 
			// CHECK QUERY 1, RETURN TRUE IF PET DEAD
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query1);
			if (!queryResponseEmpty(result)) {
				return true;
			}
			
			stmt.close();
			
			// CHECK QUERY 2, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			result = stmt.executeQuery(query2);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, pet has pending adoption applications");
				return false;
			}
			
			stmt.close();
			
			// CHECK QUERY 3, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			result = stmt.executeQuery(query3);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, pet hasn't done their follow up schedule");
				return false;
			}
			
			stmt.close();
			
		
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		        return false;
		}	
		
		return true;
	}
	
	/* Method checkOrderDelete(int orderId)
	 * 
	 * Purpose: Checks the requirements that a order
	 * may be deleted. They can only be deleted if made on accident
	 * (empty set of items in order)
	 * 
	 * 
	 * Pre-condition: orderId should be a valid pk for foodOrder
	 * 
	 * Post-condition: returns true if able to delete, false otherwise
	 * 
	 * Parameters:  orderId -- pk of order (in)
	 * Returns: boolean.
	 */
	private static boolean checkOrderDelete(int orderId) {
		
		// check items within order, if empty it can be deleted;
		String query1 = "SELECT * FROM dreynaldo.foodOrder join dreynaldo.orderItem"
				+ " ON dreynaldo.foodOrder.orderId=dreynaldo.orderItem.orderId"
				+ " WHERE dreynaldo.foodOrder.orderId = " + orderId;

		
		Statement stmt = null;
		
		try { 
			// CHECK QUERY 1, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query1);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, this is a valid order");
				return false;
			}
			
			stmt.close();

			
		
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		        return false;
		}	
		
		return true;
	}
	
	/* Method checkReserveDelete(int resId)
	 * 
	 * Purpose: Checks the requirements that a reservation booking
	 * may be deleted. They must have no food orders and must be cancelled more
	 * than a day in advance
	 * 
	 * 
	 * Pre-condition: resId should be a valid pk for reservationBooking
	 * 
	 * Post-condition: returns true if able to delete, false otherwise
	 * 
	 * Parameters:  resId -- pk of reservationBooking (in)
	 * Returns: boolean.
	 */
	private static boolean checkReserveDelete(int resId) {
		
		// check orders associated with reservation
		String query1 = "SELECT * FROM dreynaldo.reserveBooking JOIN dreynaldo.foodOrder"
				+ " ON dreynaldo.reserveBooking.resId = dreynaldo.foodOrder.reservationId"
				+ " WHERE dreynaldo.reserveBooking.resId = " + resId;
		
		
		// check reservations which are on or past date
		String query2 = "SELECT * FROM dreynaldo.reserveBooking"
				+ " WHERE resId = " + resId
				+ " AND SYSDATE >= reserveTime"; 

		
		Statement stmt = null;
		
		try { 
			// CHECK QUERY 1, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query1);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, this reservation has orders");
				return false;
			}
			
			stmt.close();
			
			// CHECK QUERY 2, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			result = stmt.executeQuery(query2);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, past date of reservation");
				return false;
			}
			
			stmt.close();

			
		
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		        return false;
		}	
		
		return true;
	}
	
	/* Method checkAppDelete(int appId)
	 * 
	 * Purpose: Checks the requirements that a adopt application
	 * may be deleted. They must be unreviewed to delete
	 * 
	 * 
	 * Pre-condition: appId should be a valid pk for adoptApplication
	 * 
	 * Post-condition: returns true if able to delete, false otherwise
	 * 
	 * Parameters:  appid -- pk of adoptApplication (in)
	 * Returns: boolean.
	 */
	private static boolean checkAppDelete(int appId) {
		
		// check apps that are past unreviewed
		String query1 = "SELECT * FROM dreynaldo.adoptApplication"
				+ " WHERE appId = " + appId
				+ " AND UPPER(appStatus) <> UPPER('unreviewed')";


		
		Statement stmt = null;
		
		try { 
			// CHECK QUERY 1, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query1);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, adoption has started being reviewed");
				return false;
			}
			
			stmt.close();
		
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		        return false;
		}	
		
		return true;
	}
	
	/* Method checkEventDelete(int appId)
	 * 
	 * Purpose: Checks the requirements that a event booking
	 * may be deleted. It must be done before the date of the event.
	 * 
	 * 
	 * Pre-condition: eventId should be a valid pk for eventBooking
	 * 
	 * Post-condition: returns true if able to delete, false otherwise
	 * 
	 * Parameters:  eventId -- pk of eventBooking (in)
	 * Returns: boolean.
	 */
	private static boolean checkEventDelete(int eventId) {

		// check events which are on or past date
		String query1 = "SELECT * FROM dreynaldo.eventBooking JOIN dreynaldo.event "
				+ " ON dreynaldo.eventBooking.eventID = dreynaldo.event.eventID"
				+ " WHERE dreynaldo.event.eventId = " + eventId
				+ " AND SYSDATE >= dreynaldo.event.eventDate"; 

		
		Statement stmt = null;
		
		try { 
			// CHECK QUERY 1, RETURN FALSE IF RESULT HAS RECORDS
			stmt = dbconn.createStatement();
			ResultSet result = stmt.executeQuery(query1);
			if (!queryResponseEmpty(result)) {
				System.out.println("  Error: Can't delete, past date of event");
				return false;
			}
			
			stmt.close();
		
		} catch (SQLException e) {
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());	
		        return false;
		}	
		
		return true;
	}
	
	
	
	/* Method selectDelete(int input, Scanner scan)
	 * 
	 * Purpose: Switches between deletion between 7 different tables, depending
	 * on 'input'. Does a check if the corresponding record can be deleted, and then
	 * carries forth.
	 * 
	 * 
	 * Pre-condition: input should be an int 1-7, scan is set to System.in
	 * 
	 * Post-condition: The corresponding table will have a record deleted or an
	 * error message will be printed if it can't be deleted.
	 * 
	 * Parameters:  input -- selects a table 1-7 to delete from (in)
	 * 				scan -- System.in used for input (in)
	 * Returns: void.
	 */
	private static void selectDelete(int input, Scanner scan) {
		String tableName;
		String pkName;
		int pk = -1;
		int selectField = -1;
		
		System.out.println("Please enter the PRIMARY KEY (int) of the entity in the table");
		if (scan.hasNextInt()) {
			pk = scan.nextInt();
			scan.nextLine();
		}
		else {
			scan.nextLine();
			return;
		}
		boolean canDelete;
		switch (input) {
			case 1:
				canDelete = checkCustomerDelete(pk);
				
				if (canDelete) {
					deleteWithPk("customer",pk,"custId");
					deleteWithPk("reserveBooking",pk,"custId");
					deleteWithPk("eventBooking",pk,"custId");
					deleteWithPk("foodOrder",pk,"custId");
					deleteWithPk("adoptApplication",pk,"custId");
				}
				break;
			case 2:
				canDelete = checkPetDelete(pk);
				
				if (canDelete) {;
					deleteWithPk("pet",pk,"petId");
					deleteWithPk("adoptApplication",pk,"petId");
					deleteWithPk("adoption",pk,"petId");

				}
				break;
			case 3:
				canDelete = checkOrderDelete(pk);
				
				if (canDelete) {;
					deleteWithPk("foodOrder",pk,"orderId");
					deleteWithPk("orderItem",pk,"orderId");

				}
				break;
			case 4:
				canDelete = checkReserveDelete(pk);
				
				if (canDelete) {;
					deleteWithPk("reserveBooking",pk,"resId");

				}
				break;
			case 5:
				System.out.println("Health records cannot be deleted. Errors should be corrected"
						+ " with marking the status as 'void' or 'corrected");
				break;
			case 6:
				canDelete = checkAppDelete(pk);
				
				if (canDelete) {;
					deleteWithPk("adoptApplication",pk,"appId");

				}
				break;
			case 7:
				canDelete = checkEventDelete(pk);
				
				if (canDelete) {;
					deleteWithPk("eventBooking",pk,"eventId");
				}
				break;
			default:
				break;
		}
	}
	
	
	
	/* Method main(String[] args)
	 * 
	 * Purpose: Connects to the Oracle database using {username} and {password} from args.
	 * Then, prompts the user for input to perform modifications to the tables
	 * or to view certain queries.
	 * 
	 * 
	 * Pre-condition: args should be of length 2: {username} and {password}.
	 * (Can use 'dreynaldo' and 'a7463' if for some reason their isn't grant to tables.
	 * 
	 * Post-condition: The input will end when the user enters the exit command (8)
	 * 
	 * Parameters:  args -- command line args: {username} {password}
	 * Returns: void.
	 */
	public static void main(String[] args) {

        final String oracleURL =   // Magic lectura -> aloe access spell
                        "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        final String query =       // our test query
                        "SELECT sno, status FROM mccann.s";

        String username = null,    // Oracle DBMS username
               password = null;    // Oracle DBMS password


        if (args.length == 2) {    // get username/password from cmd line args
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage:  java JDBC <username> <password>\n"
                             + "    where <username> is your Oracle DBMS"
                             + " username,\n    and <password> is your Oracle"
                             + " password (not your system password).\n");
            System.exit(-1);
        }

        

        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.

        try {

                Class.forName("oracle.jdbc.OracleDriver");

        } catch (ClassNotFoundException e) {

                System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
                System.exit(-1);

        }

        
        // make and return a database connection to the user's
        // Oracle database
        try {
                dbconn = DriverManager.getConnection
                               (oracleURL,username,password);

        } catch (SQLException e) {

                System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
                System.err.println("\tMessage:   " + e.getMessage());
                System.err.println("\tSQLState:  " + e.getSQLState());
                System.err.println("\tErrorCode: " + e.getErrorCode());
                System.exit(-1);

        }
		
        
        
        
        // USER INPUT AFTER CONNECTION COMPLETED
		Scanner scan = new Scanner(System.in);
		
		int input = 0;
		boolean loop = false;
		
		
		
		while (true) {
			
			// Choose option
			do {
				loop = false;
				System.out.println("Please Select an Option:");
				System.out.println("(1) Modify Tables");
				System.out.println("(2) Queries");
				System.out.println("(3) EXIT PROGRAM");
				

				if (scan.hasNextInt()) {
					input = scan.nextInt();
					scan.nextLine();
					if (input < 1 || input > 3) {
						System.out.println("Please enter a integer 1-8");
						loop = true;
					}
				}
				else {
					System.out.println("Please enter a integer 1-8");
					scan.nextLine();
					loop = true;
				}
			} while (loop);
			
			// MODIFY TABLE
			if (input == 1) {
				do {
					loop = false;
					System.out.println("Please Select a Table to Modify:");
					System.out.println("(1) Member");
					System.out.println("(2) Pet");
					System.out.println("(3) Food/Beverage Order");
					System.out.println("(4) Reservation Booking");
					System.out.println("(5) Pet Health Record");
					System.out.println("(6) Adoption Application");
					System.out.println("(7) Event Booking");
					

					if (scan.hasNextInt()) {
						input = scan.nextInt();
						scan.nextLine();
						if (input < 1 || input > 7) {
							System.out.println("Please enter a integer 1-7");
							loop = true;
						}
					}
					else {
						System.out.println("Please enter a integer 1-7");
						scan.nextLine();
						loop = true;
					}
				} while (loop);
				
				
				int modifyMode = -1;
				

				do {
					loop = false;
					System.out.println("Would you like to Insert(1), Modify(2), or Delete(3) from a table? (enter an integer):");
					if (scan.hasNextInt()) {
						modifyMode = scan.nextInt();
						scan.nextLine();
						if (modifyMode < 1 || modifyMode > 3) {
							System.out.println("Please enter a integer 1-3");
							loop = true;
						}
					}
					else {
						System.out.println("Please enter a integer 1-3");
						scan.nextLine();
						loop = true;
					}
					
				}while (loop);

				if (modifyMode == 1) {
					selectInsert(input, scan);
				}
				else if (modifyMode == 2) {
					selectModify(input, scan);
				}
				else {
					selectDelete(input, scan);
				}
			}
			// QUERIES
			else if (input == 2) {
				do {
					loop = false;
					System.out.println("Please Select A Query");
					System.out.println("(1) Pet Applications");
					System.out.println("(2) Customer Visit History");
					System.out.println("(3) Upcoming Events");
					System.out.println("(4) Custom Query");


					if (scan.hasNextInt()) {
						input = scan.nextInt();
						scan.nextLine();
						if (input < 1 || input > 7) {
							System.out.println("Please enter a integer 1-4");
							loop = true;
						}
					}
					else {
						System.out.println("Please enter a integer 1-4");
						scan.nextLine();
						loop = true;
					}
				} while (loop);
				
				
				switch (input) {
					//QUERY 1
					case 1:
						System.out.print("Enter a pet ID: ");
						if (scan.hasNextInt()) {
							input = scan.nextInt();
							scan.nextLine();
							auditPetApplications(input);	//call query
						}
						else {
							break;
						}
						break;
					//QUERY 2 GOES HERE
					case 2:
						System.out.print("Enter a customer ID: ");
						if (scan.hasNextInt()) {
							input = scan.nextInt();
							scan.nextLine();
							auditCustomer(input);	//call query
						}
						break;
					//QUERY 3 GOES HERE
					case 3:
						auditUpcomingEvents();	//call query
						break;
					// QUERY 4 GOES HERE
					case 4:
						System.out.print("Enter a customer ID: ");
						if (scan.hasNextInt()) {
							input = scan.nextInt();
							System.out.println(input);
							scan.nextLine();
							String type = scan.nextLine();
							auditAllHealthRecords(input, type);	//call query
						}
						break;
				}
			}
			// EXIT PROGRAM
			else {
				break;
			}

		}
		
		

        scan.close();
        
		// Close the database
		try {
			dbconn.close();
		} catch (SQLException e) {
			System.exit(-1);
		}
	}

}
