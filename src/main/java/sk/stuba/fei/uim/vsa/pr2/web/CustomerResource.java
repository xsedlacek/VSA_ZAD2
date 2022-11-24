package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.Entities.Car;
import sk.stuba.fei.uim.vsa.pr2.Entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.Entities.Customer;
import sk.stuba.fei.uim.vsa.pr2.services.CustomerService;
import sk.stuba.fei.uim.vsa.pr2.web.response.factory.MessageDto;

import java.util.Collections;
import java.util.List;

@Path("/users")
public class CustomerResource {

    private final CustomerService service = new CustomerService();
    private final ObjectMapper json = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("email") String email){

        List<Customer> customer;

        if (email != null ){
            customer = Collections.singletonList(service.getUser(email));

        }else {
            customer = service.getUsers();
        }
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(customer)).build();
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
    public Response getById(@PathParam("id")Long id){

        Customer customer;
        customer = service.getUser(id);

        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(customer)).build();
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
    public Response addCustomer(String body){
        try {
            Customer c = json.readValue(body,Customer.class);
            c = service.createUser(c.getFirstName(), c.getLastName(), c.getEmail());
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
    public Response deleteById(@PathParam(("id")) Long id){
        Customer cp = service.deleteUser(id);
        return Response.status(Response.Status.NO_CONTENT).entity("{}").build();
    }
}
