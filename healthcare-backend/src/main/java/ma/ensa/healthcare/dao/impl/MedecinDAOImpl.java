package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IMedecinDAO;
import ma.ensa.healthcare.model.Medecin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAOImpl implements IMedecinDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(MedecinDAOImpl.class);

    @Override
    public Medecin save(Medecin medecin) {
        String sql = "INSERT INTO MEDECIN (NOM, PRENOM, SPECIALITE, EMAIL, TELEPHONE) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            
            ps.setString(1, medecin.getNom());
            ps.setString(2, medecin.getPrenom());
            ps.setString(3, medecin.getSpecialite());
            ps.setString(4, medecin.getEmail());
            ps.setString(5, medecin.getTelephone());
            
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                medecin.setId(rs.getLong(1));
            }
            logger.info("Médecin enregistré avec succès ID: {}", medecin.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Medecin", e);
            throw new RuntimeException(e);
        }
        return medecin;
    }

    @Override
    public Medecin findById(Long id) {
        String sql = "SELECT * FROM MEDECIN WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedecin(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Medecin", e);
        }
        return null;
    }
    
    @Override
    public List<Medecin> findAll() {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT * FROM MEDECIN";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medecins.add(mapResultSetToMedecin(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Medecin", e);
        }
        return medecins;
    }

    @Override
    public void update(Medecin medecin) {
        String sql = "UPDATE MEDECIN SET NOM=?, PRENOM=?, SPECIALITE=?, EMAIL=?, TELEPHONE=? WHERE ID=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medecin.getNom());
            ps.setString(2, medecin.getPrenom());
            ps.setString(3, medecin.getSpecialite());
            ps.setString(4, medecin.getEmail());
            ps.setString(5, medecin.getTelephone());
            ps.setLong(6, medecin.getId());
            
            ps.executeUpdate();
            logger.info("Médecin mis à jour ID: {}", medecin.getId());
        } catch (SQLException e) {
            logger.error("Erreur update Medecin", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM MEDECIN WHERE ID=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Médecin supprimé ID: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete Medecin", e);
            throw new RuntimeException(e);
        }
    }

    // Méthode utilitaire pour éviter la duplication de code
    private Medecin mapResultSetToMedecin(ResultSet rs) throws SQLException {
        return Medecin.builder()
                .id(rs.getLong("ID"))
                .nom(rs.getString("NOM"))
                .prenom(rs.getString("PRENOM"))
                .specialite(rs.getString("SPECIALITE"))
                .email(rs.getString("EMAIL"))
                .telephone(rs.getString("TELEPHONE"))
                .build();
    }
}