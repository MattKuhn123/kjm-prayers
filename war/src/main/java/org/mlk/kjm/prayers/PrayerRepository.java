package org.mlk.kjm.prayers;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface PrayerRepository {

    void createPrayer(Prayer prayer);

    List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date);

    Optional<Prayer> getPrayer(String firstName, String lastName, LocalDate date);
}
