package org.mlk.kjm;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesTestImpl implements ApplicationProperties {
    private final String dbUrlProperty = "dbUrl";
    private final String dbUserProperty = "dbUser";
    private final String dbPasswordProperty = "dbPassword";

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
}
