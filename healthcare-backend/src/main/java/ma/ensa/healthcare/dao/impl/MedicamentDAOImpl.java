package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IMedicamentDAO;
import ma.ensa.healthcare.model.Medicament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour l'entité MEDICAMENT
 * VERSION MINIMALE - Seulement les méthodes essentielles
 */
public class MedicamentDAOImpl implements IMedicamentDAO {
    private static final Logger logger = LoggerFactory.getLogger(MedicamentDAOImpl.class);

    @Override
    public Medicament save(Medicament m) {
        String sql = "INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, " +
                     "forme, dosage, prix_unitaire, stock_disponible, stock_alerte) " +
                     "VALUES (seq_medicament.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_medicament"})) {
            
            ps.setString(1, m.getNomCommercial());
            ps.setString(2, m.getPrincipeActif());
            ps.setString(3, m.getForme());
            ps.setString(4, m.getDosage());
            ps.setBigDecimal(5, m.getPrixUnitaire());
            ps.setInt(6, m.getStockDisponible());
            ps.setInt(7, m.getStockAlerte());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getLong(1));
                }
            }
            
            logger.info("Médicament enregistré : {} (ID: {})", m.getNomCommercial(), m.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Medicament: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du médicament", e);
        }
        return m;
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM MEDICAMENT WHERE id_medicament = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicament(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Medicament: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Medicament> findAll() {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT ORDER BY nom_commercial";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToMedicament(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Medicament: {}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Medicament m) {
        String sql = "UPDATE MEDICAMENT SET nom_commercial = ?, principe_actif = ?, " +
                     "forme = ?, dosage = ?, prix_unitaire = ?, stock_disponible = ?, " +
                     "stock_alerte = ? WHERE id_medicament = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNomCommercial());
            ps.setString(2, m.getPrincipeActif());
            ps.setString(3, m.getForme());
            ps.setString(4, m.getDosage());
            ps.setBigDecimal(5, m.getPrixUnitaire());
            ps.setInt(6, m.getStockDisponible());
            ps.setInt(7, m.getStockAlerte());
            ps.setLong(8, m.getId());
            
            ps.executeUpdate();
            logger.info("Médicament mis à jour : ID {}", m.getId());
        } catch (SQLException e) {
            logger.error("Erreur update Medicament: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du médicament", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM MEDICAMENT WHERE id_medicament = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Médicament supprimé : ID {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete Medicament: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du médicament", e);
        }
    }

    @Override
    public List<Medicament> findByNom(String nom) {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT WHERE nom_commercial LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedicament(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findByNom: {}", e.getMessage(), e);
        }
        return list;
    }

    private Medicament mapResultSetToMedicament(ResultSet rs) throws SQLException {
        return Medicament.builder()
                .id(rs.getLong("id_medicament"))
                .nomCommercial(rs.getString("nom_commercial"))
                .principeActif(rs.getString("principe_actif"))
                .forme(rs.getString("forme"))
                .dosage(rs.getString("dosage"))
                .prixUnitaire(rs.getBigDecimal("prix_unitaire"))
                .stockDisponible(rs.getInt("stock_disponible"))
                .stockAlerte(rs.getInt("stock_alerte"))
                .build();
    }
}