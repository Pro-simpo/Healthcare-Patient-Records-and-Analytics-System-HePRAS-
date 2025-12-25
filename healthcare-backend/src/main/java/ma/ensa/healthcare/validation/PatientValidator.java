package ma.ensa.healthcare.validation;

import ma.ensa.healthcare.exception.PatientException;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.enums.Sexe;

import java.time.LocalDate;

/**
 * Validateur pour l'entité Patient
 * Validation complète selon les règles métier et contraintes BD
 */
public class PatientValidator {

    /**
     * Valide un patient complet
     */
    public static void validate(Patient patient) {
        if (patient == null) {
            throw new PatientException("Le patient ne peut pas être null");
        }
        
        // 1. CIN obligatoire et format valide
        validateCin(patient.getCin());
        
        // 2. Nom obligatoire
        if (!ValidationUtils.isNotEmpty(patient.getNom())) {
            throw new PatientException("Le nom du patient est obligatoire");
        }
        
        // 3. Prénom obligatoire
        if (!ValidationUtils.isNotEmpty(patient.getPrenom())) {
            throw new PatientException("Le prénom du patient est obligatoire");
        }
        
        // 4. Date de naissance obligatoire et dans le passé
        validateDateNaissance(patient.getDateNaissance());
        
        // 5. Sexe obligatoire
        if (patient.getSexe() == null) {
            throw new PatientException("Le sexe est obligatoire (M ou F)");
        }
        
        // 6. Email valide (si fourni)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            if (!ValidationUtils.isValidEmail(patient.getEmail())) {
                throw new PatientException("Format d'email invalide");
            }
        }
        
        // 7. Téléphone valide (si fourni)
        if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
            validateTelephone(patient.getTelephone());
        }
        
        // 8. Groupe sanguin valide (si fourni)
        if (patient.getGroupeSanguin() != null && !patient.getGroupeSanguin().trim().isEmpty()) {
            validateGroupeSanguin(patient.getGroupeSanguin());
        }
    }

    /**
     * Valide le format du CIN marocain
     * Formats acceptés: X123456 ou 12345678
     */
    public static void validateCin(String cin) {
        if (cin == null || cin.trim().isEmpty()) {
            throw new PatientException("Le CIN est obligatoire");
        }
        
        String cinTrim = cin.trim();
        
        // Format 1: Lettre + 6 chiffres (ex: A123456)
        boolean formatLettre = cinTrim.matches("^[A-Z][0-9]{6}$");
        
        // Format 2: 8 chiffres (ex: 12345678)
        boolean format8Chiffres = cinTrim.matches("^[0-9]{8}$");
        
        if (!formatLettre && !format8Chiffres) {
            throw new PatientException(
                "Format CIN invalide. Formats acceptés: X123456 ou 12345678"
            );
        }
    }

    /**
     * Valide la date de naissance
     */
    public static void validateDateNaissance(LocalDate dateNaissance) {
        if (dateNaissance == null) {
            throw new PatientException("La date de naissance est obligatoire");
        }
        
        // Date dans le passé
        if (dateNaissance.isAfter(LocalDate.now())) {
            throw new PatientException("La date de naissance doit être dans le passé");
        }
        
        // Âge minimum 0, maximum 150 ans (validation réaliste)
        LocalDate dateMinimum = LocalDate.now().minusYears(150);
        if (dateNaissance.isBefore(dateMinimum)) {
            throw new PatientException("Date de naissance invalide (trop ancienne)");
        }
    }

    /**
     * Valide le format du téléphone marocain
     * Formats acceptés: 0612345678 ou +212612345678
     */
    public static void validateTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return; // Téléphone optionnel
        }
        
        String telTrim = telephone.trim();
        
        // Format national: 0612345678 (commence par 06 ou 07 ou 05)
        boolean formatNational = telTrim.matches("^0[5-7][0-9]{8}$");
        
        // Format international: +212612345678
        boolean formatInternational = telTrim.matches("^\\+212[5-7][0-9]{8}$");
        
        if (!formatNational && !formatInternational) {
            throw new PatientException(
                "Format de téléphone invalide. Formats acceptés: 0612345678 ou +212612345678"
            );
        }
    }

    /**
     * Valide le groupe sanguin
     * Valeurs acceptées: A+, A-, B+, B-, AB+, AB-, O+, O-
     */
    public static void validateGroupeSanguin(String groupeSanguin) {
        if (groupeSanguin == null || groupeSanguin.trim().isEmpty()) {
            return; // Groupe sanguin optionnel
        }
        
        String groupe = groupeSanguin.trim().toUpperCase();
        
        if (!groupe.matches("^(A|B|AB|O)[+-]$")) {
            throw new PatientException(
                "Groupe sanguin invalide. Valeurs acceptées: A+, A-, B+, B-, AB+, AB-, O+, O-"
            );
        }
    }

    /**
     * Valide uniquement les champs obligatoires (pour création rapide)
     */
    public static void validateMinimal(Patient patient) {
        if (patient == null) {
            throw new PatientException("Le patient ne peut pas être null");
        }
        
        validateCin(patient.getCin());
        
        if (!ValidationUtils.isNotEmpty(patient.getNom())) {
            throw new PatientException("Le nom du patient est obligatoire");
        }
        
        if (!ValidationUtils.isNotEmpty(patient.getPrenom())) {
            throw new PatientException("Le prénom du patient est obligatoire");
        }
        
        validateDateNaissance(patient.getDateNaissance());
        
        if (patient.getSexe() == null) {
            throw new PatientException("Le sexe est obligatoire (M ou F)");
        }
    }
}