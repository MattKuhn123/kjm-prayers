package org.mlk.kjm.helpers;

import java.io.InputStream;
import java.util.Properties;

import org.mlk.kjm.shared.ApplicationProperties;

public class ApplicationPropertiesTestingImpl implements ApplicationProperties {
    public String getDbUrl() {
        try {
            String result = getProperty(dbUrlProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String getDbUser() {
        try {
            String result = getProperty(dbUserProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String getDbPassword() {
        try {
            String result = getProperty(dbPasswordProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isProduction() {
        return false;
    }
    
    private String getProperty(String property) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("application.test.properties");
        Properties properties = new Properties();
        properties.load(is);
        String result = properties.getProperty(property);
        return result;
    }

    @Override
    public String getEmailAddress() {
        try {
            String result = getProperty(mailUserProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getEmailPassword() {
        try {
            String result = getProperty(mailPasswordProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getApiKey() {
        try {
            String result = getProperty(apiKeyProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
