package org.mlk.kjm.prayers;

import java.util.List;

import org.mlk.kjm.ServletUtils;
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

import java.util.ArrayList;

public class PrayerRepositoryImpl implements PrayerRepository {
    private static PrayerRepository instance;
    public static PrayerRepository getInstance() {
        if (instance == null) {
            instance = new PrayerRepositoryImpl();
            initPrayers(instance);
        }

        return instance;
    }
    
    private final List<Prayer> prayers;

    private PrayerRepositoryImpl() {
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
    
    private static void initPrayers(PrayerRepository pr) {
        for (int x = 0; x < 10; x++) {
            Jail j = new Jail("County: " + x);
            Inmate i = new Inmate("Inmate: " + x, "Smith", j);
            pr.createPrayer(new Prayer(i, ServletUtils.stringToDate("01/01/1990"), "Prayer: " + x));
        }
    }
}
