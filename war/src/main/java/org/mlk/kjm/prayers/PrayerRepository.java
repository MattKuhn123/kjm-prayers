package org.mlk.kjm.prayers;

import java.util.List;
import java.util.Optional;

import java.sql.SQLException;
import java.time.LocalDate;

public interface PrayerRepository {

    public enum OrderBy {
        first_name,
        last_name,
        county,
        date
    }

    int createPrayer(Prayer prayer) throws SQLException;

    List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date, int page, int pageLength, Optional<OrderBy> orderByEnum,
            Optional<Boolean> orderAsc) throws SQLException;

    int getCount(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date) throws SQLException;

    Optional<Prayer> getPrayer(String firstName, String lastName, LocalDate date) throws SQLException;
}
