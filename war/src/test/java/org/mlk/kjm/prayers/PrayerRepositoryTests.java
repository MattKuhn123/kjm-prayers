package org.mlk.kjm.prayers;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.junit.Test;
import org.mlk.kjm.ServletUtils;
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

import junit.framework.TestCase;

public class PrayerRepositoryTests extends TestCase {
    
    @Test
    public void test_Something() throws Exception {
        int prayerCount = 10;

        PrayerRepository prayers = getPrayerRepository(10);

        String queryLastName = "Smi";

        Optional<String> firstName = Optional.empty();
        Optional<String> lastName = Optional.of(queryLastName);
        Optional<String> county = Optional.empty();
        Optional<LocalDate> date = Optional.empty();
        List<Prayer> result = prayers.getPrayers(firstName, lastName, county, date);

        assertEquals(result.size(), prayerCount);
    }

    private PrayerRepository getPrayerRepository(int count) {
        PrayerRepository prayers = PrayerRepositoryImpl.getInstance();
        initPrayers(prayers, count);
        return prayers;
    }

    public void initPrayers(PrayerRepository prayers, int count) {
        for (int x = 0; x < count; x++) {
            Jail j = new Jail("County: " + x);
            Inmate i = new Inmate("Inmate: " + x, "Smith", j);
            prayers.createPrayer(new Prayer(i, ServletUtils.stringToDate("01/01/1990"), "Prayer: " + x));
        }
    }
}
