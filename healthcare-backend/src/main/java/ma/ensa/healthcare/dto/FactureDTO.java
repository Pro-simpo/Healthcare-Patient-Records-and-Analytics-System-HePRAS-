package ma.ensa.healthcare.dto;

public class FactureDTO {
    private Long id;
    private Double montant;
    private String dateFacture;
    private String statut;
    private String patientNom;

    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public String getDateFacture() { return dateFacture; }
    public void setDateFacture(String dateFacture) { this.dateFacture = dateFacture; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String patientNom) { this.patientNom = patientNom; }
}