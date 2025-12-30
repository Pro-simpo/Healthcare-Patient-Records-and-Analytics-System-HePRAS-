package ma.ensa.healthcare.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ma.ensa.healthcare.model.*;
import ma.ensa.healthcare.model.enums.StatutRendezVous;
import ma.ensa.healthcare.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

/**
 * Dialogue de création/modification d'une consultation
 */
public class ConsultationDialog extends Dialog<Consultation> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsultationDialog.class);
    
    // Services
    private final RendezVousService rdvService;
    private final ConsultationService consultationService;
    private final PatientService patientService = new PatientService();
    private final RendezVousService rendezVousService = new RendezVousService();
    
    // Composants UI
    private ComboBox<RendezVous> rdvComboBox;
    private DatePicker dateConsultationPicker;
    private TextArea symptomesArea;
    private TextArea diagnosticArea;
    private TextArea observationsArea;
    private TextArea prescriptionArea;
    private TextArea examensArea;
    private TextField tarifField;
    
    // Données
    private Consultation consultation;
    private boolean isEditMode;

    /**
     * Constructeur pour création
     */
    public ConsultationDialog(Stage owner) {
        this(owner, null);
    }

    /**
     * Constructeur pour modification
     */
    public ConsultationDialog(Stage owner, Consultation consultation) {
        this.rdvService = new RendezVousService();
        this.consultationService = new ConsultationService();
        this.consultation = consultation;
        this.isEditMode = (consultation != null);
        
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(isEditMode ? "Modifier la consultation" : "Nouvelle consultation");
        setHeaderText(isEditMode ? "Modification d'une consultation existante" : "Enregistrement d'une nouvelle consultation");
        
        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Contenu
        getDialogPane().setContent(createContent());
        
        // Largeur du dialogue
        getDialogPane().setPrefWidth(700);
        
        // Chargement des données si mode édition
        if (isEditMode) {
            loadConsultationData();
        }
        
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
                return buildConsultation();
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
        
        // ========== SECTION 1: RENDEZ-VOUS ==========
        VBox rdvSection = createSection("Rendez-vous associé");
        
        rdvComboBox = new ComboBox<>();
        rdvComboBox.setPromptText("Sélectionner un rendez-vous");
        rdvComboBox.setMaxWidth(Double.MAX_VALUE);
        rdvComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RendezVous rdv, boolean empty) {
                super.updateItem(rdv, empty);
                if (empty || rdv == null) {
                    setText(null);
                } else {
                    String text = String.format("%s - Dr. %s %s (%s)",
                        patientService.getPatientById(rdv.getIdPatient()).getNom() + " " + patientService.getPatientById(rdv.getIdPatient()).getPrenom(),
                        rdv.getMedecin().getNom(),
                        rdv.getMedecin().getPrenom(),
                        rdv.getDateRdv()
                    );
                    setText(text);
                }
            }
        });
        rdvComboBox.setButtonCell(rdvComboBox.getCellFactory().call(null));
        
        // Charger les RDV confirmés sans consultation
        loadRendezVous();
        
        rdvSection.getChildren().add(rdvComboBox);
        
        // ========== SECTION 2: DATE ==========
        VBox dateSection = createSection("Date de consultation");
        dateConsultationPicker = new DatePicker(LocalDate.now());
        dateConsultationPicker.setMaxWidth(Double.MAX_VALUE);
        dateSection.getChildren().add(dateConsultationPicker);
        
        // ========== SECTION 3: SYMPTÔMES ==========
        VBox symptomesSection = createSection("Symptômes *");
        symptomesArea = new TextArea();
        symptomesArea.setPromptText("Décrire les symptômes du patient...");
        symptomesArea.setPrefRowCount(3);
        symptomesArea.setWrapText(true);
        symptomesSection.getChildren().add(symptomesArea);
        
        // ========== SECTION 4: DIAGNOSTIC ==========
        VBox diagnosticSection = createSection("Diagnostic *");
        diagnosticArea = new TextArea();
        diagnosticArea.setPromptText("Diagnostic médical...");
        diagnosticArea.setPrefRowCount(3);
        diagnosticArea.setWrapText(true);
        diagnosticSection.getChildren().add(diagnosticArea);
        
        // ========== SECTION 5: OBSERVATIONS ==========
        VBox observationsSection = createSection("Observations");
        observationsArea = new TextArea();
        observationsArea.setPromptText("Observations médicales (tension, pouls, température, etc.)...");
        observationsArea.setPrefRowCount(3);
        observationsArea.setWrapText(true);
        observationsSection.getChildren().add(observationsArea);
        
        // ========== SECTION 6: PRESCRIPTION ==========
        VBox prescriptionSection = createSection("Prescription");
        prescriptionArea = new TextArea();
        prescriptionArea.setPromptText("Ordonnance / Traitements prescrits...");
        prescriptionArea.setPrefRowCount(3);
        prescriptionArea.setWrapText(true);
        prescriptionSection.getChildren().add(prescriptionArea);
        
        // ========== SECTION 7: EXAMENS ==========
        VBox examensSection = createSection("Examens demandés");
        examensArea = new TextArea();
        examensArea.setPromptText("Examens complémentaires à réaliser...");
        examensArea.setPrefRowCount(2);
        examensArea.setWrapText(true);
        examensSection.getChildren().add(examensArea);
        
        // ========== SECTION 8: TARIF ==========
        VBox tarifSection = createSection("Tarif consultation (MAD) *");
        tarifField = new TextField();
        tarifField.setPromptText("450.00");
        tarifField.setMaxWidth(200);
        tarifSection.getChildren().add(tarifField);
        
        // Note obligatoire
        Label noteLabel = new Label("* Champs obligatoires");
        noteLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        // Assemblage
        content.getChildren().addAll(
            rdvSection,
            dateSection,
            symptomesSection,
            diagnosticSection,
            observationsSection,
            prescriptionSection,
            examensSection,
            tarifSection,
            noteLabel
        );
        
        // ScrollPane pour le contenu
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);
        
        VBox wrapper = new VBox(scrollPane);
        return wrapper;
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
     * Charge les rendez-vous disponibles
     */
    private void loadRendezVous() {
        try {
            List<RendezVous> rdvList = rdvService.getAllRendezVous();
            
            // Filtrer: RDV confirmés sans consultation
            List<RendezVous> availableRdv = rdvList.stream()
                .filter(rdv -> rdv.getStatut() == StatutRendezVous.CONFIRME)
                .filter(rdv -> consultationService.getConsultationByRendezVous(rdv.getId()) == null)
                .toList();
            
            rdvComboBox.getItems().setAll(availableRdv);
            
            if (availableRdv.isEmpty()) {
                rdvComboBox.setPromptText("Aucun rendez-vous disponible");
                rdvComboBox.setDisable(true);
            }
            
        } catch (Exception e) {
            logger.error("Erreur chargement RDV", e);
            showError("Impossible de charger les rendez-vous");
        }
    }

    /**
     * Charge les données de la consultation (mode édition)
     */
    private void loadConsultationData() {
        if (consultation == null) return;
        
        // RDV (désactivé en mode édition)
        rdvComboBox.setValue(rendezVousService.getRendezVousById(consultation.getIdRendezVous()));
        rdvComboBox.setDisable(true);
        
        // Données
        dateConsultationPicker.setValue(consultation.getDateConsultation());
        symptomesArea.setText(consultation.getSymptomes());
        diagnosticArea.setText(consultation.getDiagnostic());
        observationsArea.setText(consultation.getObservations());
        prescriptionArea.setText(consultation.getPrescription());
        examensArea.setText(consultation.getExamenesDemandes());
        
        if (consultation.getTarifConsultation() != null) {
            tarifField.setText(consultation.getTarifConsultation().toString());
        }
    }

    /**
     * Valide les champs
     */
    private boolean validateInput() {
        List<String> errors = new ArrayList<>();
        
        // RDV obligatoire (sauf en édition)
        if (!isEditMode && rdvComboBox.getValue() == null) {
            errors.add("Veuillez sélectionner un rendez-vous");
        }
        
        // Date obligatoire
        if (dateConsultationPicker.getValue() == null) {
            errors.add("La date de consultation est obligatoire");
        }
        
        // Symptômes
        if (symptomesArea.getText().trim().isEmpty()) {
            errors.add("Les symptômes sont obligatoires");
        }
        
        // Diagnostic obligatoire
        if (diagnosticArea.getText().trim().isEmpty()) {
            errors.add("Le diagnostic est obligatoire");
        }
        
        // Tarif obligatoire et valide
        if (tarifField.getText().trim().isEmpty()) {
            errors.add("Le tarif est obligatoire");
        } else {
            try {
                BigDecimal tarif = new BigDecimal(tarifField.getText().trim());
                if (tarif.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add("Le tarif doit être supérieur à 0");
                }
            } catch (NumberFormatException e) {
                errors.add("Format de tarif invalide");
            }
        }
        
        if (!errors.isEmpty()) {
            showError(String.join("\n", errors));
            return false;
        }
        
        return true;
    }

    /**
     * Construit l'objet Consultation
     */
    private Consultation buildConsultation() {
        Consultation c = isEditMode ? consultation : new Consultation();
        
        // RDV (seulement en création)
        if (!isEditMode) {
            c.setIdRendezVous(rdvComboBox.getValue().getId());
        }
        
        c.setDateConsultation(dateConsultationPicker.getValue());
        c.setSymptomes(symptomesArea.getText().trim());
        c.setDiagnostic(diagnosticArea.getText().trim());
        c.setObservations(observationsArea.getText().trim());
        c.setPrescription(prescriptionArea.getText().trim());
        c.setExamenesDemandes(examensArea.getText().trim());
        c.setTarifConsultation(new BigDecimal(tarifField.getText().trim()));
        
        return c;
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