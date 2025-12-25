package ma.ensa.healthcare.model;

import java.math.BigDecimal;

/**
 * Modèle Medicament - Correspond à la table MEDICAMENT
 */
public class Medicament {
    private Long id;                    // id_medicament
    private String nomCommercial;       // nom_commercial (UNIQUE NOT NULL)
    private String principeActif;       // principe_actif (NOT NULL)
    private String forme;               // forme (COMPRIME, SIROP, INJECTION, GELULE, POMMADE)
    private String dosage;              // dosage
    private BigDecimal prixUnitaire;    // prix_unitaire
    private Integer stockDisponible;    // stock_disponible (DEFAULT 0)
    private Integer stockAlerte;        // stock_alerte (DEFAULT 0)

    // --- Constructeurs ---
    public Medicament() {}

    public Medicament(Long id, String nomCommercial, String principeActif, String forme, 
                     String dosage, BigDecimal prixUnitaire, Integer stockDisponible, Integer stockAlerte) {
        this.id = id;
        this.nomCommercial = nomCommercial;
        this.principeActif = principeActif;
        this.forme = forme;
        this.dosage = dosage;
        this.prixUnitaire = prixUnitaire;
        this.stockDisponible = stockDisponible;
        this.stockAlerte = stockAlerte;
    }

    // --- Pattern Builder ---
    public static MedicamentBuilder builder() {
        return new MedicamentBuilder();
    }

    public static class MedicamentBuilder {
        private Long id;
        private String nomCommercial;
        private String principeActif;
        private String forme;
        private String dosage;
        private BigDecimal prixUnitaire;
        private Integer stockDisponible;
        private Integer stockAlerte;

        public MedicamentBuilder id(Long id) { this.id = id; return this; }
        public MedicamentBuilder nomCommercial(String nomCommercial) { this.nomCommercial = nomCommercial; return this; }
        public MedicamentBuilder principeActif(String principeActif) { this.principeActif = principeActif; return this; }
        public MedicamentBuilder forme(String forme) { this.forme = forme; return this; }
        public MedicamentBuilder dosage(String dosage) { this.dosage = dosage; return this; }
        public MedicamentBuilder prixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; return this; }
        public MedicamentBuilder stockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; return this; }
        public MedicamentBuilder stockAlerte(Integer stockAlerte) { this.stockAlerte = stockAlerte; return this; }

        public Medicament build() {
            return new Medicament(id, nomCommercial, principeActif, forme, dosage, 
                                prixUnitaire, stockDisponible, stockAlerte);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomCommercial() { return nomCommercial; }
    public void setNomCommercial(String nomCommercial) { this.nomCommercial = nomCommercial; }

    public String getPrincipeActif() { return principeActif; }
    public void setPrincipeActif(String principeActif) { this.principeActif = principeActif; }

    public String getForme() { return forme; }
    public void setForme(String forme) { this.forme = forme; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Integer getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }

    public Integer getStockAlerte() { return stockAlerte; }
    public void setStockAlerte(Integer stockAlerte) { this.stockAlerte = stockAlerte; }
}