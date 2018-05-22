package de.nmichael.efa.api;

import de.nmichael.efa.Daten;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/*
Provides information about the project: project name and description
 */
@Path("project")
public class ProjectResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getProject() {
        Map<String, String> project = new HashMap<String, String>(){{
            put("name", Daten.project.getName());
            put("description", Daten.project.getDescription());
        }};
        return project;
    }
}
