package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IDepartementDAO;
import ma.ensa.healthcare.model.Departement;
import ma.ensa.healthcare.model.Medecin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour l'entité DEPARTEMENT
 * Correspondance avec la table Oracle DEPARTEMENT
 */
public class DepartementDAOImpl implements IDepartementDAO {
    private static final Logger logger = LoggerFactory.getLogger(DepartementDAOImpl.class);

    @Override
    public Departement save(Departement dept) {
        // ✅ IMPORTANT: Vérifier d'abord si le département existe déjà
        Departement existing = findByNom(dept.getNomDepartement());
        if (existing != null) {
            logger.warn("Le département '{}' existe déjà avec l'ID {}. Retour de l'instance existante.", 
                       dept.getNomDepartement(), existing.getId());
            return existing;
        }
        
        // Si on arrive ici, le département n'existe pas, on peut l'insérer
        String sql = "INSERT INTO DEPARTEMENT (id_departement, nom_departement, " +
                     "chef_departement_id, nombre_lits, telephone) " +
                     "VALUES (seq_departement.NEXTVAL, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_departement"})) {
            
            ps.setString(1, dept.getNomDepartement());
            
            // Chef de département peut être null
            if (dept.getChefDepartement() != null && dept.getChefDepartement().getId() != null) {
                ps.setLong(2, dept.getChefDepartement().getId());
            } else {
                ps.setNull(2, Types.NUMERIC);
            }
            
            ps.setInt(3, dept.getNombreLits());
            ps.setString(4, dept.getTelephone());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        dept.setId(rs.getLong(1));
                    }
                }
            }
            
            logger.info("Département enregistré avec succès : {} (ID: {})", 
                       dept.getNomDepartement(), dept.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Departement: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du département", e);
        }
        return dept;
    }

    /**
     * Recherche un département par son nom
     */
    public Departement findByNom(String nom) {
        String sql = "SELECT * FROM DEPARTEMENT WHERE nom_departement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setQueryTimeout(10); // Timeout de 10 secondes
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartement(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findByNom Departement: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Departement findById(Long id) {
        String sql = "SELECT * FROM DEPARTEMENT WHERE id_departement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartement(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Departement: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Departement> findAll() {
        List<Departement> list = new ArrayList<>();
        String sql = "SELECT * FROM DEPARTEMENT ORDER BY nom_departement";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToDepartement(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Departement: {}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Departement dept) {
        String sql = "UPDATE DEPARTEMENT SET nom_departement = ?, chef_departement_id = ?, " +
                     "nombre_lits = ?, telephone = ? WHERE id_departement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getNomDepartement());
            
            if (dept.getChefDepartement() != null && dept.getChefDepartement().getId() != null) {
                ps.setLong(2, dept.getChefDepartement().getId());
            } else {
                ps.setNull(2, Types.NUMERIC);
            }
            
            ps.setInt(3, dept.getNombreLits());
            ps.setString(4, dept.getTelephone());
            ps.setLong(5, dept.getId());
            
            int rowsAffected = ps.executeUpdate();
            logger.info("Département mis à jour : {} lignes affectées", rowsAffected);
        } catch (SQLException e) {
            logger.error("Erreur update Departement: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du département", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM DEPARTEMENT WHERE id_departement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();
            logger.info("Département supprimé : {} lignes affectées", rowsAffected);
        } catch (SQLException e) {
            logger.error("Erreur delete Departement: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du département", e);
        }
    }

    /**
     * Récupère tous les médecins d'un département
     */
    public List<Medecin> getMedecinsByDepartement(Long departementId) {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT * FROM MEDECIN WHERE id_departement = ? ORDER BY nom, prenom";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medecin m = new Medecin();
                    m.setId(rs.getLong("id_medecin"));
                    m.setNumeroOrdre(rs.getString("numero_ordre"));
                    m.setNom(rs.getString("nom"));
                    m.setPrenom(rs.getString("prenom"));
                    m.setSpecialite(rs.getString("specialite"));
                    m.setTelephone(rs.getString("telephone"));
                    m.setEmail(rs.getString("email"));
                    m.setDateEmbauche(rs.getDate("date_embauche").toLocalDate());
                    medecins.add(m);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur getMedecinsByDepartement: {}", e.getMessage(), e);
        }
        return medecins;
    }

    /**
     * Compte le nombre de médecins dans un département
     */
    public int countMedecins(Long departementId) {
        String sql = "SELECT COUNT(*) FROM MEDECIN WHERE id_departement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, departementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur countMedecins: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Définit le chef de département
     */
    public void setChefDepartement(Long departementId, Long medecinId) {
        String sql = "UPDATE DEPARTEMENT SET chef_departement_id = ? WHERE id_departement = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ps.setLong(2, departementId);
            ps.executeUpdate();
            logger.info("Chef de département défini : médecin {} pour département {}", 
                       medecinId, departementId);
        } catch (SQLException e) {
            logger.error("Erreur setChefDepartement: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la définition du chef de département", e);
        }
    }

    /**
     * Mapper ResultSet vers objet Departement
     */
    private Departement mapResultSetToDepartement(ResultSet rs) throws SQLException {
        Departement.DepartementBuilder builder = Departement.builder()
                .id(rs.getLong("id_departement"))
                .nomDepartement(rs.getString("nom_departement"))
                .nombreLits(rs.getInt("nombre_lits"))
                .telephone(rs.getString("telephone"));
        
        // Chef de département (peut être null)
        Long chefId = rs.getLong("chef_departement_id");
        if (!rs.wasNull() && chefId != null && chefId > 0) {
            Medecin chef = new Medecin();
            chef.setId(chefId);
            builder.chefDepartement(chef);
        }
        
        return builder.build();
    }

    /**
     * Récupère un département avec tous ses détails (chef + médecins)
     */
    public Departement findByIdWithDetails(Long id) {
        Departement dept = findById(id);
        if (dept != null) {
            // Récupérer les détails du chef si présent
            if (dept.getChefDepartement() != null && dept.getChefDepartement().getId() != null) {
                Medecin chef = findMedecinById(dept.getChefDepartement().getId());
                dept.setChefDepartement(chef);
            }
            // Récupérer la liste des médecins
            List<Medecin> medecins = getMedecinsByDepartement(id);
            dept.setMedecins(medecins);
        }
        return dept;
    }

    /**
     * Helper method pour récupérer un médecin par ID
     */
    private Medecin findMedecinById(Long id) {
        String sql = "SELECT * FROM MEDECIN WHERE id_medecin = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Medecin m = new Medecin();
                    m.setId(rs.getLong("id_medecin"));
                    m.setNumeroOrdre(rs.getString("numero_ordre"));
                    m.setNom(rs.getString("nom"));
                    m.setPrenom(rs.getString("prenom"));
                    m.setSpecialite(rs.getString("specialite"));
                    m.setTelephone(rs.getString("telephone"));
                    m.setEmail(rs.getString("email"));
                    m.setDateEmbauche(rs.getDate("date_embauche").toLocalDate());
                    return m;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findMedecinById: {}", e.getMessage(), e);
        }
        return null;
    }
}