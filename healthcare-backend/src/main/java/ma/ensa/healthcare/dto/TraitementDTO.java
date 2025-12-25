package ma.ensa.healthcare.dto;

import java.math.BigDecimal;

/**
 * DTO pour l'affichage des traitements prescrits
 */
public class TraitementDTO {
    private Long id;
    private String medicamentNom;           // Nom commercial du médicament
    private String principeActif;           // Principe actif
    private String forme;                   // COMPRIME, SIROP, INJECTION, etc.
    private String dosage;                  // Ex: "500mg"
    private String posologie;               // Ex: "1 comprimé 3 fois/jour"
    private Integer dureeTraitement;        // Durée en jours
    private Integer quantite;               // Quantité totale prescrite
    private String instructions;            // Instructions spéciales
    private BigDecimal prixUnitaire;        // Prix unitaire du médicament
    private BigDecimal montantTotal;        // quantite * prixUnitaire

    public TraitementDTO() {}

    // Getters et Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getMedicamentNom() { 
        return medicamentNom; 
    }
    
    public void setMedicamentNom(String medicamentNom) { 
        this.medicamentNom = medicamentNom; 
    }

    public String getPrincipeActif() { 
        return principeActif; 
    }
    
    public void setPrincipeActif(String principeActif) { 
        this.principeActif = principeActif; 
    }

    public String getForme() { 
        return forme; 
    }
    
    public void setForme(String forme) { 
        this.forme = forme; 
    }

    public String getDosage() { 
        return dosage; 
    }
    
    public void setDosage(String dosage) { 
        this.dosage = dosage; 
    }

    public String getPosologie() { 
        return posologie; 
    }
    
    public void setPosologie(String posologie) { 
        this.posologie = posologie; 
    }

    public Integer getDureeTraitement() { 
        return dureeTraitement; 
    }
    
    public void setDureeTraitement(Integer dureeTraitement) { 
        this.dureeTraitement = dureeTraitement; 
    }

    public Integer getQuantite() { 
        return quantite; 
    }
    
    public void setQuantite(Integer quantite) { 
        this.quantite = quantite; 
    }

    public String getInstructions() { 
        return instructions; 
    }
    
    public void setInstructions(String instructions) { 
        this.instructions = instructions; 
    }

    public BigDecimal getPrixUnitaire() { 
        return prixUnitaire; 
    }
    
    public void setPrixUnitaire(BigDecimal prixUnitaire) { 
        this.prixUnitaire = prixUnitaire; 
    }

    public BigDecimal getMontantTotal() { 
        return montantTotal; 
    }
    
    public void setMontantTotal(BigDecimal montantTotal) { 
        this.montantTotal = montantTotal; 
    }

    /**
     * Retourne une description complète du médicament
     */
    public String getDescriptionComplete() {
        StringBuilder sb = new StringBuilder();
        sb.append(medicamentNom);
        if (dosage != null && !dosage.isEmpty()) {
            sb.append(" ").append(dosage);
        }
        if (forme != null && !forme.isEmpty()) {
            sb.append(" (").append(forme).append(")");
        }
        return sb.toString();
    }

    /**
     * Retourne la posologie avec la durée
     */
    public String getPosologieComplete() {
        StringBuilder sb = new StringBuilder();
        if (posologie != null) {
            sb.append(posologie);
        }
        if (dureeTraitement != null && dureeTraitement > 0) {
            sb.append(" pendant ").append(dureeTraitement).append(" jour");
            if (dureeTraitement > 1) {
                sb.append("s");
            }
        }
        return sb.toString();
    }
}