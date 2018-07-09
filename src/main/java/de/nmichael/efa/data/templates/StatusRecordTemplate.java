package de.nmichael.efa.data.templates;

import java.util.UUID;

public class StatusRecordTemplate extends IDRecordTemplate {

    private String name;
    private String type;
    private int membership;
    private boolean setOnAge;
    private int minAge;
    private int maxAge;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMembership() {
        return membership;
    }

    public void setMembership(int membership) {
        this.membership = membership;
    }

    public boolean isSetOnAge() {
        return setOnAge;
    }

    public void setSetOnAge(boolean setOnAge) {
        this.setOnAge = setOnAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }
}
