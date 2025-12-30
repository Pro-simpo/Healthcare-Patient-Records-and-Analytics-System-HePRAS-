package ma.ensa.healthcare.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.model.enums.StatutRendezVous;
import ma.ensa.healthcare.service.*;
import ma.ensa.healthcare.ui.dialogs.RendezVousDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.animation.Interpolator;


public class RendezVousController {

    private static final Logger logger = LoggerFactory.getLogger(RendezVousController.class);

    @FXML private DatePicker dpFilterDate;
    @FXML private ComboBox<String> cmbFilterStatut;
    
    @FXML private Label lblToday;
    @FXML private Label lblWeek;
    @FXML private Label lblEnAttente;
    @FXML private Label lblAnnules;
    
    @FXML private TableView<RendezVous> tableRendezVous;
    @FXML private TableColumn<RendezVous, Long> colId;
    @FXML private TableColumn<RendezVous, String> colDateTime;
    @FXML private TableColumn<RendezVous, String> colPatient;
    @FXML private TableColumn<RendezVous, String> colCin;
    @FXML private TableColumn<RendezVous, String> colMedecin;
    @FXML private TableColumn<RendezVous, String> colSpecialite;
    @FXML private TableColumn<RendezVous, String> colMotif;
    @FXML private TableColumn<RendezVous, String> colStatut;
    @FXML private TableColumn<RendezVous, Void> colActions;
    @FXML private Label lblTotal;
    @FXML private HBox hboxStats;

    private final RendezVousService rdvService = new RendezVousService();
    private final PatientService patientService = new PatientService();
    private ObservableList<RendezVous> rdvList = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupComboBox();
        setupTableColumns();
        loadRendezVous();
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

    /**
     * Configure le ComboBox des statuts
     */
    private void setupComboBox() {
        cmbFilterStatut.getItems().addAll(
            "Tous",
            "PLANIFIE",
            "CONFIRME",
            "TERMINE",
            "ANNULE"
        );
        cmbFilterStatut.setValue("Tous");
    }

    /**
     * Configure les colonnes du TableView
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // ✅ CORRECTION : Utiliser heureDebut au lieu de getDateHeure()
        colDateTime.setCellValueFactory(cellData -> {
            LocalDateTime heureDebut = cellData.getValue().getHeureDebut();
            String formatted = heureDebut != null ? heureDebut.format(DATETIME_FORMATTER) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        
        colPatient.setCellValueFactory(cellData -> {
            RendezVous rdv = cellData.getValue();
            if (rdv.getIdPatient() != 0) {
                return new javafx.beans.property.SimpleStringProperty(
                    patientService.getPatientById(rdv.getIdPatient()).getNom() + " " + patientService.getPatientById(rdv.getIdPatient()).getPrenom());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colCin.setCellValueFactory(cellData -> {
            RendezVous rdv = cellData.getValue();
            if (rdv.getIdPatient() != 0) {
                return new javafx.beans.property.SimpleStringProperty(patientService.getPatientById(rdv.getIdPatient()).getCin());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colMedecin.setCellValueFactory(cellData -> {
            RendezVous rdv = cellData.getValue();
            if (rdv.getMedecin() != null) {
                return new javafx.beans.property.SimpleStringProperty("Dr. " + rdv.getMedecin().getNom());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colSpecialite.setCellValueFactory(cellData -> {
            RendezVous rdv = cellData.getValue();
            if (rdv.getMedecin() != null) {
                return new javafx.beans.property.SimpleStringProperty(rdv.getMedecin().getSpecialite());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        
        colStatut.setCellValueFactory(cellData -> {
            String statut = cellData.getValue().getStatut() != null ? 
                           cellData.getValue().getStatut().name() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(statut);
        });

        // Style pour la colonne Statut
        colStatut.setCellFactory(column -> new TableCell<RendezVous, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "CONFIRME" -> setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        case "PLANIFIE" -> setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                        case "ANNULE" -> setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                        case "TERMINE" -> setStyle("-fx-text-fill: #9E9E9E; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: #757575;");
                    }
                }
            }
        });

        setupActionsColumn();
    }

    /**
     * Configure la colonne Actions
     */
    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnConfirmer = new Button("Confirmer");
            private final Button btnAnnuler = new Button("Annuler");
            private final Button btnModifier = new Button("Modifier");
            private final HBox hbox = new HBox(5, btnConfirmer, btnAnnuler, btnModifier);

            {
                btnConfirmer.getStyleClass().add("action-button-success");
                btnConfirmer.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnConfirmer.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    handleConfirmer(rdv);
                });

                btnAnnuler.getStyleClass().add("action-button-danger");
                btnAnnuler.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnAnnuler.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    handleAnnuler(rdv);
                });

                btnModifier.getStyleClass().add("action-button");
                btnModifier.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnModifier.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    handleModifier(rdv);
                });

                hbox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    // Masquer bouton Confirmer si déjà confirmé
                    btnConfirmer.setVisible(rdv.getStatut() != StatutRendezVous.CONFIRME && 
                                           rdv.getStatut() != StatutRendezVous.TERMINE);
                    btnAnnuler.setVisible(rdv.getStatut() != StatutRendezVous.ANNULE);
                    setGraphic(hbox);
                }
            }
        });
    }

    /**
     * Charge tous les rendez-vous
     */
    private void loadRendezVous() {
        try {
            // ✅ CORRECTION : Utiliser obtenirTousLesRendezVous() ou getAllRendezVous()
            List<RendezVous> rdvs = rdvService.obtenirTousLesRendezVous();
            rdvList.setAll(rdvs);
            tableRendezVous.setItems(rdvList);
            lblTotal.setText(String.valueOf(rdvs.size()));
            
            logger.info("Chargement de {} rendez-vous", rdvs.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des rendez-vous", e);
            showError("Erreur", "Impossible de charger les rendez-vous");
        }
    }

    /**
     * Met à jour les statistiques
     */
    private void updateStatistics() {
        try {
            List<RendezVous> allRdv = rdvService.obtenirTousLesRendezVous();
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            LocalDate weekEnd = weekStart.plusDays(6);

            // ✅ CORRECTION : Utiliser dateRdv au lieu de getDateHeure()
            long countToday = allRdv.stream()
                .filter(r -> r.getDateRdv() != null && r.getDateRdv().equals(today))
                .count();
            
            long countWeek = allRdv.stream()
                .filter(r -> {
                    LocalDate date = r.getDateRdv();
                    return date != null && !date.isBefore(weekStart) && !date.isAfter(weekEnd);
                })
                .count();
            
            long countEnAttente = allRdv.stream()
                .filter(r -> r.getStatut() == StatutRendezVous.PLANIFIE)
                .count();
            
            long countAnnules = allRdv.stream()
                .filter(r -> r.getStatut() == StatutRendezVous.ANNULE)
                .count();

            lblToday.setText(String.valueOf(countToday));
            lblWeek.setText(String.valueOf(countWeek));
            lblEnAttente.setText(String.valueOf(countEnAttente));
            lblAnnules.setText(String.valueOf(countAnnules));

        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques", e);
        }
    }

    @FXML
    private void handleFilter() {
        try {
            List<RendezVous> allRdv = rdvService.obtenirTousLesRendezVous();
            LocalDate filterDate = dpFilterDate.getValue();
            String filterStatut = cmbFilterStatut.getValue();

            // ✅ CORRECTION : Utiliser dateRdv au lieu de getDateHeure()
            List<RendezVous> filtered = allRdv.stream()
                .filter(rdv -> {
                    boolean dateMatch = filterDate == null || 
                                      (rdv.getDateRdv() != null && rdv.getDateRdv().equals(filterDate));
                    boolean statutMatch = "Tous".equals(filterStatut) || 
                                        (rdv.getStatut() != null && rdv.getStatut().name().equals(filterStatut));
                    return dateMatch && statutMatch;
                })
                .toList();

            rdvList.setAll(filtered);
            tableRendezVous.setItems(rdvList);
            lblTotal.setText(String.valueOf(filtered.size()));

        } catch (Exception e) {
            logger.error("Erreur lors du filtrage", e);
        }
    }

    @FXML
    private void handleReset() {
        dpFilterDate.setValue(null);
        cmbFilterStatut.setValue("Tous");
        loadRendezVous();
    }

    @FXML
    private void handleAddRendezVous() {
        RendezVousDialog dialog = new RendezVousDialog();
        Optional<RendezVous> result = dialog.showAndWait();

        result.ifPresent(rdv -> {
            try {
                rdvService.planifierRendezVous(rdv);
                showSuccess("Succès", "Rendez-vous créé avec succès !");
                loadRendezVous();
                updateStatistics();
            } catch (Exception e) {
                logger.error("Erreur lors de la création du RDV", e);
                showError("Erreur", "Impossible de créer le rendez-vous : " + e.getMessage());
            }
        });
    }

    private void handleConfirmer(RendezVous rdv) {
        try {
            rdv.setStatut(StatutRendezVous.CONFIRME);
            rdvService.updateRendezVous(rdv);
            showSuccess("Succès", "Rendez-vous confirmé !");
            loadRendezVous();
            updateStatistics();
        } catch (Exception e) {
            logger.error("Erreur confirmation RDV", e);
            showError("Erreur", "Impossible de confirmer le RDV");
        }
    }

    private void handleAnnuler(RendezVous rdv) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annuler le rendez-vous");
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler ce rendez-vous ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // ✅ CORRECTION : Ajouter le motif d'annulation
                    rdvService.annulerRendezVous(rdv.getId(), "Annulé par l'utilisateur");
                    showSuccess("Succès", "Rendez-vous annulé");
                    loadRendezVous();
                    updateStatistics();
                } catch (Exception e) {
                    logger.error("Erreur annulation RDV", e);
                    showError("Erreur", "Impossible d'annuler le RDV");
                }
            }
        });
    }

    private void handleModifier(RendezVous rdv) {
        RendezVousDialog dialog = new RendezVousDialog(rdv);
        Optional<RendezVous> result = dialog.showAndWait();

        result.ifPresent(updatedRdv -> {
            try {
                rdvService.updateRendezVous(updatedRdv);
                showSuccess("Succès", "Rendez-vous modifié !");
                loadRendezVous();
                updateStatistics();
            } catch (Exception e) {
                logger.error("Erreur modification RDV", e);
                showError("Erreur", "Impossible de modifier le RDV");
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
}