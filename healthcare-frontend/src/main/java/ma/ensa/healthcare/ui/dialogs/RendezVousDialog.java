package ma.ensa.healthcare.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ma.ensa.healthcare.model.*;
import ma.ensa.healthcare.model.enums.StatutRendezVous;
import ma.ensa.healthcare.service.*;

import java.time.*;
import java.util.List;

public class RendezVousDialog extends Dialog<RendezVous> {

    private ComboBox<Patient> cmbPatient;
    private ComboBox<Medecin> cmbMedecin;
    private DatePicker dpDate;
    private Spinner<Integer> spinnerHeure;
    private Spinner<Integer> spinnerMinute;
    private TextArea txtMotif;
    private ComboBox<StatutRendezVous> cmbStatut;

    private final PatientService patientService = new PatientService();
    private final MedecinService medecinService = new MedecinService();
    private RendezVous rdvToEdit;

    public RendezVousDialog() {
        this(null);
    }

    public RendezVousDialog(RendezVous rdv) {
        this.rdvToEdit = rdv;
        
        setTitle(rdv == null ? "Nouveau Rendez-vous" : "Modifier Rendez-vous");
        setHeaderText("Planifier un rendez-vous médical");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = createForm();
        getDialogPane().setContent(grid);

        if (rdv != null) {
            fillForm(rdv);
        }

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateForm()) {
                event.consume();
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createRendezVousFromForm();
            }
            return null;
        });
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setPrefWidth(500);

        int row = 0;

        // Patient
        grid.add(new Label("Patient *:"), 0, row);
        cmbPatient = new ComboBox<>();
        cmbPatient.setPromptText("Sélectionner un patient");
        cmbPatient.setPrefWidth(300);
        loadPatients();
        grid.add(cmbPatient, 1, row++);

        // Médecin
        grid.add(new Label("Médecin *:"), 0, row);
        cmbMedecin = new ComboBox<>();
        cmbMedecin.setPromptText("Sélectionner un médecin");
        cmbMedecin.setPrefWidth(300);
        loadMedecins();
        grid.add(cmbMedecin, 1, row++);

        // Date
        grid.add(new Label("Date *:"), 0, row);
        dpDate = new DatePicker();
        dpDate.setValue(LocalDate.now());
        dpDate.setPromptText("JJ/MM/AAAA");
        dpDate.setPrefWidth(200);
        grid.add(dpDate, 1, row++);

        // Heure
        grid.add(new Label("Heure *:"), 0, row);
        
        spinnerHeure = new Spinner<>(8, 18, 9);
        spinnerHeure.setEditable(true);
        spinnerHeure.setPrefWidth(80);
        
        spinnerMinute = new Spinner<>(0, 59, 0, 15);
        spinnerMinute.setEditable(true);
        spinnerMinute.setPrefWidth(80);
        
        javafx.scene.layout.HBox hboxTime = new javafx.scene.layout.HBox(10, 
            spinnerHeure, new Label(":"), spinnerMinute);
        grid.add(hboxTime, 1, row++);

        // Motif
        grid.add(new Label("Motif:"), 0, row);
        txtMotif = new TextArea();
        txtMotif.setPromptText("Raison de la consultation...");
        txtMotif.setPrefHeight(80);
        txtMotif.setWrapText(true);
        grid.add(txtMotif, 1, row++);

        // Statut
        grid.add(new Label("Statut *:"), 0, row);
        cmbStatut = new ComboBox<>();
        cmbStatut.getItems().addAll(StatutRendezVous.values());
        cmbStatut.setValue(StatutRendezVous.PLANIFIE);
        cmbStatut.setPrefWidth(200);
        grid.add(cmbStatut, 1, row++);

        return grid;
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            cmbPatient.getItems().setAll(patients);
            
            // Affichage personnalisé
            cmbPatient.setCellFactory(param -> new ListCell<Patient>() {
                @Override
                protected void updateItem(Patient item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNom() + " " + item.getPrenom() + 
                               " - " + item.getCin());
                    }
                }
            });
            cmbPatient.setButtonCell(cmbPatient.getCellFactory().call(null));
            
        } catch (Exception e) {
            showError("Erreur", "Impossible de charger les patients");
        }
    }

    private void loadMedecins() {
        try {
            List<Medecin> medecins = medecinService.getAllMedecins();
            cmbMedecin.getItems().setAll(medecins);
            
            cmbMedecin.setCellFactory(param -> new ListCell<Medecin>() {
                @Override
                protected void updateItem(Medecin item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Dr. " + item.getNom() + " - " + item.getSpecialite());
                    }
                }
            });
            cmbMedecin.setButtonCell(cmbMedecin.getCellFactory().call(null));
            
        } catch (Exception e) {
            showError("Erreur", "Impossible de charger les médecins");
        }
    }

    private void fillForm(RendezVous rdv) {
        cmbPatient.setValue(rdv.getPatient());
        cmbMedecin.setValue(rdv.getMedecin());
        dpDate.setValue(rdv.getDateHeure().toLocalDate());
        spinnerHeure.getValueFactory().setValue(rdv.getDateHeure().getHour());
        spinnerMinute.getValueFactory().setValue(rdv.getDateHeure().getMinute());
        txtMotif.setText(rdv.getMotif());
        cmbStatut.setValue(rdv.getStatut());
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (cmbPatient.getValue() == null) {
            errors.append("• Veuillez sélectionner un patient\n");
        }
        if (cmbMedecin.getValue() == null) {
            errors.append("• Veuillez sélectionner un médecin\n");
        }
        if (dpDate.getValue() == null) {
            errors.append("• Veuillez sélectionner une date\n");
        } else if (dpDate.getValue().isBefore(LocalDate.now())) {
            errors.append("• La date ne peut pas être dans le passé\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Veuillez corriger les erreurs :");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private RendezVous createRendezVousFromForm() {
        LocalDateTime dateTime = LocalDateTime.of(
            dpDate.getValue(),
            LocalTime.of(spinnerHeure.getValue(), spinnerMinute.getValue())
        );

        RendezVous.RendezVousBuilder builder = RendezVous.builder()
                .patient(cmbPatient.getValue())
                .medecin(cmbMedecin.getValue())
                .dateHeure(dateTime)
                .motif(txtMotif.getText().trim())
                .statut(cmbStatut.getValue());

        if (rdvToEdit != null) {
            builder.id(rdvToEdit.getId());
        }

        return builder.build();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}