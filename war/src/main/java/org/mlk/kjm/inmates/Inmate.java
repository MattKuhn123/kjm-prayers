package org.mlk.kjm.inmates;

import org.mlk.kjm.jails.Jail;

public class Inmate {
    public final String firstName;
    public final String lastName;
    public final Jail jail;

    public Inmate(String firstName, String lastName, Jail jail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.jail = jail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Jail getJail() {
        return jail;
    }
}
