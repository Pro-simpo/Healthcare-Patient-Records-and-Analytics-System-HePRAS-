package ma.ensa.healthcare.model;

public class Traitement {
    private Long id;
    private String posologie; // ex: "1 comprimé 3 fois par jour"
    private Integer dureeJours;
    private Consultation consultation;
    private Medicament medicament;

    public Traitement() {}

    public Traitement(Long id, String posologie, Integer dureeJours, Consultation consultation, Medicament medicament) {
        this.id = id;
        this.posologie = posologie;
        this.dureeJours = dureeJours;
        this.consultation = consultation;
        this.medicament = medicament;
    }

    // Builder manuel
    public static TraitementBuilder builder() { return new TraitementBuilder(); }

    public static class TraitementBuilder {
        private Long id;
        private String posologie;
        private Integer dureeJours;
        private Consultation consultation;
        private Medicament medicament;

        public TraitementBuilder id(Long id) { this.id = id; return this; }
        public TraitementBuilder posologie(String posologie) { this.posologie = posologie; return this; }
        public TraitementBuilder dureeJours(Integer dureeJours) { this.dureeJours = dureeJours; return this; }
        public TraitementBuilder consultation(Consultation consultation) { this.consultation = consultation; return this; }
        public TraitementBuilder medicament(Medicament medicament) { this.medicament = medicament; return this; }
        public Traitement build() { return new Traitement(id, posologie, dureeJours, consultation, medicament); }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPosologie() { return posologie; }
    public void setPosologie(String posologie) { this.posologie = posologie; }
    public Integer getDureeJours() { return dureeJours; }
    public void setDureeJours(Integer dureeJours) { this.dureeJours = dureeJours; }
    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
    public Medicament getMedicament() { return medicament; }
    public void setMedicament(Medicament medicament) { this.medicament = medicament; }
}