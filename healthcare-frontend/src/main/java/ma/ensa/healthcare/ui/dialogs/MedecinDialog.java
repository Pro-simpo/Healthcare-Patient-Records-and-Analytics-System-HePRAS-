package ma.ensa.healthcare.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ma.ensa.healthcare.dao.impl.DepartementDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IDepartementDAO;
import ma.ensa.healthcare.model.Departement;
import ma.ensa.healthcare.model.Medecin;
import ma.ensa.healthcare.validation.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialogue de création/modification d'un médecin
 */
public class MedecinDialog extends Dialog<Medecin> {
    
    private static final Logger logger = LoggerFactory.getLogger(MedecinDialog.class);
    
    // Service
    private final IDepartementDAO departementDAO;
    
    // Composants UI
    private TextField numeroOrdreField;
    private TextField nomField;
    private TextField prenomField;
    private TextField specialiteField;
    private TextField telephoneField;
    private TextField emailField;
    private DatePicker dateEmbauchePicker;
    private ComboBox<Departement> departementComboBox;
    
    // Données
    private Medecin medecin;
    private boolean isEditMode;

    /**
     * Constructeur pour création
     */
    public MedecinDialog(Stage owner) {
        this(owner, null);
    }

    /**
     * Constructeur pour modification
     */
    public MedecinDialog(Stage owner, Medecin medecin) {
        this.departementDAO = new DepartementDAOImpl();
        this.medecin = medecin;
        this.isEditMode = (medecin != null);
        
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(isEditMode ? "Modifier le médecin" : "Nouveau médecin");
        setHeaderText(isEditMode ? "Modification des informations du médecin" : "Ajout d'un nouveau médecin");
        
        // Boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Contenu
        getDialogPane().setContent(createContent());
        
        // Largeur du dialogue
        getDialogPane().setPrefWidth(550);
        
        // Chargement des données si mode édition
        if (isEditMode) {
            loadMedecinData();
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
                return buildMedecin();
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
        
        // ========== SECTION 1: IDENTIFICATION ==========
        VBox identificationSection = createSection("Identification");
        GridPane identGrid = new GridPane();
        identGrid.setHgap(15);
        identGrid.setVgap(10);
        
        // N° Ordre
        identGrid.add(new Label("N° Ordre: *"), 0, 0);
        numeroOrdreField = new TextField();
        numeroOrdreField.setPromptText("CARD12345");
        identGrid.add(numeroOrdreField, 1, 0);
        
        // Nom
        identGrid.add(new Label("Nom: *"), 0, 1);
        nomField = new TextField();
        nomField.setPromptText("BENJELLOUN");
        identGrid.add(nomField, 1, 1);
        
        // Prénom
        identGrid.add(new Label("Prénom: *"), 0, 2);
        prenomField = new TextField();
        prenomField.setPromptText("Karim");
        identGrid.add(prenomField, 1, 2);
        
        // Spécialité
        identGrid.add(new Label("Spécialité: *"), 0, 3);
        specialiteField = new TextField();
        specialiteField.setPromptText("Cardiologie");
        identGrid.add(specialiteField, 1, 3);
        
        identificationSection.getChildren().add(identGrid);
        
        // ========== SECTION 2: CONTACT ==========
        VBox contactSection = createSection("Coordonnées");
        GridPane contactGrid = new GridPane();
        contactGrid.setHgap(15);
        contactGrid.setVgap(10);
        
        // Téléphone
        contactGrid.add(new Label("Téléphone:"), 0, 0);
        telephoneField = new TextField();
        telephoneField.setPromptText("0612345678");
        contactGrid.add(telephoneField, 1, 0);
        
        // Email
        contactGrid.add(new Label("Email:"), 0, 1);
        emailField = new TextField();
        emailField.setPromptText("medecin@hospital.ma");
        contactGrid.add(emailField, 1, 1);
        
        contactSection.getChildren().add(contactGrid);
        
        // ========== SECTION 3: AFFECTATION ==========
        VBox affectationSection = createSection("Affectation");
        GridPane affectGrid = new GridPane();
        affectGrid.setHgap(15);
        affectGrid.setVgap(10);
        
        // Date embauche
        affectGrid.add(new Label("Date embauche: *"), 0, 0);
        dateEmbauchePicker = new DatePicker(LocalDate.now());
        dateEmbauchePicker.setMaxWidth(200);
        affectGrid.add(dateEmbauchePicker, 1, 0);
        
        // Département
        affectGrid.add(new Label("Département: *"), 0, 1);
        departementComboBox = new ComboBox<>();
        departementComboBox.setPromptText("Sélectionner un département");
        departementComboBox.setMaxWidth(Double.MAX_VALUE);
        departementComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Departement dept, boolean empty) {
                super.updateItem(dept, empty);
                if (empty || dept == null) {
                    setText(null);
                } else {
                    setText(dept.getNomDepartement());
                }
            }
        });
        departementComboBox.setButtonCell(departementComboBox.getCellFactory().call(null));
        
        // Charger les départements
        loadDepartements();
        
        affectGrid.add(departementComboBox, 1, 1);
        
        affectationSection.getChildren().add(affectGrid);
        
        // ========== AIDE ==========
        VBox aideSection = new VBox(5);
        aideSection.setPadding(new Insets(10, 0, 0, 0));
        aideSection.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label aideTitle = new Label("💡 Informations");
        aideTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1976D2;");
        
        Label aideText = new Label(
            "• N° Ordre: Identifiant unique du médecin (obligatoire)\n" +
            "• Spécialité: Domaine médical (Cardiologie, Pédiatrie, etc.)\n" +
            "• Email: Format valide attendu (exemple@domain.com)\n" +
            "• Téléphone: Format marocain (06XXXXXXXX ou +212XXXXXXXXX)"
        );
        aideText.setWrapText(true);
        aideText.setStyle("-fx-font-size: 11px; -fx-text-fill: #424242;");
        
        aideSection.getChildren().addAll(aideTitle, aideText);
        
        // Note obligatoire
        Label noteLabel = new Label("* Champs obligatoires");
        noteLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        // Assemblage
        content.getChildren().addAll(
            identificationSection,
            contactSection,
            affectationSection,
            aideSection,
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
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1976D2;");
        section.getChildren().add(titleLabel);
        return section;
    }

    /**
     * Charge les départements disponibles
     */
    private void loadDepartements() {
        try {
            List<Departement> departements = departementDAO.findAll();
            departementComboBox.getItems().setAll(departements);
            
            if (departements.isEmpty()) {
                departementComboBox.setPromptText("Aucun département disponible");
                departementComboBox.setDisable(true);
                showWarning("Aucun département n'est configuré. Veuillez en créer un d'abord.");
            }
            
        } catch (Exception e) {
            logger.error("Erreur chargement départements", e);
            showError("Impossible de charger les départements");
        }
    }

    /**
     * Charge les données du médecin (mode édition)
     */
    private void loadMedecinData() {
        if (medecin == null) return;
        
        numeroOrdreField.setText(medecin.getNumeroOrdre());
        nomField.setText(medecin.getNom());
        prenomField.setText(medecin.getPrenom());
        specialiteField.setText(medecin.getSpecialite());
        telephoneField.setText(medecin.getTelephone());
        emailField.setText(medecin.getEmail());
        dateEmbauchePicker.setValue(medecin.getDateEmbauche());
        departementComboBox.setValue(medecin.getDepartement());
    }

    /**
     * Valide les champs
     */
    private boolean validateInput() {
        List<String> errors = new ArrayList<>();
        
        // N° Ordre obligatoire
        if (!ValidationUtils.isNotEmpty(numeroOrdreField.getText())) {
            errors.add("Le numéro d'ordre est obligatoire");
        }
        
        // Nom obligatoire
        if (!ValidationUtils.isNotEmpty(nomField.getText())) {
            errors.add("Le nom est obligatoire");
        }
        
        // Prénom obligatoire
        if (!ValidationUtils.isNotEmpty(prenomField.getText())) {
            errors.add("Le prénom est obligatoire");
        }
        
        // Spécialité obligatoire
        if (!ValidationUtils.isNotEmpty(specialiteField.getText())) {
            errors.add("La spécialité est obligatoire");
        }
        
        // Email valide (si fourni)
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            errors.add("Format d'email invalide");
        }
        
        // Téléphone valide (si fourni)
        String telephone = telephoneField.getText().trim();
        if (!telephone.isEmpty() && !ValidationUtils.isValidTelephone(telephone)) {
            errors.add("Format de téléphone invalide (attendu: 0612345678 ou +212612345678)");
        }
        
        // Date embauche obligatoire
        if (dateEmbauchePicker.getValue() == null) {
            errors.add("La date d'embauche est obligatoire");
        } else if (dateEmbauchePicker.getValue().isAfter(LocalDate.now())) {
            errors.add("La date d'embauche ne peut pas être dans le futur");
        }
        
        // Département obligatoire
        if (departementComboBox.getValue() == null) {
            errors.add("Le département est obligatoire");
        }
        
        if (!errors.isEmpty()) {
            showError(String.join("\n", errors));
            return false;
        }
        
        return true;
    }

    /**
     * Construit l'objet Medecin
     */
    private Medecin buildMedecin() {
        Medecin m = isEditMode ? medecin : new Medecin();
        
        m.setNumeroOrdre(numeroOrdreField.getText().trim().toUpperCase());
        m.setNom(nomField.getText().trim().toUpperCase());
        m.setPrenom(prenomField.getText().trim());
        m.setSpecialite(specialiteField.getText().trim());
        m.setTelephone(telephoneField.getText().trim());
        m.setEmail(emailField.getText().trim().toLowerCase());
        m.setDateEmbauche(dateEmbauchePicker.getValue());
        m.setDepartement(departementComboBox.getValue());
        
        return m;
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

    /**
     * Affiche un avertissement
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(getOwner());
        alert.showAndWait();
    }
}