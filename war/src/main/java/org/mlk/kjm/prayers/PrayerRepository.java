package org.mlk.kjm.prayers;

import java.util.List;
import java.util.Optional;

import java.sql.SQLException;
import java.time.LocalDate;

public interface PrayerRepository {
    public final static String firstNameColumn = "first_name";
    public final static String lastNameColumn = "last_name";
    public final static String countyColumn = "county";
    public final static String dateColumn = "date";
    public final static String prayerColumn = "prayer";
    public final static String table = "kjm.prayers";
    public final static String[] columns = { firstNameColumn, lastNameColumn, countyColumn, dateColumn, prayerColumn };

    int createPrayer(Prayer prayer) throws SQLException;

    List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date, int page, int pageLength, Optional<String> orderByEnum,
            Optional<Boolean> orderAsc) throws SQLException;

    int getCount(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date) throws SQLException;

    Optional<Prayer> getPrayer(String firstName, String lastName, LocalDate date) throws SQLException;
}
