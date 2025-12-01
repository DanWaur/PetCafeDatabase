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
	
	private static final String[] customerPrompts = {"Customer Id", "Customer Name", "Phone Number", "Email", 
			"Date of Birth", "Emergency Contact Name", "Emergency Contact Phone Number",
			"tier", "Date of Registration"};
	private static final types[] customerTypes = {types.t_int, types.t_string, types.t_string, types.t_string,
			types.t_date, types.t_string, types.t_string, types.t_string, types.t_date};
	
	
	public static void insertFields(String tablename, String[] fieldnames, types[] fieldtypes) {
		Scanner scan = new Scanner(System.in);
		String build = "INSERT into dreynaldo."+tablename+" values (";
		
		// For each field
		for (int i = 0; i < fieldnames.length; i++) {
			
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
			
			// Add commas in between
			if (i < fieldnames.length-1) build+= ",";
		}
		// Final parenthesis
		build+=")";
		
		System.out.println(build);
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
        
        insertFields("customer", customerPrompts, customerTypes);
        
        
        
	}

}
