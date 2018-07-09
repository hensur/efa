package de.nmichael.efa.data.templates;

import java.util.UUID;

public class WatersRecordTemplate extends IDRecordTemplate {

    private String efbID;

    private String name;
    private String details;

    public String getEfbID() {
        return efbID;
    }

    public void setEfbID(String efbID) {
        this.efbID = efbID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
