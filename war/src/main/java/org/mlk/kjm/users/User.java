package org.mlk.kjm.users;

import java.time.LocalDate;
import java.util.Optional;

public class User {
    private final String email;
    private final Optional<String> code;
    private final Optional<LocalDate> codeExpires;
    private final boolean canLogin;
    private final boolean canEditInmates;
    private final boolean canEditUsers;

    public User(String email, Optional<String> code, Optional<LocalDate> codeExpires, int canLogin, int canEditInmates, int canEditUsers) {
        this.email = email;
        this.code = code;
        this.codeExpires = codeExpires;
        this.canLogin = canLogin != 0;
        this.canEditInmates = canEditInmates != 0;
        this.canEditUsers = canEditUsers != 0;
    }

    public String getEmail() {
        return email;
    }

    public Optional<String> getCode() {
        return code;
    }

    public Optional<LocalDate> getCodeExpires() {
        return codeExpires;
    } 

    public boolean canLogin() {
        return canLogin;
    }

    public boolean canEditInmates() {
        return canEditInmates;
    }

    public boolean canEditUsers() {
        return canEditUsers;
    }
}
