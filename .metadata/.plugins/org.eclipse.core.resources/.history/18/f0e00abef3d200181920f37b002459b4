package lotmanager.ws2;

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
	/*
	@Path("/customers")
	@POST
	@Produces("text/plain")
	@Consumes("application/x-www-form-urlencoded")
	public Response createCustomer(MultivaluedMap<String,String> formFields) throws SQLException, ClassNotFoundException{
		System.out.println("In create Ingredient");
		
		String newName = formFields.getFirst("name");
		System.out.println("name stored");
		String newCategory = formFields.getFirst("category");
		System.out.println("category stored");
		
		
		String connectStr="jdbc:mysql://localhost:3306/fooddb";
		String username="root";
		String password="csci330pass";
		String driver="com.mysql.jdbc.Driver";
		Class.forName(driver);
		Connection con = DriverManager.getConnection(
				connectStr, username, password);

		
		System.out.println("to add: "+newName + " " + newCategory);
		
		String SQLAll = "INSERT INTO ingredient (name, category, id) values(?,?,null)";
		PreparedStatement preStatement = con.prepareStatement(SQLAll);
		preStatement.setString(1, newName);	
		preStatement.setString(2, newCategory);
		
		int res = preStatement.executeUpdate();
		
		System.out.println("Result is : "+res);
		
		if(res==1) {
			PreparedStatement retrieveStmt = con.prepareStatement("Select * from ingredient where name=? and category=?");
			retrieveStmt.setString(1,  newName);;
			retrieveStmt.setString(2, newCategory);
			ResultSet rs = retrieveStmt.executeQuery();
			
			String result="";
			int count = 0;
			int MAX = 100;
			Ingredient[] ingArray = new Ingredient[MAX];
			
			while (rs.next()) {
				int theId = rs.getInt("id");
				String theName = rs.getString("name");
				String theCategory = rs.getString("category");
				Ingredient ing = new Ingredient(theId, theName, theCategory);
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
			Ingredient[] blankIngArray = new Ingredient[1];
			blankIngArray[0] = new Ingredient(0,"none","none");
			String blankResult = theGsonobj.toJson(blankIngArray);
			//return blankResult;		
			return Response.status(700).build();
		}
			
	}
}
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
