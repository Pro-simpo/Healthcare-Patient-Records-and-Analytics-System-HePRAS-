package ma.ensa.healthcare.validation;

import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.exception.RendezVousException;
import java.time.LocalDateTime;

public class RendezVousValidator {
    public static void validate(RendezVous rdv) {
        if (rdv.getPatient() == null || rdv.getPatient().getId() == null) 
            throw new RendezVousException("Un patient valide est requis.");
        if (rdv.getMedecin() == null || rdv.getMedecin().getId() == null) 
            throw new RendezVousException("Un médecin valide est requis.");
        if (rdv.getDateHeure().isBefore(LocalDateTime.now())) 
            throw new RendezVousException("La date du rendez-vous ne peut pas être dans le passé.");
    }
}