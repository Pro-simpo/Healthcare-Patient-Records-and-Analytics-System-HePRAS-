package ma.ensa.healthcare.validation;

import ma.ensa.healthcare.exception.RendezVousException;
import ma.ensa.healthcare.model.RendezVous;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Validateur pour l'entité RendezVous
 * Validation selon la nouvelle structure (dateRdv, heureDebut, heureFin)
 */
public class RendezVousValidator {

    /**
     * Valide un rendez-vous complet
     */
    public static void validate(RendezVous rdv) {
        if (rdv == null) {
            throw new RendezVousException("Le rendez-vous ne peut pas être null");
        }
        
        // 1. Patient obligatoire
        if (rdv.getPatient() == null || rdv.getPatient().getId() == null) {
            throw new RendezVousException("Un patient valide est requis");
        }
        
        // 2. Médecin obligatoire
        if (rdv.getMedecin() == null || rdv.getMedecin().getId() == null) {
            throw new RendezVousException("Un médecin valide est requis");
        }
        
        // 3. Date obligatoire
        if (rdv.getDateRdv() == null) {
            throw new RendezVousException("La date du rendez-vous est obligatoire");
        }
        
        // 4. Heure début obligatoire
        if (rdv.getHeureDebut() == null) {
            throw new RendezVousException("L'heure de début est obligatoire");
        }
        
        // 5. Heure fin obligatoire
        if (rdv.getHeureFin() == null) {
            throw new RendezVousException("L'heure de fin est obligatoire");
        }
        
        // 6. Heure fin doit être après heure début
        if (!rdv.getHeureFin().isAfter(rdv.getHeureDebut())) {
            throw new RendezVousException("L'heure de fin doit être après l'heure de début");
        }
        
        // 7. Vérifier durée raisonnable (pas plus de 4 heures)
        long minutes = java.time.Duration.between(rdv.getHeureDebut(), rdv.getHeureFin()).toMinutes();
        if (minutes > 240) { // 4 heures = 240 minutes
            throw new RendezVousException("La durée du rendez-vous ne peut pas dépasser 4 heures");
        }
        
        if (minutes < 15) { // Au moins 15 minutes
            throw new RendezVousException("La durée du rendez-vous doit être d'au moins 15 minutes");
        }
        
        // 8. Date/heure dans le futur (seulement pour nouveau RDV)
        if (rdv.getId() == null) { // Nouveau rendez-vous
            validateFutureDateTime(rdv.getHeureDebut());
        }
    }

    /**
     * Valide que la date/heure est dans le futur
     */
    public static void validateFutureDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new RendezVousException("La date/heure ne peut pas être null");
        }
        
        LocalDateTime maintenant = LocalDateTime.now();
        
        if (dateTime.isBefore(maintenant)) {
            throw new RendezVousException("Impossible de planifier un rendez-vous dans le passé");
        }
        
        // Vérifier qu'on ne planifie pas trop loin (max 1 an)
        LocalDateTime unAnPlus = maintenant.plusYears(1);
        if (dateTime.isAfter(unAnPlus)) {
            throw new RendezVousException("Impossible de planifier un rendez-vous à plus d'un an");
        }
    }

    /**
     * Valide que le rendez-vous est dans les heures ouvrables
     * Exemple: Lundi-Vendredi 8h-18h, Samedi 8h-13h
     */
    public static void validateHeuresOuvrables(RendezVous rdv) {
        LocalDateTime heureDebut = rdv.getHeureDebut();
        int dayOfWeek = heureDebut.getDayOfWeek().getValue();
        int heure = heureDebut.getHour();
        
        // Dimanche (7) - Fermé
        if (dayOfWeek == 7) {
            throw new RendezVousException("Le cabinet est fermé le dimanche");
        }
        
        // Samedi (6) - 8h à 13h
        if (dayOfWeek == 6) {
            if (heure < 8 || heure >= 13) {
                throw new RendezVousException("Le samedi, les horaires sont de 8h à 13h");
            }
        }
        
        // Lundi à Vendredi - 8h à 18h
        if (dayOfWeek >= 1 && dayOfWeek <= 5) {
            if (heure < 8 || heure >= 18) {
                throw new RendezVousException("Les horaires sont de 8h à 18h en semaine");
            }
        }
    }

    /**
     * Valide les champs obligatoires minimaux
     */
    public static void validateMinimal(RendezVous rdv) {
        if (rdv == null) {
            throw new RendezVousException("Le rendez-vous ne peut pas être null");
        }
        
        if (rdv.getPatient() == null || rdv.getPatient().getId() == null) {
            throw new RendezVousException("Un patient valide est requis");
        }
        
        if (rdv.getMedecin() == null || rdv.getMedecin().getId() == null) {
            throw new RendezVousException("Un médecin valide est requis");
        }
        
        if (rdv.getDateRdv() == null) {
            throw new RendezVousException("La date du rendez-vous est obligatoire");
        }
        
        if (rdv.getHeureDebut() == null) {
            throw new RendezVousException("L'heure de début est obligatoire");
        }
        
        if (rdv.getHeureFin() == null) {
            throw new RendezVousException("L'heure de fin est obligatoire");
        }
    }
}