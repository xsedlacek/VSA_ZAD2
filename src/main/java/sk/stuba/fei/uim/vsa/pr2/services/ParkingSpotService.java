package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.Entities.ParkingSpot;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParkingSpotService {

    private final CarParkService service = new CarParkService();
    private final ReservationService rs = new ReservationService();

    protected EntityManagerFactory emf;

    public ParkingSpotService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }


    public ParkingSpot createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier) {

        if (floorIdentifier == null || spotIdentifier == null || carParkId == null) return null;


        EntityManager manager = emf.createEntityManager();
        Query q = manager.createNativeQuery("SELECT PARKINGSPOTIDENTIFIER FROM PARKING_SPOT" +
                "    JOIN CAR_PARK_FLOOR CPF on CPF.ID = PARKING_SPOT.floor_id " +
                "WHERE carPark = "+ carParkId +" and IDENTIFIER = '"+ floorIdentifier +"' and PARKINGSPOTIDENTIFIER = '"+ spotIdentifier +"'");
        try{
            q.getSingleResult();
            return null;
        }catch (NoResultException e){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            ParkingSpot parkingSpot = new ParkingSpot();
            parkingSpot.setParkingSpotIdentifier(spotIdentifier);
            manager.persist(parkingSpot);
            transaction.commit();
            try{
                TypedQuery<CarParkFloor> query = manager.createNamedQuery(CarParkFloor.FIND_BY_FLOOR_IDENTIFIER,CarParkFloor.class);
                query.setParameter("floor_id",floorIdentifier);
                CarParkFloor carParkFloor = query.getSingleResult();
                transaction.begin();
                List<ParkingSpot> spots = carParkFloor.getSpots();
                spots.add(parkingSpot);
                carParkFloor.setSpots(spots);
                transaction.commit();
            }
            catch (NoResultException ex) {
                return null;
            }
            return parkingSpot;

        }

    }

    public ParkingSpot getParkingSpot(Long parkingSpotId) {
        EntityManager manager = emf.createEntityManager();

        return manager.find(ParkingSpot.class,parkingSpotId);
    }

    public List<ParkingSpot> getParkingSpots(Long carParkId, String floorIdentifier) {
        EntityManager manager = emf.createEntityManager();

        Query query = manager.createNativeQuery("SELECT * FROM PARKING_SPOT " +
                "JOIN CAR_PARK_FLOOR CPF on PARKING_SPOT.floor_id = CPF.ID  " +
                "WHERE CPF.FLOORIDENTIFIER = '" + floorIdentifier + "' and car_park_id = "+ carParkId);

        List<ParkingSpot> spots= query.getResultList();

        return spots;

    }

    public Map<String, List<ParkingSpot>> getParkingSpots(Long carParkId) {

        EntityManager manager = emf.createEntityManager();
        Map<String,List<ParkingSpot>> map = new HashMap<>();
        CarPark park = manager.find(CarPark.class,carParkId);
        List<CarParkFloor> floors = park.getFloors();

        for (CarParkFloor f: floors) {
            List<ParkingSpot> list = f.getSpots();

            map.put(f.getIdentifier(),list);

        }

        return map;
    }

    public Map<String, List<ParkingSpot>> getAvailableParkingSpots(String carParkName) {

        Map<String,List<ParkingSpot>> map = new HashMap<>();

        CarPark park = service.getCarPark(carParkName);
        List<CarParkFloor> floors = park.getFloors();
        for (CarParkFloor f: floors) {
            List<ParkingSpot> spots = new ArrayList<>();
            for (ParkingSpot spot: f.getSpots()) {
                if (spot.getCar() == null){
                    spots.add(spot);
                }
            }
            List<ParkingSpot> list = spots;

            map.put(f.getIdentifier(),list);
        }

        return map;

    }

    public Map<String, List<ParkingSpot>> getOccupiedParkingSpots(String carParkName) {
        Map<String,List<ParkingSpot>> map = new HashMap<>();

        CarPark park = service.getCarPark(carParkName);
        List<CarParkFloor> floors = park.getFloors();
        for (CarParkFloor f: floors) {
            List<ParkingSpot> spots = new ArrayList<>();
            for (ParkingSpot spot: f.getSpots()) {
                if (spot.getCar() != null){
                    spots.add(spot);
                }
            }
            List<ParkingSpot> list = spots;
            map.put(f.getIdentifier(),list);
        }
        return map;
    }

    public ParkingSpot updateParkingSpot(ParkingSpot parkingSpot) {

        if (parkingSpot == null) return null;

        EntityManager manager = emf.createEntityManager();

        if (manager.find(CarParkFloor.class,((ParkingSpot) parkingSpot).getId()) != null) {
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            manager.merge(parkingSpot);
            transaction.commit();
        }
        return parkingSpot;
    }


    public ParkingSpot deleteParkingSpot(Long parkingSpotId) {

        EntityManager manager = emf.createEntityManager();
        ParkingSpot spot = manager.find(ParkingSpot.class,parkingSpotId);

        if (spot != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            Car car = spot.getCar();
            if (car != null) {
                rs.endReservation(car.getReservation().getId());
            }
            manager.remove(spot);
            transaction.commit();
        }

        return spot;
    }
}
