package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IRendezVousDAO;
import ma.ensa.healthcare.model.Medecin;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.model.enums.StatutRendezVous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour l'entité RENDEZ_VOUS
 * VERSION MINIMALE - Correspondance exacte avec la table Oracle
 */
public class RendezVousDAOImpl implements IRendezVousDAO {
    private static final Logger logger = LoggerFactory.getLogger(RendezVousDAOImpl.class);

    @Override
    public RendezVous save(RendezVous rdv) {
        // ✅ Colonnes exactes de la table RENDEZ_VOUS
        String sql = "INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, " +
                     "heure_debut, heure_fin, motif, statut, salle, date_creation) " +
                     "VALUES (seq_rdv.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_rdv"})) {
            
            // Validation
            if (rdv.getPatient() == null || rdv.getPatient().getId() == null) {
                throw new IllegalArgumentException("Patient ID ne peut pas être null");
            }
            if (rdv.getMedecin() == null || rdv.getMedecin().getId() == null) {
                throw new IllegalArgumentException("Medecin ID ne peut pas être null");
            }
            
            ps.setLong(1, rdv.getPatient().getId());
            ps.setLong(2, rdv.getMedecin().getId());
            ps.setDate(3, Date.valueOf(rdv.getDateRdv()));
            ps.setTimestamp(4, Timestamp.valueOf(rdv.getHeureDebut()));
            ps.setTimestamp(5, Timestamp.valueOf(rdv.getHeureFin()));
            ps.setString(6, rdv.getMotif());
            ps.setString(7, rdv.getStatut().name());
            ps.setString(8, rdv.getSalle());
            
            // date_creation a une valeur par défaut SYSDATE
            if (rdv.getDateCreation() != null) {
                ps.setDate(9, Date.valueOf(rdv.getDateCreation()));
            } else {
                ps.setNull(9, Types.DATE);
            }

            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rdv.setId(rs.getLong(1));
                }
            }
            
            logger.info("Rendez-vous créé avec succès, ID: {}", rdv.getId());
        } catch (SQLException e) {
            logger.error("Erreur save RendezVous: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du rendez-vous", e);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return rdv;
    }

    @Override
    public RendezVous findById(Long id) {
        String sql = "SELECT r.*, " +
                     "p.nom as patient_nom, p.prenom as patient_prenom, " +
                     "m.nom as medecin_nom, m.prenom as medecin_prenom, m.specialite " +
                     "FROM RENDEZ_VOUS r " +
                     "JOIN PATIENT p ON r.id_patient = p.id_patient " +
                     "JOIN MEDECIN m ON r.id_medecin = m.id_medecin " +
                     "WHERE r.id_rdv = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRendezVous(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById RendezVous: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<RendezVous> findAll() {
        List<RendezVous> list = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "p.nom as patient_nom, p.prenom as patient_prenom, " +
                     "m.nom as medecin_nom, m.prenom as medecin_prenom, m.specialite " +
                     "FROM RENDEZ_VOUS r " +
                     "JOIN PATIENT p ON r.id_patient = p.id_patient " +
                     "JOIN MEDECIN m ON r.id_medecin = m.id_medecin " +
                     "ORDER BY r.date_rdv DESC, r.heure_debut DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                list.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll RendezVous: {}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(RendezVous rdv) {
        String sql = "UPDATE RENDEZ_VOUS SET date_rdv = ?, heure_debut = ?, heure_fin = ?, " +
                     "motif = ?, statut = ?, salle = ? WHERE id_rdv = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(rdv.getDateRdv()));
            ps.setTimestamp(2, Timestamp.valueOf(rdv.getHeureDebut()));
            ps.setTimestamp(3, Timestamp.valueOf(rdv.getHeureFin()));
            ps.setString(4, rdv.getMotif());
            ps.setString(5, rdv.getStatut().name());
            ps.setString(6, rdv.getSalle());
            ps.setLong(7, rdv.getId());
            
            ps.executeUpdate();
            logger.info("Rendez-vous mis à jour ID: {}", rdv.getId());
        } catch (SQLException e) {
            logger.error("Erreur update RendezVous: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du rendez-vous", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM RENDEZ_VOUS WHERE id_rdv = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Rendez-vous supprimé ID: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete RendezVous: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du rendez-vous", e);
        }
    }

    private RendezVous mapResultSetToRendezVous(ResultSet rs) throws SQLException {
        // Reconstruction de l'objet Patient (minimal)
        Patient p = new Patient();
        p.setId(rs.getLong("id_patient"));
        p.setNom(rs.getString("patient_nom"));
        p.setPrenom(rs.getString("patient_prenom"));

        // Reconstruction de l'objet Medecin (minimal)
        Medecin m = new Medecin();
        m.setId(rs.getLong("id_medecin"));
        m.setNom(rs.getString("medecin_nom"));
        m.setPrenom(rs.getString("medecin_prenom"));
        m.setSpecialite(rs.getString("specialite"));

        RendezVous.RendezVousBuilder builder = RendezVous.builder()
                .id(rs.getLong("id_rdv"))
                .patient(p)
                .medecin(m)
                .dateRdv(rs.getDate("date_rdv").toLocalDate())
                .heureDebut(rs.getTimestamp("heure_debut").toLocalDateTime())
                .heureFin(rs.getTimestamp("heure_fin").toLocalDateTime())
                .motif(rs.getString("motif"))
                .statut(StatutRendezVous.valueOf(rs.getString("statut")))
                .salle(rs.getString("salle"));
        
        // date_creation peut être null
        Date dateCreation = rs.getDate("date_creation");
        if (dateCreation != null) {
            builder.dateCreation(dateCreation.toLocalDate());
        }
        
        return builder.build();
    }
}