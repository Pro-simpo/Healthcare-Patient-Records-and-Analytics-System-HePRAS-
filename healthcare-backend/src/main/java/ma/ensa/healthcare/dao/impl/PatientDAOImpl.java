package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IPatientDAO;
import ma.ensa.healthcare.model.enums.Sexe;
import ma.ensa.healthcare.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation DAO pour l'entité PATIENT
 * VERSION MINIMALE - Correspondance exacte avec la table Oracle
 */
public class PatientDAOImpl implements IPatientDAO {
    private static final Logger logger = LoggerFactory.getLogger(PatientDAOImpl.class);

    @Override
    public Patient save(Patient patient) {
        // ✅ Colonnes exactes de la table PATIENT
        String sql = "INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, " +
                     "adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies) " +
                     "VALUES (seq_patient.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_patient"})) {

            ps.setString(1, patient.getCin());
            ps.setString(2, patient.getNom());
            ps.setString(3, patient.getPrenom());
            
            if (patient.getDateNaissance() != null) {
                ps.setDate(4, Date.valueOf(patient.getDateNaissance()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            ps.setString(5, patient.getSexe().name());
            ps.setString(6, patient.getAdresse());
            ps.setString(7, patient.getVille());
            ps.setString(8, patient.getCodePostal());
            ps.setString(9, patient.getTelephone());
            ps.setString(10, patient.getEmail());
            ps.setString(11, patient.getGroupeSanguin());
            ps.setString(12, patient.getAllergies());
            
            // date_inscription a une valeur par défaut SYSDATE, mais on peut la spécifier
            if (patient.getDateInscription() != null) {
                ps.setDate(13, Date.valueOf(patient.getDateInscription()));
            } else {
                ps.setNull(13, Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    patient.setId(rs.getLong(1));
                }
            }
            
            logger.info("Patient créé avec succès, ID: {}", patient.getId());
        } catch (SQLException e) {
            logger.error("Erreur save Patient: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du patient", e);
        }
        return patient;
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM PATIENT WHERE id_patient = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Patient: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Patient findByCin(String cin) {
        String sql = "SELECT * FROM PATIENT WHERE cin = ?";
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cin);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche par CIN", e);
            throw new RuntimeException("Erreur findByCin", e);
        }
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PATIENT ORDER BY nom, prenom";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Patient: {}", e.getMessage(), e);
        }
        return patients;
    }

    @Override
    public List<Patient> findByNom(String nom) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PATIENT WHERE UPPER(nom) LIKE UPPER(?) " +
                     "OR UPPER(prenom) LIKE UPPER(?) ORDER BY nom, prenom"; 
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + nom + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findByNom Patient: {}", e.getMessage(), e);
        }
        return patients;
    }

    @Override
    public void update(Patient patient) {
        String sql = "UPDATE PATIENT SET cin = ?, nom = ?, prenom = ?, date_naissance = ?, " +
                     "sexe = ?, adresse = ?, ville = ?, code_postal = ?, telephone = ?, " +
                     "email = ?, groupe_sanguin = ?, allergies = ? WHERE id_patient = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, patient.getCin());
            ps.setString(2, patient.getNom());
            ps.setString(3, patient.getPrenom());
            
            if (patient.getDateNaissance() != null) {
                ps.setDate(4, Date.valueOf(patient.getDateNaissance()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            ps.setString(5, patient.getSexe().name());
            ps.setString(6, patient.getAdresse());
            ps.setString(7, patient.getVille());
            ps.setString(8, patient.getCodePostal());
            ps.setString(9, patient.getTelephone());
            ps.setString(10, patient.getEmail());
            ps.setString(11, patient.getGroupeSanguin());
            ps.setString(12, patient.getAllergies());
            ps.setLong(13, patient.getId());
            
            ps.executeUpdate();
            logger.info("Patient mis à jour ID: {}", patient.getId());
        } catch (SQLException e) {
            logger.error("Erreur update Patient: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du patient", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM PATIENT WHERE id_patient = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Patient supprimé ID: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete Patient: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du patient", e);
        }
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient.PatientBuilder builder = Patient.builder()
                .id(rs.getLong("id_patient"))
                .cin(rs.getString("cin"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .sexe(Sexe.valueOf(rs.getString("sexe")))
                .adresse(rs.getString("adresse"))
                .ville(rs.getString("ville"))
                .codePostal(rs.getString("code_postal"))
                .telephone(rs.getString("telephone"))
                .email(rs.getString("email"))
                .groupeSanguin(rs.getString("groupe_sanguin"))
                .allergies(rs.getString("allergies"));
        
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            builder.dateNaissance(dateNaissance.toLocalDate());
        }
        
        Date dateInscription = rs.getDate("date_inscription");
        if (dateInscription != null) {
            builder.dateInscription(dateInscription.toLocalDate());
        }
        
        return builder.build();
    }
}