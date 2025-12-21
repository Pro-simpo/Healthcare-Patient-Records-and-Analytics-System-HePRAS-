package ma.ensa.healthcare.dto;

public class PatientDTO {
    private String nomComplet;
    private String cin;
    private String contact;
    private String statutMedical;

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getStatutMedical() { return statutMedical; }
    public void setStatutMedical(String statutMedical) { this.statutMedical = statutMedical; }
}