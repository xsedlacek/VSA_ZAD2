package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.Auth;
import sk.stuba.fei.uim.vsa.pr2.Entities.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.services.ParkingSpotService;
import sk.stuba.fei.uim.vsa.pr2.web.response.factory.MessageDto;

@Path("/parkingspots")
public class ParkingSpotResource {
    private final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final ParkingSpotService pss = new ParkingSpotService();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpotsById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam(("id")) Long id){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        ParkingSpot spot = pss.getParkingSpot(id);

        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(spot)).build();
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
    public Response deleteSpotsById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,@PathParam(("id")) Long id){
        if (Auth.auth(auth)) return Response.status(Response.Status.UNAUTHORIZED).build();
        ParkingSpot spot = pss.deleteParkingSpot(id);
        if (spot != null){
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }
}
