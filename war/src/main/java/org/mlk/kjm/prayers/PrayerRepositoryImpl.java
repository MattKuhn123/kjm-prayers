package org.mlk.kjm.prayers;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.mlk.kjm.shared.ApplicationProperties;
import org.mlk.kjm.shared.InsertValue;
import org.mlk.kjm.shared.OrderByClause;
import org.mlk.kjm.shared.QueryParameter;
import org.mlk.kjm.shared.QueryParameter.QueryOperator;

import static java.util.stream.Collectors.toList;
import static org.mlk.kjm.shared.RepositoryUtils.*;

public class PrayerRepositoryImpl implements PrayerRepository {
    private final String url;
    private final String user;
    private final String password;

    public PrayerRepositoryImpl(ApplicationProperties appProps) {
        this.url = appProps.getDbUrl();
        this.user = appProps.getDbUser();
        this.password = appProps.getDbPassword();
    }

    @Override
    public int createPrayer(Prayer prayer) throws SQLException {
        InsertValue[] insertValue = toInsertValue(prayer);
        int result = insert(table, insertValue, url, user, password);
        return result;
    }

    @Override
    public List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date, Optional<String> prayerText, int page, int pageLength, Optional<String> orderByEnum,
            Optional<Boolean> orderAsc)
            throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, county, date, prayerText);

        List<OrderByClause> orderBys = toOrderBys(orderByEnum, orderAsc);

        List<Map<String, Object>> queryResults = queryTable(table, columns, parameters, page, pageLength, orderBys, url, user, password);
        List<Prayer> results = queryResults.stream().map(queryResult -> {
            Prayer prayer = mapToPrayer(queryResult);
            return prayer;
        }).collect(toList());

        return results;
    }

    @Override
    public Optional<Prayer> getPrayer(String firstName, String lastName, LocalDate date) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, date);
        int page = 0;
        int pageLength = 1;
        List<Map<String, Object>> queryResults = queryTable(table, columns, parameters, page, pageLength,
                new ArrayList<OrderByClause>(), url, user, password);
        Optional<Prayer> result = queryResults.stream().map(queryResult -> {
            Prayer prayer = mapToPrayer(queryResult);
            return prayer;
        }).findFirst();

        return result;
    }

    @Override
    public int getCount(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date, Optional<String> prayertext) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, county, date, prayertext);
        int result = queryTableCount(table, parameters, url, user, password);
        return result;
    }

    private List<OrderByClause> toOrderBys(Optional<String> orderByColumn, Optional<Boolean> isAsc) throws SQLException {
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

        if (orderByColumn.isPresent() && orderByColumn.get().equals(dateColumn)) {
            return result;
        }

        boolean defaultIsAsc = false;
        result.add(new OrderByClause(dateColumn, defaultIsAsc));
        return result;
    }

    private List<QueryParameter> toQueryParameters(Optional<String> firstName, Optional<String> lastName,
            Optional<String> county, Optional<LocalDate> date, Optional<String> prayerText) {
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

        if (date.isPresent()) {
            QueryParameter qp = new QueryParameter(dateColumn, QueryOperator.equals, date.get());
            parameters.add(qp);
        }

        if (prayerText.isPresent()) {
            QueryParameter qp = new QueryParameter(prayerColumn, QueryOperator.like, prayerText.get());
            parameters.add(qp);
        }

        return parameters;
    }

    private List<QueryParameter> toQueryParameters(String firstName, String lastName, LocalDate date) {
        List<QueryParameter> parameters = new ArrayList<QueryParameter>();
        QueryParameter qp1 = new QueryParameter(firstNameColumn, QueryOperator.like, firstName);
        parameters.add(qp1);
        QueryParameter qp2 = new QueryParameter(lastNameColumn, QueryOperator.like, lastName);
        parameters.add(qp2);
        QueryParameter qp3 = new QueryParameter(dateColumn, QueryOperator.equals, date);
        parameters.add(qp3);
        return parameters;
    }

    private Prayer mapToPrayer(Map<String, Object> map) {
        String firstName = (String) map.get(firstNameColumn);
        String lastName = (String) map.get(lastNameColumn);
        String county = (String) map.get(countyColumn);
        Date dateSql = (Date) map.get(dateColumn);
        LocalDate date = dateSql.toLocalDate();
        String prayer = (String) map.get(prayerColumn);
        Prayer result = new Prayer(firstName, lastName, county, date, prayer);
        return result;
    }

    private InsertValue[] toInsertValue(Prayer prayer) {
        InsertValue firstNameIV = new InsertValue(firstNameColumn, prayer.getFirstName());
        InsertValue lastNameIV = new InsertValue(lastNameColumn, prayer.getLastName());
        InsertValue countyIV = new InsertValue(countyColumn, prayer.getCounty());
        InsertValue dateIV = new InsertValue(dateColumn, prayer.getDate());
        InsertValue prayerIV = new InsertValue(prayerColumn, prayer.getPrayer());
        return new InsertValue[] {
            firstNameIV, lastNameIV, countyIV, dateIV, prayerIV
        };
    }
}
