package ma.ensa.healthcare.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.model.enums.ModePaiement;
import ma.ensa.healthcare.model.enums.StatutPaiement;
import ma.ensa.healthcare.service.*;
import ma.ensa.healthcare.ui.dialogs.PaiementDialog;
import ma.ensa.healthcare.ui.dialogs.RendezVousDialog;
import ma.ensa.healthcare.ui.utils.PermissionManager;
import ma.ensa.healthcare.util.PdfExportService;
import ma.ensa.healthcare.ui.dialogs.FactureDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.stage.FileChooser;
import ma.ensa.healthcare.util.PdfExportService;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.animation.Interpolator;

import ma.ensa.healthcare.ui.utils.PermissionManager;

public class FacturesController {

    private static final Logger logger = LoggerFactory.getLogger(FacturesController.class);

    @FXML private TextField searchField;
    @FXML private ComboBox<String> cmbFilterStatut;
    @FXML private DatePicker dpDateDebut;
    @FXML private DatePicker dpDateFin;
    
    @FXML private Label lblTotalFacture;
    @FXML private Label lblTotalPaye;
    @FXML private Label lblTotalImpaye;
    @FXML private Label lblTauxRecouvrement;
    
    @FXML private TableView<Facture> tableFactures;
    @FXML private TableColumn<Facture, Long> colId;
    @FXML private TableColumn<Facture, String> colNumero;
    @FXML private TableColumn<Facture, String> colDate;
    @FXML private TableColumn<Facture, String> colPatient;
    @FXML private TableColumn<Facture, String> colCin;
    @FXML private TableColumn<Facture, String> colMontantConsultation;
    @FXML private TableColumn<Facture, String> colMontantMedicaments;
    @FXML private TableColumn<Facture, String> colMontantTotal;
    @FXML private TableColumn<Facture, String> colMontantPaye;
    @FXML private TableColumn<Facture, String> colMontantRestant;
    @FXML private TableColumn<Facture, String> colStatut;
    @FXML private TableColumn<Facture, Void> colActions;
    @FXML private Label lblTotal;
    @FXML private HBox hboxStats;
    @FXML private Button btnAddFacture;
    @FXML private Button btnExportPdf;

    private final FacturationService facturationService = new FacturationService();
    private final PatientService patientService = new PatientService();
    private ObservableList<Facture> facturesList = FXCollections.observableArrayList();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurePermissions();
        setupComboBox();
        setupTableColumns();
        loadFactures();
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
        cmbFilterStatut.getItems().addAll(
            "Tous",
            "EN_ATTENTE",
            "PARTIEL",
            "PAYE"
        );
        cmbFilterStatut.setValue("Tous");
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroFacture"));
        
        colDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateFacture();
            String formatted = date != null ? date.format(DATE_FORMATTER) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        
        colPatient.setCellValueFactory(cellData -> {
            Facture f = cellData.getValue();
            if (f.getIdPatient() != 0) {
                return new javafx.beans.property.SimpleStringProperty(
                    patientService.getPatientById(f.getIdPatient()).getNom() + " " + patientService.getPatientById(f.getIdPatient()).getPrenom());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colCin.setCellValueFactory(cellData -> {
            Facture f = cellData.getValue();
            if (f.getIdPatient() != 0) {
                return new javafx.beans.property.SimpleStringProperty(patientService.getPatientById(f.getIdPatient()).getCin());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        colMontantConsultation.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatMontant(cellData.getValue().getMontantConsultation())));
        
        colMontantMedicaments.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatMontant(cellData.getValue().getMontantMedicaments())));
        
        colMontantTotal.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatMontant(cellData.getValue().getMontantTotal())));
        
        colMontantPaye.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatMontant(cellData.getValue().getMontantPaye())));
        
        colMontantRestant.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                formatMontant(cellData.getValue().getMontantRestant())));
        
        colStatut.setCellValueFactory(cellData -> {
            String statut = cellData.getValue().getStatutPaiement() != null ? 
                           cellData.getValue().getStatutPaiement().name() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(statut);
        });

        // Style pour la colonne Statut
        colStatut.setCellFactory(column -> new TableCell<Facture, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "PAYE" -> setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        case "PARTIEL" -> setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                        case "EN_ATTENTE" -> setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: #757575;");
                    }
                }
            }
        });

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnPayer = new Button("Payer");
            private final Button btnDetails = new Button("Détails");
            private final Button btnImprimer = new Button("Imprimer");
            private final HBox hbox = new HBox(5, btnPayer, btnDetails, btnImprimer);

            {
                btnPayer.getStyleClass().add("action-button-success");
                btnPayer.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnPayer.setOnAction(event -> {
                    Facture f = getTableView().getItems().get(getIndex());
                    handleEncaisserPaiement(f);
                });

                btnDetails.getStyleClass().add("action-button");
                btnDetails.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnDetails.setOnAction(event -> {
                    Facture f = getTableView().getItems().get(getIndex());
                    handleViewDetails(f);
                });

                btnImprimer.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                btnImprimer.setOnAction(event -> {
                    Facture f = getTableView().getItems().get(getIndex());
                    handleExportSingleFacture(f);
                });

                hbox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Facture f = getTableView().getItems().get(getIndex());
                    boolean showPayer = f.getStatutPaiement() != StatutPaiement.PAYE &&
                                    PermissionManager.canRegisterPayment();
                    btnPayer.setVisible(showPayer);
                    setGraphic(hbox);
                }
            }
        });
    }
    private void loadFactures() {
        try {
            List<Facture> factures = facturationService.getToutesLesFactures();
            facturesList.setAll(factures);
            tableFactures.setItems(facturesList);
            lblTotal.setText(String.valueOf(factures.size()));
            
            logger.info("Chargement de {} factures", factures.size());
            
        } catch (Exception e) {
            logger.error("Erreur chargement factures", e);
            showError("Erreur", "Impossible de charger les factures");
        }
    }

    private void updateStatistics() {
        try {
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(
                LocalDate.now().lengthOfMonth());
            
            BigDecimal revenusMois = facturationService.getRevenusPeriode(startOfMonth, endOfMonth);
            BigDecimal totalImpaye = facturationService.getTotalImpaye();
            
            List<Facture> all = facturationService.getToutesLesFactures();
            BigDecimal totalFacture = all.stream()
                .map(Facture::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalPaye = all.stream()
                .map(Facture::getMontantPaye)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            double tauxRecouvrement = totalFacture.compareTo(BigDecimal.ZERO) > 0 ?
                totalPaye.divide(totalFacture, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue() : 0;

            lblTotalFacture.setText(String.format("%.2f MAD", revenusMois));
            lblTotalPaye.setText(String.format("%.2f MAD", totalPaye));
            lblTotalImpaye.setText(String.format("%.2f MAD", totalImpaye));
            lblTauxRecouvrement.setText(String.format("%.1f%%", tauxRecouvrement));

        } catch (Exception e) {
            logger.error("Erreur calcul statistiques", e);
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadFactures();
            return;
        }

        try {
            List<Facture> all = facturationService.getToutesLesFactures();
            
            List<Facture> filtered = all.stream()
                .filter(f -> {
                    String numero = f.getNumeroFacture() != null ? f.getNumeroFacture().toLowerCase() : "";
                    String patientNom = "";
                    String cin = "";
                    
                    if (f.getIdPatient() != 0) {
                        patientNom = (patientService.getPatientById(f.getIdPatient()).getNom() + " " + 
                                     patientService.getPatientById(f.getIdPatient()).getPrenom()).toLowerCase();
                        cin = patientService.getPatientById(f.getIdPatient()).getCin() != null ? patientService.getPatientById(f.getIdPatient()).getCin().toLowerCase() : "";
                    }
                    
                    return numero.contains(searchText) || 
                           patientNom.contains(searchText) || 
                           cin.contains(searchText);
                })
                .toList();

            facturesList.setAll(filtered);
            lblTotal.setText(String.valueOf(filtered.size()));

        } catch (Exception e) {
            logger.error("Erreur recherche", e);
        }
    }

    @FXML
    private void handleFilter() {
        try {
            List<Facture> all = facturationService.getToutesLesFactures();
            String filterStatut = cmbFilterStatut.getValue();

            List<Facture> filtered = all.stream()
                .filter(f -> {
                    boolean statutMatch = "Tous".equals(filterStatut) || 
                        (f.getStatutPaiement() != null && f.getStatutPaiement().name().equals(filterStatut));
                    
                    return statutMatch;
                })
                .toList();

            facturesList.setAll(filtered);
            lblTotal.setText(String.valueOf(filtered.size()));

        } catch (Exception e) {
            logger.error("Erreur filtrage", e);
        }
    }

    private void handleEncaisserPaiement(Facture f) {
        PaiementDialog dialog = new PaiementDialog(f);
        Optional<PaiementDialog.PaiementData> result = dialog.showAndWait();

        result.ifPresent(data -> {
            try {
                facturationService.encaisserPaiement(
                    f.getId(), 
                    data.getMontant(), 
                    data.getMode(), 
                    LocalDate.now()
                );
                showSuccess("Succès", "Paiement enregistré avec succès !");
                loadFactures();
                updateStatistics();
            } catch (Exception e) {
                logger.error("Erreur enregistrement paiement", e);
                showError("Erreur", "Impossible d'enregistrer le paiement");
            }
        });
    }

    private void handleViewDetails(Facture f) {
        showInfo("Détails Facture", 
                "N° Facture: " + f.getNumeroFacture() + "\n" +
                "Date: " + f.getDateFacture().format(DATE_FORMATTER) + "\n" +
                "Montant Total: " + formatMontant(f.getMontantTotal()) + "\n" +
                "Montant Payé: " + formatMontant(f.getMontantPaye()) + "\n" +
                "Reste: " + formatMontant(f.getMontantRestant()) + "\n" +
                "Statut: " + f.getStatutPaiement().name());
    }

    private void handleImprimer(Facture f) {
        showInfo("Impression", "Génération PDF en cours de développement");
    }

    private String formatMontant(BigDecimal montant) {
        return montant != null ? String.format("%.2f", montant) : "0.00";
    }

    @FXML
    private void handleAddFacture() {
        try {
            Stage stage = (Stage) tableFactures.getScene().getWindow();
            FactureDialog dialog = new FactureDialog(stage);
            Optional<Facture> result = dialog.showAndWait();

            result.ifPresent(facture -> {
                try {
                    facturationService.genererFacture(facture.getIdConsultation(), facture.getMontantMedicaments());
                    showSuccess("Succès", "Facture créée avec succès !");
                    loadFactures();
                    updateStatistics();
                } catch (Exception e) {
                    logger.error("Erreur lors de la création de la facture", e);
                    showError("Erreur", "Impossible de créer la facture : " + e.getMessage());
                }
            });
        } catch (Exception e) {
            // Ceci affichera l'erreur précise (ex: NullPointerException, NoClassDefFoundError)
            e.printStackTrace(); 
        }
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
        // Récupérer le Stage interne de l'Alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Charger ton icône
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));

        // Appliquer l'icône au Stage de l'alerte
        alertStage.getIcons().add(icon);

        alert.showAndWait();
    }


    /**
     * Exporte toutes les factures visibles dans la table en PDF
     */
    @FXML
    private void handleExportPdf() {
        try {
            // Ouvrir un FileChooser pour choisir où sauvegarder
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les factures en PDF");
            fileChooser.setInitialFileName("factures_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File file = fileChooser.showSaveDialog(tableFactures.getScene().getWindow());
            
            if (file != null) {
                // Récupérer les factures actuellement affichées dans la table
                List<Facture> facturesToExport = new ArrayList<>(tableFactures.getItems());
                
                if (facturesToExport.isEmpty()) {
                    showWarning("Aucune donnée", "Il n'y a aucune facture à exporter");
                    return;
                }
                
                // Générer le PDF
                String outputPath = PdfExportService.exportFacturesListToPdf(
                    facturesToExport, 
                    file.getAbsolutePath()
                );
                
                // Demander si on veut ouvrir le fichier
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setHeaderText("PDF généré avec succès !");
                alert.setContentText(facturesToExport.size() + " facture(s) exportée(s).\n\n" +
                                    "Fichier: " + file.getName());
                
                ButtonType btnOpen = new ButtonType("Ouvrir");
                ButtonType btnClose = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(btnOpen, btnClose);
                
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnOpen) {
                    openFile(file);
                }
                
                logger.info("Export PDF réussi : {}", outputPath);
                
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'export PDF", e);
            showError("Erreur d'export", 
                "Impossible de générer le PDF : " + e.getMessage());
        }
    }

    /**
     * Exporte UNE facture spécifique en PDF
     */
    private void handleExportSingleFacture(Facture facture) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter la facture en PDF");
            fileChooser.setInitialFileName(facture.getNumeroFacture() + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File file = fileChooser.showSaveDialog(tableFactures.getScene().getWindow());
            
            if (file != null) {
                String outputPath = PdfExportService.exportFactureToPdf(
                    facture, 
                    file.getAbsolutePath()
                );
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setHeaderText("Facture exportée !");
                alert.setContentText("Fichier: " + file.getName());
                
                ButtonType btnOpen = new ButtonType("Ouvrir");
                ButtonType btnClose = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(btnOpen, btnClose);
                
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnOpen) {
                    openFile(file);
                }
            }
            
        } catch (Exception e) {
            logger.error("Erreur export facture PDF", e);
            showError("Erreur", "Impossible d'exporter la facture : " + e.getMessage());
        }
    }

    /**
     * Ouvre un fichier avec l'application par défaut du système
     */
    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            logger.error("Impossible d'ouvrir le fichier", e);
            showWarning("Ouverture impossible", 
                "Le fichier a été créé mais impossible de l'ouvrir automatiquement.\n" +
                "Ouvrez-le manuellement depuis : " + file.getAbsolutePath());
        }
    }

    /**
     * Affiche un avertissement
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void configurePermissions() {
        btnAddFacture.setVisible(PermissionManager.canCreateFacture());
        btnAddFacture.setManaged(PermissionManager.canCreateFacture());
        
        btnExportPdf.setVisible(PermissionManager.canExportData());
        btnExportPdf.setManaged(PermissionManager.canExportData());
    }
}