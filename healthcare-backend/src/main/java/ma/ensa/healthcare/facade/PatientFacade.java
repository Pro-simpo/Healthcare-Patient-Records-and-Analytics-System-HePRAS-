package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.dto.PatientDTO;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.service.PatientService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade pour simplifier les opérations sur les patients
 */
public class PatientFacade {
    private final PatientService patientService;

    public PatientFacade() {
        this.patientService = new PatientService();
    }

    /**
     * Inscrit un nouveau patient
     */
    public Patient inscrirePatient(Patient patient) {
        return patientService.createPatient(patient);
    }

    /**
     * Récupère tous les patients
     */
    public List<Patient> listerPatients() {
        return patientService.getAllPatients();
    }

    /**
     * Récupère tous les patients en DTO pour l'affichage
     */
    public List<PatientDTO> listerPatientsPourAffichage() {
        return patientService.getAllPatients().stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Recherche un patient par son ID
     */
    public Patient getPatientById(Long id) {
        return patientService.getPatientById(id);
    }

    /**
     * Recherche des patients par nom
     */
    public List<Patient> rechercherPatients(String nom) {
        return patientService.rechercherPatients(nom);
    }

    /**
     * Recherche un patient par CIN
     */
    public Patient rechercherParCin(String cin) {
        return patientService.rechercherParCin(cin);
    }

    /**
     * Met à jour les informations d'un patient
     */
    public void modifierPatient(Patient patient) {
        patientService.updatePatient(patient);
    }

    /**
     * Supprime un patient
     */
    public void supprimerPatient(Long id) {
        patientService.deletePatient(id);
    }

    /**
     * Récupère le dossier médical complet d'un patient
     * (Consultations, traitements, factures)
     */
    public Patient getDossierMedicalComplet(Long patientId) {
        Patient patient = patientService.getPatientById(patientId);
        if (patient != null) {
            // Charger les consultations, rendez-vous, factures
            // Cette méthode peut être enrichie selon vos besoins
        }
        return patient;
    }

    /**
     * Convertit un Patient en DTO pour l'affichage
     */
    private PatientDTO convertirEnDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        
        // Nom complet
        String nomComplet = patient.getNom() + " " + patient.getPrenom();
        dto.setNomComplet(nomComplet);
        
        // CIN
        dto.setCin(patient.getCin());
        
        // Contact (téléphone et email)
        String contact = patient.getTelephone();
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            contact += " / " + patient.getEmail();
        }
        dto.setContact(contact);
        
        // Statut médical (basé sur les allergies)
        String statutMedical = "Normal";
        if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
            statutMedical = "⚠ Allergies";
        }
        dto.setStatutMedical(statutMedical);
        
        return dto;
    }

    /**
     * Compte le nombre total de patients
     */
    public long getNombreTotalPatients() {
        return patientService.compterPatients();
    }

    /**
     * Vérifie si un CIN existe déjà
     */
    public boolean cinExiste(String cin) {
        return patientService.rechercherParCin(cin) != null;
    }
}