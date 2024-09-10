package org.mlk.kjm.prayers;

import java.util.Optional;

import org.mlk.kjm.ServletUtils;

import java.time.LocalDate;
import java.util.Map;

public class CreatePrayerRequest {
    private final static String inmateFirstNameKey = "inmateFirstName";
    private final static String inmateLastNameKey = "inmateLastName";
    private final static String countyKey = "county";
    private final static String dateKey = "date";
    private final static String prayerKey = "prayer";

    private final String inmateFirstName;
    private final String inmateLastName;
    private final String county;
    private final LocalDate date;
    private final String prayer;

    public CreatePrayerRequest(Map<String, Optional<String>> postBody) {
        inmateFirstName = postBody.get(inmateFirstNameKey).get();
        inmateLastName = postBody.get(inmateLastNameKey).get();
        county = postBody.get(countyKey).get();
        String dateString = postBody.get(dateKey).get();
        date = ServletUtils.stringToDate(dateString);
        prayer = postBody.get(prayerKey).get();
    }

    public String getInmateFirstName() {
        return inmateFirstName;
    }

    public String getInmateLastName() {
        return inmateLastName;
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
