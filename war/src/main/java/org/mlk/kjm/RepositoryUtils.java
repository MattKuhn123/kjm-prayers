package org.mlk.kjm;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static void executeFromFile(String url, String user, String password, String file)
            throws IOException, SQLException {
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

    public static int queryTableCount(String table, List<QueryParameter> parameters, String url, String user,
            String password) throws SQLException {
        Optional<Connection> connection = Optional.empty();
        Optional<PreparedStatement> statement = Optional.empty();
        Optional<ResultSet> resultSet = Optional.empty();
        try {
            Class.forName(driverName);
            connection = Optional.of(DriverManager.getConnection(url, user, password));
            statement = Optional.of(getQueryCountPreparedStatement(connection.get(), table, parameters));
            resultSet = Optional.of(statement.get().executeQuery());
            resultSet.get().next();
            int columnIndex = 1;
            int result = resultSet.get().getInt(columnIndex);
            return result;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return -1;
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

    public static List<Map<String, Object>> queryTable(
            String table,
            String[] projection,
            List<QueryParameter> parameters,
            int page,
            int pageLength,
            List<OrderByClause> orderBys,
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
                    getQueryPreparedStatement(connection.get(), table, projection, parameters, page, pageLength,
                            orderBys));
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

    public static int insert(String table,
            InsertValue[] insertValues,
            String url,
            String user,
            String password)
            throws SQLException {
        Optional<Connection> connection = Optional.empty();
        Optional<PreparedStatement> statement = Optional.empty();

        try {
            Class.forName(driverName);
            connection = Optional.of(DriverManager.getConnection(url, user, password));
            statement = Optional.of(getInsertPreparedStatement(connection.get(), table, insertValues));
            int result = statement.get().executeUpdate();
            return result;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
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

    public static int update(String table,
            InsertValue[] insertValues,
            List<QueryParameter> where,
            String url,
            String user,
            String password)
            throws SQLException {
        Optional<Connection> connection = Optional.empty();
        Optional<PreparedStatement> statement = Optional.empty();

        try {
            Class.forName(driverName);
            connection = Optional.of(DriverManager.getConnection(url, user, password));
            statement = Optional.of(getUpdatePreparedStatement(connection.get(), table, insertValues, where));
            int result = statement.get().executeUpdate();
            return result;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
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

    private static PreparedStatement getUpdatePreparedStatement(
            Connection connection,
            String table,
            InsertValue[] insertValues,
            List<QueryParameter> where) throws SQLException {
        String comma = " , ";
        List<String> updates = Arrays.stream(insertValues).map(qp -> {
            String result = "`" + qp.getColumn() + "`" + " = " + qp.toSqlStringValue();
            return result;
        }).collect(toList());
        String updateClause = String.join(comma, updates);
        String whereClause = toWhereClause(where);
        String sql = " UPDATE " + table + " SET " + updateClause + " " + whereClause;

        PreparedStatement statement = connection.prepareStatement(sql);

        for (int i = 0; i < insertValues.length; i++) {
            statement.setString(i + 1, insertValues[i].toPreparedStatementValue());
        }

        for (int j = 0; j < where.size(); j++) {
            statement.setString(insertValues.length + j + 1, where.get(j).toPreparedStatementValue());
        }

        return statement;
    }

    private static PreparedStatement getInsertPreparedStatement(
            Connection connection,
            String table,
            InsertValue[] insertValues) throws SQLException {
        String comma = " , ";
        List<String> columns = Arrays.stream(insertValues).map(qp -> qp.getColumn()).collect(toList());
        String columnsSqlString = String.join(comma, columns);
        List<String> values = Arrays.stream(insertValues).map(qp -> qp.toSqlStringValue()).collect(toList());
        String valuesSqlString = String.join(comma, values);
        String sql = " INSERT INTO " + table + "(" + columnsSqlString + ") VALUES (" + valuesSqlString + ")";
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < insertValues.length; i++) {
            statement.setString(i + 1, insertValues[i].toPreparedStatementValue());
        }

        return statement;
    }

    private static PreparedStatement getQueryPreparedStatement(
            Connection connection,
            String table,
            String[] projection,
            List<QueryParameter> parameters,
            int page,
            int pageLength,
            List<OrderByClause> orderBys) throws SQLException {
        String sql = toSelectClause(projection)
                + toFromClause(table)
                + toWhereClause(parameters)
                + toOrderByClause(orderBys)
                + toLimitClause(page, pageLength);

        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.size(); i++) {
            statement.setString(i + 1, parameters.get(i).toPreparedStatementValue());
        }

        return statement;
    }

    private static PreparedStatement getQueryCountPreparedStatement(
            Connection connection,
            String table,
            List<QueryParameter> parameters) throws SQLException {
        String sql = "SELECT COUNT(*) "
                + toFromClause(table)
                + toWhereClause(parameters);

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
        String result = " WHERE " + whereClause;
        return result;
    }

    private static String toOrderByClause(List<OrderByClause> orderBys) {
        if (orderBys.size() == 0) {
            return "";
        }

        List<String> list = orderBys.stream().map(ob -> ob.toSqlStringValue()).collect(toList());
        String comma = " , ";
        String orderByClause = String.join(comma, list);
        String result = " ORDER BY " + orderByClause;
        return result;
    }

    private static String toLimitClause(int page, int pageLength) {
        int offset = page * pageLength;
        return " LIMIT " + pageLength + " OFFSET " + offset;
    }
}
