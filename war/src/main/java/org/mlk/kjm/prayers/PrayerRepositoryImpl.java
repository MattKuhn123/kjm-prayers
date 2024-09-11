package org.mlk.kjm.prayers;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.mlk.kjm.ApplicationProperties;

import java.sql.*;

public class PrayerRepositoryImpl implements PrayerRepository {
    private static PrayerRepositoryImpl instance;

    public static PrayerRepositoryImpl getInstance(ApplicationProperties appProps) {
        if (instance == null) {
            String dbUrl = appProps.getDbUrl();
            String dbUser = appProps.getDbUser();
            String dbPassword = appProps.getDbPassword();
            instance = new PrayerRepositoryImpl(dbUrl, dbUser, dbPassword);
        }

        return instance;
    }

    private final String driverName = "com.mysql.cj.jdbc.Driver";
    private final String url;
    private final String user;
    private final String password;

    public PrayerRepositoryImpl(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        String sql = "SELECT * FROM kjm.Test;";
        String[] columns = { "id", "name" };
        try {
            List<Map<String, Object>> result = query(sql, columns);
            System.out.println("ok!");
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }

    @Override
    public void createPrayer(Prayer prayer) {
        throw new UnsupportedOperationException("Unimplemented method 'createPrayer'");
    }

    @Override
    public List<Prayer> getPrayers(Optional<String> firstName, Optional<String> lastName, Optional<String> county,
            Optional<LocalDate> date) {
        throw new UnsupportedOperationException("Unimplemented method 'getPrayers'");
    }

    @Override
    public Optional<Prayer> getPrayer(String firstName, String lastName, LocalDate date) {
        throw new UnsupportedOperationException("Unimplemented method 'getPrayer'");
    }

    private List<Map<String, Object>> query(String sql, String[] columns)
            throws SQLException, ClassNotFoundException {
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
