package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.Entities.ParkingSpot;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

public class CarParkFloorService {

    ReservationService service = new ReservationService();
    protected EntityManagerFactory emf;
    public CarParkFloorService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }


    public CarParkFloor createCarParkFloor(Long carParkId, String floorIdentifier) {

        if (floorIdentifier == null || carParkId == null) return null;

        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNativeQuery("SELECT IDENTIFIER FROM CAR_PARK_FLOOR WHERE carPark = " + carParkId + " and IDENTIFIER = '" + floorIdentifier +"'");
        try{
            query.getSingleResult();
            return null;

        }catch (NoResultException e){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            CarParkFloor carParkFloor = new CarParkFloor();
            carParkFloor.setIdentifier(floorIdentifier);
            manager.persist(carParkFloor);
            transaction.commit();

            transaction.begin();
            CarPark carPark = manager.find(CarPark.class, carParkId);
            List<CarParkFloor> floors = carPark.getFloors();
            floors.add(carParkFloor);
            carPark.setFloors(floors);
            transaction.commit();
            return carParkFloor;
        }

    }

    public CarParkFloor getCarParkFloor(Long carParkFloorId) {
        EntityManager manager = emf.createEntityManager();
        return manager.find(CarParkFloor.class,carParkFloorId);
    }

    public List<CarParkFloor> getCarParkFloors(Long carParkId) {
        if (carParkId == null) return null;

        EntityManager manager = emf.createEntityManager();
        CarPark carPark = manager.find(CarPark.class,carParkId);
        List<CarParkFloor> list = carPark.getFloors();

        return list;
    }


    public CarParkFloor updateCarParkFloor(Object carParkFloor) {

        if (carParkFloor == null) return null;

        EntityManager manager = emf.createEntityManager();

        if (manager.find(CarParkFloor.class,((CarParkFloor) carParkFloor).getId()) != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(carParkFloor);
            transaction.commit();
        }

        return manager.find(CarParkFloor.class,((CarParkFloor) carParkFloor).getId());
    }


    public CarParkFloor deleteCarParkFloor(Long carParkFloorId) {

        if (carParkFloorId == null) return null;
        EntityManager manager = emf.createEntityManager();
        CarParkFloor carParkFloor = manager.find(CarParkFloor.class,carParkFloorId);

        if (carParkFloor.getId() != null){

            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            List<ParkingSpot> spots = carParkFloor.getSpots();
            for (ParkingSpot s :spots) {
                Car car = s.getCar();
                if (car != null){
                    service.endReservation(car.getReservation().getId());
                }
            }


            manager.remove(carParkFloor);
            transaction.commit();

            return carParkFloor;
        }
        return null;

    }
}
