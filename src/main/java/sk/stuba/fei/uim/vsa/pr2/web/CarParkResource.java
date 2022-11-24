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
import sk.stuba.fei.uim.vsa.pr2.Entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.Entities.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.services.CarParkFloorService;
import sk.stuba.fei.uim.vsa.pr2.services.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.services.ParkingSpotService;
import sk.stuba.fei.uim.vsa.pr2.web.response.factory.MessageDto;

import java.util.*;

@Path("/carparks")
public class CarParkResource {
    private final CarParkService cps = new CarParkService();
    private final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final CarParkFloorService cpfs = new CarParkFloorService();
    private final ParkingSpotService pss = new ParkingSpotService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("name") String name){

        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

        List<CarPark> cp;

        if (name != null){
            if (cps.getCarPark(name) == null){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();

            }
            cp = Collections.singletonList(cps.getCarPark(name));
        }else{
            cp = cps.getCarParks();
        }

        try {
           return Response.status(Response.Status.OK).entity(json.writeValueAsString(cp)).build();
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
    public Response getById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam(("id")) Long id){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

        CarPark cp = cps.getCarPark(id);
        if (cp == null){
            return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
        }
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(cp)).build();
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
    public Response addCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,String body){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();

        try {
            CarPark c = json.readValue(body,CarPark.class);
            c = cps.createCarPark(c.getName(),c.getAddress(),c.getPrices());

            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(c)).build();
        }catch (JsonProcessingException e){
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

        CarPark cp = cps.deleteCarPark(id);
        return Response.status(Response.Status.NO_CONTENT).entity("{}").build();
    }

    @GET
    @Path("/{id}/floors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFloors(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam(("id")) Long id){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        List<CarParkFloor> cpf = cpfs.getCarParkFloors(id);

        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(cpf)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }

    @DELETE
    @Path("/{id}/floors/{identifier}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFloors(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam(("id")) Long id,
                               @PathParam(("identifier")) String identifier ){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        List<CarParkFloor> cpf = cpfs.getCarParkFloors(id);

        for (CarParkFloor f: cpf ) {
            if (f.getIdentifier().equals(identifier)){
                cpfs.deleteCarParkFloor(f.getId());
            }
        }
        return Response.status(Response.Status.NO_CONTENT).entity("{}").build();
    }

    @POST
    @Path("/{id}/floors")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCarParkFloor(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,String body,@PathParam(("id")) Long id){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            CarParkFloor c = json.readValue(body,CarParkFloor.class);
            c = cpfs.createCarParkFloor(id,c.getIdentifier());
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(c)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }

    @GET
    @Path("/{id}/spots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpots(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam(("id")) Long id, @QueryParam("free") Boolean free){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        Map<String,List<ParkingSpot>> spots = new HashMap<>();

        if (free){
            spots = pss.getAvailableParkingSpots(cps.getCarPark(id).getName());

        }else if (!free){
            spots = pss.getAvailableParkingSpots(cps.getCarPark(id).getName());
        }else if (free == null){
            spots = pss.getParkingSpots(id);
        }
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(spots)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }
    @GET
    @Path("/{id}/floors/{identifier}/spots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpotsOnFloor(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam(("id")) Long id, @PathParam("identifier") String identifier){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        List<ParkingSpot> list = pss.getParkingSpots(id,identifier);
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(list)).build();
        }catch (JsonProcessingException e){
            try {
                return Response.status(Response.Status.BAD_REQUEST).entity(json.writeValueAsString(MessageDto.buildError(e.getMessage()))).build();
            }catch (JsonProcessingException ex){
                return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
            }
        }
    }


}
