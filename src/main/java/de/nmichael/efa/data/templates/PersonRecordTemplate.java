package de.nmichael.efa.data.templates;

import java.time.LocalDate;
import java.util.UUID;

public class PersonRecordTemplate extends IDRecordTemplate {

    private String efbID;

    private String firstName;
    private String lastName;
    private String firstlastName;

    private String nameAffix;
    private String title;
    private String gender;

    private LocalDate birthday;
    private String association;

    private StatusRecordTemplate status;
    private AddressTemplate address;

    private String email;
    private String membershipNo;
    private String password; // expert mode
    private String externalID; // expert mode

    private boolean disability;
    private boolean excludeFromStatistic;
    private boolean excludeFromCompete;
    private boolean excludeFromClubwork;
    private boolean boatUsageBan;

    private String inputShortcut;

    private BoatRecordTemplate defaultBoat;
    private String[] freeUse = new String[3];

    public PersonRecordTemplate(UUID id) {
        super(id);
    }

    public String getEfbID() {
        return efbID;
    }

    public void setEfbID(String efbID) {
        this.efbID = efbID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNameAffix() {
        return nameAffix;
    }

    public void setNameAffix(String nameAffix) {
        this.nameAffix = nameAffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public StatusRecordTemplate getStatus() {
        return status;
    }

    public void setStatus(StatusRecordTemplate status) {
        this.status = status;
    }

    public AddressTemplate getAddress() {
        return address;
    }

    public void setAddress(AddressTemplate address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembershipNo() {
        return membershipNo;
    }

    public void setMembershipNo(String membershipNo) {
        this.membershipNo = membershipNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public boolean isDisability() {
        return disability;
    }

    public void setDisability(boolean disability) {
        this.disability = disability;
    }

    public boolean isExcludeFromStatistic() {
        return excludeFromStatistic;
    }

    public void setExcludeFromStatistic(boolean excludeFromStatistic) {
        this.excludeFromStatistic = excludeFromStatistic;
    }

    public boolean isExcludeFromCompete() {
        return excludeFromCompete;
    }

    public void setExcludeFromCompete(boolean excludeFromCompete) {
        this.excludeFromCompete = excludeFromCompete;
    }

    public boolean isExcludeFromClubwork() {
        return excludeFromClubwork;
    }

    public void setExcludeFromClubwork(boolean excludeFromClubwork) {
        this.excludeFromClubwork = excludeFromClubwork;
    }

    public boolean isBoatUsageBan() {
        return boatUsageBan;
    }

    public void setBoatUsageBan(boolean boatUsageBan) {
        this.boatUsageBan = boatUsageBan;
    }

    public String getInputShortcut() {
        return inputShortcut;
    }

    public void setInputShortcut(String inputShortcut) {
        this.inputShortcut = inputShortcut;
    }

    public BoatRecordTemplate getDefaultBoat() {
        return defaultBoat;
    }

    public void setDefaultBoat(BoatRecordTemplate defaultBoat) {
        this.defaultBoat = defaultBoat;
    }

    public String[] getFreeUse() {
        return freeUse;
    }
}
