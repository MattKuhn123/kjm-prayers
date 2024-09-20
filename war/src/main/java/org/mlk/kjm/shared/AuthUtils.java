package org.mlk.kjm.shared;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.mlk.kjm.users.AuthToken;
import org.mlk.kjm.users.User;
import org.mlk.kjm.users.UserRepository;

public class AuthUtils {
    public static boolean isAuthTokenValid(Optional<AuthToken> authToken, UserRepository users) throws SQLException {
        if (!authToken.isPresent()) {
            return false;
        }

        String email = authToken.get().getEmail();
        String code = authToken.get().getCode().toString();
        Optional<AuthToken> result = isAuthTokenValid(email, code, users);
        return result.isPresent();
    }

    public static Optional<AuthToken> isAuthTokenValid(String email, String codeString, UserRepository users) throws SQLException {
        UUID code = null;
        try {
            code = UUID.fromString(codeString);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        Optional<User> user = users.getUser(email);
        if (!user.isPresent()) {
            return Optional.empty();
        }

        Optional<String> userCodeString = user.get().getCode();
        if (!userCodeString.isPresent()) {
            return Optional.empty();
        }

        UUID userCode = null;
        try {
            userCode = UUID.fromString(userCodeString.get());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        Optional<LocalDate> userCodeExpires = user.get().getCodeExpires();
        if (!userCodeExpires.isPresent()) {
            return Optional.empty();
        }

        AuthToken authToken = new AuthToken(userCodeExpires.get(), userCode, email);
        boolean isIncorrectCode = !authToken.getCode().equals(code);
        if (isIncorrectCode) {
            return Optional.empty();
        }

        boolean isExpired = authToken.isExpired();
        if (isExpired) {
            return Optional.empty();
        }

        return Optional.of(authToken);
    }

}
