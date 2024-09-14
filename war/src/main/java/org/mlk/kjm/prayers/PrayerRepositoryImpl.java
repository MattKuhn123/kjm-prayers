package org.mlk.kjm.prayers;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import org.mlk.kjm.ApplicationProperties;
import org.mlk.kjm.OrderByClause;
import org.mlk.kjm.QueryParameter;
import org.mlk.kjm.QueryParameter.QueryOperator;

import static org.mlk.kjm.RepositoryUtils.*;

public class PrayerRepositoryImpl implements PrayerRepository {

    private final String url;
    private final String user;
    private final String password;

    private final String firstNameColumn = "first_name";
    private final String lastNameColumn = "last_name";
    private final String countyColumn = "county";
    private final String dateColumn = "date";
    private final String prayerColumn = "prayer";
    private final String table = "kjm.prayers";
    private final String[] columns = { firstNameColumn, lastNameColumn, countyColumn, dateColumn, prayerColumn };

    public PrayerRepositoryImpl(ApplicationProperties appProps) {
        this.url = appProps.getDbUrl();
        this.user = appProps.getDbUser();
        this.password = appProps.getDbPassword();
    }

    @Override
    public void createPrayer(Prayer prayer) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'createPrayer'");
    }

    @Override
    public List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date, int page, int pageLength, Optional<OrderBy> orderByEnum,
            Optional<Boolean> orderAsc)
            throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, county, date);

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
            Optional<LocalDate> date) throws SQLException {
        List<QueryParameter> parameters = toQueryParameters(firstName, lastName, county, date);
        int result = queryTableCount(table, parameters, url, user, password);
        return result;
    }

    private List<OrderByClause> toOrderBys(Optional<OrderBy> orderByEnum, Optional<Boolean> isAsc) {
        List<OrderByClause> result = new ArrayList<OrderByClause>();
        if (orderByEnum.isPresent()) {
            String orderBy = orderByEnum.get().toString();
            boolean defaultIsAsc = true;
            OrderByClause clause = new OrderByClause(orderBy, isAsc.orElse(defaultIsAsc));
            result.add(clause);
        }

        if (orderByEnum.isPresent() && orderByEnum.get().equals(OrderBy.date)) {
            return result;
        }

        boolean defaultIsAsc = true;
        result.add(new OrderByClause(OrderBy.date.toString(), defaultIsAsc));
        return result;
    }

    private List<QueryParameter> toQueryParameters(Optional<String> firstName, Optional<String> lastName,
            Optional<String> county, Optional<LocalDate> date) {
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
}
