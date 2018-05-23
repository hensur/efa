package de.nmichael.efa.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.nmichael.efa.Daten;
import de.nmichael.efa.data.Logbook;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.types.DataTypeIntString;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/*
Allows the creation and retrieval of LogbookRecords
 */
@Path("logbook")
public class LogbookResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ObjectNode getAllLogbookEntries() {
        return null;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public LogbookRecord getLogbookEntry(@PathParam("id") String id) {
        Logbook logbook = Daten.project.getLogbook("2018", false);
        try {
            LogbookRecord record = (LogbookRecord) logbook.data().get(LogbookRecord.getKey(DataTypeIntString.parseString(id)));
            return record;
        } catch (Exception e) {
            return null;
        }
    }

    /*
    splits all entries in pages with an equal amount of entries per page.

    @return array of logbook entries as json nodes
     */
    private LogbookRecord[] getAllEntries(int page, int per_page) {
        return null;
    }

    /*
    save and verify a new logbook entry

    @param new logbook record
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveEntryNode(LogbookRecord entry) {
        Logbook logbook = Daten.project.getLogbook("2018", false);
        entry.setEntryId(logbook.getNextEntryNo());
        entry.setPersistence(logbook);
        long lock = 0;
        try {
            lock = logbook.data().acquireGlobalLock();
            logbook.data().add(entry, lock);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            logbook.data().releaseGlobalLock(lock);
        }
    }
}
