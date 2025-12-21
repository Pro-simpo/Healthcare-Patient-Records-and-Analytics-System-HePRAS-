package ma.ensa.healthcare.dto;

import java.util.List;

public class ConsultationDTO {
    private Long id;
    private String date;
    private String patientNom;
    private String medecinNom;
    private String diagnostic;
    private List<String> medicamentsPrescrits; // Liste simple des noms de médicaments

    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String patientNom) { this.patientNom = patientNom; }
    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String medecinNom) { this.medecinNom = medecinNom; }
    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }
    public List<String> getMedicamentsPrescrits() { return medicamentsPrescrits; }
    public void setMedicamentsPrescrits(List<String> medicamentsPrescrits) { this.medicamentsPrescrits = medicamentsPrescrits; }
}