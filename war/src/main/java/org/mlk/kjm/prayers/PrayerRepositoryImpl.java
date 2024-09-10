package org.mlk.kjm.prayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;

import org.mlk.kjm.ServletUtils;
import org.mlk.kjm.inmates.Inmate;
import org.mlk.kjm.jails.Jail;

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
    public List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date) {
        List<Prayer> filteredFirstName = filterFirstName(prayers, firstName);
        List<Prayer> filteredLastName = filterLastName(filteredFirstName, lastName);
        List<Prayer> filteredCounty = filterCounty(filteredLastName, county);
        List<Prayer> filteredDate = filterDate(filteredCounty, date);
        return filteredDate;
    }

    private static List<Prayer> filterFirstName(List<Prayer> input, Optional<String> firstName) {
        if (firstName.isEmpty()) {
            return input;
        }

        return input.stream()
            .filter(p -> p.getInmate().getFirstName().indexOf(firstName.get()) > 0)
            .collect(toList());
    }

    private static List<Prayer> filterLastName(List<Prayer> input, Optional<String> lastName) {
        if (lastName.isEmpty()) {
            return input;
        }

        return input.stream()
            .filter(p -> p.getInmate().getLastName().indexOf(lastName.get()) > 0)
            .collect(toList());
    }

    private static List<Prayer> filterCounty(List<Prayer> input, Optional<String> county) {
        if (county.isEmpty()) {
            return input;
        }

        return input.stream()
            .filter(p -> p.getInmate().getJail().getCounty().indexOf(county.get()) > 0)
            .collect(toList());
    }

    private static List<Prayer> filterDate(List<Prayer> input, Optional<LocalDate> date) {
        if (date.isEmpty()) {
            return input;
        }

        return input.stream()
            .filter(p -> p.getDate().isEqual(date.get()))
            .collect(toList());
    }

    private static void initPrayers(PrayerRepository pr) {
        for (int x = 0; x < 10; x++) {
            Jail j = new Jail("County: " + x);
            Inmate i = new Inmate("Inmate: " + x, "Smith", j);
            pr.createPrayer(new Prayer(i, ServletUtils.stringToDate("01/01/1990"), "Prayer: " + x));
        }
    }
}
