package org.mlk.kjm.shared;

public interface ApplicationProperties {
    final static String environmentProperty = "env";
    final static String dbUrlProperty = "dbUrl";
    final static String dbUserProperty = "dbUser";
    final static String dbPasswordProperty = "dbPassword";
    final static String mailUserProperty = "mailUser";
    final static String mailPasswordProperty = "mailPassword";

    String getDbUrl();
    String getDbUser();
    String getDbPassword();
    boolean isProduction();
    String getEmailAddress();
    String getEmailPassword();
}
