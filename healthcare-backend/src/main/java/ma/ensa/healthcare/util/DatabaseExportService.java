package ma.ensa.healthcare.util;

import ma.ensa.healthcare.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service d'export de la base de données Oracle vers fichier SQL
 * Exporte la structure (DDL) et les données (INSERT)
 */
public class DatabaseExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExportService.class);
    private static final DateTimeFormatter FILENAME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    // Liste des tables dans l'ordre de dépendance (pour respecter les FK)
    private static final String[] TABLES_ORDER = {
        "DEPARTEMENT",
        "MEDECIN", 
        "PATIENT",
        "UTILISATEUR",
        "RENDEZ_VOUS",
        "MEDICAMENT",
        "CONSULTATION",
        "TRAITEMENT",
        "FACTURE"
    };
    
    /**
     * Exporte la base de données complète (structure + données)
     * 
     * @param outputDirectory Répertoire de sortie (ex: "./exports")
     * @return Chemin complet du fichier créé
     */
    public static String exportDatabase(String outputDirectory) throws SQLException, IOException {
        logger.info("Début de l'export de la base de données...");
        
        // Créer le répertoire si nécessaire
        Path dirPath = Paths.get(outputDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            logger.info("Répertoire créé: {}", outputDirectory);
        }
        
        // Générer le nom du fichier avec timestamp
        String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
        String filename = String.format("healthcare_export_%s.sql", timestamp);
        String filepath = Paths.get(outputDirectory, filename).toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            // En-tête du fichier
            writeHeader(writer);
            
            // Export structure + données pour chaque table
            for (String tableName : TABLES_ORDER) {
                logger.info("Export de la table: {}", tableName);
                exportTable(tableName, writer);
            }
            
            // Pied de page
            writeFooter(writer);
            
            writer.flush();
        }
        
        // Calculer la taille du fichier
        File exportFile = new File(filepath);
        long fileSizeKB = exportFile.length() / 1024;
        
        logger.info("Export terminé avec succès: {} ({} KB)", filepath, fileSizeKB);
        return filepath;
    }
    
    /**
     * Exporte uniquement les données (INSERT statements)
     */
    public static String exportDataOnly(String outputDirectory) throws SQLException, IOException {
        logger.info("Début de l'export des données uniquement...");
        
        Path dirPath = Paths.get(outputDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
        String filename = String.format("healthcare_data_%s.sql", timestamp);
        String filepath = Paths.get(outputDirectory, filename).toString();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writeHeader(writer);
            
            for (String tableName : TABLES_ORDER) {
                logger.info("Export données: {}", tableName);
                exportTableData(tableName, writer);
            }
            
            writeFooter(writer);
            writer.flush();
        }
        
        logger.info("Export données terminé: {}", filepath);
        return filepath;
    }
    
    /**
     * Exporte une table complète (structure + données)
     */
    private static void exportTable(String tableName, BufferedWriter writer) throws SQLException, IOException {
        writer.write("\n-- ========================================\n");
        writer.write(String.format("-- Table: %s\n", tableName));
        writer.write("-- ========================================\n\n");
        
        // Structure de la table
        exportTableStructure(tableName, writer);
        
        // Données
        exportTableData(tableName, writer);
    }
    
    /**
     * Exporte la structure d'une table (CREATE TABLE)
     */
    private static void exportTableStructure(String tableName, BufferedWriter writer) 
            throws SQLException, IOException {
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Récupérer les colonnes
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            
            writer.write(String.format("CREATE TABLE %s (\n", tableName));
            
            List<String> columnDefinitions = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                String nullable = columns.getString("IS_NULLABLE");
                
                StringBuilder colDef = new StringBuilder();
                colDef.append("  ").append(columnName).append(" ");
                
                // Type de données
                if (dataType.equals("NUMBER")) {
                    int decimalDigits = columns.getInt("DECIMAL_DIGITS");
                    if (decimalDigits > 0) {
                        colDef.append(String.format("NUMBER(%d,%d)", columnSize, decimalDigits));
                    } else {
                        colDef.append(String.format("NUMBER(%d)", columnSize));
                    }
                } else if (dataType.equals("VARCHAR2")) {
                    colDef.append(String.format("VARCHAR2(%d)", columnSize));
                } else if (dataType.equals("DATE") || dataType.equals("TIMESTAMP")) {
                    colDef.append(dataType);
                } else {
                    colDef.append(dataType);
                }
                
                // NOT NULL
                if ("NO".equals(nullable)) {
                    colDef.append(" NOT NULL");
                }
                
                columnDefinitions.add(colDef.toString());
            }
            
            // Écrire les colonnes
            writer.write(String.join(",\n", columnDefinitions));
            writer.write("\n);\n\n");
        }
    }
    
    /**
     * Exporte les données d'une table (INSERT statements)
     */
    private static void exportTableData(String tableName, BufferedWriter writer) 
            throws SQLException, IOException {
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String query = String.format("SELECT * FROM %s", tableName);
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            
            int columnCount = metaData.getColumnCount();
            int rowCount = 0;
            
            while (rs.next()) {
                StringBuilder insert = new StringBuilder();
                insert.append(String.format("INSERT INTO %s (", tableName));
                
                // Noms des colonnes
                List<String> columnNames = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }
                insert.append(String.join(", ", columnNames));
                insert.append(") VALUES (");
                
                // Valeurs
                List<String> values = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    
                    if (value == null) {
                        values.add("NULL");
                    } else if (value instanceof String) {
                        // Échapper les apostrophes
                        String strValue = value.toString().replace("'", "''");
                        values.add(String.format("'%s'", strValue));
                    } else if (value instanceof Date || value instanceof Timestamp) {
                        values.add(String.format("TO_DATE('%s', 'YYYY-MM-DD HH24:MI:SS')", 
                            value.toString()));
                    } else if (value instanceof Number) {
                        values.add(value.toString());
                    } else {
                        values.add(String.format("'%s'", value.toString()));
                    }
                }
                
                insert.append(String.join(", ", values));
                insert.append(");\n");
                
                writer.write(insert.toString());
                rowCount++;
            }
            
            writer.write(String.format("-- %d ligne(s) exportée(s)\n\n", rowCount));
            logger.debug("{}: {} ligne(s) exportée(s)", tableName, rowCount);
        }
    }
    
    /**
     * Écrit l'en-tête du fichier SQL
     */
    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("-- ================================================================\n");
        writer.write("-- HEALTHCARE SYSTEM - EXPORT BASE DE DONNÉES\n");
        writer.write(String.format("-- Date: %s\n", LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        writer.write("-- ================================================================\n");
        writer.write("-- ATTENTION: Cet export contient la structure et les données\n");
        writer.write("-- Assurez-vous de sauvegarder votre base avant d'importer\n");
        writer.write("-- ================================================================\n\n");
        
        writer.write("SET DEFINE OFF;\n");
        writer.write("SET SERVEROUTPUT ON;\n\n");
    }
    
    /**
     * Écrit le pied de page du fichier SQL
     */
    private static void writeFooter(BufferedWriter writer) throws IOException {
        writer.write("\n-- ================================================================\n");
        writer.write("-- FIN DE L'EXPORT\n");
        writer.write("-- ================================================================\n");
        writer.write("\nCOMMIT;\n");
    }
    
    /**
     * Compte le nombre total d'enregistrements dans la base
     */
    public static int getTotalRecordCount() throws SQLException {
        int total = 0;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String tableName : TABLES_ORDER) {
                String query = String.format("SELECT COUNT(*) FROM %s", tableName);
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    total += rs.getInt(1);
                }
            }
        }
        
        return total;
    }
    
    /**
     * Retourne des statistiques sur la base de données
     */
    public static String getDatabaseStats() throws SQLException {
        StringBuilder stats = new StringBuilder();
        stats.append("Statistiques de la base de données:\n\n");
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String tableName : TABLES_ORDER) {
                String query = String.format("SELECT COUNT(*) FROM %s", tableName);
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    int count = rs.getInt(1);
                    stats.append(String.format("  %-20s: %5d ligne(s)\n", tableName, count));
                }
            }
        }
        
        return stats.toString();
    }
}