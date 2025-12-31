package ma.ensa.healthcare.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.service.PatientService;
import ma.ensa.healthcare.ui.dialogs.PatientDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import ma.ensa.healthcare.ui.utils.PermissionManager;

public class PatientsController {

    private static final Logger logger = LoggerFactory.getLogger(PatientsController.class);

    @FXML private TextField searchField;
    @FXML private TableView<Patient> tablePatients;
    @FXML private TableColumn<Patient, Long> colId;
    @FXML private TableColumn<Patient, String> colCin;
    @FXML private TableColumn<Patient, String> colNom;
    @FXML private TableColumn<Patient, String> colPrenom;
    @FXML private TableColumn<Patient, String> colSexe;
    @FXML private TableColumn<Patient, String> colDateNaissance;
    @FXML private TableColumn<Patient, String> colTelephone;
    @FXML private TableColumn<Patient, String> colEmail;
    @FXML private TableColumn<Patient, Void> colActions;
    @FXML private Label lblTotal;
    @FXML private Button btnAddPatient;

    private final PatientService patientService = new PatientService();
    private ObservableList<Patient> patientsList = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurePermissions();
        setupTableColumns();
        loadPatients();
    }

    /**
     * Configure les colonnes du TableView
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        
        colSexe.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSexe().name()));
        
        colDateNaissance.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateNaissance() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateNaissance().format(DATE_FORMATTER));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Colonne Actions avec boutons
        setupActionsColumn();
    }

    /**
     * Configure la colonne Actions avec boutons Edit/Delete
     */
    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox hbox = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("action-button");
                btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 5 10;");
                btnEdit.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    handleEditPatient(patient);
                });

                btnDelete.getStyleClass().add("action-button-danger");
                btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 5 10;");
                btnDelete.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    handleDeletePatient(patient);
                });

                hbox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    /**
     * Charge tous les patients depuis la base de données
     */
    private void loadPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            patientsList.setAll(patients);
            tablePatients.setItems(patientsList);
            lblTotal.setText(String.valueOf(patients.size()));
            
            logger.info("Chargement de {} patients", patients.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des patients", e);
            showError("Erreur", "Impossible de charger la liste des patients");
        }
    }

    /**
     * Recherche de patients
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadPatients();
            return;
        }

        try {
            List<Patient> allPatients = patientService.getAllPatients();
            
            List<Patient> filteredPatients = allPatients.stream()
                .filter(p -> 
                    p.getNom().toLowerCase().contains(searchText) ||
                    p.getPrenom().toLowerCase().contains(searchText) ||
                    (p.getCin() != null && p.getCin().toLowerCase().contains(searchText)) ||
                    (p.getTelephone() != null && p.getTelephone().contains(searchText)) ||
                    (p.getEmail() != null && p.getEmail().toLowerCase().contains(searchText))
                )
                .toList();

            patientsList.setAll(filteredPatients);
            tablePatients.setItems(patientsList);
            lblTotal.setText(String.valueOf(filteredPatients.size()));

        } catch (Exception e) {
            logger.error("Erreur lors de la recherche", e);
            showError("Erreur", "Erreur lors de la recherche");
        }
    }

    /**
     * Ajouter un nouveau patient
     */
    @FXML
    private void handleAddPatient() {
        Stage stage = (Stage) tablePatients.getScene().getWindow();
        PatientDialog dialog = new PatientDialog(stage);
        Optional<Patient> result = dialog.showAndWait();

        result.ifPresent(patient -> {
            try {
                patientService.createPatient(patient);
                showSuccess("Succès", "Patient ajouté avec succès !");
                loadPatients();
            } catch (Exception e) {
                logger.error("Erreur lors de l'ajout du patient", e);
                showError("Erreur", "Impossible d'ajouter le patient : " + e.getMessage());
            }
        });
    }

    /**
     * Modifier un patient existant
     */
    private void handleEditPatient(Patient patient) {
        Stage stage = (Stage) tablePatients.getScene().getWindow();
        PatientDialog dialog = new PatientDialog(stage, patient);
        Optional<Patient> result = dialog.showAndWait();

        result.ifPresent(updatedPatient -> {
            try {
                patientService.updatePatient(updatedPatient);
                showSuccess("Succès", "Patient modifié avec succès !");
                loadPatients();
            } catch (Exception e) {
                logger.error("Erreur lors de la modification du patient", e);
                showError("Erreur", "Impossible de modifier le patient : " + e.getMessage());
            }
        });
    }

    /**
     * Supprimer un patient
     */
    private void handleDeletePatient(Patient patient) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le patient");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + 
                                    patient.getPrenom() + " " + patient.getNom() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    patientService.deletePatient(patient.getId());
                    showSuccess("Succès", "Patient supprimé avec succès !");
                    loadPatients();
                } catch (Exception e) {
                    logger.error("Erreur lors de la suppression du patient", e);
                    showError("Erreur", "Impossible de supprimer le patient : " + e.getMessage());
                }
            }
        });
    }

    /**
     * Afficher un message d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Afficher un message de succès
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void configurePermissions() {
        btnAddPatient.setVisible(PermissionManager.canModifyPatient());
        btnAddPatient.setManaged(PermissionManager.canModifyPatient());
    }
}