package org.mlk.kjm;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesImpl implements ApplicationProperties {
    private static ApplicationPropertiesImpl instance;
    public static ApplicationPropertiesImpl getInstance() {
        if (instance == null) {
            instance = new ApplicationPropertiesImpl();
        }

        return instance;
    }

    private final String environmentProperty = "env";
    private final String dbUrlProperty = "dbUrl";
    private final String dbUserProperty = "dbUser";
    private final String dbPasswordProperty = "dbPassword";

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

    private String getProperty(String property) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("application." + getEnvironment() + ".properties");
        Properties properties = new Properties();
        properties.load(is);
        String result = properties.getProperty(property);
        return result;
    }

    private String getEnvironment() throws Exception {
        String result = System.getProperty(environmentProperty);
        return result;
    }
}
