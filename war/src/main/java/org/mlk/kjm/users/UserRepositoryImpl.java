package org.mlk.kjm.users;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.mlk.kjm.shared.ApplicationProperties;
import org.mlk.kjm.shared.InsertValue;
import org.mlk.kjm.shared.OrderByClause;
import org.mlk.kjm.shared.QueryParameter;
import org.mlk.kjm.shared.QueryParameter.QueryOperator;

import static org.mlk.kjm.shared.RepositoryUtils.*;

public class UserRepositoryImpl implements UserRepository {
    private final String url;
    private final String user;
    private final String password;

    public UserRepositoryImpl(ApplicationProperties appProps) {
        this.url = appProps.getDbUrl();
        this.user = appProps.getDbUser();
        this.password = appProps.getDbPassword();
    }

    @Override
    public Optional<User> getUser(String email) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(email);

        List<OrderByClause> orderBys = new ArrayList<OrderByClause>();
        int page = 0;
        int pageLength = 1;

        List<Map<String, Object>> queryResults = queryTable(table, columns, parameters, page, pageLength, orderBys, url, user, password);
        Optional<User> result = queryResults.stream().map(queryResult -> {
            User user = mapToUser(queryResult);
            return user;
        }).findFirst();

        return result;
    }

    @Override
    public Optional<User> getUser(AuthToken token) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(token.getEmail(), token.getCode());

        List<OrderByClause> orderBys = new ArrayList<OrderByClause>();
        int page = 0;
        int pageLength = 1;

        List<Map<String, Object>> queryResults = queryTable(table, columns, parameters, page, pageLength, orderBys, url, user, password);
        Optional<User> result = queryResults.stream().map(queryResult -> {
            User user = mapToUser(queryResult);
            return user;
        }).findFirst();

        return result;
    }

    @Override
    public void setAuthToken(AuthToken authToken) throws SQLException {
        Optional<User> user = getUser(authToken.getEmail());

        if (!user.isPresent()) {
            String msg = "This user does not exist!";
            throw new SQLException(msg);
        }

        InsertValue[] insertValues = toInsertValues(authToken);
        List<QueryParameter> queryParameters = toQueryParameters(authToken.getEmail());
        int result = update(table, insertValues, queryParameters, url, this.user, password);
        if (result != 1) {
            String msg = "Did not update user as expected";
            throw new SQLException(msg);
        }
    }
    
    private List<QueryParameter> toQueryParameters(String email) {
        List<QueryParameter> parameters = new ArrayList<QueryParameter>();
        QueryParameter qp = new QueryParameter(emailColumn, QueryOperator.equals, email);
        parameters.add(qp);
        return parameters;
    }

    private List<QueryParameter> toQueryParameters(String email, UUID code) {
        List<QueryParameter> parameters = new ArrayList<QueryParameter>();
        
        QueryParameter qp1 = new QueryParameter(emailColumn, QueryOperator.equals, email);
        parameters.add(qp1);
        
        QueryParameter qp2 = new QueryParameter(codeColumn, QueryOperator.equals, code.toString());
        parameters.add(qp2);
        return parameters;
    }

    private User mapToUser(Map<String, Object> map) {
        String email = (String) map.get(emailColumn);

        int canLogin = (int) map.get(canLoginColumn);
        int canEditInmates = (int) map.get(canEditInmatesColumn);
        int canEditUsers = (int) map.get(canEditUsersColumn);
        
        Optional<String> code = map.containsKey(codeColumn) && map.get(codeColumn) != null
            ? Optional.of((String) map.get(codeColumn))
            : Optional.empty();
        
        Optional<Date> codeExpiresSql = map.containsKey(codeExpiresColumn) && map.get(codeExpiresColumn) != null
            ? Optional.of((Date) map.get(codeExpiresColumn))
            : Optional.empty();
        
        Optional<LocalDate> codeExpires = codeExpiresSql.isPresent()
            ? Optional.of(codeExpiresSql.get().toLocalDate())
            : Optional.empty();
        
        User result = new User(email, code, codeExpires, canLogin, canEditInmates, canEditUsers);
        return result;
    }

    private InsertValue[] toInsertValues(AuthToken authToken) {
        InsertValue[] insertValues = new InsertValue[2];
        insertValues[0] = new InsertValue(codeColumn, authToken.getCode().toString());
        insertValues[1] = new InsertValue(codeExpiresColumn, authToken.getExpires());

        return insertValues;
    }
}
