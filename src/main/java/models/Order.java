package models;

import java.time.LocalDate;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Order {

	private ObjectId id;
	@BsonProperty(value="hotel_id")
	private ObjectId hotelId;
	@BsonProperty(value="customer_id")
	private ObjectId customerId;
	@BsonProperty(value="order_date")
	private LocalDate orderDate;
	@BsonProperty(value="start_date")
	private LocalDate startDate;
	@BsonProperty(value="num_nights")
	private int numNights;
	@BsonProperty(value="total_price")
	private double totalPrice;
	@BsonProperty(value="num_people")
	private int numPeople;
	
	
	public Order()
	{
		
	}
	
	
	public Order(ObjectId hotelId, ObjectId customerId, LocalDate orderDate, LocalDate startDate, int numNights, int numPeople) {
		setHotelId(hotelId);
		setCustomerId(customerId);
		setOrderDate(orderDate);
		setStartDate(startDate);
		setNumNights(numNights);
		setNumPeople(numPeople);
	}
	
	
	
	public int getNumPeople() {
		return numPeople;
	}


	public void setNumPeople(int numPeople) {
		this.numPeople = numPeople;
	}


	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public ObjectId getHotelId() {
		return hotelId;
	}
	public void setHotelId(ObjectId hotelId) {
		this.hotelId = hotelId;
	}
	public ObjectId getCustomerId() {
		return customerId;
	}
	public void setCustomerId(ObjectId customerId) {
		this.customerId = customerId;
	}
	public LocalDate getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public int getNumNights() {
		return numNights;
	}
	public void setNumNights(int numNights) {
		this.numNights = numNights;
	}
	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	

	
	
}
