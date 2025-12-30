package ma.ensa.healthcare.ui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.model.enums.StatutRendezVous;
import ma.ensa.healthcare.model.Consultation;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.service.*;
import ma.ensa.healthcare.ui.dialogs.ConsultationDialog;
import ma.ensa.healthcare.ui.dialogs.PatientDialog;
import ma.ensa.healthcare.ui.dialogs.RendezVousDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.animation.Interpolator;


public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    // KPI Labels
    @FXML private Label lblTotalPatients;
    @FXML private Label lblRdvToday;
    @FXML private Label lblRdvTodayDetails;
    @FXML private Label lblConsultations;
    @FXML private Label lblConsultationsDetails;
    @FXML private Label lblRevenus;
    @FXML private Label lblRevenusDetails;
    @FXML private GridPane gridKpiCards;

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
    private final ConsultationService consultationService = new ConsultationService();
    private final FacturationService facturationService = new FacturationService();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardData();

        // Parcourir tous les enfants du VBox
        for (Node node : gridKpiCards.getChildren()) {
            if (node.getStyleClass().contains("homeCard")) {
                // Créer une transition de scale
                ScaleTransition st = new ScaleTransition(Duration.seconds(0.2), node);
                st.setToX(1.02);
                st.setToY(1.02);
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
            if (rdv.getIdPatient() != 0) {
                String nom = patientService.getPatientById(rdv.getIdPatient()).getNom() + " " + patientService.getPatientById(rdv.getIdPatient()).getPrenom();
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
            long countPlanifies = allRdv.stream()
                .filter(r -> r.getStatut() == StatutRendezVous.PLANIFIE && r.getDateRdv().isEqual(today))
                .count();
            
            long countConfirmes = allRdv.stream()
                .filter(r -> r.getStatut() == StatutRendezVous.CONFIRME && r.getDateRdv().isEqual(today))
                .count();
            
            lblRdvTodayDetails.setText(countConfirmes + " confirmés, " + countPlanifies + " planifiés");
            lblRdvToday.setText(String.valueOf(rdvToday));

            // Consultations (vous pouvez ajouter un service pour ça plus tard)
            List<Consultation> allConsultations = consultationService.listerToutesConsultations();
            LocalDate startOfMonth = today.withDayOfMonth(1);
            long countToday = allConsultations.stream()
                .filter(c -> c.getDateConsultation().equals(today))
                .count();
            
            long countMonth = allConsultations.stream()
                .filter(c -> !c.getDateConsultation().isBefore(startOfMonth))
                .count();
            lblConsultations.setText(String.valueOf(countMonth));
            lblConsultationsDetails.setText(countToday + " aujourd'hui, " + countMonth + " ce mois");

            // Revenus (vous pouvez utiliser FacturationService.getRevenusPeriode())
            LocalDate RevenusStartOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(
                LocalDate.now().lengthOfMonth());
            
            BigDecimal revenusMois = facturationService.getRevenusPeriode(RevenusStartOfMonth, endOfMonth);
            BigDecimal totalImpaye = facturationService.getTotalImpaye();
            lblRevenus.setText(revenusMois.toString() + " MAD");
            lblRevenusDetails.setText("Factures à encaisser: " + totalImpaye.toString() + " MAD");

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
            ObservableList<String> factures = FXCollections.observableArrayList();
            List<Facture> facturesList = facturationService.getToutesLesFactures();
            List<Facture> filtered = facturesList.stream()
                .filter(f -> {
                    boolean statutMatch = "Tous".equals("EN_ATTENTE") || 
                        (f.getStatutPaiement() != null && f.getStatutPaiement().name().equals("EN_ATTENTE"));
                    
                    return statutMatch;
                })
                .toList();
            factures.setAll(filtered.stream()
                .map(f -> "Facture " + f.getNumeroFacture() + " - Patient: " + patientService.getPatientById(f.getIdPatient()).getNom() + " " + patientService.getPatientById(f.getIdPatient()).getPrenom() + " - Montant: " + f.getMontantTotal() + " MAD")
                .toList());
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
        Stage stage = (Stage) tableProchainRdv.getScene().getWindow();
        PatientDialog dialog = new PatientDialog(stage);
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
        Stage stage = (Stage) tableProchainRdv.getScene().getWindow();
        RendezVousDialog dialog = new RendezVousDialog(stage);
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
     * Ajouter une nouvelle consultation
     */
    @FXML
    private void handleAddConsultation() {
        Stage stage = (Stage) tableProchainRdv.getScene().getWindow();
        ConsultationDialog dialog = new ConsultationDialog(stage);
        Optional<Consultation> result = dialog.showAndWait();

        result.ifPresent(consultation -> {
            try {
                consultationService.enregistrerConsultation(consultation);
                showSuccess("Succès", "Consultation ajoutée avec succès !");
            } catch (Exception e) {
                logger.error("Erreur lors de l'ajout de la consultation", e);
                showError("Erreur", "Impossible d'ajouter la consultation : " + e.getMessage());
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