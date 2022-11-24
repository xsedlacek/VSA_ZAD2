package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.Entities.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.Entities.Reservation;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReservationService {

    protected EntityManagerFactory emf;

    public ReservationService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }
    protected void close() {
        emf.close();
    }

    public Reservation createReservation(Long parkingSpotId, Long carId) {
        if (parkingSpotId == null || carId == null) return null;
        EntityManager manager = emf.createEntityManager();
        ParkingSpot spot = manager.find(ParkingSpot.class,parkingSpotId);
        Car car = manager.find(Car.class,carId);

        if (car == null) return null;
        if (spot.getCar() == null){
            try{
                EntityTransaction transaction = manager.getTransaction();
                transaction.begin();
                Reservation reservation = new Reservation();
                Query query = manager.createNativeQuery("SELECT CP.PRICES FROM PARKING_SPOT JOIN CAR_PARK_FLOOR CPF on CPF.ID = PARKING_SPOT.floor_id JOIN CAR_PARK CP on CP.ID = CPF.carPark where PARKING_SPOT.id = " + parkingSpotId);
                reservation.setPrice((Integer) query.getSingleResult());
                spot.setCar(car);
                car.setReservation(reservation);
                manager.persist(reservation);
                transaction.commit();

                return reservation;
            }catch (NoResultException e){
                return null;
            }

        }

        return null;
    }

    public Reservation endReservation(Long reservationId) {
        if (reservationId == null) return null;

        EntityManager manager = emf.createEntityManager();
        Reservation reservation = manager.find(Reservation.class,reservationId);

        if (reservation != null){
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            reservation = manager.find(Reservation.class,reservationId);
            Date date = new Date(System.currentTimeMillis());
            reservation.setEndTime(date);
            long elapsedms = reservation.getBeginTime().getTime() - reservation.getEndTime().getTime();
            long diff = TimeUnit.HOURS.convert(elapsedms, TimeUnit.MILLISECONDS);

            reservation.setPrice((int) (reservation.getPrice() * (diff+1)));

            TypedQuery<Car> query = manager.createNamedQuery(Car.FIND_BY_RESERVATION,Car.class);
            query.setParameter("id",reservationId);
            Car car = query.getSingleResult();
            TypedQuery<ParkingSpot> q = manager.createNamedQuery(ParkingSpot.FIND_BY_CAR,ParkingSpot.class);
            q.setParameter("id",car.getId());
            ParkingSpot spot = q.getSingleResult();
            spot.setCar(null);
            car.setReservation(null);
            transaction.commit();

        }

        return reservation;
    }


    public List<Reservation> getReservations(Long parkingSpotId, Date date) {

        EntityManager manager = emf.createEntityManager();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = formatter.format(date);
        Query query = manager.createNativeQuery("SELECT * FROM RESERVATION JOIN CAR C on RESERVATION.ID = C.RESERVATION_ID JOIN PARKING_SPOT PS on C.ID = PS.CAR_ID " +
                "WHERE PS.ID  = "+ parkingSpotId +" and RESERVATION.BEGINTIME LIKE '"+ strDate +"%'");


        try {
            return query.getResultList();

        }catch (NoResultException e){
            return new ArrayList<>();
        }
    }


    public List<Object> getMyReservations(Long userId) {

////        EntityManager manager = emf.createEntityManager();
//
//        Query query = manager.createNativeQuery("SELECT * FROM RESERVATION " +
//                "JOIN CAR C on RESERVATION.ID = C.RESERVATION_ID " +
//                "JOIN USER U on U.ID = C.user " +
//                "WHERE user = "+ userId );
//
//        try {
//
//            return query.getResultList();
//        }catch (NoResultException e){
//            return new ArrayList<>();
//        }
        return null;
    }

    public Object updateReservation(Object reservation) {
        return null;
    }
    public List<Reservation> getAllReservations(){
        EntityManager manager = emf.createEntityManager();

        TypedQuery<Reservation> query = manager.createNamedQuery(Reservation.FIND_ALL,Reservation.class);

        return  query.getResultList();
    }
    public Reservation getById(Long id){
        EntityManager manager = emf.createEntityManager();
        return manager.find(Reservation.class,id);
    }
}
