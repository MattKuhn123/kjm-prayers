package org.mlk.kjm.prayers;

import java.util.List;

public interface PrayerRepository {

    void createPrayer(Prayer prayer);

    List<Prayer> getPrayers();
}
