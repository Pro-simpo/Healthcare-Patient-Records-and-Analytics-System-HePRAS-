package ma.ensa.healthcare.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ma.ensa.healthcare.model.Medecin;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.Utilisateur;
import ma.ensa.healthcare.model.enums.Role;
import ma.ensa.healthcare.service.MedecinService;
import ma.ensa.healthcare.service.PatientService;
import ma.ensa.healthcare.service.UtilisateurService;
import ma.ensa.healthcare.ui.MainApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller pour le dialog d'ajout d'utilisateur
 */
public class AddUserDialogController {

    private static final Logger logger = LoggerFactory.getLogger(AddUserDialogController.class);

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private ComboBox<Patient> cmbPatient;
    @FXML private ComboBox<Medecin> cmbMedecin;
    @FXML private Label lblPatient;
    @FXML private Label lblMedecin;
    @FXML private RadioButton rbActif;
    @FXML private RadioButton rbInactif;

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final PatientService patientService = new PatientService();
    private final MedecinService medecinService = new MedecinService();

    private boolean created = false;

    @FXML
    public void initialize() {
        logger.info("Initialisation du dialog d'ajout d'utilisateur");
        
        // Remplir les rôles (sans ADMIN pour sécurité)
        cmbRole.getItems().addAll("MEDECIN", "RECEPTIONNISTE", "PATIENT");
        
        // Listener sur le rôle pour afficher/masquer les champs conditionnels
        cmbRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            handleRoleChange(newVal);
        });
        
        // Charger les patients et médecins
        loadPatients();
        loadMedecins();
        
        // Formatter pour afficher les patients
        cmbPatient.setConverter(new javafx.util.StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                if (patient == null) return "";
                return patient.getNom() + " " + patient.getPrenom() + " (CIN: " + patient.getCin() + ")";
            }

            @Override
            public Patient fromString(String string) {
                return null;
            }
        });
        
        // Formatter pour afficher les médecins
        cmbMedecin.setConverter(new javafx.util.StringConverter<Medecin>() {
            @Override
            public String toString(Medecin medecin) {
                if (medecin == null) return "";
                return "Dr. " + medecin.getNom() + " " + medecin.getPrenom() + " (" + medecin.getSpecialite() + ")";
            }

            @Override
            public Medecin fromString(String string) {
                return null;
            }
        });
    }

    private void handleRoleChange(String role) {
        if (role == null) return;
        
        switch (role) {
            case "PATIENT":
                lblPatient.setVisible(true);
                lblPatient.setManaged(true);
                cmbPatient.setVisible(true);
                cmbPatient.setManaged(true);
                
                lblMedecin.setVisible(false);
                lblMedecin.setManaged(false);
                cmbMedecin.setVisible(false);
                cmbMedecin.setManaged(false);
                break;
                
            case "MEDECIN":
                lblMedecin.setVisible(true);
                lblMedecin.setManaged(true);
                cmbMedecin.setVisible(true);
                cmbMedecin.setManaged(true);
                
                lblPatient.setVisible(false);
                lblPatient.setManaged(false);
                cmbPatient.setVisible(false);
                cmbPatient.setManaged(false);
                break;
                
            default:
                lblPatient.setVisible(false);
                lblPatient.setManaged(false);
                cmbPatient.setVisible(false);
                cmbPatient.setManaged(false);
                
                lblMedecin.setVisible(false);
                lblMedecin.setManaged(false);
                cmbMedecin.setVisible(false);
                cmbMedecin.setManaged(false);
                break;
        }
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            cmbPatient.getItems().setAll(patients);
            logger.debug("{} patients chargés", patients.size());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des patients", e);
            showError("Erreur", "Impossible de charger la liste des patients");
        }
    }

    private void loadMedecins() {
        try {
            List<Medecin> medecins = medecinService.getAllMedecins();
            cmbMedecin.getItems().setAll(medecins);
            logger.debug("{} médecins chargés", medecins.size());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des médecins", e);
            showError("Erreur", "Impossible de charger la liste des médecins");
        }
    }

    @FXML
    private void handleCreate() {
        try {
            // Validation
            if (!validateForm()) {
                return;
            }
            
            // Créer l'utilisateur
            Utilisateur user = new Utilisateur();
            user.setUsername(txtUsername.getText().trim());
            user.setEmail(txtEmail.getText().trim());
            user.setPasswordHash(txtPassword.getText()); // En prod: hasher avec BCrypt
            user.setRole(Role.valueOf(cmbRole.getValue()));
            user.setStatut(rbActif.isSelected() ? "ACTIF" : "INACTIF");
            user.setDateCreation(LocalDate.now());
            user.setTentativesEchec(0);
            
            // Lier au patient/médecin si nécessaire
            if ("PATIENT".equals(cmbRole.getValue())) {
                Patient patient = cmbPatient.getValue();
                if (patient != null) {
                    user.setPatient(patient);
                }
            } else if ("MEDECIN".equals(cmbRole.getValue())) {
                Medecin medecin = cmbMedecin.getValue();
                if (medecin != null) {
                    user.setMedecin(medecin);
                }
            }
            
            // Enregistrer
            utilisateurService.inscrire(user);
            
            logger.info("Utilisateur créé avec succès: {}", user.getUsername());
            showSuccess("Succès", "Utilisateur créé avec succès!");
            
            created = true;
            closeDialog();
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur", e);
            showError("Erreur", "Impossible de créer l'utilisateur:\n\n" + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateForm() {
        // Username
        if (txtUsername.getText() == null || txtUsername.getText().trim().isEmpty()) {
            showError("Erreur de Validation", "Le nom d'utilisateur est obligatoire");
            txtUsername.requestFocus();
            return false;
        }
        
        if (txtUsername.getText().length() < 3) {
            showError("Erreur de Validation", "Le nom d'utilisateur doit contenir au moins 3 caractères");
            txtUsername.requestFocus();
            return false;
        }
        
        // Email
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            showError("Erreur de Validation", "L'email est obligatoire");
            txtEmail.requestFocus();
            return false;
        }
        
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Erreur de Validation", "Format d'email invalide");
            txtEmail.requestFocus();
            return false;
        }
        
        // Mot de passe
        if (txtPassword.getText() == null || txtPassword.getText().isEmpty()) {
            showError("Erreur de Validation", "Le mot de passe est obligatoire");
            txtPassword.requestFocus();
            return false;
        }
        
        if (txtPassword.getText().length() < 6) {
            showError("Erreur de Validation", "Le mot de passe doit contenir au moins 6 caractères");
            txtPassword.requestFocus();
            return false;
        }
        
        // Confirmation
        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            showError("Erreur de Validation", "Les mots de passe ne correspondent pas");
            txtConfirmPassword.requestFocus();
            return false;
        }
        
        // Rôle
        if (cmbRole.getValue() == null) {
            showError("Erreur de Validation", "Veuillez sélectionner un rôle");
            cmbRole.requestFocus();
            return false;
        }
        
        // Patient (si rôle PATIENT)
        if ("PATIENT".equals(cmbRole.getValue()) && cmbPatient.getValue() == null) {
            showError("Erreur de Validation", "Veuillez sélectionner un patient");
            cmbPatient.requestFocus();
            return false;
        }
        
        // Médecin (si rôle MEDECIN)
        if ("MEDECIN".equals(cmbRole.getValue()) && cmbMedecin.getValue() == null) {
            showError("Erreur de Validation", "Veuillez sélectionner un médecin");
            cmbMedecin.requestFocus();
            return false;
        }
        
        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }

    public boolean isCreated() {
        return created;
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
            new Image(MainApp.class.getResourceAsStream("/images/icon.png"))
        );
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}