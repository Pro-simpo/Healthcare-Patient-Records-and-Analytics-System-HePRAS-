package ma.ensa.healthcare.dto;

import java.math.BigDecimal;

/**
 * DTO pour l'affichage des factures
 */
public class FactureDTO {
    private Long id;
    private String numeroFacture;           // FAC-2024-0001
    private String patientNom;              // Nom complet du patient
    private String patientCin;              // CIN du patient
    private String dateFacture;             // Format: "23/12/2024"
    private BigDecimal montantConsultation; // Montant de la consultation
    private BigDecimal montantMedicaments;  // Montant des médicaments
    private BigDecimal montantTotal;        // Total à payer
    private BigDecimal montantPaye;         // Déjà payé
    private BigDecimal montantRestant;      // Reste à payer
    private String statutPaiement;          // EN_ATTENTE, PAYE, PARTIEL
    private String modePaiement;            // ESPECES, CARTE, CHEQUE, VIREMENT
    private String datePaiement;            // Date du paiement (si payé)

    public FactureDTO() {}

    // Getters et Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getNumeroFacture() { 
        return numeroFacture; 
    }
    
    public void setNumeroFacture(String numeroFacture) { 
        this.numeroFacture = numeroFacture; 
    }

    public String getPatientNom() { 
        return patientNom; 
    }
    
    public void setPatientNom(String patientNom) { 
        this.patientNom = patientNom; 
    }

    public String getPatientCin() { 
        return patientCin; 
    }
    
    public void setPatientCin(String patientCin) { 
        this.patientCin = patientCin; 
    }

    public String getDateFacture() { 
        return dateFacture; 
    }
    
    public void setDateFacture(String dateFacture) { 
        this.dateFacture = dateFacture; 
    }

    public BigDecimal getMontantConsultation() { 
        return montantConsultation; 
    }
    
    public void setMontantConsultation(BigDecimal montantConsultation) { 
        this.montantConsultation = montantConsultation; 
    }

    public BigDecimal getMontantMedicaments() { 
        return montantMedicaments; 
    }
    
    public void setMontantMedicaments(BigDecimal montantMedicaments) { 
        this.montantMedicaments = montantMedicaments; 
    }

    public BigDecimal getMontantTotal() { 
        return montantTotal; 
    }
    
    public void setMontantTotal(BigDecimal montantTotal) { 
        this.montantTotal = montantTotal; 
    }

    public BigDecimal getMontantPaye() { 
        return montantPaye; 
    }
    
    public void setMontantPaye(BigDecimal montantPaye) { 
        this.montantPaye = montantPaye; 
    }

    public BigDecimal getMontantRestant() { 
        return montantRestant; 
    }
    
    public void setMontantRestant(BigDecimal montantRestant) { 
        this.montantRestant = montantRestant; 
    }

    public String getStatutPaiement() { 
        return statutPaiement; 
    }
    
    public void setStatutPaiement(String statutPaiement) { 
        this.statutPaiement = statutPaiement; 
    }

    public String getModePaiement() { 
        return modePaiement; 
    }
    
    public void setModePaiement(String modePaiement) { 
        this.modePaiement = modePaiement; 
    }

    public String getDatePaiement() { 
        return datePaiement; 
    }
    
    public void setDatePaiement(String datePaiement) { 
        this.datePaiement = datePaiement; 
    }

    /**
     * Retourne une représentation lisible du statut
     */
    public String getStatutLibelle() {
        if (statutPaiement == null) return "";
        switch (statutPaiement) {
            case "EN_ATTENTE": return "En attente";
            case "PAYE": return "Payé";
            case "PARTIEL": return "Partiellement payé";
            default: return statutPaiement;
        }
    }

    /**
     * Vérifie si la facture est complètement payée
     */
    public boolean isPayee() {
        return "PAYE".equals(statutPaiement);
    }

    /**
     * Calcule le pourcentage payé
     */
    public double getPourcentagePaye() {
        if (montantTotal == null || montantTotal.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        if (montantPaye == null) {
            return 0.0;
        }
        return montantPaye.divide(montantTotal, 4, BigDecimal.ROUND_HALF_UP)
                          .multiply(BigDecimal.valueOf(100))
                          .doubleValue();
    }
}