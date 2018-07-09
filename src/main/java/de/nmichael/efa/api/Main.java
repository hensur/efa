package de.nmichael.efa.api;

import de.nmichael.efa.*;
import de.nmichael.efa.data.Project;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main extends Program {

    private static final URI BASE_URI = URI.create("http://localhost:8080/api/");

    public Main(String[] args) {
        super(Daten.APPL_API, args);
        Project.openProject(args[0], true);

        try {
            final ResourceConfig resourceConfig = getAPIConfig();
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    server.shutdownNow();
                    Daten.haltProgram(0);
                }
            }));
            server.start();

            System.out.printf("Application started.\nTry out %s\nStop the application using CTRL+C", BASE_URI);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static ResourceConfig getAPIConfig(){
        ResourceConfig rsConfig = new ResourceConfig(
                PingResource.class,
                AboutResource.class,
                ProjectResource.class,
                LogbookResource.class,
                ObjectMapperProvider.class,
                BoatResource.class,
                PersonResource.class
        );
        return rsConfig;
    }

    public static void main(String[] args) {
        new Main(args);
    }
}
