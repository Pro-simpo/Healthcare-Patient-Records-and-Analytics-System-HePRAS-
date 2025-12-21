package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.PatientDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IPatientDAO;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.validation.PatientValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PatientService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private final IPatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = new PatientDAOImpl();
    }

    public Patient createPatient(Patient patient) {
        // 1. Validation Métier
        PatientValidator.validate(patient);
        
        // 2. Vérification existence (Règle métier : CIN unique)
        // Note: Idéalement il faudrait une méthode findByCin, on simule ici
        List<Patient> exists = patientDAO.findByNom(patient.getNom());
        // ... Logique de vérification CIN ...

        // 3. Appel DAO
        try {
            return patientDAO.save(patient);
        } catch (Exception e) {
            logger.error("Erreur service creation patient", e);
            throw e;
        }
    }

    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }
    
    public Patient getPatientById(Long id) {
        return patientDAO.findById(id);
    }
}