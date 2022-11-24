package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.services.CarService;
import sk.stuba.fei.uim.vsa.pr2.web.response.factory.MessageDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/cars")
public class CarResource {
    private final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final CarService service = new CarService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("vrp") String vrp, @QueryParam("user") Long user){

        List<Car> car;

        if (user != null && vrp != null){
            car = Collections.singletonList(service.getCar(vrp));

        }else if (user == null && vrp != null){
            car  = Collections.singletonList(service.getCar(vrp));
        }else if(user != null){
            car = service.getCars(user);
        }else {
            car = service.getCars();
        }
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(car)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCar(@PathParam("id")Long id){

        Car car = service.getCar(id);

        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(car)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response deleteCar(@PathParam("id")Long id){

        Car car = service.deleteCar(id);

        return Response.status(Response.Status.NO_CONTENT).entity("{}").build();

    }
}
