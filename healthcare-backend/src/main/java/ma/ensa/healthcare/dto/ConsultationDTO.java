package ma.ensa.healthcare.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour l'affichage des consultations
 */
public class ConsultationDTO {
    private Long id;
    private String date;                        // Format: "23/12/2024"
    private String patientNom;                  // Nom complet du patient
    private String patientCin;                  // CIN du patient
    private String medecinNom;                  // Dr. Nom Prénom
    private String specialite;                  // Spécialité du médecin
    
    // Détails médicaux
    private String symptomes;
    private String diagnostic;
    private String observations;
    private String prescription;                // Texte de la prescription
    private String examenesDemandes;            // Examens demandés
    private BigDecimal tarifConsultation;
    
    // Liste des traitements prescrits
    private List<TraitementDTO> traitements;

    public ConsultationDTO() {}

    // Getters et Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getDate() { 
        return date; 
    }
    
    public void setDate(String date) { 
        this.date = date; 
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

    public String getMedecinNom() { 
        return medecinNom; 
    }
    
    public void setMedecinNom(String medecinNom) { 
        this.medecinNom = medecinNom; 
    }

    public String getSpecialite() { 
        return specialite; 
    }
    
    public void setSpecialite(String specialite) { 
        this.specialite = specialite; 
    }

    public String getSymptomes() { 
        return symptomes; 
    }
    
    public void setSymptomes(String symptomes) { 
        this.symptomes = symptomes; 
    }

    public String getDiagnostic() { 
        return diagnostic; 
    }
    
    public void setDiagnostic(String diagnostic) { 
        this.diagnostic = diagnostic; 
    }

    public String getObservations() { 
        return observations; 
    }
    
    public void setObservations(String observations) { 
        this.observations = observations; 
    }

    public String getPrescription() { 
        return prescription; 
    }
    
    public void setPrescription(String prescription) { 
        this.prescription = prescription; 
    }

    public String getExamenesDemandes() { 
        return examenesDemandes; 
    }
    
    public void setExamenesDemandes(String examenesDemandes) { 
        this.examenesDemandes = examenesDemandes; 
    }

    public BigDecimal getTarifConsultation() { 
        return tarifConsultation; 
    }
    
    public void setTarifConsultation(BigDecimal tarifConsultation) { 
        this.tarifConsultation = tarifConsultation; 
    }

    public List<TraitementDTO> getTraitements() { 
        return traitements; 
    }
    
    public void setTraitements(List<TraitementDTO> traitements) { 
        this.traitements = traitements; 
    }

    /**
     * Retourne le nombre de médicaments prescrits
     */
    public int getNombreMedicaments() {
        return (traitements != null) ? traitements.size() : 0;
    }
}