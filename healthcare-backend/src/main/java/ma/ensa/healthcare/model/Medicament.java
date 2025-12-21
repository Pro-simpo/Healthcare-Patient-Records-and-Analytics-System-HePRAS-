package ma.ensa.healthcare.model;

public class Medicament {
    private Long id;
    private String nom;
    private String dosage;
    private String instructions;

    public Medicament() {}

    public Medicament(Long id, String nom, String dosage, String instructions) {
        this.id = id;
        this.nom = nom;
        this.dosage = dosage;
        this.instructions = instructions;
    }

    // Builder manuel
    public static MedicamentBuilder builder() {
        return new MedicamentBuilder();
    }

    public static class MedicamentBuilder {
        private Long id;
        private String nom;
        private String dosage;
        private String instructions;

        public MedicamentBuilder id(Long id) { this.id = id; return this; }
        public MedicamentBuilder nom(String nom) { this.nom = nom; return this; }
        public MedicamentBuilder dosage(String dosage) { this.dosage = dosage; return this; }
        public MedicamentBuilder instructions(String instructions) { this.instructions = instructions; return this; }

        public Medicament build() {
            return new Medicament(id, nom, dosage, instructions);
        }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}