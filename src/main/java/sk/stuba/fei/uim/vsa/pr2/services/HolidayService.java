package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.Entities.Holidays;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HolidayService {

    protected EntityManagerFactory emf;
    public HolidayService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    public Holidays createHoliday(String name, Date date) {
        if (name == null || date == null) return null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
            String strDate = formatter.format(date);
            EntityManager manager = emf.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            Holidays holidays = new Holidays(name,strDate);
            manager.persist(holidays);
            transaction.commit();
            return holidays;
        }catch (RollbackException e){
            return null;
        }

    }


    public Holidays getHoliday(Date date) {
        if (date == null) return null;
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
            String strDate = formatter.format(date);
            EntityManager manager = emf.createEntityManager();
            TypedQuery<Holidays> query = manager.createNamedQuery(Holidays.FIND_BY_DATE,Holidays.class);
            query.setParameter("date",strDate);
            return query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }

    }


    public List<Holidays> getHolidays() {
        EntityManager manager = emf.createEntityManager();

        TypedQuery<Holidays> query = manager.createNamedQuery(Holidays.FIND_ALL,Holidays.class);

        return query.getResultList();
    }


    public Holidays deleteHoliday(Long holidayId) {

        EntityManager manager = emf.createEntityManager();
        Holidays holidays = manager.find(Holidays.class,holidayId);

        if (holidays != null){
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            Query query = manager.createNativeQuery("DELETE FROM HOLIDAYS WHERE ID = " + holidayId);
            query.executeUpdate();
            transaction.commit();
        }

        return holidays;
    }

}
