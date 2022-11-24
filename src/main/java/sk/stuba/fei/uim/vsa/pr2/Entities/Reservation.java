package sk.stuba.fei.uim.vsa.pr2.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@NamedNativeQuery(name = Reservation.FIND_ALL, query = "select * from RESERVATION", resultClass = Reservation.class)
public class Reservation implements Serializable {

    public Reservation() {
        this.beginTime = new Date(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Europe/Bratislava")
    private Date beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Europe/Bratislava")
    private Date endTime;
    private int price;

    public static final String FIND_ALL = "Reservation.findAll";

}
