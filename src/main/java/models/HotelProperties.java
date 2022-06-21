package models;

public enum HotelProperties {
	
	HERMOSO(4,2),
	LINDO(3,4),
	BELLO(2,3);

	public final int numRooms;
	public final int roomCapacity;
	
	private HotelProperties(int numRooms, int roomCapacity) {
		this.numRooms = numRooms;
		this.roomCapacity = roomCapacity;
	}
	
	
}
