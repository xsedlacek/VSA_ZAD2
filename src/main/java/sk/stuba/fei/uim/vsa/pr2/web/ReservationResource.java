package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.Auth;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.Entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.services.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.services.ReservationService;
import sk.stuba.fei.uim.vsa.pr2.web.response.factory.MessageDto;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Path("/reservations")
public class ReservationResource {
    private final ReservationService rs = new ReservationService();
    private final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("user") String user, @QueryParam("spot") Long spot, @QueryParam("date")Date date){

        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

        List<Reservation> reservations;


        if (user == null && date != null && spot != null){
            reservations = rs.getReservations(spot,date);
        }else reservations = rs.getAllReservations();


        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(reservations)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam("id") Long id){

        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

        Reservation reservation = rs.getById(id);

        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(reservation)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }
    @POST
    @Path("/{id}/end")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response endReservation(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam("id") Long id){

        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

         Reservation reservation = rs.endReservation(id);


        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(reservation)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }
}
