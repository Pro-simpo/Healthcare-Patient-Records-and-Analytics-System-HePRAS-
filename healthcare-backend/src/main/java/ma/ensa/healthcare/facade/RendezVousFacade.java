package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.dto.RendezVousDTO;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.service.RendezVousService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade pour simplifier les opérations sur les rendez-vous
 */
public class RendezVousFacade {
    private final RendezVousService rdvService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public RendezVousFacade() {
        this.rdvService = new RendezVousService();
    }

    /**
     * Récupère le planning complet du jour
     */
    public List<RendezVousDTO> getPlanningDuJour() {
        LocalDate aujourdhui = LocalDate.now();
        return rdvService.obtenirRendezVousParDate(aujourdhui).stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère tous les rendez-vous
     */
    public List<RendezVousDTO> getTousLesRendezVous() {
        return rdvService.obtenirTousLesRendezVous().stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les rendez-vous d'un patient
     */
    public List<RendezVousDTO> getRendezVousPatient(Long patientId) {
        return rdvService.obtenirRendezVousPatient(patientId).stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les rendez-vous d'un médecin
     */
    public List<RendezVousDTO> getRendezVousMedecin(Long medecinId) {
        return rdvService.obtenirRendezVousMedecin(medecinId).stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Crée un nouveau rendez-vous
     */
    public RendezVous creerRendezVous(RendezVous rdv) {
        return rdvService.planifierRendezVous(rdv);
    }

    /**
     * Confirme un rendez-vous
     */
    public void confirmerRendezVous(Long rdvId) {
        rdvService.confirmerRendezVous(rdvId);
    }

    /**
     * Annule un rendez-vous
     */
    public void annulerRendezVous(Long rdvId, String motifAnnulation) {
        rdvService.annulerRendezVous(rdvId, motifAnnulation);
    }

    /**
     * Convertit un RendezVous en DTO pour l'affichage
     */
    private RendezVousDTO convertirEnDTO(RendezVous rdv) {
        // Format: "21/12/2024 à 14:30"
        String dateLabel = rdv.getDateRdv().format(DATE_FORMATTER);
        String heureLabel = rdv.getHeureDebut().format(TIME_FORMATTER);
        String dateHeureLabel = dateLabel + " à " + heureLabel;
        
        // Nom complet du patient
        String patientNom = rdv.getPatient().getNom() + " " + rdv.getPatient().getPrenom();
        
        // Nom complet du médecin avec titre
        String medecinNom = "Dr. " + rdv.getMedecin().getNom() + " " + rdv.getMedecin().getPrenom();
        
        return RendezVousDTO.builder()
            .dateHeureLabel(dateHeureLabel)
            .patientNom(patientNom)
            .medecinNom(medecinNom)
            .motif(rdv.getMotif())
            .statut(rdv.getStatut().name())
            .build();
    }
}