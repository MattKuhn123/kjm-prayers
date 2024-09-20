package org.mlk.kjm.users;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {
    public final static String emailColumn = "email";
    public final static String codeColumn = "code";
    public final static String codeExpiresColumn = "code_expires";
    public final static String table = "kjm.users";
    public final static String[] columns = { emailColumn, codeColumn, codeExpiresColumn };

    Optional<User> getUser(String email) throws SQLException;

    Optional<User> getUser(AuthToken token) throws SQLException;

    void setAuthToken(AuthToken authToken) throws SQLException;
}
