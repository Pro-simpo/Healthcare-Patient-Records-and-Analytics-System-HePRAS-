package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.Sexe;
import java.time.LocalDate;

public class Patient {

    private Long id;
    private String nom;
    private String prenom;
    private String cin;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String antecedentsMedicaux;
    private LocalDate dateCreation;

    // --- 1. Constructeur vide (Obligatoire) ---
    public Patient() {
    }

    // --- 2. Constructeur complet (Builder manuel) ---
    public Patient(Long id, String nom, String prenom, String cin, String adresse, 
                   String telephone, String email, LocalDate dateNaissance, 
                   Sexe sexe, String antecedentsMedicaux, LocalDate dateCreation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.antecedentsMedicaux = antecedentsMedicaux;
        this.dateCreation = dateCreation;
    }

    // --- 3. Pattern Builder (Pour que le Main fonctionne sans changer le code) ---
    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    public static class PatientBuilder {
        private Long id;
        private String nom;
        private String prenom;
        private String cin;
        private String adresse;
        private String telephone;
        private String email;
        private LocalDate dateNaissance;
        private Sexe sexe;
        private String antecedentsMedicaux;
        private LocalDate dateCreation;

        public PatientBuilder id(Long id) { this.id = id; return this; }
        public PatientBuilder nom(String nom) { this.nom = nom; return this; }
        public PatientBuilder prenom(String prenom) { this.prenom = prenom; return this; }
        public PatientBuilder cin(String cin) { this.cin = cin; return this; }
        public PatientBuilder adresse(String adresse) { this.adresse = adresse; return this; }
        public PatientBuilder telephone(String telephone) { this.telephone = telephone; return this; }
        public PatientBuilder email(String email) { this.email = email; return this; }
        public PatientBuilder dateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; return this; }
        public PatientBuilder sexe(Sexe sexe) { this.sexe = sexe; return this; }
        public PatientBuilder antecedentsMedicaux(String antecedentsMedicaux) { this.antecedentsMedicaux = antecedentsMedicaux; return this; }
        public PatientBuilder dateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; return this; }

        public Patient build() {
            return new Patient(id, nom, prenom, cin, adresse, telephone, email, dateNaissance, sexe, antecedentsMedicaux, dateCreation);
        }
    }

    // --- 4. Getters et Setters (Ceux qui manquaient !) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }

    public String getAntecedentsMedicaux() { return antecedentsMedicaux; }
    public void setAntecedentsMedicaux(String antecedentsMedicaux) { this.antecedentsMedicaux = antecedentsMedicaux; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}