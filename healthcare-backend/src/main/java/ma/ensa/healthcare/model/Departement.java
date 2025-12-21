package ma.ensa.healthcare.model;

public class Departement {
    private Long id;
    private String nom;
    private String description;

    // Constructeurs
    public Departement() {}

    public Departement(Long id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    // Pattern Builder Manuel
    public static DepartementBuilder builder() {
        return new DepartementBuilder();
    }

    public static class DepartementBuilder {
        private Long id;
        private String nom;
        private String description;

        public DepartementBuilder id(Long id) { this.id = id; return this; }
        public DepartementBuilder nom(String nom) { this.nom = nom; return this; }
        public DepartementBuilder description(String description) { this.description = description; return this; }

        public Departement build() {
            return new Departement(id, nom, description);
        }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}