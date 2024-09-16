package org.mlk.kjm.inmates;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import org.mlk.kjm.ApplicationProperties;
import org.mlk.kjm.InsertValue;
import org.mlk.kjm.OrderByClause;
import org.mlk.kjm.QueryParameter;
import org.mlk.kjm.QueryParameter.QueryOperator;

import static org.mlk.kjm.RepositoryUtils.*;

public class InmateRepositoryImpl implements InmateRepository {
    private final String url;
    private final String user;
    private final String password;

    public InmateRepositoryImpl(ApplicationProperties appProps) {
        this.url = appProps.getDbUrl();
        this.user = appProps.getDbUser();
        this.password = appProps.getDbPassword();
    }

    @Override
    public int createInmate(Inmate inmate) throws SQLException {
        InsertValue[] insertValue = toInsertValue(inmate);
        int result = insert(table, insertValue, url, user, password);
        return result;
    }

    @Override
    public List<Inmate> getInmates(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> birthDay, Optional<Boolean> isMale, int page, int pageLength,
            Optional<String> orderByEnum, Optional<Boolean> orderAsc) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, county, birthDay, isMale);

        List<OrderByClause> orderBys = toOrderBys(orderByEnum, orderAsc);

        List<Map<String, Object>> queryResults = queryTable(table, columns, parameters, page, pageLength, orderBys, url,
                user, password);
        List<Inmate> results = queryResults.stream().map(queryResult -> {
            Inmate inmate = mapToInmate(queryResult);
            return inmate;
        }).collect(toList());

        return results;
    }

    @Override
    public int getCount(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> birthDay, Optional<Boolean> isMale, int page, int pageLength,
            Optional<String> orderByEnum, Optional<Boolean> orderAsc) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, county, birthDay, isMale);
        int result = queryTableCount(table, parameters, url, user, password);
        return result;
    }

    @Override
    public Optional<Inmate> getInmate(String firstName, String lastName, String county) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, county);
        int page = 0;
        int pageLength = 1;
        List<Map<String, Object>> queryResults = queryTable(table, columns, parameters, page, pageLength,
                new ArrayList<OrderByClause>(), url, user, password);
        Optional<Inmate> result = queryResults.stream().map(queryResult -> {
            Inmate prayer = mapToInmate(queryResult);
            return prayer;
        }).findFirst();

        return result;
    }

    private InsertValue[] toInsertValue(Inmate inmate) {
        List<InsertValue> ivs = new ArrayList<InsertValue>();
        InsertValue firstNameIV = new InsertValue(firstNameColumn, inmate.getFirstName());
        ivs.add(firstNameIV);
        InsertValue lastNameIV = new InsertValue(lastNameColumn, inmate.getLastName());
        ivs.add(lastNameIV);
        InsertValue countyIV = new InsertValue(countyColumn, inmate.getCounty());
        ivs.add(countyIV);
        
        if (inmate.getBirthDay().isPresent()) {
            InsertValue birthDayIV = new InsertValue(birthdayColumn, inmate.getBirthDay().getClass());
            ivs.add(birthDayIV);
        }
        
        if (inmate.isMale().isPresent()) {
            InsertValue isMaleIV = new InsertValue(isMaleColumn, inmate.isMale().get());
            ivs.add(isMaleIV);
        }
        
        if (inmate.getInfo().isPresent()) {
            InsertValue infoIV = new InsertValue(infoColumn, inmate.getInfo().get());
            ivs.add(infoIV);
        }

        InsertValue[] result = ivs.toArray(InsertValue[]::new);
        return result;
    }

    private List<OrderByClause> toOrderBys(Optional<String> orderByColumn, Optional<Boolean> isAsc)
            throws SQLException {
        List<OrderByClause> result = new ArrayList<OrderByClause>();
        if (orderByColumn.isPresent()) {
            if (Arrays.stream(columns).noneMatch(c -> c.equals(orderByColumn.get()))) {
                throw new SQLException("Invalid order by column: " + orderByColumn.get());
            }

            String orderBy = orderByColumn.get();
            boolean defaultIsAsc = false;
            OrderByClause clause = new OrderByClause(orderBy, isAsc.orElse(defaultIsAsc));
            result.add(clause);
        }

        if (orderByColumn.isPresent() && orderByColumn.get().equals(birthdayColumn)) {
            return result;
        }

        boolean defaultIsAsc = false;
        result.add(new OrderByClause(birthdayColumn, defaultIsAsc));
        return result;
    }

    private List<QueryParameter> toQueryParameters(String firstName, String lastName, String county) {
        List<QueryParameter> parameters = new ArrayList<QueryParameter>();
        QueryParameter qp1 = new QueryParameter(firstNameColumn, QueryOperator.like, firstName);
        parameters.add(qp1);
        QueryParameter qp2 = new QueryParameter(lastNameColumn, QueryOperator.like, lastName);
        parameters.add(qp2);
        QueryParameter qp3 = new QueryParameter(countyColumn, QueryOperator.equals, county);
        parameters.add(qp3);

        return parameters;
    }

    private List<QueryParameter> toQueryParameters(Optional<String> firstName, Optional<String> lastName,
            Optional<String> county, Optional<LocalDate> birthDay, Optional<Boolean> isMale) {
        List<QueryParameter> parameters = new ArrayList<QueryParameter>();
        if (firstName.isPresent()) {
            QueryParameter qp = new QueryParameter(firstNameColumn, QueryOperator.like, firstName.get());
            parameters.add(qp);
        }

        if (lastName.isPresent()) {
            QueryParameter qp = new QueryParameter(lastNameColumn, QueryOperator.like, lastName.get());
            parameters.add(qp);
        }

        if (county.isPresent()) {
            QueryParameter qp = new QueryParameter(countyColumn, QueryOperator.equals, county.get());
            parameters.add(qp);
        }

        if (birthDay.isPresent()) {
            QueryParameter qp = new QueryParameter(birthdayColumn, QueryOperator.equals, birthDay.get());
            parameters.add(qp);
        }

        if (isMale.isPresent()) {
            QueryParameter qp = new QueryParameter(isMaleColumn, QueryOperator.equals, isMale.get());
            parameters.add(qp);
        }

        return parameters;
    }

    private Inmate mapToInmate(Map<String, Object> map) {
        String firstName = (String) map.get(firstNameColumn);
        String lastName = (String) map.get(lastNameColumn);
        String county = (String) map.get(countyColumn);
        Optional<Date> birthDaySql = (Optional<Date>) map.get(birthdayColumn);
        Optional<LocalDate> birthDay = birthDaySql.isPresent() 
            ? Optional.of(birthDaySql.get().toLocalDate()) 
            : Optional.empty();
        Optional<Boolean> isMale = (Optional<Boolean>) map.get(isMaleColumn);
        Optional<String> info = (Optional<String>) map.get(infoColumn);
        Inmate result = new Inmate(firstName, lastName, county, birthDay, isMale, info);
        return result;
    }
}
