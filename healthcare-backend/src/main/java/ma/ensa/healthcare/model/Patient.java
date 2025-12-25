package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.Sexe;
import java.time.LocalDate;

/**
 * Modèle Patient - Correspond à la table PATIENT
 */
public class Patient {
    private Long id;                    // id_patient
    private String cin;                 // cin (UNIQUE)
    private String nom;                 // nom
    private String prenom;              // prenom
    private LocalDate dateNaissance;    // date_naissance
    private Sexe sexe;                  // sexe (M/F)
    private String adresse;             // adresse
    private String ville;               // ville
    private String codePostal;          // code_postal
    private String telephone;           // telephone
    private String email;               // email
    private String groupeSanguin;       // groupe_sanguin (A+, A-, B+, B-, AB+, AB-, O+, O-)
    private String allergies;           // allergies
    private LocalDate dateInscription;  // date_inscription

    // --- Constructeur vide ---
    public Patient() {}

    // --- Constructeur complet ---
    public Patient(Long id, String cin, String nom, String prenom, LocalDate dateNaissance, 
                   Sexe sexe, String adresse, String ville, String codePostal, String telephone, 
                   String email, String groupeSanguin, String allergies, LocalDate dateInscription) {
        this.id = id;
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.telephone = telephone;
        this.email = email;
        this.groupeSanguin = groupeSanguin;
        this.allergies = allergies;
        this.dateInscription = dateInscription;
    }

    // --- Pattern Builder ---
    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    public static class PatientBuilder {
        private Long id;
        private String cin;
        private String nom;
        private String prenom;
        private LocalDate dateNaissance;
        private Sexe sexe;
        private String adresse;
        private String ville;
        private String codePostal;
        private String telephone;
        private String email;
        private String groupeSanguin;
        private String allergies;
        private LocalDate dateInscription;

        public PatientBuilder id(Long id) { this.id = id; return this; }
        public PatientBuilder cin(String cin) { this.cin = cin; return this; }
        public PatientBuilder nom(String nom) { this.nom = nom; return this; }
        public PatientBuilder prenom(String prenom) { this.prenom = prenom; return this; }
        public PatientBuilder dateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; return this; }
        public PatientBuilder sexe(Sexe sexe) { this.sexe = sexe; return this; }
        public PatientBuilder adresse(String adresse) { this.adresse = adresse; return this; }
        public PatientBuilder ville(String ville) { this.ville = ville; return this; }
        public PatientBuilder codePostal(String codePostal) { this.codePostal = codePostal; return this; }
        public PatientBuilder telephone(String telephone) { this.telephone = telephone; return this; }
        public PatientBuilder email(String email) { this.email = email; return this; }
        public PatientBuilder groupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; return this; }
        public PatientBuilder allergies(String allergies) { this.allergies = allergies; return this; }
        public PatientBuilder dateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; return this; }

        public Patient build() {
            return new Patient(id, cin, nom, prenom, dateNaissance, sexe, adresse, ville, 
                             codePostal, telephone, email, groupeSanguin, allergies, dateInscription);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
}