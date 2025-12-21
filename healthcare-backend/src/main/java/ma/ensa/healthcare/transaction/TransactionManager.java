package ma.ensa.healthcare.transaction;

import ma.ensa.healthcare.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    public static void beginTransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    public static void commit(Connection conn) throws SQLException {
        conn.commit();
        conn.setAutoCommit(true);
    }

    public static void rollback(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}