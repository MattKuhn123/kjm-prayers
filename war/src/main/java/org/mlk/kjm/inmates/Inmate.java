package org.mlk.kjm.inmates;

import java.util.Optional;
import java.time.LocalDate;
import org.mlk.kjm.jails.Jail;

public class Inmate {
    private final String firstName;
    private final String lastName;
    private final Optional<LocalDate> birthDay;
    private final Optional<Boolean> isMale;
    private final Optional<String> info;
    private final Jail jail;

    public Inmate(String firstName, String lastName, Optional<LocalDate> birthDay, Optional<Boolean> isMale,
            Optional<String> info, Jail jail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.isMale = isMale;
        this.info = info;
        this.jail = jail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public Jail getJail() {
        return jail;
    }
}
