package org.mlk.kjm.users;

import java.time.LocalDate;
import java.util.Optional;

public class User {
    private final String email;
    private final Optional<String> code;
    private final Optional<LocalDate> codeExpires;

    public User(String email, Optional<String> code, Optional<LocalDate> codeExpires) {
        this.email = email;
        this.code = code;
        this.codeExpires = codeExpires;
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
}
