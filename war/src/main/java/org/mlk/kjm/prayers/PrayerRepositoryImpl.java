package org.mlk.kjm.prayers;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import org.mlk.kjm.ApplicationProperties;
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

import static org.mlk.kjm.RepositoryUtils.*;

public class PrayerRepositoryImpl implements PrayerRepository {
    private static PrayerRepositoryImpl instance;

    public static PrayerRepositoryImpl getInstance(ApplicationProperties appProps) {
        if (instance == null) {
            String dbUrl = appProps.getDbUrl();
            String dbUser = appProps.getDbUser();
            String dbPassword = appProps.getDbPassword();
            instance = new PrayerRepositoryImpl(dbUrl, dbUser, dbPassword);
        }

        return instance;
    }

    private final String url;
    private final String user;
    private final String password;

    private final String comma = ",";
    private final String firstNameColumn = "first_name";
    private final String lastNameColumn = "last_name";
    private final String countyColumn = "county";
    private final String dateColumn = "date";
    private final String prayerColumn = "prayer";
    private final String[] columns = { firstNameColumn, lastNameColumn, countyColumn, dateColumn, prayerColumn };

    public PrayerRepositoryImpl(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public void createPrayer(Prayer prayer) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'createPrayer'");
    }

    @Override
    public List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date) throws SQLException {
        String sql = "SELECT " + String.join(comma, columns) + " FROM "
                + "kjm.prayers";

        List<Map<String, Object>> queryResults = query(sql, columns, url, user, password);
        List<Prayer> results = queryResults.stream().map(queryResult -> {
            Prayer prayer = mapToPrayer(queryResult);
            return prayer;
        }).collect(toList());

        return results;
    }

    @Override
    public Optional<Prayer> getPrayer(String firstName, String lastName, LocalDate date) throws SQLException {
        throw new UnsupportedOperationException("Unimplemented method 'getPrayer'");
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
