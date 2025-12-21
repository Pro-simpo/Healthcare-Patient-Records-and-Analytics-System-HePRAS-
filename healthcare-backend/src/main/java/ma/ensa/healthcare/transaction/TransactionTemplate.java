package ma.ensa.healthcare.transaction;

import ma.ensa.healthcare.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionTemplate {
    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction(Connection conn) throws SQLException;
    }

    public <T> T execute(TransactionCallback<T> callback) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Début transaction
            
            T result = callback.doInTransaction(conn);
            
            conn.commit(); // Succès
            return result;
        } catch (Exception e) {
            TransactionManager.rollback(conn); // Échec
            throw new RuntimeException("Erreur lors de la transaction : " + e.getMessage(), e);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}