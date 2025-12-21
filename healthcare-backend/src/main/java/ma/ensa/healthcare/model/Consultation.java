package ma.ensa.healthcare.model;

import java.time.LocalDate;

public class Consultation {
    private Long id;
    private LocalDate dateConsultation;
    private String diagnostic;
    private String traitementPrescrit;
    private String notesMedecin;
    private RendezVous rendezVous;

    public Consultation() {}

    public Consultation(Long id, LocalDate dateConsultation, String diagnostic, String traitementPrescrit, String notesMedecin, RendezVous rendezVous) {
        this.id = id;
        this.dateConsultation = dateConsultation;
        this.diagnostic = diagnostic;
        this.traitementPrescrit = traitementPrescrit;
        this.notesMedecin = notesMedecin;
        this.rendezVous = rendezVous;
    }

    // Builder manuel
    public static ConsultationBuilder builder() { return new ConsultationBuilder(); }

    public static class ConsultationBuilder {
        private Long id;
        private LocalDate dateConsultation;
        private String diagnostic;
        private String traitementPrescrit;
        private String notesMedecin;
        private RendezVous rendezVous;

        public ConsultationBuilder id(Long id) { this.id = id; return this; }
        public ConsultationBuilder dateConsultation(LocalDate dateConsultation) { this.dateConsultation = dateConsultation; return this; }
        public ConsultationBuilder diagnostic(String diagnostic) { this.diagnostic = diagnostic; return this; }
        public ConsultationBuilder traitementPrescrit(String traitementPrescrit) { this.traitementPrescrit = traitementPrescrit; return this; }
        public ConsultationBuilder notesMedecin(String notesMedecin) { this.notesMedecin = notesMedecin; return this; }
        public ConsultationBuilder rendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; return this; }
        public Consultation build() { return new Consultation(id, dateConsultation, diagnostic, traitementPrescrit, notesMedecin, rendezVous); }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDate dateConsultation) { this.dateConsultation = dateConsultation; }
    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }
    public String getTraitementPrescrit() { return traitementPrescrit; }
    public void setTraitementPrescrit(String traitementPrescrit) { this.traitementPrescrit = traitementPrescrit; }
    public String getNotesMedecin() { return notesMedecin; }
    public void setNotesMedecin(String notesMedecin) { this.notesMedecin = notesMedecin; }
    public RendezVous getRendezVous() { return rendezVous; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }
}