package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Address;
import entities.Person;
import entities.PersonDTO;
import exceptions.GenericExceptionMapper;
import exceptions.PersonNotFoundException;
import exceptions.PersonNotFoundExceptionMapper;
import utils.EMF_Creator;
import facades.PersonFacade;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/f2w1onsdag",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final PersonFacade fc = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getRenameMeCount() {
        long count = fc.getRenameMeCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }

    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPeople() {
        List<PersonDTO> dto = new ArrayList();
        for (Person p : fc.getAllPeople()) {
            dto.add(new PersonDTO(p));
        }
        return new Gson().toJson(dto);
    }

    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getSingle(@PathParam("id") int id) {
        PersonDTO dto = new PersonDTO(fc.getPerson(id));
        return new Gson().toJson(dto);
    }

    @Path("/delete/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePerson(@PathParam("id") int id) {
        try {
            fc.deletePerson(id);
            return Response.ok("{\"status\": \"the person with id=" + id + " has been deleted\"}").build();
        } catch (PersonNotFoundException ex) {
            return new PersonNotFoundExceptionMapper().toResponse(ex);
        } catch (RuntimeException ex) {
            return new GenericExceptionMapper().toResponse(ex);
        }
    }

    @Path("/add")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addPerson(String person) {
        PersonDTO p = GSON.fromJson(person, PersonDTO.class);
        PersonDTO dto = new PersonDTO(fc.addPerson(p.getfName(), p.getlName(), p.getPhone(),
                p.getAddress().getStreet(), p.getAddress().getZip(), p.getAddress().getCity()));
        return Response.ok(dto).build();
    }

    @Path("/edit/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response editPerson(@PathParam("id") int id, String person) {
        try {
            PersonDTO dto = GSON.fromJson(person, PersonDTO.class);
            Person p = fc.getPerson(id);

            p.setFirstName(dto.getfName());
            p.setLastName(dto.getlName());
            p.setPhone(dto.getPhone());
            p = fc.editPerson(p);
            return Response.ok(new PersonDTO(p)).build();
        } catch (PersonNotFoundException ex) {
            return new PersonNotFoundExceptionMapper().toResponse(ex);
        }

    }

    public static void main(String[] args) {

        EntityManager em = EMF.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Person("Asger", "Sørensen", "222", new Address("What", "is", "this")));
        em.getTransaction().commit();

    }

}
