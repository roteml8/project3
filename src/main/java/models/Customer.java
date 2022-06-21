package models;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Customer {

	private ObjectId id;
	@BsonProperty(value="first_name")
	private String firstName;
	@BsonProperty(value="last_name")
	private String lastName;
	private String country;
	private List<Order> orders;
	
	
	
	public Customer(ObjectId id, String firstName, String lastName, String country, List<Order> orders) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.country = country;
		this.orders = orders;
	}

	public Customer(String firstName, String lastName, String country) {
		setFirstName(firstName);
		setLastName(lastName);
		setCountry(country);
		this.orders = new ArrayList<>();
	}
	
	public Customer()
	{
		
	}

	public void addOrder(Order order)
	{
		orders.add(order);
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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
	
	
	
	
}
