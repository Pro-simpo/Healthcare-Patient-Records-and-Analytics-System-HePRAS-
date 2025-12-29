package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.StatutRendezVous;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle RendezVous - Correspond à la table RENDEZ_VOUS
 */
public class RendezVous {
    private Long id;                        // id_rdv
    private Long idPatient;                // id_patient (FK NOT NULL)
    private Medecin medecin;                // id_medecin (FK NOT NULL)
    private LocalDate dateRdv;              // date_rdv (NOT NULL)
    private LocalDateTime heureDebut;       // heure_debut (TIMESTAMP NOT NULL)
    private LocalDateTime heureFin;         // heure_fin (TIMESTAMP NOT NULL)
    private String motif;                   // motif
    private StatutRendezVous statut;        // statut (DEFAULT 'PLANIFIE')
    private String salle;                   // salle
    private LocalDate dateCreation;         // date_creation (DEFAULT SYSDATE)

    // --- Constructeurs ---
    public RendezVous() {}

    public RendezVous(Long id, Long idPatient, Medecin medecin, LocalDate dateRdv, 
                     LocalDateTime heureDebut, LocalDateTime heureFin, String motif, 
                     StatutRendezVous statut, String salle, LocalDate dateCreation) {
        this.id = id;
        this.idPatient = idPatient;
        this.medecin = medecin;
        this.dateRdv = dateRdv;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.motif = motif;
        this.statut = statut;
        this.salle = salle;
        this.dateCreation = dateCreation;
    }

    // --- Pattern Builder ---
    public static RendezVousBuilder builder() {
        return new RendezVousBuilder();
    }

    public static class RendezVousBuilder {
        private Long id;
        private Long idPatient;
        private Medecin medecin;
        private LocalDate dateRdv;
        private LocalDateTime heureDebut;
        private LocalDateTime heureFin;
        private String motif;
        private StatutRendezVous statut;
        private String salle;
        private LocalDate dateCreation;

        public RendezVousBuilder id(Long id) { this.id = id; return this; }
        public RendezVousBuilder idPatient(Long idPatient) { this.idPatient = idPatient; return this; }
        public RendezVousBuilder medecin(Medecin medecin) { this.medecin = medecin; return this; }
        public RendezVousBuilder dateRdv(LocalDate dateRdv) { this.dateRdv = dateRdv; return this; }
        public RendezVousBuilder heureDebut(LocalDateTime heureDebut) { this.heureDebut = heureDebut; return this; }
        public RendezVousBuilder heureFin(LocalDateTime heureFin) { this.heureFin = heureFin; return this; }
        public RendezVousBuilder motif(String motif) { this.motif = motif; return this; }
        public RendezVousBuilder statut(StatutRendezVous statut) { this.statut = statut; return this; }
        public RendezVousBuilder salle(String salle) { this.salle = salle; return this; }
        public RendezVousBuilder dateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; return this; }

        public RendezVous build() {
            return new RendezVous(id, idPatient, medecin, dateRdv, heureDebut, heureFin, 
                                motif, statut, salle, dateCreation);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPatient() { return idPatient; }
    public void setIdPatient(Long idPatient) { this.idPatient = idPatient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public LocalDate getDateRdv() { return dateRdv; }
    public void setDateRdv(LocalDate dateRdv) { this.dateRdv = dateRdv; }

    public LocalDateTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalDateTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalDateTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalDateTime heureFin) { this.heureFin = heureFin; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public StatutRendezVous getStatut() { return statut; }
    public void setStatut(StatutRendezVous statut) { this.statut = statut; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}