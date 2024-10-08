package org.mlk.kjm.prayers;

import static org.junit.Assert.assertThrows;
import static org.mlk.kjm.shared.ServletUtils.stringToDate;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mlk.kjm.helpers.ApplicationPropertiesTestingImpl;
import org.mlk.kjm.helpers.RepositoryUtilsTesting;
import org.mlk.kjm.shared.ApplicationProperties;
import org.mlk.kjm.shared.RepositoryUtils;

import junit.framework.TestCase;

public class PrayerRepositoryInsertTests extends TestCase {
    
    @Test
    public void test_something() throws Exception {
        assertTrue(true);
    }

    @Before
    public void setUp() throws Exception {
        ApplicationProperties props = new ApplicationPropertiesTestingImpl();
        String url = props.getDbUrl();
        String username = props.getDbUser();
        String password = props.getDbPassword();
        RepositoryUtilsTesting.drop(url, username, password);
        RepositoryUtils.ensureCreated(url, username, password);
        RepositoryUtilsTesting.populate(url, username, password);
    }

    @Test
    public void zest_insertNonExistingInmate_expectError() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestingImpl());

        String firstName = "Matt";
        String lastName = "Kuhn";
        String county = "Kenton";
        String dateString = "01/01/1990";
        LocalDate date = stringToDate(dateString);
        String prayer = "I want to be alive";

        Prayer p = new Prayer(firstName, lastName, county, date, prayer);
        SQLException result = assertThrows(SQLException.class, () -> {
            test.createPrayer(p);
        });

        assertNotNull(result);
    }

    @Test
    public void zest_insertRealInmate() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestingImpl());

        String firstName = "Bettina";
        String lastName = "Venditti";
        String county = "Grant";
        String dateString = "01/01/1990";
        LocalDate date = stringToDate(dateString);
        String prayer = "I want to be alive";

        Prayer p = new Prayer(firstName, lastName, county, date, prayer);

        int expected = 1;
        int result = test.createPrayer(p);
        assertEquals(expected, result);

        Optional<Prayer> actualPrayer = test.getPrayer(firstName, lastName, date);
        assertTrue(actualPrayer.isPresent());

        assertEquals(prayer, actualPrayer.get().getPrayer());
    }
}
