package org.mlk.kjm.prayers;

import static org.mlk.kjm.ServletUtils.dateToString;
import static org.mlk.kjm.ServletUtils.stringToDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mlk.kjm.ApplicationProperties;
import org.mlk.kjm.ApplicationPropertiesTestImpl;
import org.mlk.kjm.RepositoryUtils;
import org.mlk.kjm.RepositoryUtilsTesting;

import junit.framework.TestCase;

public class PrayerRepositoryQueryTests extends TestCase {

    @Test
    public void test_something() throws Exception {
        assertTrue(true);
    }

    @Before
    public void setUp() throws Exception {
        ApplicationProperties props = new ApplicationPropertiesTestImpl();
        String url = props.getDbUrl();
        String username = props.getDbUser();
        String password = props.getDbPassword();
        RepositoryUtilsTesting.drop(url, username, password);
        RepositoryUtils.ensureCreated(url, username, password);
        RepositoryUtilsTesting.populate(url, username, password);
    }
    
    @Test
    public void test_BasicQuery() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());
        int page = 0;
        int pageLength = 100;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.empty(), Optional.empty());

        int expectedResults = 4;
        Assert.assertEquals(expectedResults, results.size());
        
    }

    @Test
    public void test_BasicQuery_WhereFirstName() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        String firstName = "al";
        int page = 0;
        int pageLength = 100;
        List<Prayer> results = test.getPrayers(Optional.of(firstName), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.empty(), Optional.empty());

        int expectedResults = 2;
        Assert.assertEquals(expectedResults, results.size());
    }

    @Test
    public void test_BasicQuery_WhereLastName() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        String lastName = "Coan";
        int page = 0;
        int pageLength = 100;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.of(lastName), Optional.empty(), Optional.empty(), page, pageLength, Optional.empty(), Optional.empty());

        int expectedResults = 1;
        Assert.assertEquals(expectedResults, results.size());
    }

    @Test
    public void test_BasicQuery_WhereCounty() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        String county = "Kenton";
        int page = 0;
        int pageLength = 100;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.of(county), Optional.empty(), page, pageLength, Optional.empty(), Optional.empty());

        int expectedResults = 2;
        Assert.assertEquals(expectedResults, results.size());
    }

    @Test
    public void test_BasicQuery_WhereDate() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        String dateString = "12/25/2023";
        LocalDate date = stringToDate(dateString);
        int page = 0;
        int pageLength = 100;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(date), page, pageLength, Optional.empty(), Optional.empty());

        int expectedResults = 1;
        Assert.assertEquals(expectedResults, results.size());
    }

    @Test
    public void test_BasicQuery_limit() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        int page = 0;
        int pageLength = 2;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.empty(), Optional.empty());

        int expectedResults = 2;
        Assert.assertEquals(expectedResults, results.size());
    }

    @Test
    public void test_BasicQuery_orderAscFirstName_page1() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        int page = 0;
        int pageLength = 2;
        String orderBy = PrayerRepository.firstNameColumn;
        Boolean isAsc = true;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.of(orderBy), Optional.of(isAsc));

        String expectedFirstFirstName = "Germana";
        String expectedFirstLastName = "Coan";
        int firstIdx = 0;
        String actualFirstFirstName = results.get(firstIdx).getFirstName();
        String actualFirstLastName = results.get(firstIdx).getLastName();
        assertEquals(expectedFirstFirstName, actualFirstFirstName);
        assertEquals(actualFirstLastName, expectedFirstLastName);
        
        String expectedSecondFirstName = "Guthrie";
        String expectedSecondLastName = "Scain";
        int secondIdx= 1;
        String actualSecondFirstName = results.get(secondIdx).getFirstName();
        String actualSecondLastName = results.get(secondIdx).getLastName();
        assertEquals(expectedSecondFirstName, actualSecondFirstName);
        assertEquals(expectedSecondLastName, actualSecondLastName);
    }

    @Test
    public void test_BasicQuery_orderAscFirstName_page2() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        int page = 1;
        int pageLength = 2;
        String orderBy = PrayerRepository.firstNameColumn;
        Boolean isAsc = true;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.of(orderBy), Optional.of(isAsc));

        String expectedFirstFirstName = "Ralf";
        String expectedFirstLastName = "Grigoriev";
        int firstIdx = 0;
        String actualFirstFirstName = results.get(firstIdx).getFirstName();
        String actualFirstLastName = results.get(firstIdx).getLastName();
        assertEquals(expectedFirstFirstName, actualFirstFirstName);
        assertEquals(actualFirstLastName, expectedFirstLastName);
        
        String expectedSecondFirstName = "Ralf";
        String expectedSecondLastName = "Grigoriev";
        int secondIdx= 1;
        String actualSecondFirstName = results.get(secondIdx).getFirstName();
        String actualSecondLastName = results.get(secondIdx).getLastName();
        assertEquals(expectedSecondFirstName, actualSecondFirstName);
        assertEquals(expectedSecondLastName, actualSecondLastName);
    }

    @Test
    public void test_BasicQuery_orderDescFirstName_page1() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        int page = 0;
        int pageLength = 2;
        String orderBy = PrayerRepository.firstNameColumn;
        Boolean isAsc = false;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.of(orderBy), Optional.of(isAsc));

        String expectedFirstFirstName = "Ralf";
        String expectedFirstLastName = "Grigoriev";
        int firstIdx = 0;
        String actualFirstFirstName = results.get(firstIdx).getFirstName();
        String actualFirstLastName = results.get(firstIdx).getLastName();
        assertEquals(expectedFirstFirstName, actualFirstFirstName);
        assertEquals(actualFirstLastName, expectedFirstLastName);
        
        String expectedSecondFirstName = "Ralf";
        String expectedSecondLastName = "Grigoriev";
        int secondIdx= 1;
        String actualSecondFirstName = results.get(secondIdx).getFirstName();
        String actualSecondLastName = results.get(secondIdx).getLastName();
        assertEquals(expectedSecondFirstName, actualSecondFirstName);
        assertEquals(expectedSecondLastName, actualSecondLastName);
    }

    @Test
    public void test_BasicQuery_orderAscDate_page1() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        int page = 0;
        int pageLength = 2;
        String orderBy = PrayerRepository.dateColumn;
        Boolean isAsc = true;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.of(orderBy), Optional.of(isAsc));

        String expectedFirstFirstName = "Guthrie";
        String expectedFirstLastName = "Scain";
        int firstIdx = 0;
        String actualFirstFirstName = results.get(firstIdx).getFirstName();
        String actualFirstLastName = results.get(firstIdx).getLastName();
        assertEquals(expectedFirstFirstName, actualFirstFirstName);
        assertEquals(actualFirstLastName, expectedFirstLastName);
        
        String expectedSecondFirstName = "Germana";
        String expectedSecondLastName = "Coan";
        int secondIdx= 1;
        String actualSecondFirstName = results.get(secondIdx).getFirstName();
        String actualSecondLastName = results.get(secondIdx).getLastName();
        assertEquals(expectedSecondFirstName, actualSecondFirstName);
        assertEquals(expectedSecondLastName, actualSecondLastName);
    }

    @Test
    public void test_getSingle() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        String firstName = "Ralf";
        String lastName = "Grigoriev";
        String dateString = "12/26/2023";
        LocalDate date = stringToDate(dateString);
        Optional<Prayer> actual = test.getPrayer(firstName, lastName, date);

        assertTrue(actual.isPresent());
        assertEquals(firstName, actual.get().getFirstName());
        assertEquals(lastName, actual.get().getLastName());
        assertEquals(dateString, dateToString(actual.get().getDate()));
    }

    @Test
    public void test_Count() throws Exception {
        PrayerRepository test = new PrayerRepositoryImpl(new ApplicationPropertiesTestImpl());;
        int expected = 4;
        int actual = test.getCount(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        assertEquals(expected, actual);
    }
}
