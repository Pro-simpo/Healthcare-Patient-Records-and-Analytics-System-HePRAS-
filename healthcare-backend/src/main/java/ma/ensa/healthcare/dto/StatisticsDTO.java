package ma.ensa.healthcare.dto;

import java.util.Map;

public class StatisticsDTO {
    private long totalPatients;
    private long totalRendezVousAujourdhui;
    private double chiffreAffaireMois;
    private Map<String, Integer> consultationsParSpecialite; // ex: {"Cardiologie": 15, "Général": 40}

    public StatisticsDTO() {}

    // Getters et Setters
    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalRendezVousAujourdhui() { return totalRendezVousAujourdhui; }
    public void setTotalRendezVousAujourdhui(long totalRendezVousAujourdhui) { this.totalRendezVousAujourdhui = totalRendezVousAujourdhui; }

    public double getChiffreAffaireMois() { return chiffreAffaireMois; }
    public void setChiffreAffaireMois(double chiffreAffaireMois) { this.chiffreAffaireMois = chiffreAffaireMois; }

    public Map<String, Integer> getConsultationsParSpecialite() { return consultationsParSpecialite; }
    public void setConsultationsParSpecialite(Map<String, Integer> consultationsParSpecialite) { this.consultationsParSpecialite = consultationsParSpecialite; }
}