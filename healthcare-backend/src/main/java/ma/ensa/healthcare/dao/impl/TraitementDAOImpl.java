package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.ITraitementDAO;
import ma.ensa.healthcare.model.Traitement;
import ma.ensa.healthcare.model.Medicament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraitementDAOImpl implements ITraitementDAO {
    private static final Logger logger = LoggerFactory.getLogger(TraitementDAOImpl.class);

    @Override
    public Traitement save(Traitement t) {
        String sql = "INSERT INTO TRAITEMENT (POSOLOGIE, DUREE_JOURS, CONSULTATION_ID, MEDICAMENT_ID) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setString(1, t.getPosologie());
            ps.setInt(2, t.getDureeJours());
            ps.setLong(3, t.getConsultation().getId());
            ps.setLong(4, t.getMedicament().getId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) t.setId(rs.getLong(1));
        } catch (SQLException e) { logger.error("Erreur save Traitement", e); }
        return t;
    }

    @Override
    public List<Traitement> findByConsultationId(Long consultationId) {
        List<Traitement> list = new ArrayList<>();
        String sql = "SELECT t.*, m.nom FROM TRAITEMENT t JOIN MEDICAMENT m ON t.medicament_id = m.id WHERE t.consultation_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Medicament m = new Medicament();
                m.setId(rs.getLong("MEDICAMENT_ID"));
                m.setNom(rs.getString("NOM"));
                list.add(new Traitement(rs.getLong("ID"), rs.getString("POSOLOGIE"), rs.getInt("DUREE_JOURS"), null, m));
            }
        } catch (SQLException e) { logger.error("Erreur findByConsultationId", e); }
        return list;
    }

    @Override public void delete(Long id) { /* SQL DELETE */ }
}