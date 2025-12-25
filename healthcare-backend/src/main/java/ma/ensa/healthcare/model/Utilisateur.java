package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle Utilisateur - Correspond à la table UTILISATEUR
 */
public class Utilisateur {
    private Long id;                        // id_utilisateur
    private String username;                // username (UNIQUE NOT NULL)
    private String passwordHash;            // password_hash (NOT NULL)
    private String email;                   // email (UNIQUE NOT NULL)
    private Role role;                      // role (NOT NULL)
    private String statut;                  // statut (DEFAULT 'ACTIF': ACTIF, INACTIF, SUSPENDU)
    private Medecin medecin;                // id_medecin (FK UNIQUE)
    private Patient patient;                // id_patient (FK UNIQUE)
    private LocalDate dateCreation;         // date_creation (DEFAULT SYSDATE)
    private LocalDateTime derniereConnexion; // derniere_connexion
    private Integer tentativesEchec;        // tentatives_echec (DEFAULT 0)

    // --- Constructeurs ---
    public Utilisateur() {}

    public Utilisateur(Long id, String username, String passwordHash, String email, Role role, 
                      String statut, Medecin medecin, Patient patient, LocalDate dateCreation, 
                      LocalDateTime derniereConnexion, Integer tentativesEchec) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.statut = statut;
        this.medecin = medecin;
        this.patient = patient;
        this.dateCreation = dateCreation;
        this.derniereConnexion = derniereConnexion;
        this.tentativesEchec = tentativesEchec;
    }

    // --- Pattern Builder ---
    public static UtilisateurBuilder builder() {
        return new UtilisateurBuilder();
    }

    public static class UtilisateurBuilder {
        private Long id;
        private String username;
        private String passwordHash;
        private String email;
        private Role role;
        private String statut = "ACTIF";
        private Medecin medecin;
        private Patient patient;
        private LocalDate dateCreation;
        private LocalDateTime derniereConnexion;
        private Integer tentativesEchec = 0;

        public UtilisateurBuilder id(Long id) { this.id = id; return this; }
        public UtilisateurBuilder username(String username) { this.username = username; return this; }
        public UtilisateurBuilder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public UtilisateurBuilder email(String email) { this.email = email; return this; }
        public UtilisateurBuilder role(Role role) { this.role = role; return this; }
        public UtilisateurBuilder statut(String statut) { this.statut = statut; return this; }
        public UtilisateurBuilder medecin(Medecin medecin) { this.medecin = medecin; return this; }
        public UtilisateurBuilder patient(Patient patient) { this.patient = patient; return this; }
        public UtilisateurBuilder dateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; return this; }
        public UtilisateurBuilder derniereConnexion(LocalDateTime derniereConnexion) { this.derniereConnexion = derniereConnexion; return this; }
        public UtilisateurBuilder tentativesEchec(Integer tentativesEchec) { this.tentativesEchec = tentativesEchec; return this; }

        public Utilisateur build() {
            return new Utilisateur(id, username, passwordHash, email, role, statut, 
                                 medecin, patient, dateCreation, derniereConnexion, tentativesEchec);
        }
    }

    // --- Getters et Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(LocalDateTime derniereConnexion) { this.derniereConnexion = derniereConnexion; }

    public Integer getTentativesEchec() { return tentativesEchec; }
    public void setTentativesEchec(Integer tentativesEchec) { this.tentativesEchec = tentativesEchec; }

    // --- Méthodes utilitaires ---
    public boolean isActif() {
        return "ACTIF".equals(statut);
    }

    public void setActif(boolean actif) {
        this.statut = actif ? "ACTIF" : "INACTIF";
    }
}