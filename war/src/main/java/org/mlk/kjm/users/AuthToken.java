package org.mlk.kjm.users;

import static org.mlk.kjm.shared.ServletUtils.dateToString;
import static org.mlk.kjm.shared.ServletUtils.stringToDate;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class AuthToken {
    private final LocalDate expires;
    private final UUID code;
    private final String email;

    public AuthToken(LocalDate expires, UUID code, String email) {
        this.expires = expires;
        this.code = code;
        this.email = email;
    }

    public boolean isExpired() {
        return expires.isBefore(LocalDate.now());
    }

    public LocalDate getExpires() {
        return expires;
    }

    public UUID getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }

    public String toString() {
        String expires = dateToString(getExpires());
        String code = getCode().toString();
        String email = getEmail();
        
        String[] parts = { expires, code, email };

        String result = String.join(";", parts);

        String encoded = Base64.getEncoder().encodeToString(result.getBytes());
        return encoded;
    }

    public static Optional<AuthToken> fromString(String input) {
        try {
            String string = new String(Base64.getDecoder().decode(input.getBytes()));
    
            String[] parts = string.split(";");
    
            String expiresString = parts[0];
            LocalDate expires = stringToDate(expiresString);
    
            String codeString = parts[1];
            UUID code = UUID.fromString(codeString);
    
            String email = parts[2];
    
            AuthToken result = new AuthToken(expires, code, email);
            return Optional.of(result);
        } catch(Exception e) {
            return Optional.empty();
        }
    }
}
