package de.nmichael.efa.api;

import de.nmichael.efa.Daten;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/*
Provides information about the server: server version
 */
@Path("about")
public class AboutResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getAbout() {
        Map<String, String> about = new HashMap<String, String>(){{
            put("version", Daten.VERSION);
            put("versionId", Daten.VERSIONID);
        }};
        return about;
    }
}
