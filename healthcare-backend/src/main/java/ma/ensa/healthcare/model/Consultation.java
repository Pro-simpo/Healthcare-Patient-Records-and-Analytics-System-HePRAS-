package ma.ensa.healthcare.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modèle Consultation - Correspond à la table CONSULTATION
 */
public class Consultation {
    private Long id;                        // id_consultation
    private RendezVous rendezVous;          // id_rdv (FK UNIQUE NOT NULL)
    private LocalDate dateConsultation;     // date_consultation (DEFAULT SYSDATE)
    private String symptomes;               // symptomes
    private String diagnostic;              // diagnostic
    private String observations;            // observations
    private String prescription;            // prescription
    private String examenesDemandes;        // examens_demandes
    private BigDecimal tarifConsultation;   // tarif_consultation

    // --- Constructeurs ---
    public Consultation() {}

    public Consultation(Long id, RendezVous rendezVous, LocalDate dateConsultation, 
                       String symptomes, String diagnostic, String observations, 
                       String prescription, String examenesDemandes, BigDecimal tarifConsultation) {
        this.id = id;
        this.rendezVous = rendezVous;
        this.dateConsultation = dateConsultation;
        this.symptomes = symptomes;
        this.diagnostic = diagnostic;
        this.observations = observations;
        this.prescription = prescription;
        this.examenesDemandes = examenesDemandes;
        this.tarifConsultation = tarifConsultation;
    }

    // --- Pattern Builder ---
    public static ConsultationBuilder builder() {
        return new ConsultationBuilder();
    }

    public static class ConsultationBuilder {
        private Long id;
        private RendezVous rendezVous;
        private LocalDate dateConsultation;
        private String symptomes;
        private String diagnostic;
        private String observations;
        private String prescription;
        private String examenesDemandes;
        private BigDecimal tarifConsultation;

        public ConsultationBuilder id(Long id) { this.id = id; return this; }
        public ConsultationBuilder rendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; return this; }
        public ConsultationBuilder dateConsultation(LocalDate dateConsultation) { this.dateConsultation = dateConsultation; return this; }
        public ConsultationBuilder symptomes(String symptomes) { this.symptomes = symptomes; return this; }
        public ConsultationBuilder diagnostic(String diagnostic) { this.diagnostic = diagnostic; return this; }
        public ConsultationBuilder observations(String observations) { this.observations = observations; return this; }
        public ConsultationBuilder prescription(String prescription) { this.prescription = prescription; return this; }
        public ConsultationBuilder examenesDemandes(String examenesDemandes) { this.examenesDemandes = examenesDemandes; return this; }
        public ConsultationBuilder tarifConsultation(BigDecimal tarifConsultation) { this.tarifConsultation = tarifConsultation; return this; }

        public Consultation build() {
            return new Consultation(id, rendezVous, dateConsultation, symptomes, diagnostic, 
                                  observations, prescription, examenesDemandes, tarifConsultation);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RendezVous getRendezVous() { return rendezVous; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }

    public LocalDate getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDate dateConsultation) { this.dateConsultation = dateConsultation; }

    public String getSymptomes() { return symptomes; }
    public void setSymptomes(String symptomes) { this.symptomes = symptomes; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getExamenesDemandes() { return examenesDemandes; }
    public void setExamenesDemandes(String examenesDemandes) { this.examenesDemandes = examenesDemandes; }

    public BigDecimal getTarifConsultation() { return tarifConsultation; }
    public void setTarifConsultation(BigDecimal tarifConsultation) { this.tarifConsultation = tarifConsultation; }
}