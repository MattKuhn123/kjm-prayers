package org.mlk.kjm.prayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;

public class PrayerRepositoryImpl implements PrayerRepository {
    private static PrayerRepository instance;

    public static PrayerRepository getInstance() {
        if (instance == null) {
            instance = new PrayerRepositoryImpl();
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

    @Override 
    public Optional<Prayer> getPrayer(String firstName, String lastName, LocalDate date) {
        Optional<Prayer> result = prayers.stream().filter(p -> true
            && firstName.equals(p.getInmate().getFirstName())
            && lastName.equals(p.getInmate().getLastName())
            && date.isEqual(p.getDate())).findFirst();

        return result;
    }

    private static List<Prayer> filterFirstName(List<Prayer> input, Optional<String> firstName) {
        if (firstName.isEmpty()) {
            return input;
        }

        return input.stream()
            .filter(p -> p.getInmate().getFirstName().toLowerCase().indexOf(firstName.get().toLowerCase()) > -1)
            .collect(toList());
    }

    private static List<Prayer> filterLastName(List<Prayer> input, Optional<String> lastName) {
        if (lastName.isEmpty()) {
            return input;
        }

        return input.stream()
            .filter(p -> p.getInmate().getLastName().toLowerCase().indexOf(lastName.get().toLowerCase()) > -1)
            .collect(toList());
    }

    private static List<Prayer> filterCounty(List<Prayer> input, Optional<String> county) {
        if (county.isEmpty()) {
            return input;
        }

        return input.stream()
            .filter(p -> p.getInmate().getJail().getCounty().toLowerCase().indexOf(county.get().toLowerCase()) > -1)
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
}
