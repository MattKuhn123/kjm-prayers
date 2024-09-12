package org.mlk.kjm.prayers;

import java.time.LocalDate;

public class Prayer {
    private final String firstName;
    private final String lastName;
    private final String county;
    private final LocalDate date;
    private final String prayer;

    public Prayer(String firstName, String lastName, String county, LocalDate date, String prayer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.county = county;
        this.date = date;
        this.prayer = prayer;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCounty() {
        return county;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPrayer() {
        return prayer;
    }
}
