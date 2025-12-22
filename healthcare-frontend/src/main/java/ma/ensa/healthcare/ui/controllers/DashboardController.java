package ma.ensa.healthcare.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import ma.ensa.healthcare.ui.MainApp;
import ma.ensa.healthcare.ui.utils.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label pageTitleLabel;
    @FXML private StackPane contentArea;

    // Navigation buttons
    @FXML private Button btnHome;
    @FXML private Button btnPatients;
    @FXML private Button btnRendezVous;
    @FXML private Button btnConsultations;
    @FXML private Button btnFactures;
    @FXML private Button btnMedecins;
    @FXML private Button btnSettings;

    @FXML
    public void initialize() {
        // Afficher les informations de l'utilisateur connecté
        loadUserInfo();
        
        // Charger la page d'accueil par défaut
        showHome();
        
        // Appliquer le style "selected" au bouton Home
        setActiveButton(btnHome);
    }

    /**
     * Charge les informations de l'utilisateur connecté
     */
    private void loadUserInfo() {
        if (SessionManager.isLoggedIn()) {
            userNameLabel.setText(SessionManager.getCurrentUserFullName());
            userRoleLabel.setText(SessionManager.getCurrentUserRole());
        }
    }

    /**
     * Charge une vue dans la zone de contenu
     */
    private void loadView(String fxmlFile, String pageTitle, Button activeButton) {
        try {
            pageTitleLabel.setText(pageTitle);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent view = loader.load();
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            setActiveButton(activeButton);
            
            logger.info("Vue chargée : {}", fxmlFile);
            
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la vue : {}", fxmlFile, e);
            showError("Erreur", "Impossible de charger la page : " + fxmlFile);
        }
    }

    /**
     * Applique le style "actif" au bouton sélectionné
     */
    private void setActiveButton(Button activeButton) {
        // Retirer le style de tous les boutons
        btnHome.getStyleClass().remove("sidebar-button-active");
        btnPatients.getStyleClass().remove("sidebar-button-active");
        btnRendezVous.getStyleClass().remove("sidebar-button-active");
        btnConsultations.getStyleClass().remove("sidebar-button-active");
        btnFactures.getStyleClass().remove("sidebar-button-active");
        btnMedecins.getStyleClass().remove("sidebar-button-active");
        btnSettings.getStyleClass().remove("sidebar-button-active");
        
        // Appliquer le style au bouton actif
        if (activeButton != null && !activeButton.getStyleClass().contains("sidebar-button-active")) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
    }

    // ========== NAVIGATION METHODS ==========

    @FXML
    private void showHome() {
        loadView("home.fxml", "Tableau de Bord", btnHome);
    }

    @FXML
    private void showPatients() {
        loadView("patients.fxml", "Gestion des Patients", btnPatients);
    }

    @FXML
    private void showRendezVous() {
        loadView("rendezvous.fxml", "Gestion des Rendez-vous", btnRendezVous);
    }

    @FXML
    private void showConsultations() {
        loadView("consultations.fxml", "Consultations Médicales", btnConsultations);
    }

    @FXML
    private void showFactures() {
        loadView("factures.fxml", "Facturation", btnFactures);
    }

    @FXML
    private void showMedecins() {
        loadView("medecins.fxml", "Gestion des Médecins", btnMedecins);
    }

    @FXML
    private void showSettings() {
        loadView("settings.fxml", "Paramètres", btnSettings);
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Confirmer la déconnexion");
        alert.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");
        alert.initOwner(MainApp.getPrimaryStage());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                SessionManager.logout();
                logger.info("Utilisateur déconnecté");
                MainApp.showLoginScreen();
            }
        });
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }
}