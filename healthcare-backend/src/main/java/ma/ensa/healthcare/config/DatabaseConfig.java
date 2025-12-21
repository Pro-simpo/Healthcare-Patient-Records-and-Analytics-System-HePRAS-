package ma.ensa.healthcare.config;

import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitaire pour la gestion des connexions et des transactions.
 * Simplifie l'accès au pool et la gestion des erreurs SQL.
 * */
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    // Constructeur privé
    private DatabaseConfig() {}

    /**
     * Obtient une connexion active depuis le pool HikariCP.
     * @return Connection JDBC
     * @throws SQLException si le pool est épuisé ou la base inaccessible
     */
    public static Connection getConnection() throws SQLException {
        return HikariCPConfig.getDataSource().getConnection();
    }

    /**
     * Ferme une connexion (la renvoie au pool).
     * @param connection La connexion à fermer
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Erreur lors de la fermeture de la connexion", e);
            }
        }
    }

    /**
     * Valide une transaction (COMMIT).
     * @param connection La connexion concernée
     */
    public static void commit(Connection connection) {
        if (connection != null) {
            try {
                connection.commit();
                logger.debug("Transaction validée (COMMIT)");
            } catch (SQLException e) {
                logger.error("Erreur lors du commit", e);
            }
        }
    }

    /**
     * Annule une transaction (ROLLBACK).
     * @param connection La connexion concernée
     */
    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                logger.warn("Transaction annulée (ROLLBACK)");
            } catch (SQLException e) {
                logger.error("Erreur lors du rollback", e);
            }
        }
    }
}