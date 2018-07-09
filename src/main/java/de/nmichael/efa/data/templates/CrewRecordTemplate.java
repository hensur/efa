package de.nmichael.efa.data.templates;

import de.nmichael.efa.data.LogbookRecord;

public class CrewRecordTemplate extends IDRecordTemplate {
    private String name;
    private PersonRecordTemplate[] crew = new PersonRecordTemplate[LogbookRecord.CREW_MAX + 1];
    private int boatCaptain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonRecordTemplate[] getCrew() {
        return crew;
    }

    public void setCrew(PersonRecordTemplate[] crew) {
        this.crew = crew;
    }

    public int getBoatCaptain() {
        return boatCaptain;
    }

    public void setBoatCaptain(int boatCaptain) {
        this.boatCaptain = boatCaptain;
    }
}
