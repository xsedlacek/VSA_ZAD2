package sk.stuba.fei.uim.vsa.pr2;


import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
        import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.services.*;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Project2 {

    public static final Logger LOGGER = Logger.getLogger(Project2.class.getName());
    public static final String BASE_URI = "http://localhost/";
    public static final int PORT = 8080;
    public static final Class<? extends Application> APPLICATION_CLASS = Project2Application.class;

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Shutting down the application...");
                    server.shutdownNow();
                    System.out.println("Exiting");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, null, e);
                }
            }));
            System.out.println("Last steps of setting up the application...");
            postStart();
            System.out.println(String.format("Application started.%nStop the application using CRL+C"));
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public static HttpServer startServer() {
        final ResourceConfig config = ResourceConfig.forApplicationClass(APPLICATION_CLASS);
        config.register(JacksonFeature.class);
        URI baseUri = UriBuilder.fromUri(BASE_URI).port(PORT).build();
        LOGGER.info("Starting Grizzly2 HTTP server...");
        LOGGER.info("Server listening on " + BASE_URI + ":" + PORT);
        return GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }

    public static void postStart() {
        CarParkService service = new CarParkService();
        CarParkFloorService service1 = new CarParkFloorService();
        ParkingSpotService service2 = new ParkingSpotService();
        CustomerService service3 = new CustomerService();
        CarService service4 = new CarService();
        ReservationService service5 = new ReservationService();
        HolidayService service6 = new HolidayService();
        service.createCarPark("park","Bratislava",1);
        service.createCarPark("park2","Bratislava",3);
        service1.createCarParkFloor(1L,"A");
        service1.createCarParkFloor(2L,"C");
        service1.createCarParkFloor(2L,"B");
        service2.createParkingSpot(2L,"C","A");
        service2.createParkingSpot(2L,"C","B");
        service2.createParkingSpot(2L,"C","C");
        service2.createParkingSpot(2L,"C","C");
        service2.createParkingSpot(2L,"B","E");
        service2.createParkingSpot(2L,"B","F");
        service2.createParkingSpot(2L,"B","G");
        service2.createParkingSpot(2L,"B","A");
        service2.createParkingSpot(2L,"B","I");
        service2.createParkingSpot(2L,"B","J");
        service2.createParkingSpot(1L,"A","A");
        service2.createParkingSpot(1L,"A","B");
        service2.createParkingSpot(1L,"A","C");
        service2.createParkingSpot(1L,"A","C");
        service2.createParkingSpot(1L,"A","E");
        service2.createParkingSpot(1L,"A","F");
        service2.createParkingSpot(1L,"A","G");
        service2.createParkingSpot(1L,"A","A");
        service2.createParkingSpot(1L,"A","I");
        service2.createParkingSpot(1L,"A","J");
        service3.createUser("JAno","Stano","admin@vsa.sk");
        service3.createUser("JAn","Standso","dasd@sadasadass");
        service3.createUser("JA","Stadsano","dasd@sadsadasdas");
        service3.createUser("J","Staasdno","dasd@sadasdas");
        service3.createUser("JAnho","Staasdano","dasd@saadasddas");
        service4.createCar(24L,"audi","a6","black","tn124dc");
        service4.createCar(24L,"audy","a6","black","tn125dc");
        service4.createCar(23L,"audidsa","a6","black","tn127dc");
        service4.createCar(23L,"audidsa","a6","black","tn123dc");
        service5.createReservation(7L,31L);
        service5.createReservation(10L,28L);
        service5.createReservation(12L,29L);
        service5.createReservation(14L,30L);
        service6.createHoliday("Sviatok Prace",new Date(Calendar.YEAR,Calendar.MAY,1));
        service6.createHoliday("Vznik SR",new Date(Calendar.YEAR,Calendar.JANUARY,1));
    }

}
