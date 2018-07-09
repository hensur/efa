package de.nmichael.efa.data.templates;

import java.time.LocalDate;
import java.util.UUID;

public class SessionGroupRecordTemplate extends IDRecordTemplate {

    private String logbookName;
    private String name;
    private String route;
    private String organizer;

    private LocalDate startDate;
    private LocalDate endDate;
    private int activeDays;

    public String getLogbookName() {
        return logbookName;
    }

    public void setLogbookName(String logbookName) {
        this.logbookName = logbookName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(int activeDays) {
        this.activeDays = activeDays;
    }
}
