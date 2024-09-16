package org.mlk.kjm.inmates;

import java.util.Optional;
import java.time.LocalDate;

public class Inmate {
    private final String firstName;
    private final String lastName;
    private final Optional<LocalDate> birthDay;
    private final Optional<Boolean> isMale;
    private final Optional<String> info;
    private final String county;

    public Inmate(String firstName, String lastName, String county, Optional<LocalDate> birthDay, Optional<Boolean> isMale,
            Optional<String> info) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.county = county;
        this.birthDay = birthDay;
        this.isMale = isMale;
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

    public Optional<LocalDate> getBirthDay() {
        return birthDay;
    }

    public Optional<Boolean> isMale() {
        return isMale;
    }

    public Optional<String> getInfo() {
        return info;
    }
}
