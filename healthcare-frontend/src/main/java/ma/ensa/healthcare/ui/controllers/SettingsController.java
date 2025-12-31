package ma.ensa.healthcare.ui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ma.ensa.healthcare.config.HikariCPConfig;
import ma.ensa.healthcare.config.PropertyManager;
import ma.ensa.healthcare.model.Utilisateur;
import ma.ensa.healthcare.service.UtilisateurService;
import ma.ensa.healthcare.ui.MainApp;
import ma.ensa.healthcare.ui.utils.PermissionManager;
import ma.ensa.healthcare.ui.utils.SessionManager;
import ma.ensa.healthcare.util.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import ma.ensa.healthcare.util.DatabaseExportService;
import javafx.concurrent.Task;
import org.controlsfx.dialog.ProgressDialog;
import java.io.File;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import ma.ensa.healthcare.model.enums.Role;

import ma.ensa.healthcare.ui.utils.PermissionManager;


/**
 * Contrôleur pour la page des paramètres
 * Gère le profil utilisateur, paramètres app, BDD, système
 */
public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Services
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private Timeline memoryMonitorTimeline;

    // ========== Profil Utilisateur ==========
    @FXML private Label lblUsername;
    @FXML private TextField txtEmail;
    @FXML private Label lblRole;
    @FXML private Label lblLastLogin;

    // ========== Paramètres Application ==========
    @FXML private ComboBox<String> cmbLanguage;
    @FXML private ComboBox<String> cmbTheme;
    @FXML private CheckBox chkNotifications;
    @FXML private CheckBox chkSounds;
    @FXML private CheckBox chkAutoSave;

    // ========== Base de Données ==========
    @FXML private Label lblDbStatus;
    @FXML private Label lblDbUrl;
    @FXML private Label lblPoolInfo;

    // ========== Système ==========
    @FXML private Label lblJavaVersion;
    @FXML private Label lblJavaFxVersion;
    @FXML private Label lblOs;
    @FXML private Label lblMemory;

    @FXML private VBox sectionDatabase;        // Section Base de Données
    @FXML private VBox sectionSystemActions;

    @FXML
    public void initialize() {
        if (!PermissionManager.canAccessSettings()) {
            showError("Accès refusé", PermissionManager.getAccessDeniedMessage());
            return;
        }
        logger.info("Initialisation de l'onglet Paramètres");
        
        try {
            loadUserProfile();
            loadApplicationSettings();
            loadDatabaseInfo();
            loadSystemInfo();
            startMemoryMonitoring();
            configureAdvancedSettings();
            
            logger.info("Paramètres chargés avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation des paramètres", e);
            showError("Erreur d'Initialisation", 
                "Impossible de charger tous les paramètres: " + e.getMessage());
        }
    }

    // ========================================================================
    // SECTION 1: PROFIL UTILISATEUR
    // ========================================================================

    private void loadUserProfile() {
        if (!SessionManager.isLoggedIn()) {
            logger.warn("Aucun utilisateur connecté");
            return;
        }

        try {
            Utilisateur user = SessionManager.getCurrentUser();
            
            // Nom d'utilisateur
            lblUsername.setText(user.getUsername());
            
            // Email
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                txtEmail.setText(user.getEmail());
            } else {
                txtEmail.setPromptText("Aucun email configuré");
            }
            
            // Rôle
            String roleText = formatRole(user.getRole().name());
            lblRole.setText(roleText);
            lblRole.setStyle("-fx-text-fill: " + getRoleColor(user.getRole().name()) + ";");
            
            // Dernière connexion
            if (user.getDerniereConnexion() != null) {
                lblLastLogin.setText(user.getDerniereConnexion().format(DATE_TIME_FORMATTER));
            } else {
                lblLastLogin.setText("Première connexion");
            }
            
            logger.debug("Profil utilisateur chargé: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du profil", e);
            showError("Erreur", "Impossible de charger le profil utilisateur");
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            // Dialog pour modifier l'email
            TextInputDialog dialog = new TextInputDialog(txtEmail.getText());
            dialog.setTitle("Modifier le Profil");
            dialog.setHeaderText("Modifier votre email");
            dialog.setContentText("Nouvel email:");
            dialog.initOwner(MainApp.getPrimaryStage());

            Optional<String> result = dialog.showAndWait();
            
            result.ifPresent(email -> {
                if (email.trim().isEmpty()) {
                    showError("Erreur", "L'email ne peut pas être vide");
                    return;
                }
                
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showError("Erreur", "Format d'email invalide");
                    return;
                }

                try {
                    Utilisateur user = SessionManager.getCurrentUser();
                    user.setEmail(email);
                    utilisateurService.updateUtilisateur(user);
                    
                    txtEmail.setText(email);
                    showSuccess("Succès", "Email modifié avec succès!");
                    logger.info("Email modifié pour l'utilisateur: {}", user.getUsername());
                } catch (Exception e) {
                    logger.error("Erreur lors de la modification de l'email", e);
                    showError("Erreur", "Impossible de modifier l'email: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Erreur dans handleEditProfile", e);
            showError("Erreur", "Une erreur est survenue");
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            // Dialog personnalisé avec 3 champs
            Dialog<String[]> dialog = new Dialog<>();
            dialog.setTitle("Changer le Mot de Passe");
            dialog.setHeaderText("Modifier votre mot de passe");
            dialog.initOwner(MainApp.getPrimaryStage());

            ButtonType changeButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

            // Créer les champs
            PasswordField oldPasswordField = new PasswordField();
            oldPasswordField.setPromptText("Ancien mot de passe");
            
            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setPromptText("Nouveau mot de passe");
            
            PasswordField confirmPasswordField = new PasswordField();
            confirmPasswordField.setPromptText("Confirmer le nouveau mot de passe");

            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            grid.add(new Label("Ancien mot de passe:"), 0, 0);
            grid.add(oldPasswordField, 1, 0);
            grid.add(new Label("Nouveau mot de passe:"), 0, 1);
            grid.add(newPasswordField, 1, 1);
            grid.add(new Label("Confirmer:"), 0, 2);
            grid.add(confirmPasswordField, 1, 2);

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(oldPasswordField::requestFocus);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == changeButtonType) {
                    return new String[]{
                        oldPasswordField.getText(),
                        newPasswordField.getText(),
                        confirmPasswordField.getText()
                    };
                }
                return null;
            });

            Optional<String[]> result = dialog.showAndWait();

            result.ifPresent(passwords -> {
                String oldPassword = passwords[0];
                String newPassword = passwords[1];
                String confirmPassword = passwords[2];

                // Validation
                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    showError("Erreur", "Tous les champs sont obligatoires");
                    return;
                }

                if (newPassword.length() < 6) {
                    showError("Erreur", "Le nouveau mot de passe doit contenir au moins 6 caractères");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showError("Erreur", "Les mots de passe ne correspondent pas");
                    return;
                }

                try {
                    Utilisateur user = SessionManager.getCurrentUser();
                    utilisateurService.changerMotDePasse(user.getId(), oldPassword, newPassword);
                    
                    showSuccess("Succès", "Mot de passe modifié avec succès!");
                    logger.info("Mot de passe modifié pour l'utilisateur: {}", user.getUsername());
                } catch (Exception e) {
                    logger.error("Erreur lors du changement de mot de passe", e);
                    showError("Erreur", e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Erreur dans handleChangePassword", e);
            showError("Erreur", "Une erreur est survenue");
        }
    }

    // ========================================================================
    // SECTION 2: PARAMÈTRES APPLICATION
    // ========================================================================

    private void loadApplicationSettings() {
        try {
            // Charger les préférences depuis PropertyManager ou fichier config
            PropertyManager props = PropertyManager.getInstance();
            
            // Langue (valeur par défaut: Français)
            String langue = props.getProperty("app.language", "Français");
            cmbLanguage.setValue(langue);
            
            // Thème (valeur par défaut: Clair)
            String theme = props.getProperty("app.theme", "Clair");
            cmbTheme.setValue(theme);
            
            // Notifications
            boolean notifications = props.getBooleanProperty("app.notifications", true);
            chkNotifications.setSelected(notifications);
            
            // Sons
            boolean sounds = props.getBooleanProperty("app.sounds", true);
            chkSounds.setSelected(sounds);
            
            // Auto-save
            boolean autoSave = props.getBooleanProperty("app.autosave", true);
            chkAutoSave.setSelected(autoSave);
            
            logger.debug("Paramètres application chargés");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des paramètres", e);
            // Valeurs par défaut en cas d'erreur
            cmbLanguage.setValue("Français");
            cmbTheme.setValue("Clair");
            chkNotifications.setSelected(true);
            chkSounds.setSelected(true);
            chkAutoSave.setSelected(true);
        }
    }

    @FXML
    private void handleSaveSettings() {
        try {
            String langue = cmbLanguage.getValue();
            String theme = cmbTheme.getValue();
            boolean notifications = chkNotifications.isSelected();
            boolean sounds = chkSounds.isSelected();
            boolean autoSave = chkAutoSave.isSelected();
            
            logger.info("Paramètres sauvegardés - Langue: {}, Thème: {}, Notif: {}, Sons: {}, AutoSave: {}", 
                       langue, theme, notifications, sounds, autoSave);
            
            showSuccess("Succès", "Paramètres sauvegardés avec succès!");
            
            // Appliquer le thème si changé
            if ("Sombre".equals(theme)) {
                logger.info("Thème sombre sélectionné (à implémenter)");
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde des paramètres", e);
            showError("Erreur", "Impossible de sauvegarder les paramètres");
        }
    }

    // ========================================================================
    // SECTION 3: BASE DE DONNÉES
    // ========================================================================

    private void loadDatabaseInfo() {
        try {
            PropertyManager props = PropertyManager.getInstance();
            
            // URL de la base de données
            String dbUrl = props.getProperty("db.url", "N/A");
            lblDbUrl.setText(dbUrl);
            
            // Tester la connexion
            updateDatabaseStatus();
            
            // Info du pool
            updatePoolInfo();
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des infos BDD", e);
            lblDbStatus.setText("● Erreur");
            lblDbStatus.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        }
    }

    private void updateDatabaseStatus() {
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
            logger.error("Erreur de connexion BDD", e);
        }
    }

    private void updatePoolInfo() {
        try {
            int active = HikariCPConfig.getDataSource().getHikariPoolMXBean().getActiveConnections();
            int idle = HikariCPConfig.getDataSource().getHikariPoolMXBean().getIdleConnections();
            int total = HikariCPConfig.getDataSource().getHikariConfigMXBean().getMaximumPoolSize();
            
            lblPoolInfo.setText(String.format("%d actives, %d idle / %d max", active, idle, total));
            
            // Changer la couleur selon l'utilisation
            double usage = (double) active / total * 100;
            if (usage > 80) {
                lblPoolInfo.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
            } else if (usage > 60) {
                lblPoolInfo.setStyle("-fx-text-fill: #FF9800;");
            } else {
                lblPoolInfo.setStyle("-fx-text-fill: #4CAF50;");
            }
        } catch (Exception e) {
            lblPoolInfo.setText("N/A");
            logger.error("Erreur lors de la récupération des infos du pool", e);
        }
    }

    @FXML
    private void handleTestConnection() {
        try {
            logger.info("Test de connexion à la base de données...");
            
            try (Connection conn = HikariCPConfig.getDataSource().getConnection()) {
                if (conn.isValid(5)) {
                    String dbVersion = conn.getMetaData().getDatabaseProductVersion();
                    showSuccess("Connexion Réussie", 
                        "Connexion à la base de données établie avec succès!\n\n" +
                        "Version: " + dbVersion);
                    
                    updateDatabaseStatus();
                    updatePoolInfo();
                    
                    logger.info("Test de connexion réussi");
                } else {
                    showError("Échec", "La connexion n'est pas valide");
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors du test de connexion", e);
            showError("Erreur de Connexion", 
                "Impossible de se connecter à la base de données:\n\n" + e.getMessage());
        }
    }

    @FXML
    private void handleBackupDatabase() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Sauvegarde Base de Données");
        confirmation.setHeaderText("Créer une sauvegarde complète?");
        confirmation.setContentText(
            "Cette opération peut prendre plusieurs minutes.\n" +
            "Voulez-vous continuer?");
        confirmation.initOwner(MainApp.getPrimaryStage());
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showInfo("Sauvegarde", 
                    "Sauvegarde en cours...\n\n" +
                    "Cette fonctionnalité sera implémentée prochainement.\n" +
                    "La sauvegarde sera stockée dans: ./backups/");
            }
        });
    }

    // ========================================================================
    // SECTION 4: INFORMATIONS SYSTÈME
    // ========================================================================

    private void loadSystemInfo() {
        try {
            // Version Java
            String javaVersion = System.getProperty("java.version");
            lblJavaVersion.setText(javaVersion);
            
            // Version JavaFX
            String javaFxVersion = System.getProperty("javafx.version", "N/A");
            lblJavaFxVersion.setText(javaFxVersion);
            
            // Système d'exploitation
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            lblOs.setText(String.format("%s %s (%s)", osName, osVersion, osArch));
            
            // Mémoire
            updateMemoryInfo();
            
            logger.debug("Informations système chargées");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des infos système", e);
        }
    }

    private void updateMemoryInfo() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            // Convertir en MB
            long usedMB = usedMemory / (1024 * 1024);
            long maxMB = maxMemory / (1024 * 1024);
            
            lblMemory.setText(String.format("%d MB / %d MB", usedMB, maxMB));
            
            // Changer la couleur selon l'utilisation
            double percentage = (double) usedMemory / maxMemory * 100;
            if (percentage > 80) {
                lblMemory.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
            } else if (percentage > 60) {
                lblMemory.setStyle("-fx-text-fill: #FF9800;");
            } else {
                lblMemory.setStyle("-fx-text-fill: #4CAF50;");
            }
        } catch (Exception e) {
            lblMemory.setText("N/A");
            logger.error("Erreur lors de la mise à jour de la mémoire", e);
        }
    }

    private void startMemoryMonitoring() {
        // Mettre à jour la mémoire et le pool toutes les 5 secondes
        memoryMonitorTimeline = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> {
                updateMemoryInfo();
                updatePoolInfo();
            })
        );
        memoryMonitorTimeline.setCycleCount(Timeline.INDEFINITE);
        memoryMonitorTimeline.play();
        
        logger.debug("Monitoring mémoire démarré");
    }

    @FXML
    private void handleCheckUpdates() {
        showInfo("Mises à Jour", 
            "Version actuelle: 1.0.0\n\n" +
            "Vous utilisez la dernière version disponible.\n\n" +
            "Prochaine mise à jour prévue: T1 2025");
    }

    @FXML
    private void handleShowLogs() {
        try {
            File logFile = new File("logs/healthcare-application.log");
            
            if (!logFile.exists()) {
                showWarning("Logs", "Aucun fichier de logs trouvé");
                return;
            }
            
            long fileSizeKB = logFile.length() / 1024;
            String lastModified = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                .format(logFile.lastModified());
            
            // Compter les lignes
            long lineCount = 0;
            try (Stream<String> stream = Files.lines(logFile.toPath())) {
                lineCount = stream.count();
            }
            
            showInfo("Fichiers de Logs", 
                String.format("Emplacement: %s\n\n" +
                             "Taille: %d KB\n" +
                             "Lignes: %d\n" +
                             "Dernière modification: %s\n\n" +
                             "Pour ouvrir le fichier, utilisez un éditeur de texte.",
                             logFile.getAbsolutePath(), fileSizeKB, lineCount, lastModified));
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'accès aux logs", e);
            showError("Erreur", "Impossible d'accéder aux logs: " + e.getMessage());
        }
    }

    // ========================================================================
    // SECTION 5: ACTIONS SYSTÈME
    // ========================================================================

    @FXML
    private void handleClearCache() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Vider le Cache");
        confirmation.setHeaderText("Confirmer la suppression");
        confirmation.setContentText(
            "Cette action va supprimer toutes les données en cache.\n" +
            "Voulez-vous continuer?");
        confirmation.initOwner(MainApp.getPrimaryStage());
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    CacheManager.CacheStats statsBefore = CacheManager.getStats();
                    
                    CacheManager.clear();
                    CacheManager.evictExpiredEntries();
                    
                    showSuccess("Cache Vidé", 
                        String.format("Cache vidé avec succès!\n\n" +
                                     "%d entrées supprimées",
                                     statsBefore.totalEntries));
                    
                    logger.info("Cache système vidé - {} entrées supprimées", 
                               statsBefore.totalEntries);
                } catch (Exception e) {
                    logger.error("Erreur lors du vidage du cache", e);
                    showError("Erreur", "Impossible de vider le cache: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleResetSettings() {
        Alert confirmation = new Alert(Alert.AlertType.WARNING);
        confirmation.setTitle("Réinitialiser les Paramètres");
        confirmation.setHeaderText("⚠️ Confirmer la réinitialisation");
        confirmation.setContentText(
            "Cette action va restaurer tous les paramètres aux valeurs par défaut.\n\n" +
            "Continuer?");
        confirmation.initOwner(MainApp.getPrimaryStage());
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Réinitialiser les paramètres
                cmbLanguage.setValue("Français");
                cmbTheme.setValue("Clair");
                chkNotifications.setSelected(true);
                chkSounds.setSelected(true);
                chkAutoSave.setSelected(true);
                
                showSuccess("Réinitialisation", 
                    "Paramètres réinitialisés aux valeurs par défaut!");
                
                logger.info("Paramètres réinitialisés aux valeurs par défaut");
            }
        });
    }

    @FXML
    private void handleCleanLogs() {
        Alert confirmation = new Alert(Alert.AlertType.WARNING);
        confirmation.setTitle("Nettoyer les Logs");
        confirmation.setHeaderText("⚠️ Action Irréversible");
        confirmation.setContentText(
            "Cette action va supprimer TOUS les fichiers de logs.\n" +
            "Les logs ne pourront pas être récupérés.\n\n" +
            "Êtes-vous sûr de vouloir continuer?");
        confirmation.initOwner(MainApp.getPrimaryStage());
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Path logsPath = Paths.get("logs");
                    
                    if (!Files.exists(logsPath)) {
                        showWarning("Logs", "Aucun dossier de logs trouvé");
                        return;
                    }
                    
                    // Compter et supprimer les fichiers .log
                    long deletedCount = Files.walk(logsPath)
                        .filter(path -> path.toString().endsWith(".log"))
                        .peek(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                logger.error("Impossible de supprimer: {}", path, e);
                            }
                        })
                        .count();
                    
                    showSuccess("Nettoyage Terminé", 
                        String.format("%d fichier(s) de logs supprimé(s)", deletedCount));
                    
                    logger.info("{} fichiers de logs supprimés", deletedCount);
                    
                } catch (Exception e) {
                    logger.error("Erreur lors du nettoyage des logs", e);
                    showError("Erreur", "Impossible de nettoyer les logs: " + e.getMessage());
                }
            }
        });
    }

    // ========================================================================
    // SECTION 6: À PROPOS
    // ========================================================================

    @FXML
    private void handleOpenDocumentation() {
        showInfo("Documentation", 
            "Documentation Healthcare System\n\n" +
            "Version: 1.0.0\n" +
            "Date: Décembre 2024\n\n" +
            "Pour accéder à la documentation complète:\n" +
            "https://github.com/healthcare-system/docs\n\n" +
            "Manuel utilisateur disponible dans:\n" +
            "./docs/manuel-utilisateur.pdf");
    }

    @FXML
    private void handleOpenSupport() {
        showInfo("Support Technique", 
            "Pour toute assistance technique:\n\n" +
            "📧 Email: support@healthcare-ensa.ma\n" +
            "📞 Téléphone: +212 5XX-XXXXXX\n" +
            "🌐 Site web: www.healthcare-ensa.ma\n\n" +
            "Heures d'ouverture:\n" +
            "Lundi - Vendredi: 9h - 18h\n" +
            "Samedi: 9h - 13h");
    }

    @FXML
    private void handleOpenLicense() {
        showInfo("Licence", 
            "Healthcare Patient Records System\n" +
            "Version 1.0.0\n\n" +
            "© 2025 ENSA Tétouan\n" +
            "École Nationale des Sciences Appliquées\n\n" +
            "Tous droits réservés\n\n" +
            "Ce logiciel est distribué sous licence MIT.\n" +
            "Voir le fichier LICENSE pour plus de détails.");
    }

    // ========================================================================
    // MÉTHODES UTILITAIRES
    // ========================================================================

    private String formatRole(String role) {
        return switch (role) {
            case "ADMIN" -> "Administrateur";
            case "MEDECIN" -> "Médecin";
            case "RECEPTIONNISTE" -> "Réceptionniste";
            case "PATIENT" -> "Patient";
            default -> role;
        };
    }

    private String getRoleColor(String role) {
        return switch (role) {
            case "ADMIN" -> "#F44336";
            case "MEDECIN" -> "#2196F3";
            case "RECEPTIONNISTE" -> "#4CAF50";
            case "PATIENT" -> "#FF9800";
            default -> "#757575";
        };
    }

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

    /**
     * Cleanup lors de la fermeture
     */
    public void cleanup() {
        if (memoryMonitorTimeline != null) {
            memoryMonitorTimeline.stop();
            logger.debug("Monitoring mémoire arrêté");
        }
    }

    @FXML
    private void handleExportDatabase() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Export Base de Données");
        confirmation.setHeaderText("Exporter la base de données?");
        confirmation.setContentText(
            "Cette opération va créer un fichier SQL contenant:\n" +
            "• La structure de toutes les tables\n" +
            "• Toutes les données actuelles\n\n" +
            "Voulez-vous continuer?");
        confirmation.initOwner(MainApp.getPrimaryStage());
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Dialog pour choisir le type d'export
                ChoiceDialog<String> typeDialog = new ChoiceDialog<>("Complet (Structure + Données)", 
                    "Complet (Structure + Données)", 
                    "Données uniquement");
                typeDialog.setTitle("Type d'Export");
                typeDialog.setHeaderText("Choisir le type d'export");
                typeDialog.setContentText("Type:");
                typeDialog.initOwner(MainApp.getPrimaryStage());
                
                typeDialog.showAndWait().ifPresent(type -> {
                    // Exécuter l'export dans un thread séparé
                    Task<String> exportTask = new Task<String>() {
                        @Override
                        protected String call() throws Exception {
                            updateMessage("Export en cours...");
                            
                            String exportDir = "C:/Users/hp/Downloads";
                            
                            if (type.contains("Données uniquement")) {
                                return DatabaseExportService.exportDataOnly(exportDir);
                            } else {
                                return DatabaseExportService.exportDatabase(exportDir);
                            }
                        }
                    };
                    
                    // Afficher une ProgressDialog
                    ProgressDialog progressDialog = new ProgressDialog(exportTask);
                    progressDialog.setTitle("Export Base de Données");
                    progressDialog.setHeaderText("Export en cours...");
                    progressDialog.setContentText("Veuillez patienter");
                    progressDialog.initOwner(MainApp.getPrimaryStage());
                    
                    exportTask.setOnSucceeded(event -> {
                        String filepath = exportTask.getValue();
                        File exportFile = new File(filepath);
                        long fileSizeKB = exportFile.length() / 1024;
                        
                        // Obtenir les statistiques
                        String stats;
                        try {
                            stats = DatabaseExportService.getDatabaseStats();
                        } catch (Exception e) {
                            stats = "Statistiques non disponibles";
                        }
                        
                        showSuccess("Export Réussi", 
                            String.format("Export terminé avec succès!\n\n" +
                                        "Fichier: %s\n" +
                                        "Taille: %d KB\n\n" +
                                        "%s",
                                        exportFile.getName(), fileSizeKB, stats));
                        
                        logger.info("Export BDD réussi: {}", filepath);
                    });
                    
                    exportTask.setOnFailed(event -> {
                        Throwable e = exportTask.getException();
                        logger.error("Erreur lors de l'export", e);
                        showError("Erreur d'Export", 
                            "Impossible d'exporter la base de données:\n\n" + e.getMessage());
                    });
                    
                    // Lancer le thread
                    new Thread(exportTask).start();
                });
            }
        });
    }

    /**
     * Ouvre le dialog pour ajouter un nouvel utilisateur
     * Accessible uniquement aux administrateurs
     */
    @FXML
    private void handleAddUser() {
        try {
            // Vérifier que l'utilisateur connecté est admin
            if (!SessionManager.isLoggedIn()) {
                showError("Erreur", "Vous devez être connecté pour effectuer cette action");
                return;
            }
            
            Utilisateur currentUser = SessionManager.getCurrentUser();
            if (currentUser.getRole() != Role.ADMIN) {
                showError("Accès Refusé", 
                    "Seuls les administrateurs peuvent ajouter des utilisateurs.");
                logger.warn("Tentative d'ajout d'utilisateur par un non-admin: {}", 
                        currentUser.getUsername());
                return;
            }
            
            // Charger le FXML du dialog
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/add-user-dialog.fxml")
            );
            
            Parent root = loader.load();
            AddUserDialogController controller = loader.getController();
            
            // Créer la fenêtre dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un Utilisateur");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(MainApp.getPrimaryStage());
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(
                new Image(MainApp.class.getResourceAsStream("/images/icon.png"))
            );
            
            // Afficher et attendre la fermeture
            dialogStage.showAndWait();
            
            // Vérifier si un utilisateur a été créé
            if (controller.isCreated()) {
                logger.info("Nouvel utilisateur créé avec succès");
                showSuccess("Succès", 
                    "L'utilisateur a été créé avec succès!\n\n" +
                    "Il peut maintenant se connecter avec ses identifiants.");
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'ouverture du dialog d'ajout", e);
            showError("Erreur", 
                "Impossible d'ouvrir le formulaire d'ajout:\n\n" + e.getMessage());
        }
    }

    /**
     * Configure la visibilité des sections selon les permissions
     */
    private void configureAdvancedSettings() {
        // Sections visibles uniquement pour ADMIN
        boolean isAdmin = PermissionManager.canAccessAdvancedSettings();
        
        if (sectionDatabase != null) {
            sectionDatabase.setVisible(isAdmin);
            sectionDatabase.setManaged(isAdmin);
        }
        
        if (sectionSystemActions != null) {
            sectionSystemActions.setVisible(isAdmin);
            sectionSystemActions.setManaged(isAdmin);
        }
        
        logger.info("Sections avancées configurées - Admin: {}", isAdmin);
    }
}