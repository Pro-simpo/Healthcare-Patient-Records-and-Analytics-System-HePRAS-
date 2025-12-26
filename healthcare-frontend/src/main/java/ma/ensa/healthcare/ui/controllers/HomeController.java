package ma.ensa.healthcare.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.service.*;
import ma.ensa.healthcare.ui.dialogs.PatientDialog;
import ma.ensa.healthcare.ui.dialogs.RendezVousDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    // KPI Labels
    @FXML private Label lblTotalPatients;
    @FXML private Label lblRdvToday;
    @FXML private Label lblConsultations;
    @FXML private Label lblRevenus;

    // Table Prochains RDV
    @FXML private TableView<RendezVous> tableProchainRdv;
    @FXML private TableColumn<RendezVous, String> colHeure;
    @FXML private TableColumn<RendezVous, String> colPatient;
    @FXML private TableColumn<RendezVous, String> colMedecin;
    @FXML private TableColumn<RendezVous, String> colMotif;
    @FXML private TableColumn<RendezVous, String> colStatut;

    // Lists
    @FXML private ListView<String> listFacturesImpayees;
    @FXML private ListView<String> listAlertesMedicaments;

    // Services
    private final PatientService patientService = new PatientService();
    private final RendezVousService rdvService = new RendezVousService();
    private final FacturationService facturationService = new FacturationService();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardData();
    }

    /**
     * Configure les colonnes du TableView
     */
    private void setupTableColumns() {
        // ✅ CORRECTION : Utiliser heureDebut au lieu de dateHeure
        colHeure.setCellValueFactory(cellData -> {
            LocalDateTime heureDebut = cellData.getValue().getHeureDebut();
            String time = heureDebut != null ? heureDebut.format(TIME_FORMATTER) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(time);
        });

        colPatient.setCellValueFactory(cellData -> {
            RendezVous rdv = cellData.getValue();
            if (rdv.getPatient() != null) {
                String nom = rdv.getPatient().getNom() + " " + rdv.getPatient().getPrenom();
                return new javafx.beans.property.SimpleStringProperty(nom);
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        colMedecin.setCellValueFactory(cellData -> {
            RendezVous rdv = cellData.getValue();
            if (rdv.getMedecin() != null) {
                String nom = "Dr. " + rdv.getMedecin().getNom();
                return new javafx.beans.property.SimpleStringProperty(nom);
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));

        colStatut.setCellValueFactory(cellData -> {
            String statut = cellData.getValue().getStatut() != null ? 
                           cellData.getValue().getStatut().name() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(statut);
        });

        // Style personnalisé pour la colonne Statut
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
                        case "CONFIRME":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "EN_ATTENTE":
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        case "PLANIFIE":
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                            break;
                        case "ANNULE":
                            setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                            break;
                        case "TERMINE":
                            setStyle("-fx-text-fill: #9E9E9E; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #757575;");
                    }
                }
            }
        });
    }

    /**
     * Charge toutes les données du dashboard
     */
    private void loadDashboardData() {
        try {
            loadKPIs();
            loadProchainRdv();
            loadFacturesImpayees();
            loadAlertesMedicaments();
            
            logger.info("Dashboard chargé avec succès");
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du dashboard", e);
            showError("Erreur", "Impossible de charger les données du dashboard");
        }
    }

    /**
     * Charge les KPIs (Indicateurs)
     */
    private void loadKPIs() {
        try {
            // Total Patients
            List<Patient> patients = patientService.getAllPatients();
            lblTotalPatients.setText(String.valueOf(patients.size()));

            // ✅ CORRECTION : Utiliser la bonne méthode
            List<RendezVous> allRdv = rdvService.getAllRendezVous(); // ou findAll() ou listerRendezVous()
            LocalDate today = LocalDate.now();
            
            long rdvToday = allRdv.stream()
                .filter(rdv -> rdv.getDateRdv() != null && rdv.getDateRdv().isEqual(today))
                .count();
            
            lblRdvToday.setText(String.valueOf(rdvToday));

            // Consultations (vous pouvez ajouter un service pour ça plus tard)
            lblConsultations.setText("156");

            // Revenus (vous pouvez utiliser FacturationService.getRevenusPeriode())
            lblRevenus.setText("45,250 MAD");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des KPIs", e);
        }
    }

    /**
     * Charge les prochains rendez-vous
     */
    private void loadProchainRdv() {
        try {
            List<RendezVous> rdvList = rdvService.getAllRendezVous(); // ou findAll() ou listerRendezVous()
            LocalDateTime now = LocalDateTime.now();
            
            // ✅ CORRECTION : Utiliser heureDebut au lieu de getDateHeure()
            List<RendezVous> prochainRdv = rdvList.stream()
                .filter(rdv -> rdv.getHeureDebut() != null && !rdv.getHeureDebut().isBefore(now))
                .sorted((r1, r2) -> r1.getHeureDebut().compareTo(r2.getHeureDebut()))
                .limit(10)
                .toList();

            ObservableList<RendezVous> observableList = FXCollections.observableArrayList(prochainRdv);
            tableProchainRdv.setItems(observableList);

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des rendez-vous", e);
        }
    }

    /**
     * Charge la liste des factures impayées
     */
    private void loadFacturesImpayees() {
        try {
            // TODO: Récupérer les vraies factures depuis le service
            // List<Facture> facturesImpayees = facturationService.findFacturesImpayees();
            
            // Pour l'instant, données fictives
            ObservableList<String> factures = FXCollections.observableArrayList(
                "Facture #1245 - Patient: Alami - 1,200 MAD",
                "Facture #1238 - Patient: Bennani - 850 MAD",
                "Facture #1220 - Patient: El Idrissi - 2,300 MAD"
            );
            listFacturesImpayees.setItems(factures);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des factures impayées", e);
        }
    }

    /**
     * Charge les alertes de stock de médicaments
     */
    private void loadAlertesMedicaments() {
        try {
            // TODO: Récupérer les vrais médicaments en alerte depuis le service
            // List<Medicament> medicamentsAlerte = medicamentService.getMedicamentsEnAlerte();
            
            // Pour l'instant, données fictives
            ObservableList<String> alertes = FXCollections.observableArrayList(
                "Paracétamol - Stock: 45 (Seuil: 100)",
                "Amoxicilline - Stock: 12 (Seuil: 50)",
                "Ibuprofène - Stock: 8 (Seuil: 30)"
            );
            listAlertesMedicaments.setItems(alertes);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des alertes médicaments", e);
        }
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddPatient() {
        PatientDialog dialog = new PatientDialog();
        Optional<Patient> result = dialog.showAndWait();

        result.ifPresent(patient -> {
            try {
                patientService.createPatient(patient);
                showSuccess("Succès", "Patient ajouté avec succès !");
            } catch (Exception e) {
                logger.error("Erreur lors de l'ajout du patient", e);
                showError("Erreur", "Impossible d'ajouter le patient : " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleAddRendezVous() {
        RendezVousDialog dialog = new RendezVousDialog();
        Optional<RendezVous> result = dialog.showAndWait();

        result.ifPresent(rdv -> {
            try {
                rdvService.planifierRendezVous(rdv);
                showSuccess("Succès", "Rendez-vous créé avec succès !");
            } catch (Exception e) {
                logger.error("Erreur lors de la création du RDV", e);
                showError("Erreur", "Impossible de créer le rendez-vous : " + e.getMessage());
            }
        });
    }


    /**
     * Afficher un message de succès
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Rafraîchit toutes les données du dashboard
     */
    @FXML
    public void handleRefresh() {
        loadDashboardData();
    }
}