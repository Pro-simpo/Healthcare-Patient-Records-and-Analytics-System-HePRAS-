package ma.ensa.healthcare.validation;

import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.exception.PatientException;

public class PatientValidator {

    public static void validate(Patient patient) {
        if (patient == null) {
            throw new PatientException("Le patient ne peut pas être null.");
        }
        if (patient.getNom() == null || patient.getNom().trim().isEmpty()) {
            throw new PatientException("Le nom du patient est obligatoire.");
        }
        if (patient.getCin() == null || patient.getCin().trim().isEmpty()) {
            throw new PatientException("Le CIN est obligatoire.");
        }
        if (patient.getEmail() != null && !patient.getEmail().contains("@")) {
            throw new PatientException("Format d'email invalide.");
        }
    }
}