package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IMedicamentDAO;
import ma.ensa.healthcare.model.Medicament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentDAOImpl implements IMedicamentDAO {
    private static final Logger logger = LoggerFactory.getLogger(MedicamentDAOImpl.class);

    @Override
    public Medicament save(Medicament m) {
        String sql = "INSERT INTO MEDICAMENT (NOM, DOSAGE, INSTRUCTIONS) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDosage());
            ps.setString(3, m.getInstructions());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) m.setId(rs.getLong(1));
            }
            logger.info("Medicament enregistré : {}", m.getNom());
        } catch (SQLException e) {
            logger.error("Erreur save Medicament", e);
        }
        return m;
    }

    @Override
    public List<Medicament> findAll() {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM MEDICAMENT";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Medicament(
                    rs.getLong("ID"),
                    rs.getString("NOM"),
                    rs.getString("DOSAGE"),
                    rs.getString("INSTRUCTIONS")
                ));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Medicament", e);
        }
        return list;
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM MEDICAMENT WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medicament(rs.getLong("ID"), rs.getString("NOM"), 
                                      rs.getString("DOSAGE"), rs.getString("INSTRUCTIONS"));
            }
        } catch (SQLException e) { logger.error("Erreur findById Medicament", e); }
        return null;
    }

    @Override public void update(Medicament m) { /* Implementation UPDATE SQL */ }
    @Override public void delete(Long id) { /* Implementation DELETE SQL */ }
    @Override public List<Medicament> findByNom(String nom) { return new ArrayList<>(); }
}