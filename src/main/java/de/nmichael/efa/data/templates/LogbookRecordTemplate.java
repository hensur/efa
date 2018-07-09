/**
 * Template for a LogbookRecord, which is identified by its entryID
 * instead of an UUID.
 */

package de.nmichael.efa.data.templates;

import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.types.DataTypeDistance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class LogbookRecordTemplate extends RecordTemplate {

    private int entryID;
    private LocalDate startDate;
    private LocalDate endDate;

    private BoatRecordTemplate boat;

    // Crew as an Array of Persons, 0 is the cox
    private PersonRecordTemplate[] crew = new PersonRecordTemplate[LogbookRecord.CREW_MAX + 1];
    private int boatCaptain; // Crew Member at this position is captain

    private LocalTime startTime;
    private LocalTime endTime;

    private DestinationRecordTemplate destination;
    private ArrayList<WatersRecordTemplate> waters;
    private DataTypeDistance distance;

    private String comments;

    private String sessionType;

    private SessionGroupRecordTemplate sessionGroup;

    public LogbookRecordTemplate(){}

    public LogbookRecordTemplate(int entryID){
        this.entryID = entryID;
    }

    public int getEntryID() {
        return entryID;
    }

    public void setEntryID(int entryID) {
        this.entryID = entryID;
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

    public BoatRecordTemplate getBoat() {
        return boat;
    }

    public void setBoat(BoatRecordTemplate boat) {
        this.boat = boat;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public DestinationRecordTemplate getDestination() {
        return destination;
    }

    public void setDestination(DestinationRecordTemplate destination) {
        this.destination = destination;
    }

    public ArrayList<WatersRecordTemplate> getWaters() {
        return waters;
    }

    public void setWaters(ArrayList<WatersRecordTemplate> waters) {
        this.waters = waters;
    }

    public DataTypeDistance getDistance() {
        return distance;
    }

    public void setDistance(DataTypeDistance distance) {
        this.distance = distance;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public SessionGroupRecordTemplate getSessionGroup() {
        return sessionGroup;
    }

    public void setSessionGroup(SessionGroupRecordTemplate sessionGroup) {
        this.sessionGroup = sessionGroup;
    }
}
