package models;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Hotel {
	
	private ObjectId id;
	private HotelProperties name;
	private Address address;
	private int rank;
	private List<Room> rooms;
	@BsonProperty(value="price_per_night")
	private double pricePerNight;
	private List<Order> orders;
	
	public Hotel()
	{
		
	}
	
	public Hotel(HotelProperties name, Address address, int rank, List<Room> rooms, double pricePerNight) {
		setName(name);
		setAddress(address);
		setRank(rank);
		setRooms(rooms);
		setPricePerNight(pricePerNight);
		this.orders = new ArrayList<>();
	}
	
	
	
	public void setId(ObjectId id) {
		this.id = id;
	}

	public void addOrder(Order order)
	{
		this.orders.add(order);
	}
	
	public HotelProperties getName() {
		return name;
	}
	public void setName(HotelProperties name) {
		this.name = name;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public List<Room> getRooms() {
		return rooms;
	}
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	public double getPricePerNight() {
		return pricePerNight;
	}
	public void setPricePerNight(double pricePerNight) {
		this.pricePerNight = pricePerNight;
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Hotel [id=" + id + ", name=" + name + ", address=" + address + ", rank=" + rank + ", rooms=" + rooms
				+ ", pricePerNight=" + pricePerNight + ", orders=" + orders + "]";
	}
	
	

}
