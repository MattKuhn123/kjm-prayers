package org.mlk.kjm.users;

import static org.mlk.kjm.shared.ServletUtils.stringToDate;
import static org.mlk.kjm.shared.ServletUtils.dateToString;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;

import junit.framework.TestCase;

public class AuthTokenTests extends TestCase {

    @Test
    public void test_a() throws Exception {
        String expiresString = "01/01/1990";
        String codeString = "d546bc73-e33c-4bf5-9f33-a4ccab715411";
        String email = "mlkuhn@tva.gov";
        String[] parts = { expiresString, codeString, email };
        String stringifiedToken = String.join(";", parts);

        byte[] encodedTokenBytes = Base64.getEncoder().encode(stringifiedToken.getBytes());
        String encodedToken = new String(encodedTokenBytes);

        Optional<AuthToken> authToken = AuthToken.fromString(encodedToken);

        assertTrue(authToken.isPresent());
        assertEquals(codeString, authToken.get().getCode().toString());
        assertEquals(email, authToken.get().getEmail());
        assertEquals(expiresString, dateToString(authToken.get().getExpires()));
    }

    @Test
    public void test_b() throws Exception {
        String expiresString = "01/01/1990";
        LocalDate expires = stringToDate(expiresString);
        String codeString = "d546bc73-e33c-4bf5-9f33-a4ccab715411";
        UUID code = UUID.fromString(codeString);
        String email = "mlkuhn@tva.gov";

        AuthToken authToken = new AuthToken(expires, code, email);

        String authTokenStringified = authToken.toString();

        String authTokenDecoded = new String(Base64.getDecoder().decode(authTokenStringified));

        String[] parts = authTokenDecoded.split(";");

        String actualExpiresString = parts[0];
        assertEquals(expiresString, actualExpiresString);
        
        String actualCodeString = parts[1];
        assertEquals(codeString, actualCodeString);

        String actualEmail = parts[2];
        assertEquals(email, actualEmail);
    }
}
