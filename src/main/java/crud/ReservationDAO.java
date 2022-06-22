package crud;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import static com.mongodb.client.model.Updates.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import models.Customer;
import models.Hotel;
import models.Order;

public class ReservationDAO {
	
	private MongoDatabase DB;
	private MongoCollection<Hotel> hotels;
	private MongoCollection<Customer> customers;
	private MongoCollection<Order> orders;
	
	public ReservationDAO(MongoDatabase DB)
	{
		this.DB = DB;
		this.hotels = DB.getCollection("hotels", Hotel.class);
		this.customers = DB.getCollection("customers", Customer.class);
		this.orders = DB.getCollection("orders", Order.class);
		
	}
	
	public List<Hotel> getAllHotels()
	{
		return hotels.find().into(new ArrayList<>());
	}
	
	public List<Order> getAllOrders()
	{
		return orders.find().into(new ArrayList<>());
	}
	
	public List<Customer> getAllCustomers()
	{
		return customers.find().into(new ArrayList<>());
	}
	
	public Hotel getHotelById(ObjectId id)
	{
		Hotel current = hotels.find(Filters.eq("_id",id)).first();
		return current;
	}
	
	public Customer getCustomerById(ObjectId id)
	{
		Customer current =  customers.find(Filters.eq("_id",id)).first();
		return current;
	}
	
	public InsertOneResult addNewOrder(Order order)
	{
		Hotel theHotel = getHotelById(order.getHotelId());
		InsertOneResult result = null;
		if (isHotelAvailable(order.getHotelId(), order.getStartDate(), order.getNumPeople()))
		{
			order.setTotalPrice(order.getNumNights() * theHotel.getPricePerNight());
			result = orders.insertOne(order);
			ObjectId orderId = result.getInsertedId().asObjectId().getValue();
			order.setId(orderId);
			Bson updateHotel = addToSet("orders", order);
			hotels.updateOne(Filters.eq("_id",theHotel.getId()), updateHotel);
			Bson updateCustomer = addToSet("orders", order);
			customers.updateOne(Filters.eq("_id", order.getCustomerId()), updateCustomer);
			
		}

		return result;
	}
	
	public boolean isHotelAvailable(ObjectId hotelId, LocalDate date, int numPeople)
	{
		Hotel theHotel = getHotelById(hotelId);
		if (theHotel.getName().roomCapacity < numPeople)
			return false;
		List<Order> orders = theHotel.getOrders();
		int numRooms = theHotel.getName().numRooms;
		int countAvailable = numRooms;
		for (Order order: orders)
		{
			if (order.getStartDate().isBefore(date) && order.getStartDate().plusDays(order.getNumNights()).isAfter(date))
				countAvailable--;
		}
		return countAvailable > 0;

	}
	
	public List<Order> getOrdersByCustomer(ObjectId customerId)
	{
		List<Order> custOrders = orders.find(Filters.eq("customer_id", customerId)).into(new ArrayList<>());
		return custOrders;
	}
	
	public List<Hotel> getHotelsByCity(String city)
	{
		List<Hotel> hotelsInCity = hotels.find(Filters.eq("address.city", city)).into(new ArrayList<>());
		return hotelsInCity;
	}
	
	public void cancelOrder(ObjectId orderId)
	{
		Order theOrder = orders.findOneAndDelete(Filters.eq("_id", orderId));
		Bson updateHotel = pull("orders",theOrder);
		hotels.updateOne(Filters.eq("_id", theOrder.getHotelId()), updateHotel);
		Bson updateCustomer = pull("orders", theOrder);
		customers.updateOne(Filters.eq("_id", theOrder.getCustomerId()), updateCustomer);
	}

}
