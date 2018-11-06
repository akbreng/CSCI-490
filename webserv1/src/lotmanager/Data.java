package lotmanager;

import java.util.ArrayList;
//import java.util.Arrays;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.QueryParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.sql.PreparedStatement;
import com.google.gson.Gson;

@Path("ws2")
public class Data{
	//variable for the max size of lot. if reduced any spots that are outside
	//the range will cause index errors
	private int lotsize=20;
	
	/**
	 * This method holds the information for MySQL database
	 * 11-8-2018
	 * By:Aaron Brengelman
	 * @return connection object to the MySQL database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public Connection Connect() throws SQLException, ClassNotFoundException {
		String connectStr="jdbc:mysql://localhost:3306/lotdb";
		String username="root";
		String password="csci330pass";
		String driver="com.mysql.jdbc.Driver";
		Class.forName(driver);
		Connection con = DriverManager.getConnection(
			connectStr, username, password);
		return(con);
	}
		
	/**
	 * This method removes dispensed vehicle from DB
	 * 11-8-2018
	 * By:Aaron Brengelman
	 * @param formFields (received by HTML page)
	 * @return 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Path("/dispense")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")
	public Response dispense(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		int slot=0; //Holds the location of the vehicle when found
		String respond; //String to be used in response builder
		ResponseBuilder rb; //Response to be returned
		Connection con = Connect(); //Creates SQL database connection
		PreparedStatement preStatement; //Declares variable name for SQL statements
		
		//System.out.println("In Dispense");
		
		//Retrieves the vehicleID from the formFields passed from HTML page
		String newVehicleID = formFields.getFirst("VehicleID");
		//System.out.println("VehicleID: " + newVehicleID);
		
		//Retrieves the slotID for the vehicle input.
		preStatement = con.prepareStatement("Select slotID from lot where vehicleID = ?");
		preStatement.setString(1,newVehicleID);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			slot = rs.getInt("slotID");
		}
		
		//if vehicle was found in lot
		if(slot!=0) {
			//System.out.println("Remove vehicleID:"+newVehicleID+" from slot "+slot);
			preStatement = con.prepareStatement("DELETE FROM lot where vehicleID = ?");
			preStatement.setString(1, newVehicleID);			
			preStatement.executeUpdate();
			
			respond = "VehicleID:" + newVehicleID + " removed from SlotID:"+slot;
			rb = Response.ok(respond, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
			
		}
		//returns error message because the vehicle was not found
		else {
			respond = "Vehicle not found in lot";			
			rb = Response.ok(respond, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
		}
	}

	/**
	 * This method adds vehicle to the lot table and returns slot its stored in
	 * 11-8-2018
	 * By:Aaron Brengelman
	 * @param formFields (received by HTML page)
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Path("/park")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")
	public Response park(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		int slot=0; //Holds the location of the vehicle when found
		String vehicleID;
		int i; //used to increment values to add booleans to an array
		ResultSet rs; //SQL statement returns
		String respond; //String to be used in response builder
		ResponseBuilder rb; //Response to be returned
		Connection con = Connect(); //Creates SQL database connection
		PreparedStatement preStatement; //Declares variable name for SQL statements
		
		//System.out.println("In Park");
		
		//Retrieves the vehicleID from the formFields passed from HTML page
		vehicleID = formFields.getFirst("VehicleID");
		//System.out.println("VehicleID: " + newVehicleID);
		
		//checks to see if vehicle is already stored
		preStatement = con.prepareStatement("Select * from lot where vehicleID = ?");
		preStatement.setString(1, vehicleID);
		rs = preStatement.executeQuery();
		if (rs.next()) {
			rb = Response.ok("Vehicle is already stored", MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
		}
		
		
		
		//Retrieves the slotIDs that are currently full
		preStatement = con.prepareStatement("Select slotID from lot");
		rs = preStatement.executeQuery();
		
		//Creates an array of booleans to symbolize whether the spot is used
		i = 0;
		boolean[] slots = new boolean[lotsize+1];
		while(rs.next()) {
			slots[rs.getInt("slotID")-1]=true;
		}
		
		//finds the first spot that is not being used
		while(slot==0 && i<lotsize) {
			if (!slots[i])
			{
				slot = i+1;
			}
			i++;
		}
		
		//System.out.print("add vehicle to: "+slot);
		
		//if spot found 
		if (slot!=0) {
			//insert vehicle and slot into SQL statement and executes
			preStatement = con.prepareStatement("INSERT INTO lot (vehicleID,slotID) values(?,?)");
			preStatement.setString(1, vehicleID);
			preStatement.setInt(2, slot);	
			preStatement.executeUpdate();			
			
			//returns what vehicle was parked where to HTML page
			respond = "parked vehicleID:"+ vehicleID + " in Slot: " + slot;
			rb = Response.ok(respond, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
		}
		
		//if lot is full returns string stating so
		else { 
			rb = Response.ok("lot is full", MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
		}
	}
	

	/**
	 * This method adds vehicle to the vehicle table
	 * 11-8-2018
	 * By:Aaron Brengelman
	 * @param formFields
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Path("/vehicles")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")//theUsername, thePass, theName, theAddress, thePhoneNum
	public Response createVehicles(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		ResponseBuilder rb; //Response to be returned
		Connection con = Connect();
		
		System.out.println("In create Vehicle");
		
		//Retrieves following values from the formFields passed from HTML page
		//theMake, theModel, thePlate, theColor, theProductionYear, theCustomerID
		String newMake = formFields.getFirst("Make");
		System.out.println("name Username: " + newMake);
		String newModel = formFields.getFirst("Model");
		System.out.println("category Model: " + newModel);
		String newPlate = formFields.getFirst("Plate");
		System.out.println("category Name: " + newPlate);
		String newColor = formFields.getFirst("Color");
		System.out.println("category Address: " + newColor);
		String newProductionYear = formFields.getFirst("ProductionYear");
		System.out.println("category ProductionYear: " + newProductionYear);
		String newCustomerID = formFields.getFirst("CustomerID");
		System.out.println("nategory CustomerID: " + newCustomerID);
		
		//System.out.println("to add vehicle with plate #: "+newPlate);

		//insert vehicle object into SQL statement and executes 
		PreparedStatement preStatement = con.prepareStatement
				("INSERT INTO vehicle (make, model, plate, color, productionYear, customerID) values(?,?,?,?,?,?)");
		preStatement.setString(1, newMake);
		preStatement.setString(2, newModel);	
		preStatement.setString(3, newPlate);
		preStatement.setString(4, newColor);	
		preStatement.setString(5, newProductionYear);
		preStatement.setString(6, newCustomerID);	
		preStatement.executeUpdate();
		
		//Returns response stating vehicle was added			
		rb = Response.ok("Vehicle was added to DataBase", MediaType.TEXT_PLAIN);
		rb.status(201);
		return rb.build();
	}
	
	
	/**
	 * This method adds customer to the user table
	 * 11-8-2018
	 * By:Aaron Brengelman
	 * @param formFields
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Path("/customers")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")//theUsername, thePass, theName, theAddress, thePhoneNum
	public Response createCustomer(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		ResponseBuilder rb; //Response to be returned
		Connection con = Connect();
		
		System.out.println("In create customer");
		
		//Retrieves following values from the formFields passed from HTML page
		//theUsername, thePass, theName, theAddress, thePhoneNum
		String newUsername = formFields.getFirst("Username");
		System.out.println("name Username: " + newUsername);
		String newPass = formFields.getFirst("Pass");
		System.out.println("category Pass");
		String newName = formFields.getFirst("Name");
		System.out.println("category Name");
		String newAddress = formFields.getFirst("Address");
		System.out.println("category Address");
		String newPhoneNum = formFields.getFirst("PhoneNum");
		System.out.println("category PhoneNum");
		
		//System.out.println("to add: "+newName);
		
		//insert customer object into SQL statement and executes
		PreparedStatement preStatement = con.prepareStatement
				("INSERT INTO user (type, Username, Pass, Name, Address, PhoneNum) values(?,?,?,?,?,?)");
		preStatement.setString(1, "customer");
		preStatement.setString(2, newUsername);	
		preStatement.setString(3, newPass);
		preStatement.setString(4, newName);	
		preStatement.setString(5, newAddress);
		preStatement.setString(6, newPhoneNum);	
		preStatement.executeUpdate();
		
		//Returns response stating vehicle was added			
		rb = Response.ok("Customer was added to DataBase", MediaType.TEXT_PLAIN);
		rb.status(201);
		return rb.build();
	}

	/**
	 * This method returns vehicles from DB
	 * 11-8-2018
	 * By:Aaron Brengelman
	 * @return Gson vehicle object
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Path("/vehicles")
	@GET
	@Produces("text/plain")
	public String getVehicle() throws SQLException, ClassNotFoundException {
		
		Connection con = Connect();

		/* Creates a statement object to be executed on
		 * the attached database.
		 */
		PreparedStatement pstmt = con.prepareStatement("SELECT * FROM vehicle");

		/* Executes a database query and returns the results
		 * as a ResultSet object
		 */
		ResultSet rs = pstmt.executeQuery();

		/* This snippet shows how to parse a ResultSet object.
		 * it loops through the object sort of like
		 * a linked list, and use the getX methods to get data
		 * from the current row. Each time you call rs.next()
		 * it advances to the next row returned.
		 * The result variable is just used to compile all the
		 * data into one string.
		 */
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
		while(rs.next()){
			int theId = rs.getInt("vehicleID");
			String theMake = rs.getString("make");
			String theModel = rs.getString("model");
			String thePlate = rs.getString("plate");
			String theColor = rs.getString("color");
			int theYear = rs.getInt("productionYear");
			int theCustId = rs.getInt("customerID");
			vehicles.add(new Vehicle(theId, theMake, theModel, thePlate, theColor, theYear, theCustId));
		}
		Gson GsonObj = new Gson();
		return GsonObj.toJson(vehicles);	
	}
	
	/**
	 * This method returns customers from DB
	 * 11-8-2018
	 * By:Aaron Brengelman
	 * @return Gson customer object
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@Path("/customers")
	@GET
	@Produces("text/plain")
	public String getCustomer() throws SQLException, ClassNotFoundException {
		
		Connection con = Connect();

		/* Creates a statement object to be executed on
		 * the attached database.
		 */
		PreparedStatement pstmt = con.prepareStatement("SELECT * FROM user");

		/* Executes a database query and returns the results
		 * as a ResultSet object
		 */
		ResultSet rs = pstmt.executeQuery();

		/* This snippet shows how to parse a ResultSet object.
		 * Basically, you loop through the object sort of like
		 * a linked list, and use the getX methods to get data
		 * from the current row. Each time you call rs.next()
		 * it advances to the next row returned.
		 * The result variable is just used to compile all the
		 * data into one string.
		 */

		ArrayList<User> customers = new ArrayList<User>();
		while(rs.next()){
			int theId = rs.getInt("userID");
			String theType = rs.getString("type");
			String theName = rs.getString("name");
			String theAddress = rs.getString("address");
			String theUsername = rs.getString("username");
			String thePass = rs.getString("pass");
			int thePhoneNum = rs.getInt("phoneNum");
			customers.add(new User(theId, theUsername, thePass, theType, theName, theAddress, thePhoneNum));
		}
		Gson GsonObj = new Gson();
		return GsonObj.toJson(customers);	
	}
}
