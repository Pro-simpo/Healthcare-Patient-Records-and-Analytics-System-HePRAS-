package ma.ensa.healthcare.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.model.enums.ModePaiement;
import ma.ensa.healthcare.service.PatientService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Dialogue pour enregistrer un paiement
 */
public class PaiementDialog extends Dialog<PaiementDialog.PaiementData> {
    
    private final PatientService patientService = new PatientService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private TextField txtMontant;
    private ComboBox<ModePaiement> cmbMode;
    private Label lblMontantRestant;
    private Label lblInfoFacture;
    private CheckBox chkPaiementComplet;
    
    private final Facture facture;

    public PaiementDialog(Facture facture) {
        this.facture = facture;
        
        setTitle("Enregistrer un Paiement");
        setHeaderText("Facture N°" + facture.getNumeroFacture());
        
        // Boutons
        ButtonType enregistrerButton = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(enregistrerButton, ButtonType.CANCEL);
        
        // Créer le formulaire
        VBox content = createForm();
        getDialogPane().setContent(content);
        
        // Validation
        Button btnEnregistrer = (Button) getDialogPane().lookupButton(enregistrerButton);
        btnEnregistrer.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateForm()) {
                event.consume();
            }
        });
        
        // Convertir le résultat
        setResultConverter(dialogButton -> {
            if (dialogButton == enregistrerButton) {
                return new PaiementData(
                    new BigDecimal(txtMontant.getText().trim()),
                    cmbMode.getValue()
                );
            }
            return null;
        });
        
        getDialogPane().setPrefWidth(500);
    }

    private VBox createForm() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");

        // Informations facture
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 15; -fx-background-radius: 8;");
        
        lblInfoFacture = new Label();
        lblInfoFacture.setWrapText(true);
        lblInfoFacture.setStyle("-fx-font-size: 13px;");
        updateInfoLabel();
        
        lblMontantRestant = new Label();
        lblMontantRestant.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #F44336;");
        updateMontantRestant();
        
        infoBox.getChildren().addAll(lblInfoFacture, lblMontantRestant);
        vbox.getChildren().add(infoBox);

        // Formulaire
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        
        int row = 0;

        // Checkbox paiement complet
        chkPaiementComplet = new CheckBox("Payer le solde complet");
        chkPaiementComplet.setStyle("-fx-font-weight: bold;");
        chkPaiementComplet.setSelected(true);
        chkPaiementComplet.setOnAction(e -> handlePaiementCompletChange());
        grid.add(chkPaiementComplet, 0, row++, 2, 1);

        // Montant
        grid.add(new Label("Montant (MAD):"), 0, row);
        txtMontant = new TextField();
        txtMontant.setPromptText("0.00");
        txtMontant.setPrefWidth(200);
        txtMontant.setText(facture.getMontantRestant().toString());
        txtMontant.setEditable(false); // Désactivé par défaut si paiement complet
        grid.add(txtMontant, 1, row++);

        // Mode de paiement
        grid.add(new Label("Mode de paiement:"), 0, row);
        cmbMode = new ComboBox<>();
        cmbMode.getItems().addAll(ModePaiement.values());
        cmbMode.setValue(ModePaiement.ESPECES);
        cmbMode.setPrefWidth(200);
        grid.add(cmbMode, 1, row++);

        vbox.getChildren().add(grid);

        // Note
        Label note = new Label("💡 Le statut de la facture sera mis à jour automatiquement.");
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px; -fx-font-style: italic;");
        vbox.getChildren().add(note);

        return vbox;
    }

    private void updateInfoLabel() {
        String patient = "N/A";
        if (facture.getIdPatient() != 0) {
            patient = patientService.getPatientById(facture.getIdPatient()).getNom() + " " + patientService.getPatientById(facture.getIdPatient()).getPrenom();
        }
        
        String info = String.format(
            "Patient: %s\n" +
            "Date: %s\n" +
            "Montant total: %.2f MAD\n" +
            "Déjà payé: %.2f MAD",
            patient,
            facture.getDateFacture().format(DATE_FORMATTER),
            facture.getMontantTotal(),
            facture.getMontantPaye()
        );
        
        lblInfoFacture.setText(info);
    }

    private void updateMontantRestant() {
        lblMontantRestant.setText(String.format("Reste à payer: %.2f MAD", 
            facture.getMontantRestant()));
    }

    private void handlePaiementCompletChange() {
        if (chkPaiementComplet.isSelected()) {
            txtMontant.setText(facture.getMontantRestant().toString());
            txtMontant.setEditable(false);
        } else {
            txtMontant.setEditable(true);
            txtMontant.requestFocus();
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        // Validation montant
        String montantStr = txtMontant.getText().trim();
        if (montantStr.isEmpty()) {
            errors.append("• Le montant est obligatoire\n");
        } else {
            try {
                BigDecimal montant = new BigDecimal(montantStr);
                
                if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.append("• Le montant doit être supérieur à 0\n");
                }
                
                if (montant.compareTo(facture.getMontantRestant()) > 0) {
                    errors.append("• Le montant ne peut pas dépasser le reste à payer\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Montant invalide\n");
            }
        }

        // Validation mode
        if (cmbMode.getValue() == null) {
            errors.append("• Veuillez sélectionner un mode de paiement\n");
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

    /**
     * Classe pour transporter les données du paiement
     */
    public static class PaiementData {
        private final BigDecimal montant;
        private final ModePaiement mode;

        public PaiementData(BigDecimal montant, ModePaiement mode) {
            this.montant = montant;
            this.mode = mode;
        }

        public BigDecimal getMontant() {
            return montant;
        }

        public ModePaiement getMode() {
            return mode;
        }
    }
}