package de.nmichael.efa.data.templates;

import de.nmichael.efa.data.types.DataTypeDistance;

import java.util.ArrayList;
import java.util.UUID;

public class DestinationRecordTemplate extends IDRecordTemplate {

    private String name;

    private String start;
    private String end;

    private boolean startIsBoathouse;
    private boolean roundtrip; // Start equals Destination

    private int passedLocks;

    private DataTypeDistance distance;

    private ArrayList<WatersRecordTemplate> waters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isStartIsBoathouse() {
        return startIsBoathouse;
    }

    public void setStartIsBoathouse(boolean startIsBoathouse) {
        this.startIsBoathouse = startIsBoathouse;
    }

    public boolean isRoundtrip() {
        return roundtrip;
    }

    public void setRoundtrip(boolean roundtrip) {
        this.roundtrip = roundtrip;
    }

    public int getPassedLocks() {
        return passedLocks;
    }

    public void setPassedLocks(int passedLocks) {
        this.passedLocks = passedLocks;
    }

    public DataTypeDistance getDistance() {
        return distance;
    }

    public void setDistance(DataTypeDistance distance) {
        this.distance = distance;
    }

    public ArrayList<WatersRecordTemplate> getWaters() {
        return waters;
    }

    public void setWaters(ArrayList<WatersRecordTemplate> waters) {
        this.waters = waters;
    }
}
