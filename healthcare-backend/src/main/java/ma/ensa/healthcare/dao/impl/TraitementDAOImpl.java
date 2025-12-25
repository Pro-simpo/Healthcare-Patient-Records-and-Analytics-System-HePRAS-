package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.ITraitementDAO;
import ma.ensa.healthcare.model.Traitement;
import ma.ensa.healthcare.model.Medicament;
import ma.ensa.healthcare.model.Consultation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour l'entité TRAITEMENT
 * VERSION MINIMALE - Correspondance exacte avec la table Oracle
 */
public class TraitementDAOImpl implements ITraitementDAO {
    private static final Logger logger = LoggerFactory.getLogger(TraitementDAOImpl.class);

    @Override
    public Traitement save(Traitement t) {
        // ✅ Colonnes exactes de la table TRAITEMENT
        String sql = "INSERT INTO TRAITEMENT (id_traitement, id_consultation, id_medicament, " +
                     "posologie, duree_traitement, instructions, quantite) " +
                     "VALUES (seq_traitement.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_traitement"})) {
            
            ps.setLong(1, t.getConsultation().getId());
            ps.setLong(2, t.getMedicament().getId());
            ps.setString(3, t.getPosologie());
            ps.setInt(4, t.getDureeTraitement());
            ps.setString(5, t.getInstructions());
            ps.setInt(6, t.getQuantite());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getLong(1));
                }
            }
            
            logger.info("Traitement créé avec succès, ID: {}", t.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Traitement: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du traitement", e);
        }
        return t;
    }

    @Override
    public Traitement findById(Long id) {
        String sql = "SELECT t.*, " +
                     "m.nom_commercial, m.principe_actif, m.forme, m.dosage " +
                     "FROM TRAITEMENT t " +
                     "JOIN MEDICAMENT m ON t.id_medicament = m.id_medicament " +
                     "WHERE t.id_traitement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTraitement(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Traitement: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Traitement> findByConsultationId(Long consultationId) {
        List<Traitement> list = new ArrayList<>();
        String sql = "SELECT t.*, " +
                     "m.nom_commercial, m.principe_actif, m.forme, m.dosage " +
                     "FROM TRAITEMENT t " +
                     "JOIN MEDICAMENT m ON t.id_medicament = m.id_medicament " +
                     "WHERE t.id_consultation = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTraitement(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findByConsultationId: {}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Traitement> findAll() {
        List<Traitement> list = new ArrayList<>();
        String sql = "SELECT t.*, " +
                     "m.nom_commercial, m.principe_actif, m.forme, m.dosage " +
                     "FROM TRAITEMENT t " +
                     "JOIN MEDICAMENT m ON t.id_medicament = m.id_medicament";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToTraitement(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Traitement: {}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Traitement t) {
        String sql = "UPDATE TRAITEMENT SET posologie = ?, duree_traitement = ?, " +
                     "instructions = ?, quantite = ? WHERE id_traitement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getPosologie());
            ps.setInt(2, t.getDureeTraitement());
            ps.setString(3, t.getInstructions());
            ps.setInt(4, t.getQuantite());
            ps.setLong(5, t.getId());
            
            ps.executeUpdate();
            logger.info("Traitement mis à jour ID: {}", t.getId());
        } catch (SQLException e) {
            logger.error("Erreur update Traitement: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du traitement", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM TRAITEMENT WHERE id_traitement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Traitement supprimé ID: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete Traitement: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du traitement", e);
        }
    }

    private Traitement mapResultSetToTraitement(ResultSet rs) throws SQLException {
        // Reconstruction de l'objet Consultation (minimal)
        Consultation c = new Consultation();
        c.setId(rs.getLong("id_consultation"));
        
        // Reconstruction de l'objet Medicament (avec détails)
        Medicament m = Medicament.builder()
                .id(rs.getLong("id_medicament"))
                .nomCommercial(rs.getString("nom_commercial"))
                .principeActif(rs.getString("principe_actif"))
                .forme(rs.getString("forme"))
                .dosage(rs.getString("dosage"))
                .build();
        
        return Traitement.builder()
                .id(rs.getLong("id_traitement"))
                .consultation(c)
                .medicament(m)
                .posologie(rs.getString("posologie"))
                .dureeTraitement(rs.getInt("duree_traitement"))
                .instructions(rs.getString("instructions"))
                .quantite(rs.getInt("quantite"))
                .build();
    }
}