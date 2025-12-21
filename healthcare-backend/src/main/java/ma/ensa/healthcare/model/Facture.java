package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.StatutPaiement;
import java.time.LocalDate;

public class Facture {
    private Long id;
    private Double montant;
    private LocalDate dateFacture;
    private StatutPaiement statut;
    private Consultation consultation;

    public Facture() {}

    public Facture(Long id, Double montant, LocalDate dateFacture, StatutPaiement statut, Consultation consultation) {
        this.id = id;
        this.montant = montant;
        this.dateFacture = dateFacture;
        this.statut = statut;
        this.consultation = consultation;
    }

    public static FactureBuilder builder() { return new FactureBuilder(); }

    public static class FactureBuilder {
        private Long id;
        private Double montant;
        private LocalDate dateFacture;
        private StatutPaiement statut;
        private Consultation consultation;

        public FactureBuilder id(Long id) { this.id = id; return this; }
        public FactureBuilder montant(Double montant) { this.montant = montant; return this; }
        public FactureBuilder dateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; return this; }
        public FactureBuilder statut(StatutPaiement statut) { this.statut = statut; return this; }
        public FactureBuilder consultation(Consultation consultation) { this.consultation = consultation; return this; }
        public Facture build() { return new Facture(id, montant, dateFacture, statut, consultation); }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public LocalDate getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }
    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }
    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
}