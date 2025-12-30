package ma.ensa.healthcare.ui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import ma.ensa.healthcare.model.Utilisateur;
import ma.ensa.healthcare.service.UtilisateurService;
import ma.ensa.healthcare.ui.MainApp;
import ma.ensa.healthcare.ui.utils.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

/**
 * Contrôleur pour l'écran de connexion
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private FontAwesomeIconView logoIcon;

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Permettre la connexion avec la touche Entrée
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        // Focus automatique sur le champ username
        usernameField.requestFocus();

        // Créer une transition de scale
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.8), logoIcon);
        st.setToX(1.5);
        st.setToY(1.5);
        st.setInterpolator(Interpolator.EASE_BOTH);

        // Survol -> agrandir
        logoIcon.setOnMouseEntered(e -> st.playFromStart());

        // Sortie -> revenir normal
        logoIcon.setOnMouseExited(e -> {
            ScaleTransition back = new ScaleTransition(Duration.seconds(0.8), logoIcon);
            back.setToX(1.0);
            back.setToY(1.0);
            back.setInterpolator(Interpolator.EASE_BOTH);
            back.play();
        });
    }

    @FXML
    private void handleLogin() {
        // Réinitialiser le message d'erreur
        errorLabel.setText("");

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation des champs
        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            // Désactiver le bouton pendant la connexion
            loginButton.setDisable(true);
            
            // Tentative de connexion
            Utilisateur utilisateur = utilisateurService.login(username, password);
            
            // Sauvegarder la session
            SessionManager.setCurrentUser(utilisateur);
            
            logger.info("Connexion réussie pour l'utilisateur : {}", username);
            
            // Rediriger vers le dashboard
            MainApp.showDashboard();
            
        } catch (Exception e) {
            logger.error("Échec de connexion pour : {}", username, e);
            showError("Nom d'utilisateur ou mot de passe incorrect");
            loginButton.setDisable(false);
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    @FXML
    private void handleCancel() {
        System.exit(0);
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Affiche une alerte d'information
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }
}