import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Prog4 {
	
	private static Connection dbconn = null;

	// Declares the types for each field
	enum types{
		t_int,
		t_float,
		t_string,
		t_date
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
	private static final types[] orderTypes = {types.t_int, types.t_int, types.t_int, types.t_date, types.t_int};
	
	
	// For insertion on order item
	private static final String[] ordItemPrompts = {"Order Item Id", "Order Id", "Menu Item Id", 
			"Quantity", "Price"};
	private static final types[] ordItemTypes = {types.t_int, types.t_int, types.t_int, types.t_int, types.t_float};
	
	
	// For insertion on reservation
	private static final String[] reservationPrompts = {"Reservation Id", "Customer Id", "Room Number", 
			"Reservation Time", "Duration", "Checkin Status(0/1)", "Checkout Status(0/1)"};
	private static final types[] reservationTypes = {types.t_int, types.t_int, types.t_int, types.t_date, types.t_float, types.t_int,
			types.t_int};
	
	
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
						System.out.print("(Date YYYY-MM-DD)");
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
		
		System.out.println(build);
		
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
		
		}
		
		
		return inputs;
	}
	
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
		
		//System.out.println(nextUniqueKey("customer","custId"));
		
		while (true) {
			
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
				System.out.println("(8) EXIT PROGRAM");
				

				if (scan.hasNextInt()) {
					input = scan.nextInt();
					scan.nextLine();
				}
				else {
					System.out.println("Please enter a integer 1-7");
					scan.nextLine();
					loop = true;
				}
			} while (loop);
			
			switch (input) {
			case 1:
				insertFields("customer", customerPrompts, customerTypes, null);
				break;
			case 2:
				insertFields("pet", petPrompts, petTypes, null);
				break;
			case 3:
				// first, user enters the order information
				System.out.println("Please enter the information for the Order:");
				String[] inputs = insertFields("foodOrder", orderPrompts, orderTypes, null);
				
				
				
				// Then User enters the individual order items. We want the foreign key to be the same
				// as what the user just enters, so we override the insertion command with that orderId
				System.out.println("Please add the items that are apart of this order:");
				String[] override = new String[inputs.length];
				override[1] = inputs[0];
			
				while (true) {
					insertFields("orderItem", ordItemPrompts, ordItemTypes, override);
					System.out.println("Add more items? (Y/N) ");
					String moreItems = scan.nextLine().trim();
					if (!moreItems.equalsIgnoreCase("y")) break;
				}
				
				break;
			case 4: 
				insertFields("reserveBooking", reservationPrompts, reservationTypes, null);
				break;
			case 5:
				insertFields("healthRecord", recordPrompts, recordTypes, null);
				break;
			case 6:
				insertFields("adoptApplication", adoptPrompts, adoptTypes, null);
				break;
			case 7:
				insertFields("eventBooking", eventPrompts, eventTypes, null);
				break;
			default:
				break;
			}
			
			if (input == 8) break;	// exit program
		}
		
		
		
        
        //insertFields("pet", orderPrompts, orderTypes, null);
        
        
        scan.close();
        
		// Close the database
		try {
			dbconn.close();
		} catch (SQLException e) {
			System.exit(-1);
		}
	}

}
