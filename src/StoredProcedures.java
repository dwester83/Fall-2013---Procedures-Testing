import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.System;
import java.util.Properties;
import java.sql.*;

public class StoredProcedures {
	
	Connection conn = null;
	Statement stmt  = null;
	PreparedStatement instructorPStmt = null;
	CallableStatement instructorCStmt = null;
	CallableStatement instructorCStmt2 = null;
	static String instructorQuery = "";
	static String caseThreePartOne = "CALL caseThreePartOne(?,?,?);";
	static String caseThreePartTwo = "CALL caseThreePartTwo(?,?,?);";
	static String course_count;

	/**
	 * Stored Procedures Main
	 */
	public static void main(String[] args) 
	{
		String userName = args[0];	// Input User Name for creating connection to University Schema
		String passWord = args[1];	// Input Password for creating connection to University Schema
	
		try
		{
			// Invoke the University constructor, passing in User Name and Password.
			StoredProcedures university = new StoredProcedures(userName, passWord);
			
			BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.print("Please input the credit amount you want to check: ");
			course_count = keyboardInput.readLine(); // This is the course count amount to find out.
			
			// This is running the methods to test the Case Three.
			System.out.println("caseThreePartOne ...");
			university.caseThreePartOne();
			System.out.println("\ncaseThreePartTwo ...");
			university.caseThreePartTwo();
			university.cleanup();
		}
		catch (SQLException exSQL)
		{
		    System.out.println("main SQLException: " + exSQL.getMessage());
		    System.out.println("main SQLState: " + exSQL.getSQLState());
		    System.out.println("main VendorError: " + exSQL.getErrorCode());
		    exSQL.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage() + " is not a number. Please enter a number to have a valid return.");
		}
	}
	
	/**
	 * Constructor for Class University
	 * @param String userName for connecting to University schema
	 * @param String passWord for connecting to University schema
	 */
	public StoredProcedures(String userName, String passWord) throws SQLException
	{		
		// Go create a connection to my "university" database.
		// "conn" is a data member of JDBC type Connection.
		conn = getConnection(userName, passWord);
		if (conn == null)
		{
			System.out.println("getConnection failed.");
			return;
		}
		
		// Create Statement object for use in creating the non-prepared queries.
		stmt = conn.createStatement();
		
		// Create the Prepared statements.
		instructorPStmt = conn.prepareStatement(instructorQuery, ResultSet.FETCH_FORWARD);
		
		// Create a Callable statement to call a stored procedure returning a course count.
		instructorCStmt = conn.prepareCall(caseThreePartOne);
		instructorCStmt2 = conn.prepareCall(caseThreePartTwo);
		
	}	// End of Stored Procedures Constructor.
	
	/**
	 * Method: cleanup()
	 * Function: To close the various JDBC objects.
	 */
	public void cleanup()
	{
		try {
			stmt.close();
			conn.close();
			instructorPStmt.close();
		}
		catch  (SQLException exSQL) {;}
	}
	
	public Connection getConnection(String userName, String passWord)
	{
		Connection conn = null;
		
		// Location of the MySQL-based "university" database.
		String university_url = "jdbc:mysql://localhost:3306/university";
			
		// Load the JDBC driver manager.
		try { Class.forName("com.mysql.jdbc.Driver").newInstance(); }
		catch (Exception ex) { 
		    System.out.println("Class.forName Exception: " + ex.getMessage());
		    ex.printStackTrace();
		    return null;
		}
		
		// Construct a Properties object for passing the User Name and Password into the DB connection.
		// Create the DB connection.
		try {
			Properties connectionProps = new Properties();
			connectionProps.put("user", userName);
			connectionProps.put("password", passWord);
			conn = DriverManager.getConnection (university_url, connectionProps);
			if (conn == null)
			{
				System.out.println("getConnection:getConnection failed.");
				return null;
			}
		}
		catch (SQLException exSQL)
		{
		    System.out.println("getConnection SQLException: " + exSQL.getMessage());
		    System.out.println("getConnection SQLState: " + exSQL.getSQLState());
		    System.out.println("getConnection VendorError: " + exSQL.getErrorCode());
		    exSQL.printStackTrace();
		}
		
		return conn;
	}	// End of getConnection()
	
	void caseThreePartOne() throws SQLException
	{
		instructorCStmt.setString(1, course_count);
		instructorCStmt.registerOutParameter(2, Types.VARCHAR);
		instructorCStmt.registerOutParameter(3, Types.VARCHAR);
		instructorCStmt.executeQuery();				// Execute the Call of the Stored Procedure Count_Courses.
		String names = instructorCStmt.getString(2);	// The names that are returned.
		String deptName = instructorCStmt.getString(3);	// The department names that are returned.
		System.out.println("Names:        " + names + "\nDepartments:  " + deptName);
	}
	
	void caseThreePartTwo() throws SQLException
	{
		instructorCStmt2.setString(1, course_count);
		instructorCStmt2.registerOutParameter(2, Types.VARCHAR);
		instructorCStmt2.registerOutParameter(3, Types.VARCHAR);
		instructorCStmt2.executeQuery();				// Execute the Call of the Stored Procedure Count_Courses.
		String names = instructorCStmt2.getString(2);	// The names that are returned.
		String deptName = instructorCStmt2.getString(3);	// The department names that are returned.
		System.out.println("Names:        " + names + "\nDepartments:  " + deptName);
	}
	
}	// End of Stored Procedures Class
