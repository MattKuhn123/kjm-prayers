package org.mlk.kjm.inmates;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InmateRepository {
    public final static String firstNameColumn = "first_name";
    public final static String lastNameColumn = "last_name";
    public final static String countyColumn = "county";
    public final static String releaseDateColumn = "release_date";
    public final static String sexColumn = "sex";
    public final static String infoColumn = "info";
    public final static String table = "kjm.inmates";
    public final static String[] columns = { firstNameColumn, lastNameColumn, countyColumn, releaseDateColumn, sexColumn, infoColumn };

    int createInmate(Inmate inmate) throws SQLException;

    List<Inmate> getInmates(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> releaseDate, Optional<String> sex, int page, int pageLength, Optional<String> orderByEnum,
            Optional<Boolean> orderAsc) throws SQLException;

    int getCount(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
        Optional<LocalDate> releaseDate, Optional<String> sex) throws SQLException;

    int updateInmate(Inmate inmate, Inmate newInmate) throws SQLException;

    Optional<Inmate> getInmate(String firstName, String lastName, String county) throws SQLException;
}
