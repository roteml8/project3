package utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;

import crud.ReservationDAO;
import models.Address;
import models.Customer;
import models.Hotel;
import models.HotelProperties;
import models.Order;
import models.Room;

public class DBInitializer {
	

	public static InsertManyResult createCustomersCollection(MongoDatabase DB)
	{
		MongoCollection<Document> collection = DB.getCollection("customers");
		List<Customer> customers = Arrays.asList(new Customer("Rotem","Levi","Israel"),
				new Customer("Yaron","Shender","Israel"),
				new Customer("Ohad","Levi","Mexico"));
		List<Document> customerDocs = new ArrayList<>();
		for (Customer c: customers)
		{
			customerDocs.add(new Document("first_name",c.getFirstName())
					.append("last_name", c.getLastName())
					.append("country", c.getCountry()));
		}
		InsertManyResult result = collection.insertMany(customerDocs);
		return result;
	}
	
	public static InsertManyResult createHotelsCollection(MongoDatabase DB)
	{
		Address add1 = new Address("Dov Karmi", 1, "Tel Aviv", "Israel");
		Address add2 = new Address("Burla", 36, "Akko", "Israel");
		Address add3 = new Address("Sesame St",50, "Rome", "Italy");
		
		List<Room> roomsForBello = Arrays.asList(new Room(1,true), new Room(2,false));
		Hotel bello = new Hotel(HotelProperties.BELLO, add1, 4, roomsForBello, 150);
		
		List<Room> roomsForLindo = Arrays.asList(new Room(1,true), new Room(2,true), new Room(3,false));
		Hotel lindo = new Hotel(HotelProperties.LINDO, add2, 5, roomsForLindo, 200);
		
		List<Room> roomsForHermoso = Arrays.asList(new Room(1,true), new Room(2,false), new Room(3, false), new Room(4, true));
		Hotel hermoso = new Hotel(HotelProperties.HERMOSO, add3, 5, roomsForHermoso, 220);
		
		List<Hotel> hotels = Arrays.asList(bello, lindo, hermoso);
		List<Document> hotelDocs = new ArrayList<>();
		MongoCollection<Document> hotelsCol = DB.getCollection("hotels");
		for (Hotel h: hotels)
		{
			hotelDocs.add(new Document("name", h.getName()).append("address", h.getAddress())
					.append("rank", h.getRank()).append("rooms", h.getRooms())
					.append("price_per_night", h.getPricePerNight()).append("orders", h.getOrders()));
		}
		InsertManyResult result = hotelsCol.insertMany(hotelDocs);
		return result;

	}
	
	public static void createOrdersCollection(MongoDatabase DB)
	{
		ReservationDAO dao = new ReservationDAO(DB);
		List<Hotel> hotels = dao.getAllHotels();
		List<Customer> customers = dao.getAllCustomers();
		LocalDate today = LocalDate.now();
		final int NUM_ORDERS = 5;
		for (int i=0; i<NUM_ORDERS; i++)
		{
			int numHotel = (int)(Math.random()*hotels.size());
			int numCust = (int)(Math.random()*customers.size());
			ObjectId hotelId = hotels.get(numHotel).getId();
			ObjectId custId = customers.get(numCust).getId();
			int randomYear = (int) (Math.random()*3 + 2023);
			int randomMonth = (int) (Math.random()*12 + 1);
			int randomDay = (int) (Math.random()*28 +1);
			LocalDate date = LocalDate.of(randomYear, randomMonth, randomDay);
			int randomNumPeople = (int) (Math.random()*4 + 1);
			int randomNumNights = (int) (Math.random()*7 + 1);
			Order order = new Order(hotelId, custId, today, date, randomNumNights, randomNumPeople);
			dao.addNewOrder(order);
		}
	}

}
