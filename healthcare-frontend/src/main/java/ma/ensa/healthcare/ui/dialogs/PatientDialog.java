package ma.ensa.healthcare.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.enums.Sexe;

import java.time.LocalDate;

public class PatientDialog extends Dialog<Patient> {

    private TextField txtNom;
    private TextField txtPrenom;
    private TextField txtCin;
    private TextField txtTelephone;
    private TextField txtEmail;
    private TextField txtAdresse;
    private DatePicker dpDateNaissance;
    private ComboBox<Sexe> cmbSexe;
    private TextArea txtAntecedents;

    private Patient patientToEdit;

    /**
     * Constructeur pour ajouter un nouveau patient
     */
    public PatientDialog() {
        this(null);
    }

    /**
     * Constructeur pour modifier un patient existant
     */
    public PatientDialog(Patient patient) {
        this.patientToEdit = patient;
        
        setTitle(patient == null ? "Nouveau Patient" : "Modifier Patient");
        setHeaderText(patient == null ? "Remplissez les informations du patient" : 
                                       "Modifiez les informations du patient");

        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Créer le formulaire
        GridPane grid = createForm();
        getDialogPane().setContent(grid);

        // Remplir les champs si modification
        if (patient != null) {
            fillForm(patient);
        }

        // Validation du bouton Save
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateForm()) {
                event.consume();
            }
        });

        // Convertir le résultat en Patient
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createPatientFromForm();
            }
            return null;
        });
    }

    /**
     * Crée le formulaire
     */
    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");

        int row = 0;

        // Nom
        grid.add(new Label("Nom *:"), 0, row);
        txtNom = new TextField();
        txtNom.setPromptText("Nom de famille");
        grid.add(txtNom, 1, row++);

        // Prénom
        grid.add(new Label("Prénom *:"), 0, row);
        txtPrenom = new TextField();
        txtPrenom.setPromptText("Prénom");
        grid.add(txtPrenom, 1, row++);

        // CIN
        grid.add(new Label("CIN *:"), 0, row);
        txtCin = new TextField();
        txtCin.setPromptText("Ex: AB123456");
        grid.add(txtCin, 1, row++);

        // Sexe
        grid.add(new Label("Sexe *:"), 0, row);
        cmbSexe = new ComboBox<>();
        cmbSexe.getItems().addAll(Sexe.values());
        cmbSexe.setPromptText("Sélectionner");
        cmbSexe.setPrefWidth(200);
        grid.add(cmbSexe, 1, row++);

        // Date de naissance
        grid.add(new Label("Date de naissance *:"), 0, row);
        dpDateNaissance = new DatePicker();
        dpDateNaissance.setPromptText("JJ/MM/AAAA");
        dpDateNaissance.setPrefWidth(200);
        grid.add(dpDateNaissance, 1, row++);

        // Téléphone
        grid.add(new Label("Téléphone:"), 0, row);
        txtTelephone = new TextField();
        txtTelephone.setPromptText("Ex: 0612345678");
        grid.add(txtTelephone, 1, row++);

        // Email
        grid.add(new Label("Email:"), 0, row);
        txtEmail = new TextField();
        txtEmail.setPromptText("exemple@email.com");
        grid.add(txtEmail, 1, row++);

        // Adresse
        grid.add(new Label("Adresse:"), 0, row);
        txtAdresse = new TextField();
        txtAdresse.setPromptText("Adresse complète");
        grid.add(txtAdresse, 1, row++);

        // Antécédents médicaux
        grid.add(new Label("Antécédents:"), 0, row);
        txtAntecedents = new TextArea();
        txtAntecedents.setPromptText("Antécédents médicaux, allergies...");
        txtAntecedents.setPrefHeight(80);
        txtAntecedents.setWrapText(true);
        grid.add(txtAntecedents, 1, row++);

        return grid;
    }

    /**
     * Remplit le formulaire avec les données du patient
     */
    private void fillForm(Patient patient) {
        txtNom.setText(patient.getNom());
        txtPrenom.setText(patient.getPrenom());
        txtCin.setText(patient.getCin());
        txtTelephone.setText(patient.getTelephone());
        txtEmail.setText(patient.getEmail());
        txtAdresse.setText(patient.getAdresse());
        dpDateNaissance.setValue(patient.getDateNaissance());
        cmbSexe.setValue(patient.getSexe());
        txtAntecedents.setText(patient.getAntecedentsMedicaux());
    }

    /**
     * Valide le formulaire
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtNom.getText().trim().isEmpty()) {
            errors.append("• Le nom est obligatoire\n");
        }
        if (txtPrenom.getText().trim().isEmpty()) {
            errors.append("• Le prénom est obligatoire\n");
        }
        if (txtCin.getText().trim().isEmpty()) {
            errors.append("• Le CIN est obligatoire\n");
        }
        if (cmbSexe.getValue() == null) {
            errors.append("• Le sexe est obligatoire\n");
        }
        if (dpDateNaissance.getValue() == null) {
            errors.append("• La date de naissance est obligatoire\n");
        } else if (dpDateNaissance.getValue().isAfter(LocalDate.now())) {
            errors.append("• La date de naissance ne peut pas être future\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes :");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    /**
     * Crée un objet Patient à partir du formulaire
     */
    private Patient createPatientFromForm() {
        Patient.PatientBuilder builder = Patient.builder()
                .nom(txtNom.getText().trim())
                .prenom(txtPrenom.getText().trim())
                .cin(txtCin.getText().trim())
                .sexe(cmbSexe.getValue())
                .dateNaissance(dpDateNaissance.getValue())
                .telephone(txtTelephone.getText().trim())
                .email(txtEmail.getText().trim())
                .adresse(txtAdresse.getText().trim())
                .antecedentsMedicaux(txtAntecedents.getText().trim())
                .dateCreation(LocalDate.now());

        // Si modification, conserver l'ID
        if (patientToEdit != null) {
            builder.id(patientToEdit.getId());
        }

        return builder.build();
    }
}