package org.mlk.kjm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.sql.*;

public class RepositoryUtils {
    private static final String driverName = "com.mysql.cj.jdbc.Driver";

    public static List<Map<String, Object>> query(String sql, String[] columns, String url, String user,
            String password)
            throws SQLException {
        Optional<Connection> connection = Optional.empty();
        Optional<Statement> statement = Optional.empty();
        Optional<ResultSet> resultSet = Optional.empty();
        try {
            Class.forName(driverName);
            connection = Optional.of(DriverManager.getConnection(url, user, password));
            statement = Optional.of(connection.get().createStatement());
            resultSet = Optional.of(statement.get().executeQuery(sql));

            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            while (resultSet.get().next()) {
                Map<String, Object> row = new HashMap<String, Object>();
                for (String column : columns) {
                    Object field = resultSet.get().getObject(column);
                    row.put(column, field);
                }

                rows.add(row);
            }

            return rows;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            try {
                if (resultSet.isPresent()) {
                    resultSet.get().close();
                }
            } catch (SQLException e) {
            }
            try {
                if (statement.isPresent()) {
                    statement.get().close();
                }
            } catch (SQLException e) {
            }
            try {
                if (connection.isPresent()) {
                    connection.get().close();
                }
            } catch (SQLException e) {
            }
        }
    }
}
