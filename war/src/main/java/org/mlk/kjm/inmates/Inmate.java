package org.mlk.kjm.inmates;

import java.util.Optional;
import java.time.LocalDate;

public class Inmate {
    private final String firstName;
    private final String lastName;
    private final Optional<LocalDate> releaseDate;
    private final Optional<String> sex;
    private final Optional<String> info;
    private final String county;

    public Inmate(String firstName, String lastName, String county, Optional<LocalDate> releaseDate, Optional<String> sex,
            Optional<String> info) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.county = county;
        this.releaseDate = releaseDate;
        this.sex = sex;
        this.info = info;
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

    public Optional<LocalDate> getReleaseDate() {
        return releaseDate;
    }

    public Optional<String> sex() {
        return sex;
    }

    public Optional<String> getInfo() {
        return info;
    }
}
