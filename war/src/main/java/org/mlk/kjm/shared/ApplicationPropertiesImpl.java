package org.mlk.kjm.shared;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesImpl implements ApplicationProperties {
    public String getDbUrl() {
        try {
            String result = getProperty(dbUrlProperty);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    
    public String getDbUser() {
        String result = System.getProperty(dbUserProperty);
        return result;
    }

    public String getDbPassword() {
        String result = System.getProperty(dbPasswordProperty);
        return result;
    }

    public boolean isProduction() {
        String environment = getEnvironment();
        boolean result = "prod".equals(environment);
        return result;
    }

    private String getProperty(String property) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("application." + getEnvironment() + ".properties");
        Properties properties = new Properties();
        properties.load(is);
        String result = properties.getProperty(property);
        return result;
    }

    private String getEnvironment() {
        String result = System.getProperty(environmentProperty);
        return result;
    }

    public String getEmailAddress() {
        String result = System.getProperty(mailUserProperty);
        return result;
    }

    public String getEmailPassword() {
        String result = System.getProperty(mailPasswordProperty);
        return result;
    }
}
