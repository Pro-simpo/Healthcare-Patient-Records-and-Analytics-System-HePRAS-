package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IUtilisateurDAO;
import ma.ensa.healthcare.model.Utilisateur;
import ma.ensa.healthcare.model.Medecin;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour l'entité UTILISATEUR
 * VERSION CORRIGÉE - Gestion correcte des valeurs par défaut Oracle
 */
public class UtilisateurDAOImpl implements IUtilisateurDAO {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurDAOImpl.class);

    @Override
    public Utilisateur save(Utilisateur u) {
        // ✅ Vérifier si l'utilisateur existe déjà par son username
        Utilisateur existing = findByUsername(u.getUsername());
        if (existing != null) {
            logger.warn("L'utilisateur '{}' existe déjà avec l'ID {}. Retour de l'instance existante.", 
                       u.getUsername(), existing.getId());
            return existing;
        }
        
        // ✅ ON NE MET PAS date_creation dans l'INSERT - Oracle utilisera SYSDATE par défaut
        String sql = "INSERT INTO UTILISATEUR (id_utilisateur, username, password_hash, email, " +
                     "role, statut, id_medecin, id_patient, derniere_connexion, tentatives_echec) " +
                     "VALUES (seq_utilisateur.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_utilisateur"})) {
            
            int paramIndex = 1;
            ps.setString(paramIndex++, u.getUsername());
            ps.setString(paramIndex++, u.getPasswordHash());
            ps.setString(paramIndex++, u.getEmail());
            ps.setString(paramIndex++, u.getRole().name());
            ps.setString(paramIndex++, u.getStatut());
            
            // id_medecin peut être null
            if (u.getMedecin() != null && u.getMedecin().getId() != null) {
                ps.setLong(paramIndex++, u.getMedecin().getId());
            } else {
                ps.setNull(paramIndex++, Types.NUMERIC);
            }
            
            // id_patient peut être null
            if (u.getPatient() != null && u.getPatient().getId() != null) {
                ps.setLong(paramIndex++, u.getPatient().getId());
            } else {
                ps.setNull(paramIndex++, Types.NUMERIC);
            }
            
            // derniere_connexion peut être null
            if (u.getDerniereConnexion() != null) {
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(u.getDerniereConnexion()));
            } else {
                ps.setNull(paramIndex++, Types.TIMESTAMP);
            }
            
            // tentatives_echec par défaut 0
            ps.setInt(paramIndex++, u.getTentativesEchec() != null ? u.getTentativesEchec() : 0);
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setId(rs.getLong(1));
                }
            }
            
            logger.info("Utilisateur créé avec succès : {} (ID: {})", u.getUsername(), u.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Utilisateur: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde de l'utilisateur", e);
        }
        return u;
    }

    @Override
    public Utilisateur findByUsername(String username) {
        String sql = "SELECT * FROM UTILISATEUR WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findByUsername: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM UTILISATEUR WHERE id_utilisateur = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Utilisateur: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Utilisateur> findAll() {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM UTILISATEUR ORDER BY username";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Utilisateur: {}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Utilisateur u) {
        String sql = "UPDATE UTILISATEUR SET username = ?, email = ?, role = ?, " +
                     "statut = ?, id_medecin = ?, id_patient = ? WHERE id_utilisateur = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getRole().name());
            ps.setString(4, u.getStatut());
            
            if (u.getMedecin() != null && u.getMedecin().getId() != null) {
                ps.setLong(5, u.getMedecin().getId());
            } else {
                ps.setNull(5, Types.NUMERIC);
            }
            
            if (u.getPatient() != null && u.getPatient().getId() != null) {
                ps.setLong(6, u.getPatient().getId());
            } else {
                ps.setNull(6, Types.NUMERIC);
            }
            
            ps.setLong(7, u.getId());
            
            ps.executeUpdate();
            logger.info("Utilisateur mis à jour ID: {}", u.getId());
        } catch (SQLException e) {
            logger.error("Erreur update Utilisateur: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    @Override
    public void updatePassword(Long id, String newPasswordHash) {
        String sql = "UPDATE UTILISATEUR SET password_hash = ? WHERE id_utilisateur = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setLong(2, id);
            ps.executeUpdate();
            logger.info("Mot de passe mis à jour pour utilisateur ID: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur updatePassword: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du mot de passe", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM UTILISATEUR WHERE id_utilisateur = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Utilisateur supprimé ID: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete Utilisateur: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }

    @Override
    public void updateDerniereConnexion(Long utilisateurId) {
        String sql = "UPDATE UTILISATEUR SET derniere_connexion = SYSTIMESTAMP, " +
                    "tentatives_echec = 0 WHERE id_utilisateur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, utilisateurId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur updateDerniereConnexion", e);
        }
    }

    @Override
    public void incrementerTentativesEchec(Long utilisateurId) {
        String sql = "UPDATE UTILISATEUR SET tentatives_echec = tentatives_echec + 1 " +
                    "WHERE id_utilisateur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, utilisateurId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur incrementerTentativesEchec", e);
        }
    }

    @Override
    public void bloquerUtilisateur(Long utilisateurId) {
        String sql = "UPDATE UTILISATEUR SET statut = 'SUSPENDU' WHERE id_utilisateur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, utilisateurId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur bloquerUtilisateur", e);
        }
    }

    @Override
    public void debloquerUtilisateur(Long utilisateurId) {
        String sql = "UPDATE UTILISATEUR SET statut = 'ACTIF', tentatives_echec = 0 " +
                    "WHERE id_utilisateur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, utilisateurId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur debloquerUtilisateur", e);
        }
    }

    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur.UtilisateurBuilder builder = Utilisateur.builder()
                .id(rs.getLong("id_utilisateur"))
                .username(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .email(rs.getString("email"))
                .role(Role.valueOf(rs.getString("role")))
                .statut(rs.getString("statut"))
                .tentativesEchec(rs.getInt("tentatives_echec"));
        
        // date_creation peut être null
        Date dateCreation = rs.getDate("date_creation");
        if (dateCreation != null) {
            builder.dateCreation(dateCreation.toLocalDate());
        }
        
        // derniere_connexion peut être null
        Timestamp derniereConnexion = rs.getTimestamp("derniere_connexion");
        if (derniereConnexion != null) {
            builder.derniereConnexion(derniereConnexion.toLocalDateTime());
        }
        
        // Médecin lié (peut être null)
        Long medecinId = rs.getLong("id_medecin");
        if (!rs.wasNull() && medecinId != null && medecinId > 0) {
            Medecin medecin = new Medecin();
            medecin.setId(medecinId);
            builder.medecin(medecin);
        }
        
        // Patient lié (peut être null)
        Long patientId = rs.getLong("id_patient");
        if (!rs.wasNull() && patientId != null && patientId > 0) {
            Patient patient = new Patient();
            patient.setId(patientId);
            builder.patient(patient);
        }
        
        return builder.build();
    }
}