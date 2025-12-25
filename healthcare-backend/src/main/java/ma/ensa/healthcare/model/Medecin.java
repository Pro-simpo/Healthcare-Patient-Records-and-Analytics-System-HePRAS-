package ma.ensa.healthcare.model;

import java.time.LocalDate;

/**
 * Modèle Medecin - Correspond à la table MEDECIN
 */
public class Medecin {
    private Long id;                    // id_medecin
    private String numeroOrdre;         // numero_ordre (UNIQUE NOT NULL)
    private String nom;                 // nom
    private String prenom;              // prenom
    private String specialite;          // specialite
    private String telephone;           // telephone
    private String email;               // email
    private LocalDate dateEmbauche;     // date_embauche (NOT NULL)
    private Departement departement;    // id_departement (FK NOT NULL)

    // --- Constructeurs ---
    public Medecin() {}

    public Medecin(Long id, String numeroOrdre, String nom, String prenom, String specialite, 
                   String telephone, String email, LocalDate dateEmbauche, Departement departement) {
        this.id = id;
        this.numeroOrdre = numeroOrdre;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.email = email;
        this.dateEmbauche = dateEmbauche;
        this.departement = departement;
    }

    // --- Pattern Builder ---
    public static MedecinBuilder builder() {
        return new MedecinBuilder();
    }

    public static class MedecinBuilder {
        private Long id;
        private String numeroOrdre;
        private String nom;
        private String prenom;
        private String specialite;
        private String telephone;
        private String email;
        private LocalDate dateEmbauche;
        private Departement departement;

        public MedecinBuilder id(Long id) { this.id = id; return this; }
        public MedecinBuilder numeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; return this; }
        public MedecinBuilder nom(String nom) { this.nom = nom; return this; }
        public MedecinBuilder prenom(String prenom) { this.prenom = prenom; return this; }
        public MedecinBuilder specialite(String specialite) { this.specialite = specialite; return this; }
        public MedecinBuilder telephone(String telephone) { this.telephone = telephone; return this; }
        public MedecinBuilder email(String email) { this.email = email; return this; }
        public MedecinBuilder dateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; return this; }
        public MedecinBuilder departement(Departement departement) { this.departement = departement; return this; }

        public Medecin build() {
            return new Medecin(id, numeroOrdre, nom, prenom, specialite, telephone, email, dateEmbauche, departement);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }
}