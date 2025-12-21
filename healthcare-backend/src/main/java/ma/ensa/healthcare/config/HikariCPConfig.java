package ma.ensa.healthcare.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuration du pool de connexions HikariCP.
 * Utilise le pattern Singleton pour fournir une source de données unique.
 * [cite: 142-143, 286]
 */
public class HikariCPConfig {

    private static final Logger logger = LoggerFactory.getLogger(HikariCPConfig.class);
    private static HikariDataSource dataSource;
    
    // Constructeur privé pour empêcher l'instanciation
    private HikariCPConfig() {}

    /**
     * Obtient l'instance unique du DataSource (Singleton).
     * Crée le pool s'il n'existe pas encore.
     */
    public static synchronized HikariDataSource getDataSource() {
        if (dataSource == null) {
            createDataSource();
        }
        return dataSource;
    }

    private static void createDataSource() {
        PropertyManager props = PropertyManager.getInstance();
        HikariConfig config = new HikariConfig();

        // 1. Configuration de base JDBC [cite: 286]
        config.setDriverClassName(props.getProperty("db.driver"));
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));

        // 2. Configuration du Pool (Taille et Timeouts) [cite: 151, 286]
        config.setMinimumIdle(props.getIntProperty("db.hikari.minimumIdle", 5));
        config.setMaximumPoolSize(props.getIntProperty("db.hikari.maximumPoolSize", 20));
        config.setConnectionTimeout(props.getIntProperty("db.hikari.connectionTimeout", 30000));
        config.setIdleTimeout(props.getIntProperty("db.hikari.idleTimeout", 600000));
        config.setMaxLifetime(props.getIntProperty("db.hikari.maxLifetime", 1800000));

        // 3. Nom du pool pour le monitoring
        config.setPoolName("Healthcare-HikariPool");

        // 4. Optimisations spécifiques Oracle [cite: 152-156, 290]
        // Active le cache des requêtes préparées pour la performance
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        // Optimisations réseau et serveur
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");

        try {
            dataSource = new HikariDataSource(config);
            logger.info("HikariCP Connection Pool démarré avec succès. URL: {}", props.getProperty("db.url"));
        } catch (Exception e) {
            logger.error("Erreur critique lors de l'initialisation de HikariCP", e);
            throw new RuntimeException("Impossible d'initialiser le pool de connexions", e);
        }
    }

    /**
     * Teste si une connexion peut être obtenue.
     * [cite: 160, 286]
     */
    public static void testConnection() {
        try (Connection conn = getDataSource().getConnection()) {
            if (conn.isValid(2)) {
                logger.info("Test Successful ! DataBase accessible.");
            }
        } catch (SQLException e) {
            logger.error("Failed to connect!", e);
        }
    }
}