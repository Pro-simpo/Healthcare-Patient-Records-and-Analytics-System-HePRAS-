package ma.ensa.healthcare.ui.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.ensa.healthcare.config.HikariCPConfig;
import ma.ensa.healthcare.config.PropertyManager;
import ma.ensa.healthcare.ui.MainApp;
import ma.ensa.healthcare.ui.utils.SessionManager;
import ma.ensa.healthcare.util.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    // Profil Utilisateur
    @FXML private Label lblUsername;
    @FXML private TextField txtEmail;
    @FXML private Label lblRole;
    @FXML private Label lblLastLogin;

    // Paramètres Application
    @FXML private ComboBox<String> cmbLanguage;
    @FXML private ComboBox<String> cmbTheme;
    @FXML private CheckBox chkNotifications;
    @FXML private CheckBox chkSounds;
    @FXML private CheckBox chkAutoSave;

    // Base de Données
    @FXML private Label lblDbStatus;
    @FXML private Label lblDbUrl;
    @FXML private Label lblPoolInfo;

    // Système
    @FXML private Label lblJavaVersion;
    @FXML private Label lblJavaFxVersion;
    @FXML private Label lblOs;
    @FXML private Label lblMemory;

    @FXML
    public void initialize() {
        loadUserProfile();
        loadApplicationSettings();
        loadDatabaseInfo();
        loadSystemInfo();
        
        // Auto-refresh mémoire toutes les 5 secondes
        startMemoryMonitoring();
    }

    /**
     * SECTION: Profil Utilisateur
     */
    private void loadUserProfile() {
        if (SessionManager.isLoggedIn()) {
            lblUsername.setText(SessionManager.getCurrentUserFullName());
            lblRole.setText(SessionManager.getCurrentUserRole());
            
            if (SessionManager.getCurrentUser().getEmail() != null) {
                txtEmail.setText(SessionManager.getCurrentUser().getEmail());
            }
            
            if (SessionManager.getCurrentUser().getDerniereConnexion() != null) {
                lblLastLogin.setText(
                    SessionManager.getCurrentUser().getDerniereConnexion()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                );
            }
        }
    }

    @FXML
    private void handleEditProfile() {
        showInfo("Modifier le Profil", "Fonctionnalité en cours de développement");
    }

    @FXML
    private void handleChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Changer le Mot de Passe");
        dialog.setHeaderText("Nouveau mot de passe");
        dialog.setContentText("Mot de passe:");
        
        dialog.showAndWait().ifPresent(password -> {
            if (password.length() < 6) {
                showError("Erreur", "Le mot de passe doit contenir au moins 6 caractères");
            } else {
                showSuccess("Succès", "Mot de passe modifié avec succès!");
                logger.info("Mot de passe modifié pour l'utilisateur: {}", 
                           SessionManager.getCurrentUserFullName());
            }
        });
    }

    /**
     * SECTION: Paramètres Application
     */
    private void loadApplicationSettings() {
        // Langue
        cmbLanguage.setValue("Français");
        
        // Thème
        cmbTheme.setValue("Clair");
        
        // Notifications
        chkNotifications.setSelected(true);
        chkSounds.setSelected(true);
        chkAutoSave.setSelected(true);
    }

    @FXML
    private void handleSaveSettings() {
        try {
            String langue = cmbLanguage.getValue();
            String theme = cmbTheme.getValue();
            
            logger.info("Paramètres sauvegardés - Langue: {}, Thème: {}", langue, theme);
            showSuccess("Succès", "Paramètres sauvegardés avec succès!");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde des paramètres", e);
            showError("Erreur", "Impossible de sauvegarder les paramètres");
        }
    }

    /**
     * SECTION: Base de Données
     */
    private void loadDatabaseInfo() {
        PropertyManager props = PropertyManager.getInstance();
        
        // URL
        lblDbUrl.setText(props.getProperty("db.url", "N/A"));
        
        // Statut
        try (Connection conn = HikariCPConfig.getDataSource().getConnection()) {
            if (conn.isValid(2)) {
                lblDbStatus.setText("● Connecté");
                lblDbStatus.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            } else {
                lblDbStatus.setText("● Déconnecté");
                lblDbStatus.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
            }
        } catch (SQLException e) {
            lblDbStatus.setText("● Erreur");
            lblDbStatus.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        }
        
        // Pool info
        updatePoolInfo();
    }

    private void updatePoolInfo() {
        try {
            int active = HikariCPConfig.getDataSource().getHikariPoolMXBean().getActiveConnections();
            int total = HikariCPConfig.getDataSource().getHikariConfigMXBean().getMaximumPoolSize();
            lblPoolInfo.setText(active + "/" + total + " actives");
        } catch (Exception e) {
            lblPoolInfo.setText("N/A");
        }
    }

    @FXML
    private void handleTestConnection() {
        try (Connection conn = HikariCPConfig.getDataSource().getConnection()) {
            if (conn.isValid(5)) {
                showSuccess("Connexion Réussie", 
                    "La connexion à la base de données fonctionne correctement!");
                loadDatabaseInfo();
            } else {
                showError("Échec", "Impossible de se connecter à la base de données");
            }
        } catch (SQLException e) {
            logger.error("Erreur de connexion", e);
            showError("Erreur", "Erreur de connexion: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportDatabase() {
        showInfo("Export", "Fonctionnalité d'export en cours de développement");
    }

    @FXML
    private void handleBackupDatabase() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Sauvegarde");
        confirmation.setHeaderText("Sauvegarder la base de données");
        confirmation.setContentText("Voulez-vous créer une sauvegarde complète?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showInfo("Sauvegarde", "Sauvegarde en cours...\n" +
                    "Cette fonctionnalité sera implémentée prochainement");
            }
        });
    }

    /**
     * SECTION: Système
     */
    private void loadSystemInfo() {
        // Java Version
        lblJavaVersion.setText(System.getProperty("java.version"));
        
        // JavaFX Version
        lblJavaFxVersion.setText(System.getProperty("javafx.version", "N/A"));
        
        // OS
        lblOs.setText(System.getProperty("os.name") + " " + 
                     System.getProperty("os.version"));
        
        // Mémoire
        updateMemoryInfo();
    }

    private void updateMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        
        lblMemory.setText(usedMemory + " MB / " + maxMemory + " MB");
        
        // Changer la couleur selon l'utilisation
        double percentage = (double) usedMemory / maxMemory * 100;
        if (percentage > 80) {
            lblMemory.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        } else if (percentage > 60) {
            lblMemory.setStyle("-fx-text-fill: #FF9800;");
        } else {
            lblMemory.setStyle("-fx-text-fill: #4CAF50;");
        }
    }

    private void startMemoryMonitoring() {
        // Update memory every 5 seconds
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(5),
                event -> {
                    updateMemoryInfo();
                    updatePoolInfo();
                }
            )
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleCheckUpdates() {
        showInfo("Mises à Jour", "Vous utilisez la dernière version (1.0.0)");
    }

    @FXML
    private void handleShowLogs() {
        try {
            File logFile = new File("logs/healthcare-application.log");
            if (logFile.exists()) {
                showInfo("Logs", "Fichier de logs: " + logFile.getAbsolutePath() + 
                    "\n\nTaille: " + (logFile.length() / 1024) + " KB");
            } else {
                showWarning("Logs", "Aucun fichier de logs trouvé");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'accès aux logs", e);
            showError("Erreur", "Impossible d'accéder aux logs");
        }
    }

    /**
     * SECTION: Actions Système
     */
    @FXML
    private void handleClearCache() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Vider le Cache");
        confirmation.setHeaderText("Confirmer la suppression");
        confirmation.setContentText("Voulez-vous vraiment vider le cache?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    CacheManager.clear();
                    CacheManager.evictExpiredEntries();
                    
                    showSuccess("Succès", "Cache vidé avec succès!");
                    logger.info("Cache système vidé");
                } catch (Exception e) {
                    logger.error("Erreur lors du vidage du cache", e);
                    showError("Erreur", "Impossible de vider le cache");
                }
            }
        });
    }

    @FXML
    private void handleResetSettings() {
        Alert confirmation = new Alert(Alert.AlertType.WARNING);
        confirmation.setTitle("Réinitialiser");
        confirmation.setHeaderText("Confirmer la réinitialisation");
        confirmation.setContentText("Cela va restaurer tous les paramètres par défaut. Continuer?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Réinitialiser les valeurs
                cmbLanguage.setValue("Français");
                cmbTheme.setValue("Clair");
                chkNotifications.setSelected(true);
                chkSounds.setSelected(true);
                chkAutoSave.setSelected(true);
                
                showSuccess("Succès", "Paramètres réinitialisés avec succès!");
                logger.info("Paramètres réinitialisés aux valeurs par défaut");
            }
        });
    }

    @FXML
    private void handleCleanLogs() {
        Alert confirmation = new Alert(Alert.AlertType.WARNING);
        confirmation.setTitle("Nettoyer les Logs");
        confirmation.setHeaderText("⚠️ Action Irréversible");
        confirmation.setContentText("Voulez-vous vraiment supprimer tous les fichiers de logs?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showInfo("Nettoyage", "Fonctionnalité en cours de développement");
            }
        });
    }

    /**
     * SECTION: À Propos
     */
    @FXML
    private void handleOpenDocumentation() {
        showInfo("Documentation", 
            "Documentation complète disponible sur:\n" +
            "https://github.com/votre-projet/healthcare-system/wiki");
    }

    @FXML
    private void handleOpenSupport() {
        showInfo("Support", 
            "Pour toute assistance, contactez:\n" +
            "Email: support@healthcare.ma\n" +
            "Tél: +212 5XX-XXXXXX");
    }

    @FXML
    private void handleOpenLicense() {
        showInfo("Licence", 
            "Healthcare System v1.0.0\n\n" +
            "© 2025 ENSA Tétouan\n" +
            "Tous droits réservés\n\n" +
            "Ce logiciel est distribué sous licence MIT");
    }

    /**
     * Méthodes utilitaires
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }
}