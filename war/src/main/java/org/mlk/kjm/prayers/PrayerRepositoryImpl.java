package org.mlk.kjm.prayers;

import java.util.List;
import java.util.ArrayList;

public class PrayerRepositoryImpl implements PrayerRepository {
    private final List<Prayer> prayers;

    public PrayerRepositoryImpl() {
        prayers = new ArrayList<Prayer>();
    }

    @Override
    public void createPrayer(Prayer prayer) {
        prayers.add(prayer);
    }

    @Override
    public List<Prayer> getPrayers() {
        return prayers;
    }
    
}
