package lotmanager;

public class Slot {
	private int vehicleID, slotID;
	public Slot(int vehicleID, int slotID) {
		this.vehicleID=vehicleID;
		this.slotID=slotID;
		
	}
	public int getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(int vehicleID) {
		this.vehicleID = vehicleID;
	}

	public int getSlotID() {
		return slotID;
	}

	public void setSlotID(int slotID) {
		this.slotID = slotID;
	}
}
