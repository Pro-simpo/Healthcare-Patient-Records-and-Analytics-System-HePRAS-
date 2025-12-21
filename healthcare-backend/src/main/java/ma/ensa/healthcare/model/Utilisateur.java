package ma.ensa.healthcare.model;

import ma.ensa.healthcare.model.enums.Role;

public class Utilisateur {
    private Long id;
    private String username;
    private String password; // Sera stocké haché idéalement
    private String email;
    private Role role;
    private boolean actif;

    public Utilisateur() {}

    public Utilisateur(Long id, String username, String password, String email, Role role, boolean actif) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.actif = actif;
    }

    // Builder manuel
    public static UtilisateurBuilder builder() { return new UtilisateurBuilder(); }

    public static class UtilisateurBuilder {
        private Long id;
        private String username;
        private String password;
        private String email;
        private Role role;
        private boolean actif = true;

        public UtilisateurBuilder id(Long id) { this.id = id; return this; }
        public UtilisateurBuilder username(String username) { this.username = username; return this; }
        public UtilisateurBuilder password(String password) { this.password = password; return this; }
        public UtilisateurBuilder email(String email) { this.email = email; return this; }
        public UtilisateurBuilder role(Role role) { this.role = role; return this; }
        public UtilisateurBuilder actif(boolean actif) { this.actif = actif; return this; }
        public Utilisateur build() { return new Utilisateur(id, username, password, email, role, actif); }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}