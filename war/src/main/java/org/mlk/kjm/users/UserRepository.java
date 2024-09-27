package org.mlk.kjm.users;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {
    public final static String emailColumn = "email";
    public final static String codeColumn = "code";
    public final static String codeExpiresColumn = "code_expires";
    public final static String canLoginColumn = "can_login";
    public final static String canEditInmatesColumn = "can_edit_inmates";
    public final static String canEditUsersColumn = "can_edit_users";
    public final static String table = "kjm.users";
    public final static String[] columns = { emailColumn, codeColumn, codeExpiresColumn, canLoginColumn, canEditInmatesColumn, canEditUsersColumn };

    Optional<User> getUser(String email) throws SQLException;

    Optional<User> getUser(AuthToken token) throws SQLException;

    void setAuthToken(AuthToken authToken) throws SQLException;
}
