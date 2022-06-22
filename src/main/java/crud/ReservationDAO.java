package crud;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import static com.mongodb.client.model.Updates.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Accumulators.sum;
import models.Customer;
import models.Hotel;
import models.Order;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.first;

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
		ObjectId hotelId = order.getHotelId();
		Hotel theHotel = getHotelById(hotelId);
		InsertOneResult result = null;
		if (isHotelAvailable(hotelId, order.getStartDate(), order.getNumPeople()))
		{
			order.setTotalPrice(order.getNumNights() * theHotel.getPricePerNight());
			result = orders.insertOne(order);
			ObjectId orderId = result.getInsertedId().asObjectId().getValue();
			order.setId(orderId);
			Bson updateHotel = addToSet("orders", order);
			hotels.updateOne(Filters.eq("_id",hotelId), updateHotel);
			Bson updateCustomer = addToSet("orders", order);
			customers.updateOne(Filters.eq("_id", order.getCustomerId()), updateCustomer);
			
		}

		return result;
	}
	
	public boolean isHotelAvailable2(ObjectId hotelId, LocalDate date, int numPeople)
	{
		Hotel theHotel = getHotelById(hotelId);
		if (theHotel.getName().roomCapacity < numPeople)
			return false;
		List<Order> hotelOrders = orders.find(Filters.eq("hotel_id", hotelId)).into(new ArrayList<>());
		int numRooms = theHotel.getName().numRooms;
		int countAvailable = numRooms;
		for (Order order: hotelOrders)
		{
			LocalDate orderEndDate = order.getStartDate().plusDays(order.getNumNights());
			if (order.getStartDate().equals(date) || (order.getStartDate().isBefore(date) && orderEndDate.isAfter(date)))
				countAvailable--;
		}
		return countAvailable > 0;

	}
	
	public boolean isHotelAvailable(ObjectId hotelId, LocalDate date, int numPeople)
	{
		Hotel theHotel = getHotelById(hotelId);
		if (theHotel.getName().roomCapacity < numPeople) 
			return false;
		
		MongoCollection<Document> orderDocs = DB.getCollection("orders");
		Bson match = match(Filters.eq("hotel_id", hotelId));
		
		Bson project = project(Projections.fields(Projections.excludeId(), 
				Projections.include("start_date", "num_nights")));
		AggregateIterable<Document> results = orderDocs.aggregate(Arrays.asList(match, project));
		int roomsAvailable = theHotel.getName().numRooms;

		for (Document d: results)
		{
			Date current = d.getDate("start_date");
			LocalDate startDate = new java.sql.Date(current.getTime()).toLocalDate();
			int nights = d.getInteger("num_nights");
			LocalDate endDate = startDate.plusDays(nights);
			if (startDate.equals(date) || (startDate.isBefore(date) && endDate.isAfter(date)))
				roomsAvailable--;
		}
		
		return roomsAvailable > 0;

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

	public void displayHotelsByIncomeDesc()
	{
		Bson unwind = Aggregates.unwind("$orders");
		Bson group = group("$_id", sum("totalIncome", "$orders.total_price"), first("name", "$name"));
		Bson sort = sort(Sorts.descending("totalIncome"));
		Bson project = project(Projections.fields(
				Projections.include("_id", "name", "totalIncome")));
		MongoCollection<Document> hotelDocs = DB.getCollection("hotels");
		List<Document> results = hotelDocs.aggregate(Arrays.asList(unwind, group, project, sort))
				.into(new ArrayList<>());
		results.forEach(printDocuments());
		
	}
	
	public void displayAllOrdersTotalPrice()
	{
		MongoCollection<Document> orderDocs = DB.getCollection("orders");
		Bson group = group(null, sum("totalOrdersPrice", "$total_price"));
		Bson project = project(Projections.fields(Projections.excludeId(),
				Projections.include("totalOrdersPrice")));
		List<Document> result = orderDocs.aggregate(Arrays.asList(group, project)).into(new ArrayList<>());
		result.forEach(printDocuments());
	}
	
	
	public void displayTopMostProfitableMonths(int top)
	{
		MongoCollection<Document> orderDocs = DB.getCollection("orders");
		
		Bson project = project(Projections.fields(Projections.excludeId(), 
				Projections.include("start_date", "total_price")));
		AggregateIterable<Document> results = orderDocs.aggregate(Arrays.asList(project));
		
		Map<Month, Double> profitsByMonth = new HashMap<>();

		for (Document d: results)
		{
			Date current = d.getDate("start_date");
			LocalDate startDate = new java.sql.Date(current.getTime()).toLocalDate();
			Month month = startDate.getMonth();
			double price = d.getDouble("total_price");
			if (profitsByMonth.containsKey(month))
				profitsByMonth.put(month, profitsByMonth.get(month)+price);
			else
				profitsByMonth.put(month,price);
		}
		
		List<Entry<Month, Double>> sortedDesc = profitsByMonth.entrySet().stream().sorted((t1,t2)->Double.compare(t2.getValue(), t1.getValue())).collect(Collectors.toList());

		for (int i=0; i<top; i++)
			System.out.println(sortedDesc.get(i));
	}
	
	private static Consumer<Document> printDocuments() {
		return doc -> System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));
	}
	
}
