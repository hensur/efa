package de.nmichael.efa.api;

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.BoatRecord;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("boats")
public class BoatResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getAllBoats() {
        List<Map<String, String>> boats = new ArrayList<Map<String, String>>();

        for (BoatRecord b : Daten.project.getBoats(false).getAllBoats(System.currentTimeMillis(), false, false)){
            Map<String, String> boat = new HashMap<String, String>();
            boat.put("id", b.getId().toString());
            boat.put("name", b.getQualifiedName());
            boats.add(boat);
        }
        return boats;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public BoatRecord getBoat(@PathParam("id") String id) {
        BoatRecord boat = Daten.project.getBoats(false).getBoat(UUID.fromString(id), System.currentTimeMillis());
        return boat;
    }
}
