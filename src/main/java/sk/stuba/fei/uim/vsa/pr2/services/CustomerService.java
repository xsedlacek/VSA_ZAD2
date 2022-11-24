package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.Entities.Customer;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerService {

    ReservationService rs = new ReservationService();
    protected EntityManagerFactory emf;
    public CustomerService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }


    public Customer createUser(String firstname, String lastname, String email) {

        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNativeQuery("SELECT EMAIL FROM USER WHERE EMAIL = '"+ email +"'");

        try{
            query.getSingleResult();
            return null;

        }catch (NoResultException e){
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            Customer customer = new Customer(firstname,lastname,email);
            manager.persist(customer);
            transaction.commit();
            return customer;
        }

    }


    public Customer getUser(Long userId) {

        EntityManager manager = emf.createEntityManager();

        return manager.find(Customer.class,userId);
    }

    public Customer getUser(String email) {
        if (email == null) return null;

        EntityManager manager = emf.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        transaction.begin();
        TypedQuery<Customer> query = manager.createNamedQuery(Customer.FIND_BY_EMAIL,Customer.class);
        query.setParameter("email",email);
        Customer customer = query.getSingleResult();
        transaction.commit();

        return customer;
    }

    public List<Customer> getUsers() {

        EntityManager manager = emf.createEntityManager();

        TypedQuery<Customer> query = manager.createNamedQuery(Customer.FIND_ALL,Customer.class);

        try {
            List<Customer> customers = query.getResultList();

            return customers;
        }catch (NoResultException e){
            return new ArrayList<>();
        }


    }


    public Customer updateUser(Customer user) {
        if(user == null) return null;
        EntityManager manager = emf.createEntityManager();

        if (manager.find(Customer.class,user.getId()) != null) {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(user);
            transaction.commit();
        }
        return manager.find(Customer.class, user.getId());
    }

    public Customer deleteUser(Long userId) {

        EntityManager manager = emf.createEntityManager();
        Customer customer = manager.find(Customer.class,userId);

        if (customer != null){
            EntityTransaction transaction = manager.getTransaction();

            transaction.begin();
            List<Car> cars =customer.getCars();
            if (cars != null){
                for (Car car:cars) {
                    if (car.getReservation() != null){
                        rs.endReservation(car.getReservation().getId());
                    }
                }
            }
            manager.remove(customer);
            transaction.commit();

        }

        return customer;
    }
}
