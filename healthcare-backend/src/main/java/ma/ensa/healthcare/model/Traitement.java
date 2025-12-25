package ma.ensa.healthcare.model;

/**
 * Modèle Traitement - Correspond à la table TRAITEMENT
 */
public class Traitement {
    private Long id;                    // id_traitement
    private Consultation consultation;  // id_consultation (FK NOT NULL)
    private Medicament medicament;      // id_medicament (FK NOT NULL)
    private String posologie;           // posologie (NOT NULL) - ex: "1 comprimé 3 fois/jour"
    private Integer dureeTraitement;    // duree_traitement (en jours)
    private String instructions;        // instructions
    private Integer quantite;           // quantite

    // --- Constructeurs ---
    public Traitement() {}

    public Traitement(Long id, Consultation consultation, Medicament medicament, 
                     String posologie, Integer dureeTraitement, String instructions, Integer quantite) {
        this.id = id;
        this.consultation = consultation;
        this.medicament = medicament;
        this.posologie = posologie;
        this.dureeTraitement = dureeTraitement;
        this.instructions = instructions;
        this.quantite = quantite;
    }

    // --- Pattern Builder ---
    public static TraitementBuilder builder() {
        return new TraitementBuilder();
    }

    public static class TraitementBuilder {
        private Long id;
        private Consultation consultation;
        private Medicament medicament;
        private String posologie;
        private Integer dureeTraitement;
        private String instructions;
        private Integer quantite;

        public TraitementBuilder id(Long id) { this.id = id; return this; }
        public TraitementBuilder consultation(Consultation consultation) { this.consultation = consultation; return this; }
        public TraitementBuilder medicament(Medicament medicament) { this.medicament = medicament; return this; }
        public TraitementBuilder posologie(String posologie) { this.posologie = posologie; return this; }
        public TraitementBuilder dureeTraitement(Integer dureeTraitement) { this.dureeTraitement = dureeTraitement; return this; }
        public TraitementBuilder instructions(String instructions) { this.instructions = instructions; return this; }
        public TraitementBuilder quantite(Integer quantite) { this.quantite = quantite; return this; }

        public Traitement build() {
            return new Traitement(id, consultation, medicament, posologie, dureeTraitement, instructions, quantite);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }

    public Medicament getMedicament() { return medicament; }
    public void setMedicament(Medicament medicament) { this.medicament = medicament; }

    public String getPosologie() { return posologie; }
    public void setPosologie(String posologie) { this.posologie = posologie; }

    public Integer getDureeTraitement() { return dureeTraitement; }
    public void setDureeTraitement(Integer dureeTraitement) { this.dureeTraitement = dureeTraitement; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}