package models;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Room {
	
	private ObjectId id;
	private int number;
	@BsonProperty(value="has_bath")
	private boolean hasBath;
	private int capacity;
	
	public Room(int number, boolean hasBath, int capacity) {
		this.number = number;
		this.hasBath = hasBath;
		this.capacity = capacity;
	}

	public Room()
	{
		
	}
	
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isHasBath() {
		return hasBath;
	}

	public void setHasBath(boolean hasBath) {
		this.hasBath = hasBath;
	}
	
	

}
