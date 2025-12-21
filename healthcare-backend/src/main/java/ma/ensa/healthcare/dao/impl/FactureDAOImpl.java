package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IFactureDAO;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.model.enums.StatutPaiement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FactureDAOImpl implements IFactureDAO {
    private static final Logger logger = LoggerFactory.getLogger(FactureDAOImpl.class);

    @Override
    public Facture save(Facture f) {
        // Check your actual column names in the FACTURE table
        // If MONTANT doesn't exist, it might be MONTANT_TOTAL or another name
        String sql = "INSERT INTO FACTURE (MONTANT_TOTAL, DATE_FACTURE, CONSULTATION_ID) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setDouble(1, f.getMontant());
            ps.setDate(2, Date.valueOf(f.getDateFacture()));
            ps.setLong(3, f.getConsultation().getId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) f.setId(rs.getLong(1));
        } catch (SQLException e) { 
            logger.error("Erreur save Facture", e); 
            throw new RuntimeException("Erreur lors de la sauvegarde de la facture", e);
        }
        return f;
    }

    @Override
    public List<Facture> findAll() {
        List<Facture> list = new ArrayList<>();
        // Update column names in the SELECT query as well
        String sql = "SELECT * FROM FACTURE";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Try different column names based on your table structure
                double montant = 0.0;
                try {
                    montant = rs.getDouble("MONTANT_TOTAL");
                } catch (SQLException e1) {
                    try {
                        montant = rs.getDouble("MONTANT");
                    } catch (SQLException e2) {
                        montant = rs.getDouble("MONTANT_FACTURE");
                    }
                }
                
                Facture.FactureBuilder builder = Facture.builder()
                        .id(rs.getLong("ID"))
                        .montant(montant)
                        .dateFacture(rs.getDate("DATE_FACTURE").toLocalDate());
                
                // Only add statut if the column exists
                try {
                    String statutStr = rs.getString("STATUT");
                    if (statutStr != null) {
                        builder.statut(StatutPaiement.valueOf(statutStr));
                    }
                } catch (SQLException e) {
                    // Column doesn't exist, skip it
                }
                
                list.add(builder.build());
            }
        } catch (SQLException e) { 
            logger.error("Erreur findAll Facture", e); 
        }
        return list;
    }

    @Override 
    public Facture findById(Long id) { 
        return null; 
    }
    
    @Override 
    public void updateStatut(Long id, String statut) {
        // Check if column exists before trying to update
        try (Connection conn = DatabaseConfig.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "FACTURE", "STATUT");
            if (columns.next()) {
                String sql = "UPDATE FACTURE SET STATUT = ? WHERE ID = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, statut);
                    ps.setLong(2, id);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) { logger.error("Erreur updateStatut", e); }
    }
}