import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
			"Reservation Time", "Duration", "Checkin Status(0/1)", "Checkout Status(0/1)"};
	private static final types[] reservationTypes = {types.t_int, types.t_int, types.t_int, types.t_time, types.t_float, types.t_int,
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
	
	// For insertion on record change audit
	private static final String[] auditPrompts = {"audit id", "record id", "desc"};
	private static final types[] auditTypes = {types.t_int, types.t_int, types.t_string};
	
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
	
	public static void auditPetApplications(int petID) {
		String query = "SELECT customerName, name, appStatus, appDate FROM (dreynaldo.adoptApplication JOIN dreynaldo.customer ON custID=custID) JOIN dreynaldo.employee ON empID=empID WHERE petID=" + petID;
		Statement stmt = null;
		
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			stmt.execute(query);
		
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
	
	public static void auditCustomer(int custID) {
		String query = "SELECT orderID, currentTier, roomNo, reservationDate, totalCost FROM ((SELECT roomNo, reservationDate, currentTier,checkInStatus, checkOutStatus FROM dreynaldo.reservationBooking WHERE checkInStatus=1 OR checkOutStatus=1) \"booking\"  JOIN dreynaldo.foodBooking ON custID=custID) JOIN room ON roomID=roomID where custID=" + Integer.toString(custID);
		Statement stmt = null;
		
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			stmt.execute(query);
		
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
		String query = "SELECT eventName, eventDate, eventStartTime, roomNo, eventCapacity, empID, eventID FROM dreynaldo.foodBooking  WHERE eventCapacity > (SELECT count(*) from dreynaldo.eventBooking where eventID=eventID) AND eventDate > date " + date;
		Statement stmt = null;
		
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			stmt.execute(query);
		
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
	
	String[] recordType = {""};
	// retrieves the health records of all pets adopted by the given customer of a specific type
	public static void auditAllHealthRecords(int custID, String type) {
		
		String date = LocalDate.now().toString();
		String query = "SELECT petID, petName, recordID, empID, recordDate, recordType, description, nextDueDate, recordStatus FROM (select custID, petID, petName appStatus FROM dreynaldo.addoptApplication WHERE appStatus=1) JOIN dreynaldo.healthRecord on petID=petID WHERE recordType=" + type + " AND custID=" + Integer.toString(custID);
		Statement stmt = null;
		
		try { // replace this with code to process output
			stmt = dbconn.createStatement();
			stmt.execute(query);
		
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
	
	private static String updateEntity(String tablename, int pk, String pkName, String fieldName, types fType, Scanner scan) {

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
		
		String value = scan.nextLine().trim();
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
	
	private static void selectInsert(int input, Scanner scan) {
		
		
		String[] overrides;
		String[] inputs;
		int pk;
		
		switch (input) {
		case 1:
			pk = nextUniqueKey("customer","custId");	// find next primary key
			overrides = new String[customerPrompts.length];	// set override input list to primary key
			overrides[0] = Integer.toString(pk);
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
				updateEntity(tableName,pk,pkName, "phone", types.t_string, scan);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "email", types.t_string, scan);
			}
			else if (selectField == 3) {
				System.out.println("Enter Tier: basic/plus/premium");
				updateEntity(tableName,pk,pkName, "tier", types.t_string, scan);
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
				updateEntity(tableName,pk,pkName, "age", types.t_int, scan);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "status", types.t_string, scan);
			}
			else if (selectField == 3) {
				updateEntity(tableName,pk,pkName, "temperment", types.t_string, scan);
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
				updateEntity(tableName,pk,pkName, "paymentStatus", types.t_int, scan);
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
				updateEntity(tableName,pk,pkName, "reserveTime", types.t_time, scan);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "duration", types.t_float, scan);
			}
			else if (selectField == 3) {
				System.out.println("Enter a status: 0/1");
				updateEntity(tableName,pk,pkName, "checkinStatus", types.t_int, scan);
			}
			else if (selectField == 4) {
				System.out.println("Enter a status: 0/1");
				updateEntity(tableName,pk,pkName, "checkinStatus", types.t_int, scan);
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
				value = updateEntity(tableName,pk,pkName, "recordType", types.t_string, scan);
				changed = "Record Type";
			}
			else if (selectField == 2) {
				value = updateEntity(tableName,pk,pkName, "description", types.t_string, scan);
				changed = "Description";
			}
			else if (selectField == 3) {
				value = updateEntity(tableName,pk,pkName, "nextDueDate", types.t_date, scan);
				changed = "Next Due Date";
			}
			else if (selectField == 4) {
				value = updateEntity(tableName,pk,pkName, "recordStatus", types.t_string, scan);
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
			updateEntity(tableName,pk,pkName, "appStatus", types.t_string, scan);
			
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
				updateEntity(tableName,pk,pkName, "attendanceStatus", types.t_int, scan);
			}
			else if (selectField == 2) {
				updateEntity(tableName,pk,pkName, "paymentStatus", types.t_int, scan);
			}

			break;
		default:
			break;
		}
	}
	
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
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			default:
				break;
		}
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
					if (input < 1 || input > 8) {
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
			
			
			int modifyMode = -1;
			
			if (input != 8) {
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
			}

			if (input == 8) break;	// exit program
			
			
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
