package org.mlk.kjm.shared;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesImpl implements ApplicationProperties {
    private final String environmentProperty = "env";
    private final String dbUrlProperty = "dbUrl";
    private final String dbUserProperty = "dbUser";
    private final String dbPasswordProperty = "dbPassword";
    private final String mailHostProperty = "mailHost";
    private final String mailFromProperty = "mailFrom";
    private final String mailUserProperty = "mailUser";
    private final String mailPasswordProperty = "mailPassword";
    
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
        String result = System.getProperty(mailPasswordProperty);
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
}
