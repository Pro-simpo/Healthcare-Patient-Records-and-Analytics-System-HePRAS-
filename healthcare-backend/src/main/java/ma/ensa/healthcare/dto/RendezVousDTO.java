package ma.ensa.healthcare.dto;

public class RendezVousDTO {
    private String dateHeureLabel; // Ex: "21/12/2025 à 14:30"
    private String patientNom;     // Ex: "M. Ahmed Alami"
    private String medecinNom;     // Ex: "Dr. Benjelloun"
    private String motif;
    private String statut;         // Ex: "CONFIRME"

    public RendezVousDTO() {}

    public RendezVousDTO(String dateHeureLabel, String patientNom, String medecinNom, String motif, String statut) {
        this.dateHeureLabel = dateHeureLabel;
        this.patientNom = patientNom;
        this.medecinNom = medecinNom;
        this.motif = motif;
        this.statut = statut;
    }

    // Builder Manuel
    public static RendezVousDTOBuilder builder() {
        return new RendezVousDTOBuilder();
    }

    public static class RendezVousDTOBuilder {
        private String dateHeureLabel;
        private String patientNom;
        private String medecinNom;
        private String motif;
        private String statut;

        public RendezVousDTOBuilder dateHeureLabel(String dateHeureLabel) { this.dateHeureLabel = dateHeureLabel; return this; }
        public RendezVousDTOBuilder patientNom(String patientNom) { this.patientNom = patientNom; return this; }
        public RendezVousDTOBuilder medecinNom(String medecinNom) { this.medecinNom = medecinNom; return this; }
        public RendezVousDTOBuilder motif(String motif) { this.motif = motif; return this; }
        public RendezVousDTOBuilder statut(String statut) { this.statut = statut; return this; }

        public RendezVousDTO build() {
            return new RendezVousDTO(dateHeureLabel, patientNom, medecinNom, motif, statut);
        }
    }

    // Getters et Setters
    public String getDateHeureLabel() { return dateHeureLabel; }
    public void setDateHeureLabel(String dateHeureLabel) { this.dateHeureLabel = dateHeureLabel; }

    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String patientNom) { this.patientNom = patientNom; }

    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String medecinNom) { this.medecinNom = medecinNom; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}