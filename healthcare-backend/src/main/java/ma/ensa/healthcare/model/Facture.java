package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.StatutPaiement;
import ma.ensa.healthcare.model.enums.ModePaiement;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modèle Facture - Correspond à la table FACTURE
 */
public class Facture {
    private Long id;                        // id_facture
    private String numeroFacture;           // numero_facture (UNIQUE NOT NULL)
    private Patient patient;                // id_patient (FK NOT NULL)
    private Consultation consultation;      // id_consultation (FK UNIQUE NOT NULL)
    private LocalDate dateFacture;          // date_facture (DEFAULT SYSDATE)
    private BigDecimal montantConsultation; // montant_consultation (DEFAULT 0)
    private BigDecimal montantMedicaments;  // montant_medicaments (DEFAULT 0)
    private BigDecimal montantTotal;        // montant_total (DEFAULT 0)
    private BigDecimal montantPaye;         // montant_paye (DEFAULT 0)
    private StatutPaiement statutPaiement;  // statut_paiement (DEFAULT 'EN_ATTENTE')
    private ModePaiement modePaiement;      // mode_paiement
    private LocalDate datePaiement;         // date_paiement

    // --- Constructeurs ---
    public Facture() {}

    public Facture(Long id, String numeroFacture, Patient patient, Consultation consultation, 
                  LocalDate dateFacture, BigDecimal montantConsultation, BigDecimal montantMedicaments, 
                  BigDecimal montantTotal, BigDecimal montantPaye, StatutPaiement statutPaiement, 
                  ModePaiement modePaiement, LocalDate datePaiement) {
        this.id = id;
        this.numeroFacture = numeroFacture;
        this.patient = patient;
        this.consultation = consultation;
        this.dateFacture = dateFacture;
        this.montantConsultation = montantConsultation;
        this.montantMedicaments = montantMedicaments;
        this.montantTotal = montantTotal;
        this.montantPaye = montantPaye;
        this.statutPaiement = statutPaiement;
        this.modePaiement = modePaiement;
        this.datePaiement = datePaiement;
    }

    // --- Pattern Builder ---
    public static FactureBuilder builder() {
        return new FactureBuilder();
    }

    public static class FactureBuilder {
        private Long id;
        private String numeroFacture;
        private Patient patient;
        private Consultation consultation;
        private LocalDate dateFacture;
        private BigDecimal montantConsultation;
        private BigDecimal montantMedicaments;
        private BigDecimal montantTotal;
        private BigDecimal montantPaye;
        private StatutPaiement statutPaiement;
        private ModePaiement modePaiement;
        private LocalDate datePaiement;

        public FactureBuilder id(Long id) { this.id = id; return this; }
        public FactureBuilder numeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; return this; }
        public FactureBuilder patient(Patient patient) { this.patient = patient; return this; }
        public FactureBuilder consultation(Consultation consultation) { this.consultation = consultation; return this; }
        public FactureBuilder dateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; return this; }
        public FactureBuilder montantConsultation(BigDecimal montantConsultation) { this.montantConsultation = montantConsultation; return this; }
        public FactureBuilder montantMedicaments(BigDecimal montantMedicaments) { this.montantMedicaments = montantMedicaments; return this; }
        public FactureBuilder montantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; return this; }
        public FactureBuilder montantPaye(BigDecimal montantPaye) { this.montantPaye = montantPaye; return this; }
        public FactureBuilder statutPaiement(StatutPaiement statutPaiement) { this.statutPaiement = statutPaiement; return this; }
        public FactureBuilder modePaiement(ModePaiement modePaiement) { this.modePaiement = modePaiement; return this; }
        public FactureBuilder datePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; return this; }

        public Facture build() {
            return new Facture(id, numeroFacture, patient, consultation, dateFacture, 
                             montantConsultation, montantMedicaments, montantTotal, montantPaye, 
                             statutPaiement, modePaiement, datePaiement);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroFacture() { return numeroFacture; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }

    public LocalDate getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }

    public BigDecimal getMontantConsultation() { return montantConsultation; }
    public void setMontantConsultation(BigDecimal montantConsultation) { this.montantConsultation = montantConsultation; }

    public BigDecimal getMontantMedicaments() { return montantMedicaments; }
    public void setMontantMedicaments(BigDecimal montantMedicaments) { this.montantMedicaments = montantMedicaments; }

    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }

    public BigDecimal getMontantPaye() { return montantPaye; }
    public void setMontantPaye(BigDecimal montantPaye) { this.montantPaye = montantPaye; }

    public StatutPaiement getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(StatutPaiement statutPaiement) { this.statutPaiement = statutPaiement; }

    public ModePaiement getModePaiement() { return modePaiement; }
    public void setModePaiement(ModePaiement modePaiement) { this.modePaiement = modePaiement; }

    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }

    // --- Méthodes utilitaires ---
    public BigDecimal getMontantRestant() {
        if (montantTotal == null || montantPaye == null) {
            return BigDecimal.ZERO;
        }
        return montantTotal.subtract(montantPaye);
    }
}