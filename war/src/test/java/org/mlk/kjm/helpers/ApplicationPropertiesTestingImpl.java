package org.mlk.kjm.helpers;

import java.io.InputStream;
import java.util.Properties;

import org.mlk.kjm.shared.ApplicationProperties;

public class ApplicationPropertiesTestingImpl implements ApplicationProperties {
    private final String dbUrlProperty = "dbUrl";
    private final String dbUserProperty = "dbUser";
    private final String dbPasswordProperty = "dbPassword";
    private final String mailHostProperty = "mailHost";
    private final String mailFromProperty = "mailFrom";
    private final String mailUserProperty = "mailUser";
    private final String mailPasswordProperty = "mailPassword";

    @Override
    public String getDbUrl() {
        try {
            String result = getProperty(dbUrlProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getDbUser() {
        try {
            String result = getProperty(dbUserProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getDbPassword() {
        try {
            String result = getProperty(dbPasswordProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
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

    public String getMailHost() {
        try {
            String result = getProperty(mailHostProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String getMailFrom() {
        try {
            String result = getProperty(mailFromProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String getMailUser() {
        try {
            String result = getProperty(mailUserProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String getMailPassword() {
        try {
            String result = getProperty(mailPasswordProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

}
