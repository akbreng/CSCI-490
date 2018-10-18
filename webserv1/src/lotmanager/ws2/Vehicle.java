package lotmanager.ws2;

public class Vehicle {
	private int id, year, custId;
	private String make, model, plate, color;
	public Vehicle(String make, String model, String plate, String color, int year, int custId){
		this.make = make;
		this.model = model;
		this.plate = plate;
		this.color = color;
		this.year = year;
		this.custId =custId;
	}
	
	public Vehicle(int id, String make, String model, String plate, String color, int year, int custId){
		this.id = id;
		this.make = make;
		this.model = model;
		this.plate = plate;
		this.color = color;
		this.year = year;
		this.custId =custId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getCustId() {
		return custId;
	}
	public void setCustId(int custId) {
		this.custId = custId;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
