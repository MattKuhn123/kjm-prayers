package org.mlk.kjm.inmates;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InmateRepository {
    public final static String firstNameColumn = "first_name";
    public final static String lastNameColumn = "last_name";
    public final static String countyColumn = "county";
    public final static String birthdayColumn = "birth_day";
    public final static String isMaleColumn = "is_male";
    public final static String infoColumn = "info";
    public final static String table = "kjm.inmates";
    public final static String[] columns = { firstNameColumn, lastNameColumn, countyColumn, birthdayColumn, isMaleColumn, infoColumn };

    int createInmate(Inmate inmate) throws SQLException;

    List<Inmate> getInmates(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> birthDay, Optional<Boolean> isMale, int page, int pageLength, Optional<String> orderByEnum,
            Optional<Boolean> orderAsc) throws SQLException;

    int getCount(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
        Optional<LocalDate> birthDay, Optional<Boolean> isMale) throws SQLException;

    int updateInmate(Inmate inmate, Inmate newInmate) throws SQLException;

    Optional<Inmate> getInmate(String firstName, String lastName, String county) throws SQLException;
}
