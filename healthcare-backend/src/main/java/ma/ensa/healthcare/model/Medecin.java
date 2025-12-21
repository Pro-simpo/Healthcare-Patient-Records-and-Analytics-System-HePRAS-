package ma.ensa.healthcare.model;

public class Medecin {
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
    private String email;
    private String telephone;

    // --- Constructeurs ---
    public Medecin() {}

    public Medecin(Long id, String nom, String prenom, String specialite, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.email = email;
        this.telephone = telephone;
    }

    // --- Pattern Builder Manuel (Pour garder la compatibilité avec votre code existant) ---
    public static MedecinBuilder builder() {
        return new MedecinBuilder();
    }

    public static class MedecinBuilder {
        private Long id;
        private String nom;
        private String prenom;
        private String specialite;
        private String email;
        private String telephone;

        public MedecinBuilder id(Long id) { this.id = id; return this; }
        public MedecinBuilder nom(String nom) { this.nom = nom; return this; }
        public MedecinBuilder prenom(String prenom) { this.prenom = prenom; return this; }
        public MedecinBuilder specialite(String specialite) { this.specialite = specialite; return this; }
        public MedecinBuilder email(String email) { this.email = email; return this; }
        public MedecinBuilder telephone(String telephone) { this.telephone = telephone; return this; }

        public Medecin build() {
            return new Medecin(id, nom, prenom, specialite, email, telephone);
        }
    }

    // --- Getters et Setters Manuels ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}