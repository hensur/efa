package de.nmichael.efa.data.templates;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class BoatRecordTemplate extends IDRecordTemplate {
    private String efbID;

    private String name;
    private String nameAffix;
    private String owner;

    private int lastVariant;
    private int defaultVariant;

    private ArrayList<BoatTypeRecord> types;
    private ArrayList<SessionGroupRecordTemplate> allowedGroups;

    private int maxNotInAllowsGroups;
    private SessionGroupRecordTemplate requiredFromGroup;

    private boolean onlyWithCaptain;

    private String manufacturer;
    private String model;
    private int maxCrewWeight;
    private LocalDate manufactionDate;
    private String serialNo;
    private LocalDate purchaseDate;
    private double purchasePrice;
    private LocalDate sellingDate;
    private double sellingPrice;
    private String currency;
    private double insuranceValue;

    private CrewRecordTemplate defaultCrew;
    private String defaultSessionType;
    private DestinationRecordTemplate defaultDestination;

    private boolean excludeFromStatistics;

    private String[] freeUse = new String[3];


}
