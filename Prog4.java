import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Prog4 {

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
		
		
		return inputs;
	}
	
	public static void main(String[] args) {
//
//        final String oracleURL =   // Magic lectura -> aloe access spell
//                        "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
//
//        final String query =       // our test query
//                        "SELECT sno, status FROM mccann.s";
//
//        String username = null,    // Oracle DBMS username
//               password = null;    // Oracle DBMS password
//
//
//        if (args.length == 2) {    // get username/password from cmd line args
//            username = args[0];
//            password = args[1];
//        } else {
//            System.out.println("\nUsage:  java JDBC <username> <password>\n"
//                             + "    where <username> is your Oracle DBMS"
//                             + " username,\n    and <password> is your Oracle"
//                             + " password (not your system password).\n");
//            System.exit(-1);
//        }
//
//        
//
//        // load the (Oracle) JDBC driver by initializing its base
//        // class, 'oracle.jdbc.OracleDriver'.
//
//        try {
//
//                Class.forName("oracle.jdbc.OracleDriver");
//
//        } catch (ClassNotFoundException e) {
//
//                System.err.println("*** ClassNotFoundException:  "
//                    + "Error loading Oracle JDBC driver.  \n"
//                    + "\tPerhaps the driver is not on the Classpath?");
//                System.exit(-1);
//
//        }
//
//        
//        // make and return a database connection to the user's
//        // Oracle database
//        Connection dbconn = null;
//        try {
//                dbconn = DriverManager.getConnection
//                               (oracleURL,username,password);
//
//        } catch (SQLException e) {
//
//                System.err.println("*** SQLException:  "
//                    + "Could not open JDBC connection.");
//                System.err.println("\tMessage:   " + e.getMessage());
//                System.err.println("\tSQLState:  " + e.getSQLState());
//                System.err.println("\tErrorCode: " + e.getErrorCode());
//                System.exit(-1);
//
//        }
		
		Scanner scan = new Scanner(System.in);
		
		int input = 0;
		boolean loop = false;
		do {
			loop = false;
			System.out.println("Please Select a Table to Modify:");
			System.out.println("(1) Members");
			System.out.println("(2) Pets");
			System.out.println("(3) Food/Beverage Orders");
			

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
		default:
			break;
		}
		
        
        //insertFields("pet", orderPrompts, orderTypes, null);
        
        
        scan.close();
	}

}
