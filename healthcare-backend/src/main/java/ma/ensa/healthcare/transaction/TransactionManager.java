package ma.ensa.healthcare.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestionnaire de transactions SQL
 * Simplifie la gestion des transactions JDBC
 */
public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    /**
     * Démarre une transaction en désactivant l'auto-commit
     */
    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("La connexion ne peut pas être null");
        }
        
        if (conn.isClosed()) {
            throw new SQLException("La connexion est fermée");
        }
        
        conn.setAutoCommit(false);
        logger.debug("Transaction démarrée");
    }

    /**
     * Valide une transaction (COMMIT) et restaure l'auto-commit
     */
    public static void commit(Connection conn) throws SQLException {
        if (conn == null) {
            logger.warn("Tentative de commit sur une connexion null");
            return;
        }
        
        if (conn.isClosed()) {
            logger.warn("Tentative de commit sur une connexion fermée");
            return;
        }
        
        try {
            conn.commit();
            logger.debug("Transaction validée (COMMIT)");
        } finally {
            // Toujours restaurer l'auto-commit
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Erreur lors de la restauration de l'auto-commit", e);
            }
        }
    }

    /**
     * Annule une transaction (ROLLBACK)
     */
    public static void rollback(Connection conn) {
        if (conn == null) {
            logger.warn("Tentative de rollback sur une connexion null");
            return;
        }
        
        try {
            if (!conn.isClosed()) {
                conn.rollback();
                logger.warn("Transaction annulée (ROLLBACK)");
                
                // Restaurer l'auto-commit
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.error("Erreur lors de la restauration de l'auto-commit après rollback", e);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors du rollback de la transaction", e);
        }
    }

    /**
     * Ferme une connexion proprement
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                // S'assurer que l'auto-commit est restauré
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
                
                conn.close();
                logger.debug("Connexion fermée");
            } catch (SQLException e) {
                logger.error("Erreur lors de la fermeture de la connexion", e);
            }
        }
    }

    /**
     * Vérifie si une connexion est valide
     */
    public static boolean isConnectionValid(Connection conn) {
        if (conn == null) {
            return false;
        }
        
        try {
            return !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            logger.error("Erreur lors de la vérification de la connexion", e);
            return false;
        }
    }
}