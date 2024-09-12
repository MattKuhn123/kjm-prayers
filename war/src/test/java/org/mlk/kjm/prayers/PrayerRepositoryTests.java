package org.mlk.kjm.prayers;

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

public class PrayerRepositoryTests extends TestCase {

    @Before
    public void setUp() throws Exception {
        ApplicationProperties props = ApplicationPropertiesTestImpl.getInstance();
        String url = props.getDbUrl();
        String username = props.getDbUser();
        String password = props.getDbPassword();
        RepositoryUtilsTesting.drop(url, username, password);
        RepositoryUtils.ensureCreated(url, username, password);
        RepositoryUtilsTesting.populate(url, username, password);
    }
    
    @Test
    public void test_BasicQuery() throws Exception {
        PrayerRepository test = PrayerRepositoryImpl.getInstance(ApplicationPropertiesTestImpl.getInstance());
        int page = 0;
        int pageLength = 100;
        List<Prayer> results = test.getPrayers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, pageLength, Optional.empty(), Optional.empty());

        int expectedResults = 4;
        Assert.assertEquals(expectedResults, results.size());
        
    }
}
