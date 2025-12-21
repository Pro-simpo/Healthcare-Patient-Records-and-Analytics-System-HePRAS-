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

public class RendezVousDAOImpl implements IRendezVousDAO {
    private static final Logger logger = LoggerFactory.getLogger(RendezVousDAOImpl.class);

    @Override
    public RendezVous save(RendezVous rdv) {
        String sql = "INSERT INTO RENDEZ_VOUS (DATE_HEURE, MOTIF, STATUT, PATIENT_ID, MEDECIN_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            
            ps.setTimestamp(1, Timestamp.valueOf(rdv.getDateHeure()));
            ps.setString(2, rdv.getMotif());
            ps.setString(3, rdv.getStatut().name());
            
            // Add null checks for patient and medecin IDs
            if (rdv.getPatient() != null && rdv.getPatient().getId() != null) {
                ps.setLong(4, rdv.getPatient().getId());
            } else {
                throw new IllegalArgumentException("Patient ID cannot be null");
            }
            
            if (rdv.getMedecin() != null && rdv.getMedecin().getId() != null) {
                ps.setLong(5, rdv.getMedecin().getId());
            } else {
                throw new IllegalArgumentException("Medecin ID cannot be null");
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                rdv.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            logger.error("Erreur save RendezVous", e);
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error in save RendezVous: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return rdv;
    }

    @Override
    public RendezVous findById(Long id) {
        String sql = "SELECT r.*, p.nom as p_nom, p.prenom as p_prenom, m.nom as m_nom, m.specialite " +
                     "FROM RENDEZ_VOUS r " +
                     "JOIN PATIENT p ON r.patient_id = p.id " +
                     "JOIN MEDECIN m ON r.medecin_id = m.id " +
                     "WHERE r.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToRendezVous(rs);
            }
        } catch (SQLException e) {
            logger.error("Erreur findById RDV", e);
        }
        return null;
    }

    @Override
    public List<RendezVous> findAll() {
        List<RendezVous> list = new ArrayList<>();
        String sql = "SELECT r.*, p.nom as p_nom, p.prenom as p_prenom, m.nom as m_nom, m.specialite " +
                     "FROM RENDEZ_VOUS r " +
                     "JOIN PATIENT p ON r.patient_id = p.id " +
                     "JOIN MEDECIN m ON r.medecin_id = m.id";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                list.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
             logger.error("Erreur findAll RDV", e);
        }
        return list;
    }

    @Override
    public void update(RendezVous rdv) {
        String sql = "UPDATE RENDEZ_VOUS SET DATE_HEURE=?, MOTIF=?, STATUT=? WHERE ID=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(rdv.getDateHeure()));
            ps.setString(2, rdv.getMotif());
            ps.setString(3, rdv.getStatut().name());
            ps.setLong(4, rdv.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur update RDV", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM RENDEZ_VOUS WHERE ID=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur delete RDV", e);
        }
    }

    private RendezVous mapResultSetToRendezVous(ResultSet rs) throws SQLException {
        // Reconstruction de l'objet Patient (minimal)
        Patient p = new Patient();
        p.setId(rs.getLong("PATIENT_ID"));
        p.setNom(rs.getString("P_NOM"));
        p.setPrenom(rs.getString("P_PRENOM"));

        // Reconstruction de l'objet Medecin (minimal)
        Medecin m = new Medecin();
        m.setId(rs.getLong("MEDECIN_ID"));
        m.setNom(rs.getString("M_NOM"));
        m.setSpecialite(rs.getString("SPECIALITE"));

        return RendezVous.builder()
                .id(rs.getLong("ID"))
                .dateHeure(rs.getTimestamp("DATE_HEURE").toLocalDateTime())
                .motif(rs.getString("MOTIF"))
                .statut(StatutRendezVous.valueOf(rs.getString("STATUT")))
                .patient(p)
                .medecin(m)
                .build();
    }
}