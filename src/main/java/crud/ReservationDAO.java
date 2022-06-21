package crud;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import static com.mongodb.client.model.Updates.*;

import java.time.LocalDate;
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
	
	public Hotel getHotelById(ObjectId id)
	{
		Hotel current = hotels.find(Filters.eq("id",id)).first();
		return current;
	}
	
	public Customer getCustomerById(ObjectId id)
	{
		Customer current =  customers.find(Filters.eq("id",id)).first();
		return current;
	}
	
	//TODO
	public InsertOneResult addNewOrder(Order order)
	{
		Hotel theHotel = getHotelById(order.getHotelId());
		InsertOneResult result = null;
		if (isHotelAvailable(order.getHotelId(), order.getStartDate()))
		{
			order.setTotalPrice(order.getNumNights() * theHotel.getPricePerNight());
			Bson updateHotel = addToSet("orders", order);
			hotels.updateOne(Filters.eq("id",theHotel.getId()), updateHotel);
			Bson updateCustomer = addToSet("orders", order);
			customers.updateOne(Filters.eq("id", order.getCustomerId()), updateCustomer);
			result = orders.insertOne(order);
		}

		return result;
	}
	
	public boolean isHotelAvailable(ObjectId hotelId, LocalDate date)
	{
		Hotel theHotel = getHotelById(hotelId);
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

}
