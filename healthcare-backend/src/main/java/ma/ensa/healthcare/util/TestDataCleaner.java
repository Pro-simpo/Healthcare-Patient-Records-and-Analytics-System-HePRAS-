package ma.ensa.healthcare.util;

import ma.ensa.healthcare.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utilitaire pour nettoyer les données de test
 */
public class TestDataCleaner {
    private static final Logger logger = LoggerFactory.getLogger(TestDataCleaner.class);

    /**
     * Nettoie toutes les données de test créées aujourd'hui
     * Respecte l'ordre des contraintes FK
     */
    public static void cleanupTodayTestData() {
        logger.info("Nettoyage des données de test...");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // 1. TRAITEMENT (dépend de CONSULTATION)
                executeDelete(conn, 
                    "DELETE FROM TRAITEMENT WHERE id_consultation IN (" +
                    "  SELECT id_consultation FROM CONSULTATION " +
                    "  WHERE date_consultation >= TRUNC(SYSDATE)" +
                    ")");
                
                // 2. FACTURE (dépend de CONSULTATION)
                executeDelete(conn, 
                    "DELETE FROM FACTURE WHERE date_facture >= TRUNC(SYSDATE)");
                
                // 3. CONSULTATION (dépend de RENDEZ_VOUS)
                executeDelete(conn, 
                    "DELETE FROM CONSULTATION WHERE date_consultation >= TRUNC(SYSDATE)");
                
                // 4. RENDEZ_VOUS (dépend de PATIENT, MEDECIN)
                executeDelete(conn, 
                    "DELETE FROM RENDEZ_VOUS WHERE date_creation >= TRUNC(SYSDATE)");
                
                // 5. UTILISATEUR (dépend de PATIENT)
                executeDelete(conn, 
                    "DELETE FROM UTILISATEUR WHERE date_creation >= TRUNC(SYSDATE)");
                
                // 6. PATIENT
                executeDelete(conn, 
                    "DELETE FROM PATIENT WHERE date_inscription >= TRUNC(SYSDATE)");
                
                // 7. MEDICAMENT (si créés aujourd'hui - optionnel)
                // executeDelete(conn, 
                //     "DELETE FROM MEDICAMENT WHERE nom_commercial LIKE '%_____'");
                
                // 8. MEDECIN (dépend de DEPARTEMENT)
                // ✅ Supprimer uniquement les médecins des départements de test
                executeDelete(conn, 
                    "DELETE FROM MEDECIN WHERE id_departement IN (" +
                    "  SELECT id_departement FROM DEPARTEMENT " +
                    "  WHERE nom_departement LIKE '%\\_%' ESCAPE '\\'" +
                    ")");
                
                // 9. DEPARTEMENT (après avoir supprimé ses médecins)
                executeDelete(conn, 
                    "DELETE FROM DEPARTEMENT WHERE nom_departement LIKE '%\\_%' ESCAPE '\\'");
                
                conn.commit();
                logger.info("✅ Nettoyage terminé avec succès");
                
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erreur lors du nettoyage", e);
                throw e;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur de connexion lors du nettoyage", e);
        }
    }

    /**
     * Nettoie TOUTES les données de test (dangereux !)
     */
    public static void cleanupAllTestData() {
        logger.warn("⚠️ NETTOYAGE COMPLET - Toutes les données vont être supprimées !");
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                executeDelete(conn, "DELETE FROM TRAITEMENT");
                executeDelete(conn, "DELETE FROM FACTURE");
                executeDelete(conn, "DELETE FROM CONSULTATION");
                executeDelete(conn, "DELETE FROM RENDEZ_VOUS");
                executeDelete(conn, "DELETE FROM UTILISATEUR");
                executeDelete(conn, "DELETE FROM PATIENT");
                executeDelete(conn, "DELETE FROM MEDICAMENT");
                executeDelete(conn, "DELETE FROM MEDECIN");
                executeDelete(conn, "DELETE FROM DEPARTEMENT");
                
                conn.commit();
                logger.info("✅ Nettoyage complet terminé");
                
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erreur lors du nettoyage complet", e);
                throw e;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur de connexion", e);
        }
    }

    /**
     * Exécute une requête DELETE et affiche le nombre de lignes supprimées
     */
    private static void executeDelete(Connection conn, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("  → {} ligne(s) supprimée(s)", rowsDeleted);
            }
        }
    }

    /**
     * Réinitialise les séquences à 1 (optionnel, dangereux)
     */
    public static void resetSequences() {
        logger.warn("⚠️ Réinitialisation des séquences à 1");
        
        String[] sequences = {
            "seq_departement",
            "seq_medecin",
            "seq_patient",
            "seq_utilisateur",
            "seq_rendez_vous",
            "seq_medicament",
            "seq_consultation",
            "seq_traitement",
            "seq_facture"
        };
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            for (String seq : sequences) {
                try (PreparedStatement pstmt = conn.prepareStatement(
                    "ALTER SEQUENCE " + seq + " RESTART START WITH 1")) {
                    pstmt.execute();
                    logger.info("  → {} réinitialisée", seq);
                } catch (SQLException e) {
                    logger.warn("Impossible de réinitialiser {}: {}", seq, e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la réinitialisation des séquences", e);
        }
    }
}