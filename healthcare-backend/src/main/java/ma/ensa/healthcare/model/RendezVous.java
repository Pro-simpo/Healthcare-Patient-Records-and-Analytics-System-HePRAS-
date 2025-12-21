package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.StatutRendezVous;
import java.time.LocalDateTime;

public class RendezVous {
    private Long id;
    private LocalDateTime dateHeure;
    private String motif;
    private StatutRendezVous statut;
    private Patient patient; // Relation vers Patient
    private Medecin medecin; // Relation vers Medecin

    public RendezVous() {}

    public RendezVous(Long id, LocalDateTime dateHeure, String motif, StatutRendezVous statut, Patient patient, Medecin medecin) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.statut = statut;
        this.patient = patient;
        this.medecin = medecin;
    }

    // Builder manuel pour la cohérence
    public static RendezVousBuilder builder() {
        return new RendezVousBuilder();
    }

    public static class RendezVousBuilder {
        private Long id;
        private LocalDateTime dateHeure;
        private String motif;
        private StatutRendezVous statut;
        private Patient patient;
        private Medecin medecin;

        public RendezVousBuilder id(Long id) { this.id = id; return this; }
        public RendezVousBuilder dateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; return this; }
        public RendezVousBuilder motif(String motif) { this.motif = motif; return this; }
        public RendezVousBuilder statut(StatutRendezVous statut) { this.statut = statut; return this; }
        public RendezVousBuilder patient(Patient patient) { this.patient = patient; return this; }
        public RendezVousBuilder medecin(Medecin medecin) { this.medecin = medecin; return this; }

        public RendezVous build() {
            return new RendezVous(id, dateHeure, motif, statut, patient, medecin);
        }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public StatutRendezVous getStatut() { return statut; }
    public void setStatut(StatutRendezVous statut) { this.statut = statut; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }
}