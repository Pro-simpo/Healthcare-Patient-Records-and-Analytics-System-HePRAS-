package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IMedecinDAO;
import ma.ensa.healthcare.model.Medecin;
import ma.ensa.healthcare.model.Departement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour l'entité MEDECIN
 * VERSION MINIMALE - Seulement les méthodes essentielles
 */
public class MedecinDAOImpl implements IMedecinDAO {
    private static final Logger logger = LoggerFactory.getLogger(MedecinDAOImpl.class);

    @Override
    public Medecin save(Medecin medecin) {
        String sql = "INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, " +
                     "specialite, telephone, email, date_embauche, id_departement) " +
                     "VALUES (seq_medecin.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_medecin"})) {
            
            ps.setString(1, medecin.getNumeroOrdre());
            ps.setString(2, medecin.getNom());
            ps.setString(3, medecin.getPrenom());
            ps.setString(4, medecin.getSpecialite());
            ps.setString(5, medecin.getTelephone());
            ps.setString(6, medecin.getEmail());
            ps.setDate(7, Date.valueOf(medecin.getDateEmbauche()));
            
            if (medecin.getDepartement() != null && medecin.getDepartement().getId() != null) {
                ps.setLong(8, medecin.getDepartement().getId());
            } else {
                ps.setNull(8, Types.NUMERIC);
            }
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    medecin.setId(rs.getLong(1));
                }
            }
            
            logger.info("Médecin enregistré : Dr. {} {} (ID: {})", 
                       medecin.getNom(), medecin.getPrenom(), medecin.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Medecin: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du médecin", e);
        }
        return medecin;
    }

    @Override
    public Medecin findById(Long id) {
        String sql = "SELECT * FROM MEDECIN WHERE id_medecin = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedecin(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Medecin: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Medecin> findBySpecialite(String specialite) {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT * FROM MEDECIN WHERE specialite = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                medecins.add(mapResultSetToMedecin(rs));
            }
            return medecins;
        } catch (SQLException e) {
            logger.error("Erreur findBySpecialite", e);
            throw new RuntimeException("Erreur findBySpecialite", e);
        }
    }
    
    @Override
    public List<Medecin> findAll() {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT * FROM MEDECIN ORDER BY nom, prenom";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                medecins.add(mapResultSetToMedecin(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Medecin: {}", e.getMessage(), e);
        }
        return medecins;
    }

    @Override
    public void update(Medecin medecin) {
        String sql = "UPDATE MEDECIN SET numero_ordre = ?, nom = ?, prenom = ?, " +
                     "specialite = ?, telephone = ?, email = ?, date_embauche = ?, " +
                     "id_departement = ? WHERE id_medecin = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medecin.getNumeroOrdre());
            ps.setString(2, medecin.getNom());
            ps.setString(3, medecin.getPrenom());
            ps.setString(4, medecin.getSpecialite());
            ps.setString(5, medecin.getTelephone());
            ps.setString(6, medecin.getEmail());
            ps.setDate(7, Date.valueOf(medecin.getDateEmbauche()));
            
            if (medecin.getDepartement() != null && medecin.getDepartement().getId() != null) {
                ps.setLong(8, medecin.getDepartement().getId());
            } else {
                ps.setNull(8, Types.NUMERIC);
            }
            
            ps.setLong(9, medecin.getId());
            
            ps.executeUpdate();
            logger.info("Médecin mis à jour : ID {}", medecin.getId());
        } catch (SQLException e) {
            logger.error("Erreur update Medecin: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du médecin", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM MEDECIN WHERE id_medecin = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Médecin supprimé : ID {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete Medecin: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du médecin", e);
        }
    }

    private Medecin mapResultSetToMedecin(ResultSet rs) throws SQLException {
        Medecin.MedecinBuilder builder = Medecin.builder()
                .id(rs.getLong("id_medecin"))
                .numeroOrdre(rs.getString("numero_ordre"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .specialite(rs.getString("specialite"))
                .email(rs.getString("email"))
                .telephone(rs.getString("telephone"))
                .dateEmbauche(rs.getDate("date_embauche").toLocalDate());
        
        // Département peut être null
        Long departementId = rs.getLong("id_departement");
        if (!rs.wasNull() && departementId != null && departementId > 0) {
            Departement dept = new Departement();
            dept.setId(departementId);
            builder.departement(dept);
        }
        
        return builder.build();
    }
}