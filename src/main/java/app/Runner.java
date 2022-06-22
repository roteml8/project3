package app;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import crud.ReservationDAO;
import models.Hotel;
import models.Order;
import utils.DBInitializer;
import utils.MyConnectionString;

public class Runner {

	public static void main(String[] args) {
		
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.ERROR);
		CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
		CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
		
		ConnectionString connectionString = MyConnectionString.uri();
		MongoClientSettings settings = MongoClientSettings.builder()
		        .applyConnectionString(connectionString)
		        .serverApi(ServerApi.builder()
		            .version(ServerApiVersion.V1)
		            .build())
		        .codecRegistry(codecRegistry)
		        .build();
		
		try (MongoClient mongoClient = MongoClients.create(settings);)
		{		
			MongoDatabase DB = mongoClient.getDatabase("Reservations");
//			DBInitializer.createCustomersCollection(DB);
//			DBInitializer.createHotelsCollection(DB);
//			DBInitializer.createOrdersCollection(DB);
			ReservationDAO dao = new ReservationDAO(DB);
//			List<Order> myOrders = dao.getOrdersByCustomer(new ObjectId("62b1d2cdc812d6268b6f7407"));
//			myOrders.forEach(System.out::println);
//			List<Hotel> hotelsInTlv = dao.getHotelsByCity("Tel Aviv");
//			hotelsInTlv.forEach(System.out::println);
//			dao.cancelOrder(new ObjectId("62b20550d3ef192212d64d05"));
//			ObjectId orderToCancel = dao.getAllOrders().get(0).getId();
//			dao.cancelOrder(orderToCancel);
//			dao.displayHotelsByIncomeDesc();
			dao.displayAllOrdersTotalPrice();
		}
	}

}
