/**
 * A generic RecordTemplate featuring a UUID to identify it
 */
package de.nmichael.efa.data.templates;

import java.util.UUID;

public class IDRecordTemplate extends RecordTemplate {
    private UUID ID;

    public IDRecordTemplate() {}

    public IDRecordTemplate(UUID ID) {
        this.ID = ID;
    }

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }
}
