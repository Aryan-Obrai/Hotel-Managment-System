/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult(String query) throws SQLException {
      Statement stmt = this._connection.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;
  
      int[] colWidths = new int[numCol];
      for (int i = 1; i <= numCol; i++) {
          colWidths[i-1] = rsmd.getColumnName(i).length();
      }
  
      while (rs.next()) {
          for (int i = 1; i <= numCol; i++) {
              String colValue = rs.getString(i);
              int colWidth = colValue.length();
              if (colWidth > colWidths[i-1]) {
                  colWidths[i-1] = colWidth;
              }
          }
          rowCount++;
      }
  
      // print the column headers
      for (int i = 1; i <= numCol; i++) {
          String colName = rsmd.getColumnName(i);
          System.out.printf("%-" + colWidths[i-1] + "s  ", colName);
      }
      System.out.println();
  
      // print the data
      rs.beforeFirst();
      while (rs.next()) {
          for (int i = 1; i <= numCol; i++) {
              String colValue = rs.getString(i);
              System.out.printf("%-" + colWidths[i-1] + "s  ", colValue);
          }
          System.out.println();
      }
  
      stmt.close();
      return rowCount;
   }
  
  

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         //userID/ connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String  authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                System.out.println("5. Update Room Information");
                System.out.println("6. View 5 recent Room Updates Info");
                System.out.println("7. View booking history of the hotel");
                System.out.println("8. View 5 regular Customers");
                System.out.println("9. Place room repair Request to a company");
                System.out.println("10. View room repair Requests history");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql); break;
                   case 4: viewRecentBookingsfromCustomer(esql); break;
                   case 5: updateRoomInfo(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewBookingHistoryofHotel(esql); break;
                   case 8: viewRegularCustomers(esql); break;
                   case 9: placeRoomRepairRequests(esql); break;
                   case 10: viewRoomRepairHistory(esql); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
	    Global.userID = userID;
            return userID;
	 }
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {
      try {
         System.out.print("\tEnter your location (latitude): ");
         String latitude = in.readLine();
         System.out.print("\tEnter your location (longitude): ");
         String longitude = in.readLine();
         String query = String.format("SELECT * FROM Hotel WHERE calculate_distance(latitude, longitude, '%s', '%s') <= 30", latitude, longitude);
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println(rowCount + " rows retrieved.");
         System.out.print("Press Enter to return to Main Menu");
         String temp = in.readLine();
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }



   public static void viewRooms(Hotel esql) {
	try {
         System.out.print("Enter hotel ID: ");
         int hotelID = Integer.parseInt(in.readLine());
         System.out.print("Enter booking date (YYYY-MM-DD): ");
         String bookingDate = in.readLine();

         String query = String.format("SELECT r.roomNumber, r.price, CASE WHEN (b.bookingDate IS NULL) THEN 'Available' ELSE 'Booked' END AS availability " + 
                                       "FROM Rooms r " +
                                       "LEFT JOIN RoomBookings b " +
                                       "ON r.hotelID = b.hotelID AND r.roomNumber = b.roomNumber AND b.bookingDate = '%s' " +
                                       "WHERE r.hotelID = %d " +
                                       "ORDER BY r.roomNumber", bookingDate, hotelID);
         esql.executeQueryAndPrintResult(query);
      }  
      catch (Exception e) {
         System.err.println(e.getMessage());
      }
}


   public static void bookRooms(Hotel esql) {
	try{
	 //System.out.print("UserID: " + Global.userID);
	 //int customerID = Integer.parseInt(in.readLine());
         System.out.print("Enter hotel ID: ");
         int hotelID = Integer.parseInt(in.readLine());
         System.out.print("Enter room number: ");
         int roomNumber = Integer.parseInt(in.readLine());
         System.out.print("Enter booking date (YYYY-MM-DD): ");
         String bookingDate = in.readLine();
	 String temp;
         String checkAvailabilityQuery = String.format("SELECT * FROM RoomBookings WHERE hotelID = %d AND roomNumber = %d AND bookingDate = '%s'",
                                                      hotelID, roomNumber, bookingDate
         );
         int numBookings = esql.executeQuery(checkAvailabilityQuery);
         if (numBookings > 0) {
            System.out.println("We apoligize but that room is not availible for the date selected.");
	    System.out.println("Press Entre to return to Main Menu.");
	    temp = in.readLine();
            return;
         }

         String getPriceQuery = String.format("SELECT price FROM Rooms WHERE hotelID = %d AND roomNumber = %d",
                                             hotelID, roomNumber
         );
         List<List<String>> roomInfo = esql.executeQueryAndReturnResult(getPriceQuery);
         if (roomInfo.isEmpty()) {
            System.out.println("No such room exists in our database.");
	    System.out.println("Press Enter to return to Main Menu.");
	    temp = in.readLine();
            return;
         }
         int roomPrice = Integer.parseInt(roomInfo.get(0).get(0));
	 
	 System.out.println("The price fore that room is $" + roomPrice);
	 System.out.println("Would you like to book the room?[yes/no]");
	 Scanner inputObj = new Scanner(System.in);
	 temp = inputObj.nextLine();
	 do{
		if (temp.equals("yes")){
         		String bookRoomQuery = String.format("INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) VALUES (%s, %d, %d, '%s')", Global.userID, hotelID, roomNumber, bookingDate);
         		esql.executeUpdate(bookRoomQuery);
         		System.out.println("Booking successful! The room price is $" + roomPrice);
	 		System.out.println("Press Enter to return to Main Menu.");
	 		temp = in.readLine();
			return;
		}
		else if(temp.equals("no")){
			System.out.println("Booking process canceled");
			System.out.println("Press Enter to return to Main Menu");
			in.readLine();
			return;
		}
		else{
			System.out.println("Improper response.\nWould you like to book this room? [yes/no]");
			temp = in.readLine();
		}
	 }while(true);
      } 
         catch (Exception e) {
         System.err.println(e.getMessage());
         }
   }

   public static void viewRecentBookingsfromCustomer(Hotel esql) {
      try {
          System.out.println("Your five most recent bookings are:");
          String RecentBookingsQuery = String.format(
              "SELECT RB.hotelID, RB.roomNumber, R.price, RB.bookingdate " +
              "FROM RoomBookings RB " +
              "INNER JOIN Rooms R " +
              "ON RB.hotelID = R.hotelID AND RB.roomNumber = R.roomNumber " +
              "WHERE customerID = %s " +
              "ORDER BY bookingDate DESC " +
              "LIMIT 5;",
              Global.userID
          ); 
          esql.executeQueryAndPrintResult(RecentBookingsQuery);
          return;
      } catch (Exception e) {
          System.err.println(e.getMessage());
      }
  }
  

   public static void updateRoomInfo(Hotel esql) {
	try{
		String temp;
		String getManagerIDQuery = String.format("SELECT userID FROM Users WHERE usertype = 'manager'");
		List<List<String>> ManagerIDs = esql.executeQueryAndReturnResult(getManagerIDQuery);
		boolean isManager = false;
		boolean hotelAccess = false;
		for(int i = 0; i < ManagerIDs.size(); i++){
			if(ManagerIDs.get(i).get(0).equals(Global.userID)){
				isManager = true;
				System.out.println("We have a Manager");
			}
		}
		if(isManager){
			System.out.println("Enter hotelID: ");
			String HotelID = in.readLine();
			System.out.println("Enter room number: ");
			String RoomNumber = in.readLine();
			
			String getManagedHotelsQuery = String.format("SELECT HotelID FROM Hotel WHERE managerUserID = %s;", Global.userID);
			List<List<String>> ManagingHotelIDs = esql.executeQueryAndReturnResult(getManagedHotelsQuery);

			for(int i = 0; i < ManagingHotelIDs.size(); i++){
				if(ManagingHotelIDs.get(i).get(0).equals(HotelID)){
					hotelAccess = true;
				}
			}
			
			if(hotelAccess){
				System.out.println("Choose option");
				System.out.println("\t1. Update room price");
				System.out.println("\t2. Update room image URL"); 
				
				switch(readChoice()){
					case 1: System.out.println("Enter new price");
						String newPrice = in.readLine();
						String UpdateRoomPrice = String.format("UPDATE Rooms SET price = %s WHERE hotelID = %s AND roomNumber = %s", newPrice, HotelID, RoomNumber );
						String UpdateRoomLog = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID,roomNumber, updatedon) VALUES( %s, %s, %s, DATE_TRUNC('minute',CURRENT_TIMESTAMP::timestamp));", Global.userID, HotelID, RoomNumber ); 
						esql.executeUpdate(UpdateRoomPrice);
						esql.executeUpdate(UpdateRoomLog);
						System.out.println("Room price updated");
						System.out.println("Update Loged");
						System.out.println("Press Enter to return to main menu");
						temp = in.readLine();
						break;
					case 2: System.out.println("Enter new image URL");
						String newURL = in.readLine();
						String UpdateRoomURL = String.format("UPDATE Rooms SET imageURL = '%s' WHERE hotelID = %s AND roomNumber = %s", newURL, HotelID, RoomNumber);
						String LogUpdateQuery = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedon) VALUES( %s, %s, %s, DATE_TRUNC('minute', CURRENT_TIMESTAMP::timestamp));", Global.userID, HotelID, RoomNumber);
						esql.executeUpdate(UpdateRoomURL);
						esql.executeUpdate(LogUpdateQuery);
						System.out.println("Room URL updated");
						System.out.println("Update Logged");
						System.out.println("Press Enter to return to main menu");
						temp = in.readLine();
						break;
					default: System.out.println("Unrecognized choice returning to Main menu");
						break;
				}
				return;	 
			} 
			else {
				System.out.println("Access Denied: You must be the manager of this hotel to update room information");
				System.out.println("Press Enter to return to Main Menu.");
				temp = in.readLine();
				return;
			}
				

		} else {
			System.out.println("Access Denied: Must be a manager");
			System.out.println("Press Enter to return to Main Menu.");
			temp = in.readLine();
			return;
		}

	}catch(Exception e){
		System.err.println(e.getMessage());
	}
}
   public static void viewRecentUpdates(Hotel esql) {
	try{
	 	String getManagedHotelQuery = String.format("SELECT HotelID FROM Hotel WHERE managerUserID = %s;", Global.userID);
		List<List<String>> ManagingHotelIDs = esql.executeQueryAndReturnResult(getManagedHotelQuery);
		boolean hotelAccess = false;
		String HotelID;
		
		System.out.println("Enter the hotelID");
		HotelID = in.readLine();
		for(int i = 0; i < ManagingHotelIDs.size(); i++){
				if(ManagingHotelIDs.get(i).get(0).equals(HotelID)){
					hotelAccess = true;
				}
		}
		if(hotelAccess){
			String getRecentHotelUpdates = String.format("SELECT * FROM RoomUpdatesLog WHERE hotelID = %s LIMIT 5;", HotelID);
			esql.executeQueryAndPrintResult(getRecentHotelUpdates);
			System.out.println("Press Enter to return to main menu");
			String temp = in.readLine();
			return;
		} 
		else{
			System.out.println("Access Denied: must be a manager of this hotel to view update history");
			System.out.println("Press Enter to return to main menu");
			String temp = in.readLine();
			return;
		}
	} 
	catch(Exception e){
		System.err.println(e.getMessage());

	} 
   }
   public static void viewBookingHistoryofHotel(Hotel esql) {
	try
	{
		String temp;
		boolean hotelAccess = false;
		String getManagedHotelsQuery = String.format("SELECT HotelID FROM Hotel WHERE managerUserID = %s;", Global.userID);
		List<List<String>> ManagingHotelIDs = esql.executeQueryAndReturnResult(getManagedHotelsQuery);
		
		System.out.println("Enter the hotelID: ");
		String HotelID = in.readLine();

		for(int i = 0; i < ManagingHotelIDs.size(); i++){
			if(ManagingHotelIDs.get(i).get(0).equals(HotelID)){
				hotelAccess = true;
			}
		}
		if(hotelAccess)
		{
			System.out.println("Choose option");
			System.out.println("1. Get bookings by date range");
			System.out.println("2. Get all bookings");
			System.out.println("3. Exit");
		
		switch(readChoice())
		{
			case 1:
				System.out.println("Enter the start date in your range [yyyy-mm-dd]");
				String startDate = in.readLine();
				System.out.println("Enter the end date in your range [yyyy-mm-dd]");
				String endDate = in.readLine();
				String HotelBookingHistory = String.format("SELECT RB.bookingID, U.name, RB.hotelID, RB.roomNumber, RB.bookingDate FROM RoomBookings RB INNER JOIN Users U ON RB.customerID = userID WHERE hotelID = %s AND RB.bookingDate > '%s' AND  RB.bookingDate < '%s';", HotelID, startDate, endDate);
				esql.executeQueryAndPrintResult(HotelBookingHistory);
				System.out.println("Press Enter to return to main menu");
				temp = in.readLine();
				break;
			case 2:
				HotelBookingHistory = String.format("SELECT RB.bookingID, U.name, RB.hotelID, RB.roomNumber, RB.bookingDate FROM RoomBookings RB INNER JOIN Users U ON RB.customerID = userID WHERE hotelID = %s;", HotelID);
				esql.executeQueryAndPrintResult(HotelBookingHistory);
				System.out.println("Press Enter to return to main menu");
				temp = in.readLine();
				break;
			default: 
				System.out.println("Unrecognized choice returning to main menu");
				System.out.println("Press Enter");
				temp = in.readLine();
				break;
		}
		}
		else
		{
			System.out.println("Access Denied: must be a manager at this hotel to access booking information");
			System.out.println("Press Enter to return to main menu");
			temp = in.readLine();
			return;
		}
						
	}
	catch(Exception e)
	{ 
		System.err.println(e.getMessage());
	}
   }
   public static void viewRegularCustomers(Hotel esql) {
	try
	{
		String temp;
		boolean hotelAccess = false;
		System.out.println("Enter hotel ID: ");
		String HotelID = in.readLine();

		String getManagedHotelsQuery = String.format("SELECT HotelID FROM Hotel WHERE managerUserID = %s;", Global.userID);
		List<List<String>> ManagingHotelIDs = esql.executeQueryAndReturnResult(getManagedHotelsQuery);

		for(int i = 0; i < ManagingHotelIDs.size(); i++){
			if(ManagingHotelIDs.get(i).get(0).equals(HotelID)){
				hotelAccess = true;
			}
		}
		if(hotelAccess)
		{
			String getRegularCustomerQuery = String.format("Select U.name From Users U WHERE U.userID = ANY(SELECT customerID FROM( SELECT DISTINCT customerID, COUNT(*) FROM RoomBookings RB WHERE RB.hotelID = %s GROUP BY customerID ORDER BY COUNT(*) LIMIT 5) AS foo);", HotelID);
			esql.executeQueryAndPrintResult(getRegularCustomerQuery);
			System.out.println("Press Enter to return to main menu");
			temp = in.readLine();
			return;
		}
		else
		{
			System.out.println("Access Denied: Must be a manager of this hotel to access");
			System.out.println("Press Enter to return to main menu");
			temp = in.readLine();
			return;
		}
	}
	catch(Exception e)
	{
		System.err.println(e.getMessage());
	}
   }
   public static void placeRoomRepairRequests(Hotel esql) 
   {
	try
	{
		String temp;
		boolean hotelAccess = false;
		System.out.println("Enter hotel ID: ");
		String HotelID = in.readLine();
		
		String getManagedHotelsQuery = String.format("SELECT HotelID FROM Hotel WHERE managerUserID = %s;", Global.userID);
		List<List<String>> ManagingHotelIDs = esql.executeQueryAndReturnResult(getManagedHotelsQuery);

		for(int i = 0; i < ManagingHotelIDs.size(); i++){
			if(ManagingHotelIDs.get(i).get(0).equals(HotelID)){
				hotelAccess = true;
			}
		}	
		if(hotelAccess)
		{
			System.out.println("Enter room number: ");
			String RoomNumber = in.readLine();
			System.out.println("Enter company ID: ");
			String CompanyID = in.readLine();
			
			String newRoomRepairQuery = String.format("INSERT INTO RoomRepairs (companyID, hotelID, roomNumber, repairdate) VALUES (%s , %s , %s, DATE_TRUNC('minute', CURRENT_TIMESTAMP::timestamp));", CompanyID, HotelID, RoomNumber);
			String getRepairID = String.format("SELECT repairID FROM RoomRepairs ORDER BY repairID DESC LIMIT 1");
			esql.executeUpdate(newRoomRepairQuery);
			List<List<String>> RepairID = esql.executeQueryAndReturnResult(getRepairID);

			String logRoomRepairQuery = String.format("INSERT INTO RoomRepairRequests (managerID, repairID) VALUES (%s, %s);", Global.userID, RepairID.get(0).get(0));
			esql.executeUpdate(logRoomRepairQuery);

			System.out.println("Repair request submitted");
			System.out.println("Repair request logged");
			System.out.println("Press Enter to return to main menu");
			temp = in.readLine();
			return;
		}
		else
		{
			System.out.println("Access Denied: must be a manager of this hotel to access");
			System.out.println("Press Enter to return to main menu");
			temp = in.readLine();
			return;
		}
	}
	catch(Exception e)
	{
		System.err.println(e.getMessage());
	}
   }
   public static void viewRoomRepairHistory(Hotel esql)
   {
	try
	{
		String temp;
		boolean hotelAccess = false;
		System.out.println("Enter hotelID: ");
		String HotelID = in.readLine();
		
		String getManagedHotelsQuery = String.format("SELECT HotelID FROM Hotel WHERE managerUserID = %s;", Global.userID);		
		List<List<String>> ManagingHotelIDs = esql.executeQueryAndReturnResult(getManagedHotelsQuery);

		for(int i = 0; i < ManagingHotelIDs.size(); i++){
			if(ManagingHotelIDs.get(i).get(0).equals(HotelID)){
				hotelAccess = true;
			}
		}
		if(hotelAccess)
		{
			String RoomRepairHistoryQuery = String.format("SELECT companyID, hotelID, roomNumber, repairDate FROM RoomRepairs WHERE hotelID = '%s';", HotelID);
			esql.executeQueryAndPrintResult(RoomRepairHistoryQuery);
			System.out.println("Press Enter to return to main menu");
			temp = in.readLine();
			return;
		}
		else
		{
			System.out.println("Access Denied: must be a manager of this hotel to access");
			System.out.println("Press Enter to return to main menu");
			temp = in.readLine();
			return;
		}
	}
	catch(Exception e)
	{
		System.err.println(e.getMessage());
	}
   }


}//end Hotel

