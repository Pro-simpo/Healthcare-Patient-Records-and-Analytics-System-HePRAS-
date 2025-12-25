package ma.ensa.healthcare.model;

import java.util.List;

/**
 * Modèle Departement - Correspond à la table DEPARTEMENT
 */
public class Departement {
    private Long id;                    // id_departement
    private String nomDepartement;      // nom_departement (UNIQUE NOT NULL)
    private Medecin chefDepartement;    // chef_departement_id (FK vers MEDECIN)
    private Integer nombreLits;         // nombre_lits (DEFAULT 0)
    private String telephone;           // telephone
    
    // Relations
    private List<Medecin> medecins;     // Liste des médecins du département

    // --- Constructeurs ---
    public Departement() {}

    public Departement(Long id, String nomDepartement, Medecin chefDepartement, 
                      Integer nombreLits, String telephone) {
        this.id = id;
        this.nomDepartement = nomDepartement;
        this.chefDepartement = chefDepartement;
        this.nombreLits = nombreLits;
        this.telephone = telephone;
    }

    // --- Pattern Builder ---
    public static DepartementBuilder builder() {
        return new DepartementBuilder();
    }

    public static class DepartementBuilder {
        private Long id;
        private String nomDepartement;
        private Medecin chefDepartement;
        private Integer nombreLits;
        private String telephone;
        private List<Medecin> medecins;

        public DepartementBuilder id(Long id) { this.id = id; return this; }
        public DepartementBuilder nomDepartement(String nomDepartement) { this.nomDepartement = nomDepartement; return this; }
        public DepartementBuilder chefDepartement(Medecin chefDepartement) { this.chefDepartement = chefDepartement; return this; }
        public DepartementBuilder nombreLits(Integer nombreLits) { this.nombreLits = nombreLits; return this; }
        public DepartementBuilder telephone(String telephone) { this.telephone = telephone; return this; }
        public DepartementBuilder medecins(List<Medecin> medecins) { this.medecins = medecins; return this; }

        public Departement build() {
            Departement dept = new Departement(id, nomDepartement, chefDepartement, nombreLits, telephone);
            dept.setMedecins(medecins);
            return dept;
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomDepartement() { return nomDepartement; }
    public void setNomDepartement(String nomDepartement) { this.nomDepartement = nomDepartement; }

    public Medecin getChefDepartement() { return chefDepartement; }
    public void setChefDepartement(Medecin chefDepartement) { this.chefDepartement = chefDepartement; }

    public Integer getNombreLits() { return nombreLits; }
    public void setNombreLits(Integer nombreLits) { this.nombreLits = nombreLits; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public List<Medecin> getMedecins() { return medecins; }
    public void setMedecins(List<Medecin> medecins) { this.medecins = medecins; }
}