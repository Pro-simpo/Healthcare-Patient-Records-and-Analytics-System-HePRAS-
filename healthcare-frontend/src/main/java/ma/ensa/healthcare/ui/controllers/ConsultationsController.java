package ma.ensa.healthcare.ui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ma.ensa.healthcare.model.Consultation;
import ma.ensa.healthcare.model.Medecin;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.service.*;
import ma.ensa.healthcare.ui.dialogs.ConsultationDetailsDialog;
import ma.ensa.healthcare.ui.dialogs.ConsultationDialog;
import ma.ensa.healthcare.ui.dialogs.PatientDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import ma.ensa.healthcare.ui.utils.PermissionManager;
import java.util.stream.Collectors;

import javafx.stage.Stage;

public class ConsultationsController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationsController.class);

    @FXML private TextField searchField;
    @FXML private DatePicker dpFilterDate;
    @FXML private ComboBox<Medecin> cmbFilterMedecin;
    
    @FXML private Label lblToday;
    @FXML private Label lblMonth;
    @FXML private Label lblRevenuMoyen;
    @FXML private Label lblDureeMoyenne;
    
    @FXML private TableView<Consultation> tableConsultations;
    @FXML private TableColumn<Consultation, Long> colId;
    @FXML private TableColumn<Consultation, String> colDate;
    @FXML private TableColumn<Consultation, String> colPatient;
    @FXML private TableColumn<Consultation, String> colCin;
    @FXML private TableColumn<Consultation, String> colMedecin;
    @FXML private TableColumn<Consultation, String> colSpecialite;
    @FXML private TableColumn<Consultation, String> colDiagnostic;
    @FXML private TableColumn<Consultation, String> colTarif;
    @FXML private TableColumn<Consultation, Void> colActions;
    @FXML private Label lblTotal;
    @FXML private HBox hboxStats;
    @FXML private Button btnAddConsultation;

    private final ConsultationService consultationService = new ConsultationService();
    private final MedecinService medecinService = new MedecinService();
    private final PatientService patientService = new PatientService();
    private final RendezVousService rendezVousService = new RendezVousService();
    private ObservableList<Consultation> consultationsList = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurePermissions();
        setupComboBox();
        setupTableColumns();
        loadConsultations();
        updateStatistics();

        // Parcourir tous les enfants du VBox
        for (Node node : hboxStats.getChildren()) {
            if (node.getStyleClass().contains("homeCard")) {
                // Créer une transition de scale
                ScaleTransition st = new ScaleTransition(Duration.seconds(0.2), node);
                st.setToX(1.05);
                st.setToY(1.05);
                st.setInterpolator(Interpolator.EASE_BOTH);

                // Survol -> agrandir
                node.setOnMouseEntered(e -> st.playFromStart());

                // Sortie -> revenir normal
                node.setOnMouseExited(e -> {
                    ScaleTransition back = new ScaleTransition(Duration.seconds(0.2), node);
                    back.setToX(1.0);
                    back.setToY(1.0);
                    back.setInterpolator(Interpolator.EASE_BOTH);
                    back.play();
                });
            }
        }
    }

    private void setupComboBox() {
        try {
            List<Medecin> medecins = medecinService.getAllMedecins();
            cmbFilterMedecin.getItems().add(null); // Option "Tous"
            cmbFilterMedecin.getItems().addAll(medecins);
            
            cmbFilterMedecin.setCellFactory(param -> new ListCell<Medecin>() {
                @Override
                protected void updateItem(Medecin item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Tous les médecins");
                    } else {
                        setText("Dr. " + item.getNom() + " - " + item.getSpecialite());
                    }
                }
            });
            cmbFilterMedecin.setButtonCell(cmbFilterMedecin.getCellFactory().call(null));
            cmbFilterMedecin.setValue(null);
            
        } catch (Exception e) {
            logger.error("Erreur chargement médecins", e);
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        colDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateConsultation();
            String formatted = date != null ? date.format(DATE_FORMATTER) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        
        colPatient.setCellValueFactory(cellData -> {
            Consultation c = cellData.getValue();
            if (rendezVousService.getRendezVousById(c.getIdRendezVous()) != null && rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    patientService.getPatientById(rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient()).getNom() + " " + 
                    patientService.getPatientById(rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient()).getPrenom());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colCin.setCellValueFactory(cellData -> {
            Consultation c = cellData.getValue();
            if (rendezVousService.getRendezVousById(c.getIdRendezVous()) != null && rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    patientService.getPatientById(rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient()).getCin());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colMedecin.setCellValueFactory(cellData -> {
            Consultation c = cellData.getValue();
            if (rendezVousService.getRendezVousById(c.getIdRendezVous()) != null && rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    "Dr. " + rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin().getNom());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colSpecialite.setCellValueFactory(cellData -> {
            Consultation c = cellData.getValue();
            if (rendezVousService.getRendezVousById(c.getIdRendezVous()) != null && rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin().getSpecialite());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colDiagnostic.setCellValueFactory(new PropertyValueFactory<>("diagnostic"));
        
        colTarif.setCellValueFactory(cellData -> {
            BigDecimal tarif = cellData.getValue().getTarifConsultation();
            String formatted = tarif != null ? tarif + " MAD" : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDetails = new Button("Détails");
            private final Button btnModifier = new Button("Modifier");
            private final Button btnOrdonnance = new Button("Ordonnance");
            private final HBox hbox = new HBox(5, btnDetails, btnModifier, btnOrdonnance);

            {
                btnDetails.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnDetails.setOnAction(event -> {
                    Consultation c = getTableView().getItems().get(getIndex());
                    handleViewDetails(c);
                });

                btnModifier.getStyleClass().add("action-button");
                btnModifier.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnModifier.setOnAction(event -> {
                    Consultation c = getTableView().getItems().get(getIndex());
                    handleModifier(c);
                });
                btnModifier.setVisible(PermissionManager.canModifyConsultation());
                btnModifier.setManaged(PermissionManager.canModifyConsultation());

                btnOrdonnance.getStyleClass().add("action-button-success");
                btnOrdonnance.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnOrdonnance.setOnAction(event -> {
                    Consultation c = getTableView().getItems().get(getIndex());
                    handleGenerateOrdonnance(c);
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

    private void loadConsultations() {
        try {
            List<Consultation> consultations = consultationService.listerToutesConsultations();
            
            // Filtrer selon le rôle
            if (PermissionManager.shouldFilterByMedecin()) {
                Long medecinId = PermissionManager.getConnectedMedecinId();
                if (medecinId != null) {
                    consultations = consultations.stream()
                        .filter(c -> {
                            RendezVous rdv = rendezVousService.getRendezVousById(c.getIdRendezVous());
                            return rdv != null && rdv.getMedecin() != null && 
                                rdv.getMedecin().getId().equals(medecinId);
                        })
                        .collect(Collectors.toList());
                }
            }
            
            consultationsList.setAll(consultations);
            tableConsultations.setItems(consultationsList);
            lblTotal.setText(String.valueOf(consultations.size()));
            
            logger.info("Chargement de {} consultations", consultations.size());
            
        } catch (Exception e) {
            logger.error("Erreur chargement consultations", e);
            showError("Erreur", "Impossible de charger les consultations");
        }
    }

    private void updateStatistics() {
        try {
            List<Consultation> all = consultationService.listerToutesConsultations();
            LocalDate today = LocalDate.now();
            LocalDate startOfMonth = today.withDayOfMonth(1);

            long countToday = all.stream()
                .filter(c -> c.getDateConsultation().equals(today))
                .count();
            
            long countMonth = all.stream()
                .filter(c -> !c.getDateConsultation().isBefore(startOfMonth))
                .count();

            BigDecimal totalRevenu = all.stream()
                .filter(c -> c.getTarifConsultation() != null)
                .map(Consultation::getTarifConsultation)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal revenuMoyen = all.isEmpty() ? BigDecimal.ZERO : 
                totalRevenu.divide(BigDecimal.valueOf(all.size()), 2, BigDecimal.ROUND_HALF_UP);

            lblToday.setText(String.valueOf(countToday));
            lblMonth.setText(String.valueOf(countMonth));
            lblRevenuMoyen.setText(revenuMoyen + " MAD");

        } catch (Exception e) {
            logger.error("Erreur calcul statistiques", e);
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadConsultations();
            return;
        }

        try {
            List<Consultation> all = consultationService.listerToutesConsultations();
            
            List<Consultation> filtered = all.stream()
                .filter(c -> {
                    String diagnostic = c.getDiagnostic() != null ? c.getDiagnostic().toLowerCase() : "";
                    String patientNom = "";
                    String medecinNom = "";
                    
                    if (rendezVousService.getRendezVousById(c.getIdRendezVous()) != null) {
                        if (rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient() != null) {
                            patientNom = (patientService.getPatientById(rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient()).getNom() + " " + 
                                         patientService.getPatientById(rendezVousService.getRendezVousById(c.getIdRendezVous()).getIdPatient()).getPrenom()).toLowerCase();
                        }
                        if (rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin() != null) {
                            medecinNom = rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin().getNom().toLowerCase();
                        }
                    }
                    
                    return diagnostic.contains(searchText) || 
                           patientNom.contains(searchText) || 
                           medecinNom.contains(searchText);
                })
                .toList();

            consultationsList.setAll(filtered);
            lblTotal.setText(String.valueOf(filtered.size()));

        } catch (Exception e) {
            logger.error("Erreur recherche", e);
        }
    }

    @FXML
    private void handleFilter() {
        try {
            List<Consultation> all = consultationService.listerToutesConsultations();
            LocalDate filterDate = dpFilterDate.getValue();
            Medecin filterMedecin = cmbFilterMedecin.getValue();

            List<Consultation> filtered = all.stream()
                .filter(c -> {
                    boolean dateMatch = filterDate == null || 
                        (c.getDateConsultation() != null && c.getDateConsultation().equals(filterDate));
                    
                    boolean medecinMatch = filterMedecin == null || 
                        (rendezVousService.getRendezVousById(c.getIdRendezVous()) != null && 
                         rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin() != null &&
                         rendezVousService.getRendezVousById(c.getIdRendezVous()).getMedecin().getId().equals(filterMedecin.getId()));

                    return dateMatch && medecinMatch;
                })
                .toList();

            consultationsList.setAll(filtered);
            lblTotal.setText(String.valueOf(filtered.size()));

        } catch (Exception e) {
            logger.error("Erreur filtrage", e);
        }
    }

    /**
     * Ajouter une nouvelle consultation
     */
    @FXML
    private void handleAddConsultation() {
        Stage stage = (Stage) tableConsultations.getScene().getWindow();
        ConsultationDialog dialog = new ConsultationDialog(stage);
        Optional<Consultation> result = dialog.showAndWait();

        result.ifPresent(consultation -> {
            try {
                consultationService.enregistrerConsultation(consultation);
                showInfo("Succès", "Consultation ajoutée avec succès !");
                loadConsultations();
            } catch (Exception e) {
                logger.error("Erreur lors de l'ajout de la consultation", e);
                showError("Erreur", "Impossible d'ajouter la consultation : " + e.getMessage());
            }
        });
    }

    /**
     * Modifier une consultation existante
     */
    private void handleModifier(Consultation consultation) {
        Stage stage = (Stage) tableConsultations.getScene().getWindow();
        ConsultationDialog dialog = new ConsultationDialog(stage, consultation);
        Optional<Consultation> result = dialog.showAndWait();
        result.ifPresent(updatedConsultation -> {
            try {
                consultationService.modifierConsultation(updatedConsultation);
                showInfo("Succès", "Consultation modifiée avec succès !");
                loadConsultations();
            } catch (Exception e) {
                logger.error("Erreur lors de la modification de la consultation", e);
                showError("Erreur", "Impossible de modifier la consultation : " + e.getMessage());
            }
        });
    }

    private void handleViewDetails(Consultation c) {
        ConsultationDetailsDialog dialog = new ConsultationDetailsDialog(c);
        dialog.showAndWait();
    }


    private void handleGenerateOrdonnance(Consultation c) {
        showInfo("Ordonnance", "Génération d'ordonnance en cours de développement");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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

    private void configurePermissions() {
        btnAddConsultation.setVisible(PermissionManager.canCreateConsultation());
        btnAddConsultation.setManaged(PermissionManager.canCreateConsultation());
    }
}