package ma.ensa.healthcare.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import ma.ensa.healthcare.model.Medecin;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.service.MedecinService;
import ma.ensa.healthcare.ui.dialogs.MedecinDialog;
import ma.ensa.healthcare.ui.dialogs.PatientDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MedecinsController {

    private static final Logger logger = LoggerFactory.getLogger(MedecinsController.class);

    @FXML private TextField searchField;
    @FXML private ComboBox<String> cmbFilterSpecialite;
    @FXML private ComboBox<String> cmbFilterDepartement;
    
    @FXML private Label lblTotalMedecins;
    @FXML private Label lblNbSpecialites;
    @FXML private Label lblNbDepartements;
    @FXML private Label lblConsultationsMoyennes;
    
    @FXML private TableView<Medecin> tableMedecins;
    @FXML private TableColumn<Medecin, Long> colId;
    @FXML private TableColumn<Medecin, String> colNumeroOrdre;
    @FXML private TableColumn<Medecin, String> colNom;
    @FXML private TableColumn<Medecin, String> colPrenom;
    @FXML private TableColumn<Medecin, String> colSpecialite;
    @FXML private TableColumn<Medecin, String> colDepartement;
    @FXML private TableColumn<Medecin, String> colTelephone;
    @FXML private TableColumn<Medecin, String> colEmail;
    @FXML private TableColumn<Medecin, String> colDateEmbauche;
    @FXML private TableColumn<Medecin, Void> colActions;
    @FXML private Label lblTotal;

    private final MedecinService medecinService = new MedecinService();
    private ObservableList<Medecin> medecinsList = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTableColumns();
        loadMedecins();
        updateStatistics();
    }

    private void setupComboBoxes() {
        try {
            List<Medecin> all = medecinService.getAllMedecins();
            
            // Spécialités uniques
            Set<String> specialites = all.stream()
                .map(Medecin::getSpecialite)
                .collect(Collectors.toSet());
            
            cmbFilterSpecialite.getItems().add("Toutes les spécialités");
            cmbFilterSpecialite.getItems().addAll(specialites);
            cmbFilterSpecialite.setValue("Toutes les spécialités");
            
            // Départements uniques
            Set<String> departements = all.stream()
                .filter(m -> m.getDepartement() != null)
                .map(m -> m.getDepartement().getNomDepartement())
                .collect(Collectors.toSet());
            
            cmbFilterDepartement.getItems().add("Tous les départements");
            cmbFilterDepartement.getItems().addAll(departements);
            cmbFilterDepartement.setValue("Tous les départements");
            
        } catch (Exception e) {
            logger.error("Erreur chargement filtres", e);
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumeroOrdre.setCellValueFactory(new PropertyValueFactory<>("numeroOrdre"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colSpecialite.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        
        colDepartement.setCellValueFactory(cellData -> {
            Medecin m = cellData.getValue();
            if (m.getDepartement() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    m.getDepartement().getNomDepartement());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        colDateEmbauche.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateEmbauche() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateEmbauche().format(DATE_FORMATTER));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");
            private final HBox hbox = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("action-button");
                btnEdit.setStyle("-fx-font-size: 11px; -fx-padding: 5 10;");
                btnEdit.setOnAction(event -> {
                    Medecin m = getTableView().getItems().get(getIndex());
                    handleEditMedecin(m);
                });

                btnDelete.getStyleClass().add("action-button-danger");
                btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 5 10;");
                btnDelete.setOnAction(event -> {
                    Medecin m = getTableView().getItems().get(getIndex());
                    handleDeleteMedecin(m);
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

    private void loadMedecins() {
        try {
            List<Medecin> medecins = medecinService.getAllMedecins();
            medecinsList.setAll(medecins);
            tableMedecins.setItems(medecinsList);
            lblTotal.setText(String.valueOf(medecins.size()));
            
            logger.info("Chargement de {} médecins", medecins.size());
            
        } catch (Exception e) {
            logger.error("Erreur chargement médecins", e);
            showError("Erreur", "Impossible de charger les médecins");
        }
    }

    private void updateStatistics() {
        try {
            List<Medecin> all = medecinService.getAllMedecins();
            
            long total = all.size();
            long nbSpecialites = all.stream()
                .map(Medecin::getSpecialite)
                .distinct()
                .count();
            
            long nbDepartements = all.stream()
                .filter(m -> m.getDepartement() != null)
                .map(m -> m.getDepartement().getId())
                .distinct()
                .count();

            lblTotalMedecins.setText(String.valueOf(total));
            lblNbSpecialites.setText(String.valueOf(nbSpecialites));
            lblNbDepartements.setText(String.valueOf(nbDepartements));
            lblConsultationsMoyennes.setText("15"); // Mock data

        } catch (Exception e) {
            logger.error("Erreur calcul statistiques", e);
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadMedecins();
            return;
        }

        try {
            List<Medecin> all = medecinService.getAllMedecins();
            
            List<Medecin> filtered = all.stream()
                .filter(m -> 
                    m.getNom().toLowerCase().contains(searchText) ||
                    m.getPrenom().toLowerCase().contains(searchText) ||
                    (m.getNumeroOrdre() != null && m.getNumeroOrdre().toLowerCase().contains(searchText)) ||
                    (m.getSpecialite() != null && m.getSpecialite().toLowerCase().contains(searchText)) ||
                    (m.getEmail() != null && m.getEmail().toLowerCase().contains(searchText))
                )
                .toList();

            medecinsList.setAll(filtered);
            lblTotal.setText(String.valueOf(filtered.size()));

        } catch (Exception e) {
            logger.error("Erreur recherche", e);
        }
    }

    @FXML
    private void handleFilter() {
        try {
            List<Medecin> all = medecinService.getAllMedecins();
            String filterSpecialite = cmbFilterSpecialite.getValue();
           // String filterDepartement = cmbFilterDepartement.getValue();

            List<Medecin> filtered = all.stream()
                .filter(m -> {
                    boolean specialiteMatch = "Toutes les spécialités".equals(filterSpecialite) || 
                        (m.getSpecialite() != null && m.getSpecialite().equals(filterSpecialite));
                    
                  //  boolean departementMatch = "Tous les départements".equals(filterDepartement) || 
                  //      (m.getDepartement() != null && 
                  //       m.getDepartement().getNomDepartement().equals(filterDepartement));
                    
                    return specialiteMatch; // && departementMatch;
                })
                .toList();

            medecinsList.setAll(filtered);
            lblTotal.setText(String.valueOf(filtered.size()));

        } catch (Exception e) {
            logger.error("Erreur filtrage", e);
        }
    }

    /**
     * Ajouter un nouveau médecin
     */
    @FXML
    private void handleAddMedecin() {
        MedecinDialog dialog = new MedecinDialog(null);
        Optional<Medecin> result = dialog.showAndWait();

        result.ifPresent(medecin -> {
            try {
                medecinService.createMedecin(medecin);
                showSuccess("Succès", "Médecin ajouté avec succès !");
                loadMedecins();
            } catch (Exception e) {
                logger.error("Erreur lors de l'ajout du médecin", e);
                showError("Erreur", "Impossible d'ajouter le médecin : " + e.getMessage());
            }
        });
    }

    /**
     * Modifier un médecin existant
     */
    private void handleEditMedecin(Medecin medecin) {
        MedecinDialog dialog = new MedecinDialog(null, medecin);
        Optional<Medecin> result = dialog.showAndWait();
        result.ifPresent(updatedMedecin -> {
            try {
                medecinService.updateMedecin(updatedMedecin);
                showSuccess("Succès", "Médecin modifié avec succès !");
                loadMedecins();
            } catch (Exception e) {
                logger.error("Erreur lors de la modification du médecin", e);
                showError("Erreur", "Impossible de modifier le médecin : " + e.getMessage());
            }
        });
    }

    private void handleDeleteMedecin(Medecin m) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le médecin");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer Dr. " + 
                                    m.getPrenom() + " " + m.getNom() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    medecinService.deleteMedecin(m.getId());
                    showSuccess("Succès", "Médecin supprimé avec succès !");
                    loadMedecins();
                    updateStatistics();
                } catch (Exception e) {
                    logger.error("Erreur suppression", e);
                    showError("Erreur", "Impossible de supprimer le médecin");
                }
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}