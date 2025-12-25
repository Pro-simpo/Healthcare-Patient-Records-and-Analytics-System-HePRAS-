package ma.ensa.healthcare.transaction;

import ma.ensa.healthcare.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Template pour simplifier l'exécution de transactions
 * Pattern: Template Method
 */
public class TransactionTemplate {
    private static final Logger logger = LoggerFactory.getLogger(TransactionTemplate.class);

    /**
     * Interface fonctionnelle pour le callback de transaction
     */
    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction(Connection conn) throws SQLException;
    }

    /**
     * Exécute une opération dans une transaction
     * Gère automatiquement le commit/rollback
     * 
     * @param callback Le code à exécuter dans la transaction
     * @return Le résultat de l'opération
     * @throws RuntimeException Si une erreur survient
     */
    public <T> T execute(TransactionCallback<T> callback) {
        Connection conn = null;
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. Obtenir une connexion
            conn = DatabaseConfig.getConnection();
            logger.debug("Connexion obtenue pour la transaction");
            
            // 2. Démarrer la transaction
            TransactionManager.beginTransaction(conn);
            
            // 3. Exécuter le callback
            T result = callback.doInTransaction(conn);
            
            // 4. Commit si succès
            TransactionManager.commit(conn);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Transaction réussie en {}ms", duration);
            
            return result;
            
        } catch (Exception e) {
            // 5. Rollback en cas d'erreur
            logger.error("Erreur lors de la transaction, rollback en cours", e);
            TransactionManager.rollback(conn);
            
            // Relancer l'exception
            throw new RuntimeException("Erreur lors de la transaction : " + e.getMessage(), e);
            
        } finally {
            // 6. Toujours fermer la connexion
            TransactionManager.closeConnection(conn);
        }
    }

    /**
     * Exécute une opération dans une transaction sans retour
     */
    public void executeWithoutResult(TransactionCallbackWithoutResult callback) {
        execute(conn -> {
            callback.doInTransaction(conn);
            return null;
        });
    }

    /**
     * Interface fonctionnelle pour callback sans retour
     */
    @FunctionalInterface
    public interface TransactionCallbackWithoutResult {
        void doInTransaction(Connection conn) throws SQLException;
    }
}