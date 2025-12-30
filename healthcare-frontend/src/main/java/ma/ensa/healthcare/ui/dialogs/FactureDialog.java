package ma.ensa.healthcare.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ma.ensa.healthcare.dao.impl.FactureDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IFactureDAO;
import ma.ensa.healthcare.model.*;
import ma.ensa.healthcare.model.enums.ModePaiement;
import ma.ensa.healthcare.model.enums.StatutPaiement;
import ma.ensa.healthcare.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialogue de création/modification d'une facture
 */
public class FactureDialog extends Dialog<Facture> {
    
    private static final Logger logger = LoggerFactory.getLogger(FactureDialog.class);
    
    // Services
    private final ConsultationService consultationService;
    private final PatientService patientService;
    private final RendezVousService rendezVousService = new RendezVousService();
    
    // Composants UI
    private ComboBox<Consultation> consultationComboBox;
    private TextField numeroFactureField;
    private DatePicker dateFacturePicker;
    private Label patientLabel;
    private TextField montantConsultationField;
    private TextField montantMedicamentsField;
    private TextField montantTotalField;
    private TextField montantPayeField;
    private ComboBox<StatutPaiement> statutComboBox;
    private ComboBox<ModePaiement> modePaiementComboBox;
    private DatePicker datePaiementPicker;
    
    // Données
    private Facture facture;
    private boolean isEditMode;

    /**
     * Constructeur pour création
     */
    public FactureDialog(Stage owner) {
        this(owner, null);
    }

    /**
     * Constructeur pour modification
     */
    public FactureDialog(Stage owner, Facture facture) {
        this.consultationService = new ConsultationService();
        this.patientService = new PatientService();
        this.facture = facture;
        this.isEditMode = (facture != null);
        
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(isEditMode ? "Modifier la facture" : "Nouvelle facture");
        setHeaderText(isEditMode ? "Modification d'une facture existante" : "Création d'une nouvelle facture");
        
        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Contenu
        getDialogPane().setContent(createContent());
        
        // Largeur du dialogue
        getDialogPane().setPrefWidth(600);
        
        // Chargement des données si mode édition
        //if (isEditMode) {
        //    loadFactureData();
        //}
        
        // Validation
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateInput()) {
                event.consume();
            }
        });
        
        // Résultat
        setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                return buildFacture();
            }
            return null;
        });
    }

    /**
     * Crée le contenu du dialogue
     */
    private VBox createContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // ========== SECTION 1: CONSULTATION ==========
        VBox consultationSection = createSection("Consultation associée *");
        
        consultationComboBox = new ComboBox<>();
        consultationComboBox.setPromptText("Sélectionner une consultation");
        consultationComboBox.setMaxWidth(Double.MAX_VALUE);
        consultationComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Consultation c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    // Sécurisation contre les valeurs nulles
                    String dateStr = (c.getDateConsultation() != null) ? c.getDateConsultation().toString() : "Date inconnue";
                    String patientStr = "Patient inconnu";
                    Long idRendezVous = c.getIdRendezVous();
                    RendezVous rdv = rendezVousService.getRendezVousById(idRendezVous);
                    Patient patient = patientService.getPatientById(rdv.getIdPatient());
                    
                    
                    if (rendezVousService.getRendezVousById(c.getIdRendezVous()) != null && patient != null) {
                        patientStr = patient.getNom() + " " + patient.getPrenom();
                    }
                    
                    setText(String.format("Consultation du %s - %s", dateStr, patientStr));
                        }
                    }
        });
        consultationComboBox.setButtonCell(consultationComboBox.getCellFactory().call(null));
        
        // Événement: charger infos patient et montants
        consultationComboBox.setOnAction(e -> loadConsultationDetails());
        
        // Charger les consultations sans facture
        loadConsultations();
        
        consultationSection.getChildren().add(consultationComboBox);
        
        // ========== SECTION 2: INFORMATIONS GÉNÉRALES ==========
        VBox infoSection = createSection("Informations générales");
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(10);
        
        // Numéro facture
        infoGrid.add(new Label("N° Facture:"), 0, 0);
        numeroFactureField = new TextField();
        numeroFactureField.setPromptText("Généré automatiquement");
        numeroFactureField.setDisable(true);
        numeroFactureField.setMaxWidth(200);
        infoGrid.add(numeroFactureField, 1, 0);
        
        // Date facture
        infoGrid.add(new Label("Date facture:"), 0, 1);
        dateFacturePicker = new DatePicker(LocalDate.now());
        dateFacturePicker.setMaxWidth(200);
        infoGrid.add(dateFacturePicker, 1, 1);
        
        // Patient
        infoGrid.add(new Label("Patient:"), 0, 2);
        patientLabel = new Label("-");
        patientLabel.setStyle("-fx-font-weight: bold;");
        infoGrid.add(patientLabel, 1, 2);
        
        infoSection.getChildren().add(infoGrid);
        
        // ========== SECTION 3: MONTANTS ==========
        VBox montantsSection = createSection("Montants (MAD)");
        GridPane montantsGrid = new GridPane();
        montantsGrid.setHgap(15);
        montantsGrid.setVgap(10);
        
        // Montant consultation
        montantsGrid.add(new Label("Consultation:"), 0, 0);
        montantConsultationField = new TextField("0.00");
        montantConsultationField.setMaxWidth(150);
        montantConsultationField.setOnKeyReleased(e -> calculerMontantTotal());
        montantsGrid.add(montantConsultationField, 1, 0);
        
        // Montant médicaments
        montantsGrid.add(new Label("Médicaments:"), 0, 1);
        montantMedicamentsField = new TextField("0.00");
        montantMedicamentsField.setMaxWidth(150);
        montantMedicamentsField.setOnKeyReleased(e -> calculerMontantTotal());
        montantsGrid.add(montantMedicamentsField, 1, 1);
        
        // Montant total
        montantsGrid.add(new Label("TOTAL:"), 0, 2);
        montantTotalField = new TextField("0.00");
        montantTotalField.setMaxWidth(150);
        montantTotalField.setDisable(true);
        montantTotalField.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        montantsGrid.add(montantTotalField, 1, 2);
        
        montantsSection.getChildren().add(montantsGrid);
        
        // ========== SECTION 4: PAIEMENT ==========
        VBox paiementSection = createSection("Informations de paiement");
        GridPane paiementGrid = new GridPane();
        paiementGrid.setHgap(15);
        paiementGrid.setVgap(10);
        
        // Montant payé
        paiementGrid.add(new Label("Montant payé:"), 0, 0);
        montantPayeField = new TextField("0.00");
        montantPayeField.setMaxWidth(150);
        montantPayeField.setOnKeyReleased(e -> updateStatutPaiement());
        paiementGrid.add(montantPayeField, 1, 0);
        
        // Statut
        paiementGrid.add(new Label("Statut:"), 0, 1);
        statutComboBox = new ComboBox<>();
        statutComboBox.getItems().setAll(StatutPaiement.values());
        statutComboBox.setValue(StatutPaiement.EN_ATTENTE);
        statutComboBox.setMaxWidth(150);
        paiementGrid.add(statutComboBox, 1, 1);
        
        // Mode paiement
        paiementGrid.add(new Label("Mode:"), 0, 2);
        modePaiementComboBox = new ComboBox<>();
        modePaiementComboBox.getItems().setAll(ModePaiement.values());
        modePaiementComboBox.setPromptText("Sélectionner");
        modePaiementComboBox.setMaxWidth(150);
        paiementGrid.add(modePaiementComboBox, 1, 2);
        
        // Date paiement
        paiementGrid.add(new Label("Date paiement:"), 0, 3);
        datePaiementPicker = new DatePicker();
        datePaiementPicker.setMaxWidth(150);
        paiementGrid.add(datePaiementPicker, 1, 3);
        
        paiementSection.getChildren().add(paiementGrid);
        
        // Note obligatoire
        Label noteLabel = new Label("* Champs obligatoires");
        noteLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        // Assemblage
        content.getChildren().addAll(
            consultationSection,
            infoSection,
            montantsSection,
            paiementSection,
            noteLabel
        );
        
        return content;
    }

    /**
     * Crée une section avec titre
     */
    private VBox createSection(String title) {
        VBox section = new VBox(8);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        section.getChildren().add(titleLabel);
        return section;
    }

    /**
     * Charge les consultations disponibles (sans facture)
     */
    private void loadConsultations() {
        try {
            // Récupérer toutes les consultations
            List<Consultation> toutesConsultations = consultationService.listerToutesConsultations();
            
            // Créer une instance du DAO pour vérifier les factures
            IFactureDAO factureDAO = new FactureDAOImpl();
            
            // Filtrer pour ne garder que les consultations sans facture
            List<Consultation> consultationsSansFac = toutesConsultations.stream()
                .filter(c -> {
                    try {
                        Facture factureExistante = factureDAO.findByConsultationId(c.getId());
                        return factureExistante == null; // Garder si pas de facture
                    } catch (Exception e) {
                        logger.warn("Erreur vérification facture pour consultation {}", c.getId(), e);
                        return true; // En cas d'erreur, on garde la consultation
                    }
                })
                .collect(Collectors.toList());
            
            consultationComboBox.getItems().setAll(consultationsSansFac);
            
            if (consultationsSansFac.isEmpty()) {
                consultationComboBox.setPromptText("Aucune consultation sans facture");
                consultationComboBox.setDisable(true);
            }
            
        } catch (Exception e) {
            logger.error("Erreur chargement consultations", e);
            showError("Impossible de charger les consultations");
        }
    }

    /**
     * Charge les détails de la consultation sélectionnée
     */
    private void loadConsultationDetails() {
        Consultation c = consultationComboBox.getValue();
        if (c == null) return;

        Long idRdv = c.getIdRendezVous();
        RendezVous rdv = rendezVousService.getRendezVousById(idRdv);
        Patient patient = patientService.getPatientById(rdv.getIdPatient());

        patientLabel.setText(patient.getNom() + " " + patient.getPrenom());
        
        // Montant consultation
        if (c.getTarifConsultation() != null) {
            montantConsultationField.setText(c.getTarifConsultation().toString());
        }
        
        calculerMontantTotal();
    }

    /**
     * Calcule le montant total
     */
    private void calculerMontantTotal() {
        try {
            BigDecimal consultation = new BigDecimal(montantConsultationField.getText().trim());
            BigDecimal medicaments = new BigDecimal(montantMedicamentsField.getText().trim());
            BigDecimal total = consultation.add(medicaments);
            montantTotalField.setText(total.toString());
        } catch (NumberFormatException e) {
            montantTotalField.setText("0.00");
        }
    }

    /**
     * Met à jour le statut de paiement automatiquement
     */
    private void updateStatutPaiement() {
        try {
            BigDecimal total = new BigDecimal(montantTotalField.getText().trim());
            BigDecimal paye = new BigDecimal(montantPayeField.getText().trim());
            
            if (paye.compareTo(BigDecimal.ZERO) == 0) {
                statutComboBox.setValue(StatutPaiement.EN_ATTENTE);
            } else if (paye.compareTo(total) >= 0) {
                statutComboBox.setValue(StatutPaiement.PAYE);
            } else {
                statutComboBox.setValue(StatutPaiement.PARTIEL);
            }
        } catch (NumberFormatException e) {
            // Ignorer
        }
    }

    /**
     * Charge les données de la facture (mode édition)
     */
    /*
    private void loadFactureData() {
        if (facture == null) return;
        
        consultationComboBox.setValue(facture.getConsultation());
        consultationComboBox.setDisable(true);
        
        numeroFactureField.setText(facture.getNumeroFacture());
        dateFacturePicker.setValue(facture.getDateFacture());
        
        Patient patient = facture.getPatient();
        patientLabel.setText(patient.getNom() + " " + patient.getPrenom());
        
        montantConsultationField.setText(facture.getMontantConsultation().toString());
        montantMedicamentsField.setText(facture.getMontantMedicaments().toString());
        montantTotalField.setText(facture.getMontantTotal().toString());
        montantPayeField.setText(facture.getMontantPaye().toString());
        
        statutComboBox.setValue(facture.getStatutPaiement());
        
        if (facture.getModePaiement() != null) {
            modePaiementComboBox.setValue(facture.getModePaiement());
        }
        
        if (facture.getDatePaiement() != null) {
            datePaiementPicker.setValue(facture.getDatePaiement());
        }
    }
    */

    /**
     * Valide les champs
     */
    private boolean validateInput() {
        List<String> errors = new ArrayList<>();
        
        // Consultation obligatoire (sauf en édition)
        if (!isEditMode && consultationComboBox.getValue() == null) {
            errors.add("Veuillez sélectionner une consultation");
        }
        
        // Date obligatoire
        if (dateFacturePicker.getValue() == null) {
            errors.add("La date de facture est obligatoire");
        }
        
        // Montants valides
        try {
            BigDecimal consultation = new BigDecimal(montantConsultationField.getText().trim());
            if (consultation.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Le montant consultation doit être positif");
            }
        } catch (NumberFormatException e) {
            errors.add("Format de montant consultation invalide");
        }
        
        try {
            BigDecimal medicaments = new BigDecimal(montantMedicamentsField.getText().trim());
            if (medicaments.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Le montant médicaments doit être positif");
            }
        } catch (NumberFormatException e) {
            errors.add("Format de montant médicaments invalide");
        }
        
        try {
            BigDecimal paye = new BigDecimal(montantPayeField.getText().trim());
            if (paye.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Le montant payé doit être positif");
            }
        } catch (NumberFormatException e) {
            errors.add("Format de montant payé invalide");
        }
        
        if (!errors.isEmpty()) {
            showError(String.join("\n", errors));
            return false;
        }
        
        return true;
    }

    /**
     * Construit l'objet Facture
     */
    private Facture buildFacture() {
        Facture f = isEditMode ? facture : new Facture();
        
        // Consultation (seulement en création)
        if (!isEditMode) {
            Consultation c = consultationComboBox.getValue();
            Long idRdv = c.getIdRendezVous();
            RendezVous rdv = rendezVousService.getRendezVousById(idRdv);
            Patient patient = patientService.getPatientById(rdv.getIdPatient());
            f.setIdConsultation(c.getId());
            f.setIdPatient(patient.getId());
        }
        
        f.setDateFacture(dateFacturePicker.getValue());
        f.setMontantConsultation(new BigDecimal(montantConsultationField.getText().trim()));
        f.setMontantMedicaments(new BigDecimal(montantMedicamentsField.getText().trim()));
        f.setMontantTotal(new BigDecimal(montantTotalField.getText().trim()));
        f.setMontantPaye(new BigDecimal(montantPayeField.getText().trim()));
        f.setStatutPaiement(statutComboBox.getValue());
        f.setModePaiement(modePaiementComboBox.getValue());
        f.setDatePaiement(datePaiementPicker.getValue());
        
        return f;
    }

    /**
     * Affiche une erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(getOwner());
        alert.showAndWait();
    }
}