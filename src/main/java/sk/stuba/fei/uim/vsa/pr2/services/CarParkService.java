package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.Entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.Entities.ParkingSpot;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarParkService {

    protected EntityManagerFactory emf;

    public CarParkService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    protected void close() {
        emf.close();
    }

    //private final ParkingSpotService service = new ParkingSpotService();
    private final ReservationService service1 = new ReservationService();

    public CarPark createCarPark(String name, String address, Integer pricePerHour) {

        if (name == null) return null;

        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        CarPark carPark = (CarPark) getCarPark(name);
        if (carPark == null){
            transaction.begin();
            carPark = new CarPark();
            carPark.setAddress(address);
            carPark.setName(name);
            carPark.setPrices(pricePerHour);
            manager.persist(carPark);
            transaction.commit();
            return carPark;
        }
        return null;
    }

    public CarPark getCarPark(Long carParkId) {
        EntityManager manager = emf.createEntityManager();

        return manager.find(CarPark.class,carParkId);
    }

    public CarPark getCarPark(String carParkName) {

        EntityManager manager = emf.createEntityManager();

        TypedQuery<CarPark> query = manager.createNamedQuery(CarPark.FIND_BY_NAME,CarPark.class);
        query.setParameter("name",carParkName);
        try{
            query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return query.getSingleResult();
    }

    public List<CarPark> getCarParks() {

        EntityManager manager = emf.createEntityManager();

        TypedQuery<CarPark> query = manager.createNamedQuery(CarPark.FIND_ALL,CarPark.class);

        return  query.getResultList();
    }

    public CarPark updateCarPark(Object carPark) {
        if (carPark == null) return null;

        EntityManager manager = emf.createEntityManager();

        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        CarPark carPark1 = manager.find(CarPark.class,((CarPark) carPark).getId());
        carPark1.setAddress(((CarPark) carPark).getAddress());
        carPark1.setName(((CarPark) carPark).getName());
        carPark1.setPrices(((CarPark) carPark).getPrices());
        transaction.commit();

        return carPark1;
    }

    public CarPark deleteCarPark(Long carParkId) {

        EntityManager manager = emf.createEntityManager();
        CarPark carPark = manager.find(CarPark.class,carParkId);

        if (carPark.getId() != null) {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
//            Map<String,List<ParkingSpot>> map = service.getOccupiedParkingSpots(carPark.getName());
//
//            for (CarParkFloor f: carPark.getFloors()) {
//                List<ParkingSpot> spots = map.get(f.getIdentifier()).stream()
//                        .map(e -> (ParkingSpot) e)
//                        .collect(Collectors.toList());
//                for (ParkingSpot s: spots) {
//                    service1.endReservation(s.getCar().getReservation().getId());
//                }
//            }
            manager.remove(carPark);
            transaction.commit();
            return carPark;
        }
        return null;
    }
}
