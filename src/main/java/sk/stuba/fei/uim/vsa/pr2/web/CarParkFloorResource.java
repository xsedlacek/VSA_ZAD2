package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.Auth;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.services.CarParkFloorService;
import sk.stuba.fei.uim.vsa.pr2.web.response.factory.MessageDto;

@Path("/carparkfloors")
public class CarParkFloorResource {

    private final CarParkFloorService cpfs = new CarParkFloorService();
    private final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);



    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFloorsById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam(("id")) Long id){

        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        CarParkFloor cpf = cpfs.getCarParkFloor(id);

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

}
