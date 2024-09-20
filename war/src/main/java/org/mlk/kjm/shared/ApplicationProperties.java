package org.mlk.kjm.shared;

public interface ApplicationProperties {
    String getDbUrl();
    String getDbUser();
    String getDbPassword();
    String getMailHost();
    String getMailUser();
    String getMailPassword();
    String getMailFrom();
    boolean isProduction();
}
