package ma.ensa.healthcare.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import ma.ensa.healthcare.model.Consultation;
import ma.ensa.healthcare.model.Traitement;
import ma.ensa.healthcare.service.TraitementService;
import ma.ensa.healthcare.ui.MainApp;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialogue pour afficher les détails complets d'une consultation
 */
public class ConsultationDetailsDialog extends Dialog<Void> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final TraitementService traitementService = new TraitementService();

    public ConsultationDetailsDialog(Consultation consultation) {
        setTitle("Détails de la Consultation");
        setHeaderText("Consultation N°" + consultation.getId());
        
        // Bouton de fermeture
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        // Modal
        initModality(Modality.APPLICATION_MODAL);
        initOwner(MainApp.getPrimaryStage());
        
        // Créer le contenu
        VBox content = createContent(consultation);
        getDialogPane().setContent(content);
        
        // Taille de la fenêtre
        getDialogPane().setPrefWidth(700);
        getDialogPane().setPrefHeight(600);
    }

    private VBox createContent(Consultation consultation) {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");

        // Section Informations Générales
        vbox.getChildren().add(createSection("Informations Générales", 
            createInfoGrid(consultation)));

        // Section Diagnostic
        vbox.getChildren().add(createSection("Diagnostic et Observations", 
            createDiagnosticSection(consultation)));

        // Section Prescription
        vbox.getChildren().add(createSection("Prescription", 
            createPrescriptionSection(consultation)));

        // Section Traitements
        if (consultation.getId() != null) {
            vbox.getChildren().add(createSection("Médicaments Prescrits", 
                createTraitementsSection(consultation.getId())));
        }

        // Section Examens
        if (consultation.getExamenesDemandes() != null && 
            !consultation.getExamenesDemandes().trim().isEmpty()) {
            vbox.getChildren().add(createSection("Examens Demandés", 
                createExamensSection(consultation)));
        }

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        
        VBox container = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return container;
    }

    private VBox createSection(String title, Region content) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 8; -fx-padding: 15;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        section.getChildren().addAll(titleLabel, new Separator(), content);
        
        return section;
    }

    private GridPane createInfoGrid(Consultation consultation) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        int row = 0;

        // Date
        addInfoRow(grid, row++, "Date:", 
            consultation.getDateConsultation() != null ? 
            consultation.getDateConsultation().format(DATE_FORMATTER) : "N/A");

        // Patient
        if (consultation.getRendezVous() != null && 
            consultation.getRendezVous().getPatient() != null) {
            String patient = consultation.getRendezVous().getPatient().getNom() + " " +
                           consultation.getRendezVous().getPatient().getPrenom();
            addInfoRow(grid, row++, "Patient:", patient);
            addInfoRow(grid, row++, "CIN:", 
                consultation.getRendezVous().getPatient().getCin());
        }

        // Médecin
        if (consultation.getRendezVous() != null && 
            consultation.getRendezVous().getMedecin() != null) {
            String medecin = "Dr. " + consultation.getRendezVous().getMedecin().getNom() +
                           " " + consultation.getRendezVous().getMedecin().getPrenom();
            addInfoRow(grid, row++, "Médecin:", medecin);
            addInfoRow(grid, row++, "Spécialité:", 
                consultation.getRendezVous().getMedecin().getSpecialite());
        }

        // Tarif
        addInfoRow(grid, row++, "Tarif:", 
            consultation.getTarifConsultation() + " MAD");

        return grid;
    }

    private VBox createDiagnosticSection(Consultation consultation) {
        VBox vbox = new VBox(10);

        // Symptômes
        if (consultation.getSymptomes() != null && 
            !consultation.getSymptomes().trim().isEmpty()) {
            Label sympLabel = new Label("Symptômes:");
            sympLabel.setStyle("-fx-font-weight: bold;");
            
            TextArea sympArea = new TextArea(consultation.getSymptomes());
            sympArea.setWrapText(true);
            sympArea.setEditable(false);
            sympArea.setPrefRowCount(3);
            sympArea.setStyle("-fx-background-color: white;");
            
            vbox.getChildren().addAll(sympLabel, sympArea);
        }

        // Diagnostic
        Label diagLabel = new Label("Diagnostic:");
        diagLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #F44336;");
        
        TextArea diagArea = new TextArea(consultation.getDiagnostic());
        diagArea.setWrapText(true);
        diagArea.setEditable(false);
        diagArea.setPrefRowCount(2);
        diagArea.setStyle("-fx-background-color: white; -fx-font-weight: bold;");
        
        vbox.getChildren().addAll(diagLabel, diagArea);

        // Observations
        if (consultation.getObservations() != null && 
            !consultation.getObservations().trim().isEmpty()) {
            Label obsLabel = new Label("Observations:");
            obsLabel.setStyle("-fx-font-weight: bold;");
            
            TextArea obsArea = new TextArea(consultation.getObservations());
            obsArea.setWrapText(true);
            obsArea.setEditable(false);
            obsArea.setPrefRowCount(3);
            obsArea.setStyle("-fx-background-color: white;");
            
            vbox.getChildren().addAll(obsLabel, obsArea);
        }

        return vbox;
    }

    private VBox createPrescriptionSection(Consultation consultation) {
        VBox vbox = new VBox(10);

        if (consultation.getPrescription() != null && 
            !consultation.getPrescription().trim().isEmpty()) {
            TextArea prescArea = new TextArea(consultation.getPrescription());
            prescArea.setWrapText(true);
            prescArea.setEditable(false);
            prescArea.setPrefRowCount(4);
            prescArea.setStyle("-fx-background-color: white; -fx-font-family: monospace;");
            
            vbox.getChildren().add(prescArea);
        } else {
            Label noPresc = new Label("Aucune prescription");
            noPresc.setStyle("-fx-text-fill: #757575; -fx-font-style: italic;");
            vbox.getChildren().add(noPresc);
        }

        return vbox;
    }

    private VBox createTraitementsSection(Long consultationId) {
        VBox vbox = new VBox(10);

        try {
            List<Traitement> traitements = traitementService.getAll().stream()
                .filter(t -> t.getConsultation() != null && 
                           consultationId.equals(t.getConsultation().getId()))
                .toList();

            if (traitements.isEmpty()) {
                Label noTrait = new Label("Aucun médicament prescrit");
                noTrait.setStyle("-fx-text-fill: #757575; -fx-font-style: italic;");
                vbox.getChildren().add(noTrait);
            } else {
                for (Traitement t : traitements) {
                    vbox.getChildren().add(createTraitementCard(t));
                }
            }
        } catch (Exception e) {
            Label error = new Label("Erreur lors du chargement des traitements");
            error.setStyle("-fx-text-fill: #F44336;");
            vbox.getChildren().add(error);
        }

        return vbox;
    }

    private HBox createTraitementCard(Traitement traitement) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; " +
                     "-fx-border-color: #E0E0E0; -fx-border-width: 1; " +
                     "-fx-border-radius: 5; -fx-background-radius: 5;");

        VBox info = new VBox(5);
        
        Label medName = new Label(traitement.getMedicament().getNomCommercial());
        medName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label posologie = new Label(traitement.getPosologie());
        posologie.setStyle("-fx-text-fill: #2196F3;");
        
        Label duree = new Label("Durée: " + traitement.getDureeTraitement() + " jours");
        duree.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
        
        Label quantite = new Label("Quantité: " + traitement.getQuantite());
        quantite.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");

        info.getChildren().addAll(medName, posologie, duree, quantite);

        if (traitement.getInstructions() != null && 
            !traitement.getInstructions().trim().isEmpty()) {
            Label instructions = new Label("⚠ " + traitement.getInstructions());
            instructions.setWrapText(true);
            instructions.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 11px;");
            info.getChildren().add(instructions);
        }

        card.getChildren().add(info);
        HBox.setHgrow(info, Priority.ALWAYS);

        return card;
    }

    private VBox createExamensSection(Consultation consultation) {
        VBox vbox = new VBox(10);

        TextArea examArea = new TextArea(consultation.getExamenesDemandes());
        examArea.setWrapText(true);
        examArea.setEditable(false);
        examArea.setPrefRowCount(3);
        examArea.setStyle("-fx-background-color: white;");

        vbox.getChildren().add(examArea);

        return vbox;
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        Label lblValue = new Label(value != null ? value : "N/A");
        lblValue.setStyle("-fx-text-fill: #333;");
        
        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }
}