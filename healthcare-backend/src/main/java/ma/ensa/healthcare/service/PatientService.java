package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.PatientDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IPatientDAO;
import ma.ensa.healthcare.exception.PatientException;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.enums.Sexe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Service métier pour la gestion des patients
 */
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private final IPatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = new PatientDAOImpl();
    }

    /**
     * Crée un nouveau patient avec validation complète
     */
    public Patient createPatient(Patient patient) {
        // 1. Validation métier
        validatePatient(patient);
        
        // 2. Vérification CIN unique
        Patient existant = patientDAO.findByCin(patient.getCin());
        if (existant != null) {
            throw new PatientException("Un patient avec le CIN " + patient.getCin() + " existe déjà");
        }
        
        // 3. Initialiser date d'inscription si non fournie
        if (patient.getDateInscription() == null) {
            patient.setDateInscription(LocalDate.now());
        }
        
        // 4. Sauvegarder
        try {
            Patient saved = patientDAO.save(patient);
            logger.info("Patient créé avec succès : {} {} (CIN: {})", 
                       saved.getNom(), saved.getPrenom(), saved.getCin());
            return saved;
        } catch (Exception e) {
            logger.error("Erreur lors de la création du patient", e);
            throw new PatientException("Impossible de créer le patient", e);
        }
    }

    /**
     * Récupère tous les patients
     */
    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }
    
    /**
     * Récupère un patient par ID
     */
    public Patient getPatientById(Long id) {
        if (id == null) {
            throw new PatientException("L'ID ne peut pas être null");
        }
        
        Patient patient = patientDAO.findById(id);
        if (patient == null) {
            throw new PatientException("Patient introuvable avec l'ID " + id);
        }
        return patient;
    }

    /**
     * Recherche un patient par CIN
     */
    public Patient rechercherParCin(String cin) {
        if (cin == null || cin.trim().isEmpty()) {
            throw new PatientException("Le CIN ne peut pas être vide");
        }
        return patientDAO.findByCin(cin);
    }

    /**
     * Recherche des patients par nom
     */
    public List<Patient> rechercherPatients(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new PatientException("Le nom ne peut pas être vide");
        }
        return patientDAO.findByNom(nom);
    }

    /**
     * Met à jour un patient existant
     */
    public void updatePatient(Patient patient) {
        if (patient.getId() == null) {
            throw new PatientException("L'ID du patient est requis pour la mise à jour");
        }
        
        validatePatient(patient);
        
        try {
            patientDAO.update(patient);
            logger.info("Patient mis à jour : {} {} (ID: {})", 
                       patient.getNom(), patient.getPrenom(), patient.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du patient", e);
            throw new PatientException("Impossible de mettre à jour le patient", e);
        }
    }

    /**
     * Supprime un patient
     */
    public void deletePatient(Long id) {
        if (id == null) {
            throw new PatientException("ID invalide");
        }
        
        try {
            patientDAO.delete(id);
            logger.info("Patient supprimé : ID {}", id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du patient", e);
            throw new PatientException("Impossible de supprimer le patient", e);
        }
    }

    /**
     * Compte le nombre total de patients
     */
    public long compterPatients() {
        return patientDAO.findAll().size();
    }

    /**
     * Validation complète d'un patient
     */
    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new PatientException("Le patient ne peut pas être null");
        }
        
        // 1. CIN obligatoire (UNIQUE NOT NULL dans la BD)
        if (patient.getCin() == null || patient.getCin().trim().isEmpty()) {
            throw new PatientException("Le CIN est obligatoire");
        }
        
        // Validation format CIN marocain (8 chiffres ou X + 6 chiffres)
        if (!patient.getCin().matches("^[A-Z][0-9]{6}$") && 
            !patient.getCin().matches("^[0-9]{8}$")) {
            throw new PatientException("Format CIN invalide (attendu: X123456 ou 12345678)");
        }
        
        // 2. Nom obligatoire
        if (patient.getNom() == null || patient.getNom().trim().isEmpty()) {
            throw new PatientException("Le nom est obligatoire");
        }
        
        // 3. Prénom obligatoire
        if (patient.getPrenom() == null || patient.getPrenom().trim().isEmpty()) {
            throw new PatientException("Le prénom est obligatoire");
        }
        
        // 4. Date de naissance obligatoire
        if (patient.getDateNaissance() == null) {
            throw new PatientException("La date de naissance est obligatoire");
        }
        
        // Vérification date de naissance dans le passé
        if (patient.getDateNaissance().isAfter(LocalDate.now())) {
            throw new PatientException("La date de naissance doit être dans le passé");
        }
        
        // 5. Sexe obligatoire
        if (patient.getSexe() == null) {
            throw new PatientException("Le sexe est obligatoire (M ou F)");
        }
        
        // 6. Groupe sanguin valide (si fourni)
        if (patient.getGroupeSanguin() != null && !patient.getGroupeSanguin().trim().isEmpty()) {
            if (!patient.getGroupeSanguin().matches("^(A|B|AB|O)[+-]$")) {
                throw new PatientException("Groupe sanguin invalide (attendu: A+, A-, B+, B-, AB+, AB-, O+, O-)");
            }
        }
        
        // 7. Email valide (si fourni)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            if (!patient.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new PatientException("Format d'email invalide");
            }
        }
        
        // 8. Téléphone (si fourni)
        if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
            // Format marocain: 0612345678 ou +212612345678
            if (!patient.getTelephone().matches("^(0[5-7][0-9]{8}|\\+212[5-7][0-9]{8})$")) {
                logger.warn("Format de téléphone non standard pour patient {}", patient.getCin());
            }
        }
    }
}