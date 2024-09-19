package org.mlk.kjm.helpers;

import java.io.IOException;
import java.sql.*;

import org.mlk.kjm.shared.RepositoryUtils;

public class RepositoryUtilsTesting {

    private static final String dropSchema = "sql/dropSchema.sql";
    private static final String createData = "sql/createData.sql";
    
    public static void drop(String url, String user, String password) throws IOException, SQLException {
        RepositoryUtils.executeFromFile(url, user, password, dropSchema);
    }

    public static void populate(String url, String user, String password) throws IOException, SQLException {
        RepositoryUtils.executeFromFile(url, user, password, createData);
    }
}
