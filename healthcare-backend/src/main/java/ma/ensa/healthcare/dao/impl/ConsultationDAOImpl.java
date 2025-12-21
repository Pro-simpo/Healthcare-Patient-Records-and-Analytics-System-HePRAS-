package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IConsultationDAO;
import ma.ensa.healthcare.model.Consultation;
import ma.ensa.healthcare.model.RendezVous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAOImpl implements IConsultationDAO {
    private static final Logger logger = LoggerFactory.getLogger(ConsultationDAOImpl.class);

    @Override
    public Consultation save(Consultation c) {
        String sql = "INSERT INTO CONSULTATION (DATE_CONSULTATION, DIAGNOSTIC, TRAITEMENT_PRESCRIT, NOTES_MEDECIN, RENDEZVOUS_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            
            ps.setDate(1, Date.valueOf(c.getDateConsultation()));
            ps.setString(2, c.getDiagnostic());
            ps.setString(3, c.getTraitementPrescrit());
            ps.setString(4, c.getNotesMedecin());
            ps.setLong(5, c.getRendezVous().getId());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) c.setId(rs.getLong(1));
            
            logger.info("Consultation enregistrée avec succès ID: {}", c.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Consultation", e);
        }
        return c;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM CONSULTATION WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToConsultation(rs);
        } catch (SQLException e) { logger.error("Erreur findById Consultation", e); }
        return null;
    }

    @Override
    public List<Consultation> findAll() {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT * FROM CONSULTATION";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToConsultation(rs));
        } catch (SQLException e) { logger.error("Erreur findAll Consultation", e); }
        return list;
    }

    @Override
    public void update(Consultation c) { /* Implementation similaire à save avec SQL UPDATE */ }

    @Override
    public void delete(Long id) { /* Implementation SQL DELETE */ }

    @Override
    public Consultation findByRendezVousId(Long rdvId) {
        String sql = "SELECT * FROM CONSULTATION WHERE RENDEZVOUS_ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, rdvId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToConsultation(rs);
        } catch (SQLException e) { logger.error("Erreur findByRdvId", e); }
        return null;
    }

    private Consultation mapResultSetToConsultation(ResultSet rs) throws SQLException {
        RendezVous rdv = new RendezVous();
        rdv.setId(rs.getLong("RENDEZVOUS_ID"));
        
        return Consultation.builder()
                .id(rs.getLong("ID"))
                .dateConsultation(rs.getDate("DATE_CONSULTATION").toLocalDate())
                .diagnostic(rs.getString("DIAGNOSTIC"))
                .traitementPrescrit(rs.getString("TRAITEMENT_PRESCRIT"))
                .notesMedecin(rs.getString("NOTES_MEDECIN"))
                .rendezVous(rdv)
                .build();
    }
}