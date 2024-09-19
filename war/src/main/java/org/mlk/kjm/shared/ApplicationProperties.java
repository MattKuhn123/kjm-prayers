package org.mlk.kjm.shared;

public interface ApplicationProperties {
    String getDbUrl();
    String getDbUser();
    String getDbPassword();
    boolean isProduction();
}
