package de.nmichael.efa.api;

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.PersonRecord;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("persons")
public class PersonResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getAllPersons() {
        List<Map<String, String>> persons = new ArrayList<Map<String, String>>();

        for (PersonRecord p : Daten.project.getPersons(false).getAllPersons(System.currentTimeMillis(), false, false)){
            Map<String, String> person = new HashMap<String, String>();
            person.put("id", p.getId().toString());
            person.put("name", p.getFirstLastName(true));
            persons.add(person);
        }
        return persons;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public PersonRecord getPerson(@PathParam("id") String id) {
        PersonRecord person = Daten.project.getPersons(false).getPerson(UUID.fromString(id), System.currentTimeMillis());
        return person;
    }
}
