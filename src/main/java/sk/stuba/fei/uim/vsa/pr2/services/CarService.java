package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.Entities.Customer;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

public class CarService {

    ReservationService rs = new ReservationService();
    protected EntityManagerFactory emf;
    public CarService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    public Car createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate) {

        EntityManager manager = emf.createEntityManager();
        TypedQuery<Car> query = manager.createNamedQuery(Car.FIND_BY_PLATE,Car.class);
        query.setParameter("plate",vehicleRegistrationPlate);
        try{
            query.getSingleResult();
            return null;
        }catch (NoResultException e){
            Customer customer = manager.find(Customer.class,userId);
            if (customer == null) return null;

            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            Car car = new Car(brand,model,vehicleRegistrationPlate,colour);
            manager.persist(car);
            transaction.commit();

            transaction.begin();
            customer = manager.find(Customer.class,userId);
            List<Car> cars = customer.getCars();
            cars.add(car);
            customer.setCars(cars);
            transaction.commit();

            return car;

        }
    }


    public Car getCar(Long carId) {
        EntityManager manager = emf.createEntityManager();

        return manager.find(Car.class,carId);
    }


    public Car getCar(String vehicleRegistrationPlate) {
        if (vehicleRegistrationPlate == null) return null;
        EntityManager manager = emf.createEntityManager();

        TypedQuery<Car> query = manager.createNamedQuery(Car.FIND_BY_PLATE,Car.class);
        query.setParameter("plate",vehicleRegistrationPlate);

        try {
            Car car = query.getSingleResult();
            return car;
        }
        catch (NoResultException e){
            return null;
        }

    }

    public List<Car> getCars(){
        EntityManager manager = emf.createEntityManager();

        TypedQuery<Car> query = manager.createNamedQuery(Car.GET_ALL,Car.class);

        return query.getResultList();
    }
    public List<Car> getCars(Long userId) {

        EntityManager manager = emf.createEntityManager();
        Customer customer = manager.find(Customer.class,userId);
        if (customer == null)return null;

        List<Car> cars= customer.getCars();


        return cars;

    }

    public Car updateCar(Car car) {
        if (car == null) return null;

        EntityManager manager = emf.createEntityManager();
        if (manager.find(CarParkFloor.class,((Car) car).getId()) != null) {
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            manager.merge(car);
            transaction.commit();
        }

        return manager.find(Car.class,car.getId());
    }

    public Car deleteCar(Long carId) {

        EntityManager manager = emf.createEntityManager();
        Car car = manager.find(Car.class,carId);

        if (car != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            if (car.getReservation() != null){
                rs.endReservation(car.getReservation().getId());
            }
            manager.remove(car);
            transaction.commit();
        }

        return car;
    }
}
