package lotmanager;

import java.util.ArrayList;
import java.util.Arrays;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

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
import java.sql.Statement;
import java.sql.PreparedStatement;
import com.google.gson.Gson;

@Path("ws2")
public class Data{
		
	@Path("/dispense")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")
	public Response dispense(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		int slot=0;
		System.out.println("In Dispense");
		
		String newVehicleID = formFields.getFirst("VehicleID");
		System.out.println("VehicleID: " + newVehicleID);
		
		Connection con = Connect();

		PreparedStatement preStatement;
		
		String retrieveSlot = "Select slotID from lot where vehicleID = ?";
		preStatement = con.prepareStatement(retrieveSlot);
		preStatement.setString(1,newVehicleID);
		ResultSet rs = preStatement.executeQuery();
		if (rs.next()) {
			slot = rs.getInt("slotID");
		}
		
		if(slot!=0) {
			System.out.println("Remove vehicleID:"+newVehicleID+" from slot "+slot);
			String SQLstate = "DELETE FROM lot where vehicleID = ?";
			preStatement = con.prepareStatement(SQLstate);
			preStatement.setString(1, newVehicleID);			
			preStatement.executeUpdate();
			
			String er = "VehicleID:" + newVehicleID + " removed from SlotID:"+slot;
			ResponseBuilder rb = Response.ok(er, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
			
		}
		else {
			String er = "Vehicle not found in lot";
			
			ResponseBuilder rb = Response.ok(er, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
		}
	}

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
	
	@Path("/park")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")
	public Response park(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		
		System.out.println("In Park");
		int lotsize = 20;
		
		String newVehicleID = formFields.getFirst("VehicleID");
		System.out.println("category VehicleID: " + newVehicleID);
		
		Connection con = Connect();

		
		System.out.println("to add: "+newVehicleID);
		
		PreparedStatement preStatement;
		
		String retrieveSlot = "Select slotID from lot";
		preStatement = con.prepareStatement(retrieveSlot);

		ResultSet rs = preStatement.executeQuery();
		
		boolean found = false; 
		int i = 0, slot=0;
		boolean[] slots = new boolean[lotsize+1];
		while(rs.next()) {
			slots[rs.getInt("slotID")-1]=true;
		}
		
		while(!found && i<lotsize) {
			if (!slots[i])
			{
				slot = i+1;
				found = true;
			}
			i++;
		}
		
		System.out.print("add vehicle to: "+slot+"   "+found);
		
		if (found) {
			String SQLstate = "INSERT INTO lot (vehicleID,slotID) values(?,?)";
			System.out.println(SQLstate);
			preStatement = con.prepareStatement(SQLstate);
			preStatement.setString(1, newVehicleID);
			preStatement.setInt(2, slot);	
					
		int res = preStatement.executeUpdate();
		
		System.out.println("Result is : "+res);
		
		if(res==1) {
			String retrieveStmt = "Select * from lot where vehicleID=? and slotID=?";
			preStatement = con.prepareStatement(retrieveStmt);
			preStatement.setString(1, newVehicleID);	
			preStatement.setInt(2, slot);
			
			rs = preStatement.executeQuery();
					
			String result="";
			int count = 0;
			int MAX = 100;
			Slot[] ingArray = new Slot[MAX];
			
			int theVehID=0,theSlotID=0;
			while (rs.next()) {
				theVehID = rs.getInt("vehicleID");
				theSlotID = rs.getInt("slotID");
				Slot ing = new Slot(theVehID, theSlotID);
				System.out.println(ing);
				ingArray[count] = ing;
				count++;
			}
			
			ingArray = Arrays.copyOf(ingArray, count);
			
			result = "parked vehicleID:"+ theVehID + " in Slot: " + theSlotID;
			System.out.print(result);
			ResponseBuilder rb = Response.ok(result, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
			//return result;
			 
		}else {
			return Response.status(700).build();
		}
		}
		else { 
			String result = "lot is full";
			
			ResponseBuilder rb = Response.ok(result, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
		}
	}
	
	@Path("/vehicles")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")//theUsername, thePass, theName, theAddress, thePhoneNum
	public Response createVehicles(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		System.out.println("In create Vehicle");
		
		
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
		
		Connection con = Connect();

		
		System.out.println("to add: "+newPlate);

		String SQLstate = "INSERT INTO vehicle (make, model, plate, color, productionYear, customerID) values(?,?,?,?,?,?)";
		System.out.println(SQLstate);
		PreparedStatement preStatement = con.prepareStatement(SQLstate);
		preStatement.setString(1, newMake);
		preStatement.setString(2, newModel);	
		preStatement.setString(3, newPlate);
		preStatement.setString(4, newColor);	
		preStatement.setString(5, newProductionYear);
		preStatement.setString(6, newCustomerID);	
		
		
		
		int res = preStatement.executeUpdate();
		
		System.out.println("Result is : "+res);
		
		if(res==1) {
			String retrieveStmt = "Select * from vehicle where customerID=? and plate=?";
			preStatement = con.prepareStatement(retrieveStmt);
			preStatement.setString(1, newCustomerID);	
			preStatement.setString(2, newPlate);
			
			ResultSet rs = preStatement.executeQuery();
					
			String result="";
			int count = 0;
			int MAX = 100;
			Vehicle[] ingArray = new Vehicle[MAX];
			
			while (rs.next()) {
				int theID = rs.getInt("vehicleID");
				String theMake = rs.getString("make");
				String theModel = rs.getString("model");
				String thePlate = rs.getString("plate");
				String theColor = rs.getString("color");
				int theProductionYear = rs.getInt("productionYear");
				int theCustomerID = rs.getInt("customerID");
				Vehicle ing = new Vehicle(theID, theMake, theModel, thePlate, theColor, theProductionYear, theCustomerID);
				System.out.println(ing);
				ingArray[count] = ing;
				count++;
			}
			
			ingArray = Arrays.copyOf(ingArray, count);
			
			Gson theGsonobj = new Gson();
			result = theGsonobj.toJson(ingArray);
			System.out.println("the json: \n" + result);
			
			ResponseBuilder rb = Response.ok(result, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
			//return result;
		}
		else {
			Gson theGsonobj = new Gson();
			Vehicle[] blankIngArray = new Vehicle[1];
			blankIngArray[0] = new Vehicle(0,"none","none","none","none",0,0);
			String blankResult = theGsonobj.toJson(blankIngArray);
			//return blankResult;		
			return Response.status(700).build();
		}
			
	}
	
	@Path("/customers")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")//theUsername, thePass, theName, theAddress, thePhoneNum
	public Response createCustomer(MultivaluedMap<String,String>formFields) throws SQLException, ClassNotFoundException{
		System.out.println("In create customer");
		
		
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
		
		Connection con = Connect();

		
		System.out.println("to add: "+newName);
		                                      //(type, name, address, phoneNum, username, pass)
		String SQLstate = "INSERT INTO user (type, Username, Pass, Name, Address, PhoneNum) values(?,?,?,?,?,?)";
		System.out.println(SQLstate);
		PreparedStatement preStatement = con.prepareStatement(SQLstate);
		preStatement.setString(1, "customer");
		preStatement.setString(2, newUsername);	
		preStatement.setString(3, newPass);
		preStatement.setString(4, newName);	
		preStatement.setString(5, newAddress);
		preStatement.setString(6, newPhoneNum);	
		
		
		
		int res = preStatement.executeUpdate();
		
		System.out.println("Result is : "+res);
		
		if(res==1) {
			String retrieveStmt = "Select * from user where Username=? and Pass=?";
			preStatement = con.prepareStatement(retrieveStmt);
			preStatement.setString(1, newUsername);	
			preStatement.setString(2, newPass);
			
			ResultSet rs = preStatement.executeQuery();
					
			String result="";
			int count = 0;
			int MAX = 100;
			User[] ingArray = new User[MAX];
			
			while (rs.next()) {
				int theId = rs.getInt("userID");
				String theType = rs.getString("type");
				String theName = rs.getString("name");
				String theAddress = rs.getString("address");
				String theUsername = rs.getString("username");
				String thePass = rs.getString("pass");
				int thePhoneNum = rs.getInt("phoneNum");
				User ing = new User(theId, theUsername, thePass, theType, theName, theAddress, thePhoneNum);
				System.out.println(ing);
				ingArray[count] = ing;
				count++;
			}
			
			ingArray = Arrays.copyOf(ingArray, count);
			
			Gson theGsonobj = new Gson();
			result = theGsonobj.toJson(ingArray);
			System.out.println("the json: \n" + result);
			
			ResponseBuilder rb = Response.ok(result, MediaType.TEXT_PLAIN);
			rb.status(201);
			return rb.build();
			//return result;
		}
		else {
			Gson theGsonobj = new Gson();
			User[] blankIngArray = new User[1];
			blankIngArray[0] = new User(0,"none","none","none","none","none",0);
			String blankResult = theGsonobj.toJson(blankIngArray);
			//return blankResult;		
			return Response.status(700).build();
		}
			
	}

	
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
		 * Basically, you loop through the object sort of like
		 * a linked list, and use the getX methods to get data
		 * from the current row. Each time you call rs.next()
		 * it advances to the next row returned.
		 * The result variable is just used to compile all the
		 * data into one string.
		 */
		 
		 //ArrayList<Matrices> list = new ArrayList<Matrices>();
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

		 //ArrayList<Matrices> list = new ArrayList<Matrices>();
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
