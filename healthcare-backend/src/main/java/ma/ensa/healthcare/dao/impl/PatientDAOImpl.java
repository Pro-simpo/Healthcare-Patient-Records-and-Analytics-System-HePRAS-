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

public class PatientDAOImpl implements IPatientDAO {

    private static final Logger logger = LoggerFactory.getLogger(PatientDAOImpl.class);

    @Override
    public Patient save(Patient patient) {
        String sql = "INSERT INTO PATIENT (NOM, PRENOM, CIN, ADRESSE, TELEPHONE, EMAIL, DATE_NAISSANCE, SEXE, ANTECEDENTS_MEDICAUX, DATE_CREATION) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {

            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setString(3, patient.getCin());
            ps.setString(4, patient.getAdresse());
            ps.setString(5, patient.getTelephone());
            ps.setString(6, patient.getEmail());
            
            if (patient.getDateNaissance() != null) {
                ps.setDate(7, Date.valueOf(patient.getDateNaissance()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            
            ps.setString(8, patient.getSexe().name());
            ps.setString(9, patient.getAntecedentsMedicaux());
            
            if (patient.getDateCreation() != null) {
                ps.setDate(10, Date.valueOf(patient.getDateCreation()));
            } else {
                ps.setNull(10, Types.DATE);
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId(generatedKeys.getLong(1));
                    }
                }
                logger.info("Patient créé avec succès, ID: {}", patient.getId());
            }

        } catch (SQLException e) {
            logger.error("Erreur save Patient", e);
            throw new RuntimeException("Erreur SQL lors de la sauvegarde", e);
        }
        return patient;
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM PATIENT WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Patient", e);
        }
        return null;
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PATIENT";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Patient", e);
        }
        return patients;
    }

    @Override
    public List<Patient> findByNom(String nom) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM PATIENT WHERE NOM LIKE ?"; 
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur findByNom Patient", e);
        }
        return patients;
    }

    @Override
    public void update(Patient patient) {
        String sql = "UPDATE PATIENT SET NOM=?, PRENOM=?, ADRESSE=?, TELEPHONE=?, EMAIL=? WHERE ID=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setString(3, patient.getAdresse());
            ps.setString(4, patient.getTelephone());
            ps.setString(5, patient.getEmail());
            ps.setLong(6, patient.getId());
            
            ps.executeUpdate();
            logger.info("Patient mis à jour ID: {}", patient.getId());
        } catch (SQLException e) {
            logger.error("Erreur update Patient", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM PATIENT WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            logger.info("Patient supprimé ID: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur delete Patient", e);
        }
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient.PatientBuilder builder = Patient.builder()
                .id(rs.getLong("ID"))
                .nom(rs.getString("NOM"))
                .prenom(rs.getString("PRENOM"))
                .cin(rs.getString("CIN"))
                .adresse(rs.getString("ADRESSE"))
                .telephone(rs.getString("TELEPHONE"))
                .email(rs.getString("EMAIL"))
                .sexe(Sexe.valueOf(rs.getString("SEXE")))
                .antecedentsMedicaux(rs.getString("ANTECEDENTS_MEDICAUX"));
        
        Date dateNaissance = rs.getDate("DATE_NAISSANCE");
        if (dateNaissance != null) {
            builder.dateNaissance(dateNaissance.toLocalDate());
        }
        
        Date dateCreation = rs.getDate("DATE_CREATION");
        if (dateCreation != null) {
            builder.dateCreation(dateCreation.toLocalDate());
        }
        
        return builder.build();
    }
}