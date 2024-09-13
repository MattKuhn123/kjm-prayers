package org.mlk.kjm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;

import static java.util.stream.Collectors.toList;
import java.sql.*;

public class RepositoryUtils {
    public static final String createSchema = "sql/createSchema.sql";
    public static final String driverName = "com.mysql.cj.jdbc.Driver";

    public static void ensureCreated(String url, String user, String password) throws IOException, SQLException {
        executeFromFile(url, user, password, createSchema);
    }

    public static void executeFromFile(String url, String user, String password, String file) throws IOException, SQLException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(file);
        byte[] bytes = inputStream.readAllBytes();
        String sql = new String(bytes).replaceAll("/n", "");
        String[] statementSqls = sql.split(";");


        Connection connection = DriverManager.getConnection(url, user, password);
        for (String statementSql : statementSqls) {
            if ("".equals(statementSql) || null == statementSql) {
                continue;
            }

            if ("".equals(statementSql.trim())) {
                continue;
            }

            Statement statement = connection.createStatement();
            statement.execute(statementSql);
            statement.close();
        }

        connection.close();
    } 

    public static List<Map<String, Object>> query(
            String table,
            String[] projection,
            List<QueryParameter> parameters,
            int page,
            int pageLength,
            Optional<String> orderBy,
            Optional<Boolean> orderAsc,
            String url,
            String user,
            String password)
            throws SQLException {
        Optional<Connection> connection = Optional.empty();
        Optional<PreparedStatement> statement = Optional.empty();
        Optional<ResultSet> resultSet = Optional.empty();

        try {
            Class.forName(driverName);
            connection = Optional.of(DriverManager.getConnection(url, user, password));
            statement = Optional.of(
                    getQueryPreparedStatement(connection.get(), table, projection, parameters, page, pageLength, orderBy,
                            orderAsc));
            resultSet = Optional.of(statement.get().executeQuery());

            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            while (resultSet.get().next()) {
                Map<String, Object> row = new HashMap<String, Object>();
                for (String column : projection) {
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

    private static PreparedStatement getQueryPreparedStatement(
            Connection connection,
            String table,
            String[] projection,
            List<QueryParameter> parameters,
            int page,
            int pageLength,
            Optional<String> orderBy,
            Optional<Boolean> orderAsc) throws SQLException {
        String sql = toSelectClause(projection)
                + toFromClause(table)
                + toWhereClause(parameters)
                + toOrderByClause(orderBy, orderAsc)
                + toLimitClause(page, pageLength);

        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.size(); i++) {
            statement.setString(i + 1, parameters.get(i).toPreparedStatementValue());
        }

        return statement;
    }

    private static String toSelectClause(String[] projection) {
        String comma = "`,`";
        return " SELECT `" + String.join(comma, projection) + "` ";
    }

    private static String toFromClause(String table) {
        return " FROM " + table + " ";
    }

    private static String toWhereClause(List<QueryParameter> parameters) {
        if (parameters.size() == 0) {
            return "";
        }

        List<String> list = parameters.stream().map(qp -> qp.toSqlStringValue()).collect(toList());
        String and = " AND ";
        String whereClause = String.join(and, list);
        return " WHERE " + whereClause;
    }

    private static String toOrderByClause(Optional<String> orderBy, Optional<Boolean> orderAsc) {
        if (orderBy.isEmpty()) {
            return "";
        }

        if (orderAsc.isEmpty()) {
            String warning = "order direction is unexpectedly empty. Default to ASC";
            System.out.println(warning);
        }

        Boolean defaultTo = true;
        return " ORDER BY " + orderBy.get() + " " + (orderAsc.orElse(defaultTo) ? " ASC " : " DESC ");
    }

    private static String toLimitClause(int page, int pageLength) {
        int offset = page * pageLength;
        return " LIMIT " + pageLength + " OFFSET " + offset;
    }
}
