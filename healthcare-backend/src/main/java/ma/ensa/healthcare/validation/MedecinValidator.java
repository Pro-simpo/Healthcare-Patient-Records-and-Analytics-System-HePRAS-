package ma.ensa.healthcare.validation;

import ma.ensa.healthcare.exception.MedecinException;
import ma.ensa.healthcare.model.Medecin;

import java.time.LocalDate;

/**
 * Validateur pour l'entité Medecin
 */
public class MedecinValidator {

    /**
     * Valide un médecin complet
     */
    public static void validate(Medecin medecin) {
        if (medecin == null) {
            throw new MedecinException("Le médecin ne peut pas être null");
        }
        
        // 1. Numéro d'ordre obligatoire (UNIQUE NOT NULL dans la BD)
        if (!ValidationUtils.isNotEmpty(medecin.getNumeroOrdre())) {
            throw new MedecinException("Le numéro d'ordre est obligatoire");
        }
        
        // 2. Nom obligatoire
        if (!ValidationUtils.isNotEmpty(medecin.getNom())) {
            throw new MedecinException("Le nom du médecin est obligatoire");
        }
        
        // 3. Prénom obligatoire
        if (!ValidationUtils.isNotEmpty(medecin.getPrenom())) {
            throw new MedecinException("Le prénom du médecin est obligatoire");
        }
        
        // 4. Spécialité obligatoire
        if (!ValidationUtils.isNotEmpty(medecin.getSpecialite())) {
            throw new MedecinException("La spécialité est obligatoire");
        }
        
        // 5. Date d'embauche obligatoire (NOT NULL dans la BD)
        if (medecin.getDateEmbauche() == null) {
            throw new MedecinException("La date d'embauche est obligatoire");
        }
        
        // Date d'embauche ne peut pas être dans le futur
        if (medecin.getDateEmbauche().isAfter(LocalDate.now())) {
            throw new MedecinException("La date d'embauche ne peut pas être dans le futur");
        }
        
        // 6. Département obligatoire (FK NOT NULL dans la BD)
        if (medecin.getDepartement() == null || medecin.getDepartement().getId() == null) {
            throw new MedecinException("Le département est obligatoire");
        }
        
        // 7. Email valide (si fourni)
        if (medecin.getEmail() != null && !medecin.getEmail().trim().isEmpty()) {
            if (!ValidationUtils.isValidEmail(medecin.getEmail())) {
                throw new MedecinException("Format d'email invalide");
            }
        }
        
        // 8. Téléphone (si fourni)
        if (medecin.getTelephone() != null && !medecin.getTelephone().trim().isEmpty()) {
            validateTelephone(medecin.getTelephone());
        }
    }

    /**
     * Valide le format du téléphone
     */
    private static void validateTelephone(String telephone) {
        String telTrim = telephone.trim();
        
        // Format national ou international
        boolean formatValide = telTrim.matches("^0[5-7][0-9]{8}$") || 
                              telTrim.matches("^\\+212[5-7][0-9]{8}$");
        
        if (!formatValide) {
            throw new MedecinException(
                "Format de téléphone invalide. Formats acceptés: 0612345678 ou +212612345678"
            );
        }
    }

    /**
     * Valide uniquement les champs obligatoires
     */
    public static void validateMinimal(Medecin medecin) {
        if (medecin == null) {
            throw new MedecinException("Le médecin ne peut pas être null");
        }
        
        if (!ValidationUtils.isNotEmpty(medecin.getNumeroOrdre())) {
            throw new MedecinException("Le numéro d'ordre est obligatoire");
        }
        
        if (!ValidationUtils.isNotEmpty(medecin.getNom())) {
            throw new MedecinException("Le nom du médecin est obligatoire");
        }
        
        if (!ValidationUtils.isNotEmpty(medecin.getPrenom())) {
            throw new MedecinException("Le prénom du médecin est obligatoire");
        }
        
        if (!ValidationUtils.isNotEmpty(medecin.getSpecialite())) {
            throw new MedecinException("La spécialité est obligatoire");
        }
        
        if (medecin.getDateEmbauche() == null) {
            throw new MedecinException("La date d'embauche est obligatoire");
        }
        
        if (medecin.getDepartement() == null || medecin.getDepartement().getId() == null) {
            throw new MedecinException("Le département est obligatoire");
        }
    }
}