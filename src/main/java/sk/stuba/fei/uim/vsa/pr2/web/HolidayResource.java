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
import sk.stuba.fei.uim.vsa.pr2.Entities.Customer;
import sk.stuba.fei.uim.vsa.pr2.Entities.Holidays;
import sk.stuba.fei.uim.vsa.pr2.services.HolidayService;
import sk.stuba.fei.uim.vsa.pr2.services.ParkingSpotService;
import sk.stuba.fei.uim.vsa.pr2.web.response.factory.MessageDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Path("/holidays")
public class HolidayResource {
    private final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final HolidayService service = new HolidayService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("date") Date date){

        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

        List<Holidays> holidays;

        if (date != null){
            holidays = Collections.singletonList(service.getHoliday(date));
        }else{
            holidays = service.getHolidays();
        }

        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(holidays)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addHoliday(String body){
        try {
            Holidays h = json.readValue(body,Holidays.class);
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(h.getDate());
            h = service.createHoliday(h.getName(),date);

            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(h)).build();
        }catch (JsonProcessingException | ParseException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam(("id")) Long id){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

        Holidays holidays = service.deleteHoliday(id);
        return Response.status(Response.Status.NO_CONTENT).entity("{}").build();
    }

}
