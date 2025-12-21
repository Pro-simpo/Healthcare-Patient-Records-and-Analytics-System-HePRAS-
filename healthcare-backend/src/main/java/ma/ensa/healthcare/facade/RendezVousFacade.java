package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.dto.RendezVousDTO;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.service.RendezVousService;
import java.util.List;
import java.util.stream.Collectors;

public class RendezVousFacade {
    private final RendezVousService rdvService = new RendezVousService();

    public List<RendezVousDTO> getPlanningDuJour() {
        return rdvService.obtenirToutLesRendezVous().stream()
            .map(rdv -> RendezVousDTO.builder()
                .dateHeureLabel(rdv.getDateHeure().toString())
                .patientNom(rdv.getPatient().getNom())
                .medecinNom(rdv.getMedecin().getNom())
                .statut(rdv.getStatut().name())
                .build())
            .collect(Collectors.toList());
    }
}