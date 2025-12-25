package ma.ensa.healthcare.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO pour les statistiques du dashboard
 */
public class StatisticsDTO {
    private long totalPatients;
    private long totalMedecins;
    private long totalRendezVousAujourdhui;
    private long consultationsAujourdhui;
    private BigDecimal chiffreAffaireMois;           // ✅ BigDecimal pour l'argent
    private BigDecimal montantImpayeTotal;           // ✅ Ajouté
    private long facturesImpayees;                   // ✅ Ajouté
    private Map<String, Integer> consultationsParSpecialite; // ex: {"Cardiologie": 15}
    private Map<String, Integer> rdvParStatut;       // ✅ Ajouté : {"PLANIFIE": 10, "CONFIRME": 5}

    public StatisticsDTO() {}

    // Getters et Setters
    public long getTotalPatients() { 
        return totalPatients; 
    }
    
    public void setTotalPatients(long totalPatients) { 
        this.totalPatients = totalPatients; 
    }

    public long getTotalMedecins() { 
        return totalMedecins; 
    }
    
    public void setTotalMedecins(long totalMedecins) { 
        this.totalMedecins = totalMedecins; 
    }

    public long getTotalRendezVousAujourdhui() { 
        return totalRendezVousAujourdhui; 
    }
    
    public void setTotalRendezVousAujourdhui(long totalRendezVousAujourdhui) { 
        this.totalRendezVousAujourdhui = totalRendezVousAujourdhui; 
    }

    public long getConsultationsAujourdhui() { 
        return consultationsAujourdhui; 
    }
    
    public void setConsultationsAujourdhui(long consultationsAujourdhui) { 
        this.consultationsAujourdhui = consultationsAujourdhui; 
    }

    public BigDecimal getChiffreAffaireMois() { 
        return chiffreAffaireMois; 
    }
    
    public void setChiffreAffaireMois(BigDecimal chiffreAffaireMois) { 
        this.chiffreAffaireMois = chiffreAffaireMois; 
    }

    public BigDecimal getMontantImpayeTotal() { 
        return montantImpayeTotal; 
    }
    
    public void setMontantImpayeTotal(BigDecimal montantImpayeTotal) { 
        this.montantImpayeTotal = montantImpayeTotal; 
    }

    public long getFacturesImpayees() { 
        return facturesImpayees; 
    }
    
    public void setFacturesImpayees(long facturesImpayees) { 
        this.facturesImpayees = facturesImpayees; 
    }

    public Map<String, Integer> getConsultationsParSpecialite() { 
        return consultationsParSpecialite; 
    }
    
    public void setConsultationsParSpecialite(Map<String, Integer> consultationsParSpecialite) { 
        this.consultationsParSpecialite = consultationsParSpecialite; 
    }

    public Map<String, Integer> getRdvParStatut() { 
        return rdvParStatut; 
    }
    
    public void setRdvParStatut(Map<String, Integer> rdvParStatut) { 
        this.rdvParStatut = rdvParStatut; 
    }
}