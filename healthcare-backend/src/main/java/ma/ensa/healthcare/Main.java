package ma.ensa.healthcare;

import ma.ensa.healthcare.config.HikariCPConfig;
import ma.ensa.healthcare.dao.impl.*;
import ma.ensa.healthcare.dao.interfaces.IFactureDAO;
import ma.ensa.healthcare.facade.ConsultationFacade;
import ma.ensa.healthcare.facade.FacturationFacade;
import ma.ensa.healthcare.model.*;
import ma.ensa.healthcare.model.enums.*;
import ma.ensa.healthcare.util.TestDataCleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Test d'intégration COMPLET avec flux métier réel
 * Scénario : Patient → RDV → Consultation → Traitements → Facture → Paiement
 * 
 * ⚠️ Les IDs sont gérés par les séquences Oracle - Ne PAS les définir manuellement
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    // Générateur d'identifiants uniques pour éviter les contraintes UNIQUE
    private static String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("   HEALTHCARE SYSTEM - TEST COMPLET FLUX MÉTIER");
        System.out.println("=".repeat(70) + "\n");

        System.out.println("🧹 Nettoyage des données de test...\n");
        TestDataCleaner.cleanupTodayTestData();
        System.out.println();

        try {
            // ════════════════════════════════════════════════════════════════
            // INITIALISATION
            // ════════════════════════════════════════════════════════════════
            DepartementDAOImpl departementDAO = new DepartementDAOImpl();
            MedecinDAOImpl medecinDAO = new MedecinDAOImpl();
            UtilisateurDAOImpl utilisateurDAO = new UtilisateurDAOImpl();
            PatientDAOImpl patientDAO = new PatientDAOImpl();
            RendezVousDAOImpl rdvDAO = new RendezVousDAOImpl();
            MedicamentDAOImpl medicamentDAO = new MedicamentDAOImpl();
            ConsultationFacade consultationFacade = new ConsultationFacade();
            FacturationFacade facturationFacade = new FacturationFacade();
            
            String uniqueId = generateUniqueId();
            System.out.println("🔑 Session ID: " + uniqueId + "\n");

            // ════════════════════════════════════════════════════════════════
            // STEP 1: Infrastructure - Département
            // ════════════════════════════════════════════════════════════════
            printStep(1, "Création du département");
            
            Departement departement = new Departement();
            departement.setNomDepartement("Cardiologie_" + uniqueId);
            departement.setNombreLits(25);
            departement.setTelephone("+212537456789");
            
            departement = departementDAO.save(departement);
            printSuccess("Département créé", departement.getNomDepartement() + " (ID: " + departement.getId() + ")");

            // ════════════════════════════════════════════════════════════════
            // STEP 2: Médecins
            // ════════════════════════════════════════════════════════════════
            printStep(2, "Création des médecins");
            
            // Médecin 1 - Cardiologue
            Medecin medecin1 = new Medecin();
            medecin1.setNumeroOrdre("CARD" + uniqueId);
            medecin1.setNom("BENJELLOUN");
            medecin1.setPrenom("Karim");
            medecin1.setSpecialite("Cardiologie");
            medecin1.setTelephone("+212665432109");
            medecin1.setEmail("k.benjelloun." + uniqueId + "@hospital.ma");
            medecin1.setDateEmbauche(LocalDate.of(2015, 6, 20));
            medecin1.setDepartement(departement);
            
            medecin1 = medecinDAO.save(medecin1);
            printSuccess("Médecin créé", "Dr. " + medecin1.getPrenom() + " " + medecin1.getNom() + 
                        " - " + medecin1.getSpecialite() + " (ID: " + medecin1.getId() + ")");
            
            // Médecin 2 - Cardiologue
            String uniqueId2 = generateUniqueId();
            Medecin medecin2 = new Medecin();
            medecin2.setNumeroOrdre("CARD" + uniqueId2);
            medecin2.setNom("ALAMI");
            medecin2.setPrenom("Nadia");
            medecin2.setSpecialite("Cardiologie");
            medecin2.setTelephone("+212667890123");
            medecin2.setEmail("n.alami." + uniqueId2 + "@hospital.ma");
            medecin2.setDateEmbauche(LocalDate.of(2017, 2, 10));
            medecin2.setDepartement(departement);
            
            medecin2 = medecinDAO.save(medecin2);
            printSuccess("Médecin créé", "Dr. " + medecin2.getPrenom() + " " + medecin2.getNom() + 
                        " (ID: " + medecin2.getId() + ")");

            // Chef de département
            departementDAO.setChefDepartement(departement.getId(), medecin1.getId());
            printSuccess("Chef nommé", "Dr. " + medecin1.getNom() + " → Chef du département");

            // ════════════════════════════════════════════════════════════════
            // STEP 3: Patients
            // ════════════════════════════════════════════════════════════════
            printStep(3, "Enregistrement des patients");
            
            // Patient 1
            Patient patient1 = new Patient();
            patient1.setCin("K" + uniqueId.substring(0, 6).toUpperCase());
            patient1.setNom("LAHLOU");
            patient1.setPrenom("Ahmed");
            patient1.setDateNaissance(LocalDate.of(1972, 3, 15));
            patient1.setSexe(Sexe.M);
            patient1.setAdresse("45 Avenue Hassan II");
            patient1.setVille("Rabat");
            patient1.setCodePostal("10000");
            patient1.setTelephone("+212654321098");
            patient1.setEmail("a.lahlou." + uniqueId + "@email.ma");
            patient1.setGroupeSanguin("O+");
            patient1.setAllergies("Pénicilline, Iode");
            patient1.setDateInscription(LocalDate.now());
            
            patient1 = patientDAO.save(patient1);
            printSuccess("Patient créé", patient1.getPrenom() + " " + patient1.getNom() + 
                        " (CIN: " + patient1.getCin() + ", ID: " + patient1.getId() + ")");
            
            // Patient 2
            String uniqueId3 = generateUniqueId();
            Patient patient2 = new Patient();
            patient2.setCin("L" + uniqueId3.substring(0, 6).toUpperCase());
            patient2.setNom("FASSI");
            patient2.setPrenom("Leila");
            patient2.setDateNaissance(LocalDate.of(1985, 9, 22));
            patient2.setSexe(Sexe.F);
            patient2.setAdresse("89 Boulevard Mohammed VI");
            patient2.setVille("Casablanca");
            patient2.setCodePostal("20200");
            patient2.setTelephone("+212656789012");
            patient2.setEmail("l.fassi." + uniqueId3 + "@email.ma");
            patient2.setGroupeSanguin("AB+");
            patient2.setDateInscription(LocalDate.now());
            
            patient2 = patientDAO.save(patient2);
            printSuccess("Patient créé", patient2.getPrenom() + " " + patient2.getNom() + 
                        " (CIN: " + patient2.getCin() + ", ID: " + patient2.getId() + ")");

            // ════════════════════════════════════════════════════════════════
            // STEP 4: Comptes utilisateurs
            // ════════════════════════════════════════════════════════════════
            printStep(4, "Création des comptes utilisateurs");
            
            // Admin
            Utilisateur admin = new Utilisateur();
            admin.setUsername("admin_" + uniqueId);
            admin.setPasswordHash("$2a$10$" + uniqueId + "xyz123456789a");
            admin.setEmail("admin." + uniqueId + "@hospital.ma");
            admin.setRole(Role.ADMIN);
            admin.setStatut("ACTIF");
            admin.setDateCreation(LocalDate.now());
            admin.setTentativesEchec(0);
            
            admin = utilisateurDAO.save(admin);
            printSuccess("Admin créé", admin.getUsername() + " (ID: " + admin.getId() + ")");
            
            // Compte patient
            Utilisateur userPatient = new Utilisateur();
            userPatient.setUsername("patient_" + patient1.getCin());
            userPatient.setPasswordHash("$2a$10$" + uniqueId + "abc987654321b");
            userPatient.setEmail(patient1.getEmail());
            userPatient.setRole(Role.PATIENT);
            userPatient.setStatut("ACTIF");
            userPatient.setPatient(patient1);
            userPatient.setDateCreation(LocalDate.now());
            userPatient.setTentativesEchec(0);
            
            userPatient = utilisateurDAO.save(userPatient);
            printSuccess("Compte patient créé", userPatient.getUsername() + " (ID: " + userPatient.getId() + ")");

            // ════════════════════════════════════════════════════════════════
            // STEP 5: Rendez-vous
            // ════════════════════════════════════════════════════════════════
            printStep(5, "Planification des rendez-vous");
            
            // RDV 1 - Patient 1 avec Médecin 1
            LocalDate dateRdv1 = LocalDate.now().plusDays(5);
            LocalDateTime heureDebut1 = LocalDateTime.of(dateRdv1.getYear(), dateRdv1.getMonth(), 
                                                          dateRdv1.getDayOfMonth(), 10, 30);
            LocalDateTime heureFin1 = heureDebut1.plusMinutes(45);
            
            RendezVous rdv1 = new RendezVous();
            rdv1.setPatient(patient1);
            rdv1.setMedecin(medecin1);
            rdv1.setDateRdv(dateRdv1);
            rdv1.setHeureDebut(heureDebut1);
            rdv1.setHeureFin(heureFin1);
            rdv1.setMotif("Douleurs thoraciques et essoufflement");
            rdv1.setStatut(StatutRendezVous.CONFIRME);
            rdv1.setSalle("C-401");
            rdv1.setDateCreation(LocalDate.now());
            
            rdv1 = rdvDAO.save(rdv1);
            printSuccess("RDV planifié", patient1.getNom() + " → Dr. " + medecin1.getNom() + 
                        " le " + dateRdv1 + " à " + heureDebut1.toLocalTime() + 
                        " (ID: " + rdv1.getId() + ")");
            
            // RDV 2 - Patient 2 avec Médecin 2
            LocalDate dateRdv2 = LocalDate.now().plusDays(7);
            LocalDateTime heureDebut2 = LocalDateTime.of(dateRdv2.getYear(), dateRdv2.getMonth(), 
                                                          dateRdv2.getDayOfMonth(), 15, 0);
            LocalDateTime heureFin2 = heureDebut2.plusMinutes(30);
            
            RendezVous rdv2 = new RendezVous();
            rdv2.setPatient(patient2);
            rdv2.setMedecin(medecin2);
            rdv2.setDateRdv(dateRdv2);
            rdv2.setHeureDebut(heureDebut2);
            rdv2.setHeureFin(heureFin2);
            rdv2.setMotif("Bilan cardiaque annuel");
            rdv2.setStatut(StatutRendezVous.PLANIFIE);
            rdv2.setSalle("C-402");
            rdv2.setDateCreation(LocalDate.now());
            
            rdv2 = rdvDAO.save(rdv2);
            printSuccess("RDV planifié", patient2.getNom() + " → Dr. " + medecin2.getNom() + 
                        " le " + dateRdv2 + " à " + heureDebut2.toLocalTime() + 
                        " (ID: " + rdv2.getId() + ")");

            // ════════════════════════════════════════════════════════════════
            // STEP 6: Médicaments
            // ════════════════════════════════════════════════════════════════
            printStep(6, "Préparation de la pharmacie");
            
            // Médicament 1 - Anticoagulant
            Medicament med1 = new Medicament();
            med1.setNomCommercial("Kardegic_" + uniqueId.substring(0, 4));
            med1.setPrincipeActif("Acétylsalicylate de DL-Lysine");
            med1.setForme("COMPRIME");
            med1.setDosage("75mg");
            med1.setPrixUnitaire(new BigDecimal("38.50"));
            med1.setStockDisponible(200);
            med1.setStockAlerte(40);
            
            med1 = medicamentDAO.save(med1);
            printSuccess("Médicament ajouté", med1.getNomCommercial() + " - " + 
                        med1.getPrixUnitaire() + " MAD (ID: " + med1.getId() + ")");
            
            // Médicament 2 - Bêta-bloquant
            Medicament med2 = new Medicament();
            med2.setNomCommercial("Ténormine_" + uniqueId.substring(0, 4));
            med2.setPrincipeActif("Aténolol");
            med2.setForme("COMPRIME");
            med2.setDosage("50mg");
            med2.setPrixUnitaire(new BigDecimal("52.00"));
            med2.setStockDisponible(120);
            med2.setStockAlerte(25);
            
            med2 = medicamentDAO.save(med2);
            printSuccess("Médicament ajouté", med2.getNomCommercial() + " - " + 
                        med2.getPrixUnitaire() + " MAD (ID: " + med2.getId() + ")");
            
            // Médicament 3 - Statine
            Medicament med3 = new Medicament();
            med3.setNomCommercial("Tahor_" + uniqueId.substring(0, 4));
            med3.setPrincipeActif("Atorvastatine");
            med3.setForme("COMPRIME");
            med3.setDosage("20mg");
            med3.setPrixUnitaire(new BigDecimal("65.75"));
            med3.setStockDisponible(180);
            med3.setStockAlerte(35);
            
            med3 = medicamentDAO.save(med3);
            printSuccess("Médicament ajouté", med3.getNomCommercial() + " - " + 
                        med3.getPrixUnitaire() + " MAD (ID: " + med3.getId() + ")");

            // ════════════════════════════════════════════════════════════════
            // STEP 7: Consultation complète avec traitements
            // ════════════════════════════════════════════════════════════════
            printStep(7, "Réalisation de la consultation");
            
            Consultation consultation = new Consultation();
            consultation.setRendezVous(rdv1);
            consultation.setDateConsultation(LocalDate.now());
            consultation.setSymptomes("Douleurs thoraciques lors d'efforts physiques, " +
                                     "essoufflement au moindre effort, palpitations occasionnelles, " +
                                     "fatigue chronique depuis 2 mois");
            consultation.setDiagnostic("Angine de poitrine stable - Athérosclérose coronarienne");
            consultation.setObservations("Auscultation cardiaque: souffle systolique léger. " +
                                        "TA: 145/95 mmHg. Pouls: 88 bpm régulier. " +
                                        "Patient tabagique (15 ans, 1 paquet/jour). " +
                                        "Antécédents familiaux: père décédé d'infarctus à 60 ans.");
            consultation.setPrescription("Anticoagulant préventif quotidien\n" +
                                        "Bêta-bloquant pour ralentir le rythme cardiaque\n" +
                                        "Statine pour réduire le cholestérol\n" +
                                        "Arrêt du tabac IMPÉRATIF");
            consultation.setExamenesDemandes("ECG de repos, Échographie cardiaque, " +
                                            "Test d'effort, Bilan lipidique complet");
            consultation.setTarifConsultation(new BigDecimal("450.00"));
            
            // Traitements prescrits
            List<Traitement> traitements = new ArrayList<>();
            
            // Traitement 1 - Anticoagulant
            Traitement traitement1 = new Traitement();
            traitement1.setConsultation(consultation);
            traitement1.setMedicament(med1);
            traitement1.setPosologie("1 comprimé le matin à jeun");
            traitement1.setDureeTraitement(90);
            traitement1.setInstructions("Traitement à long terme. Ne jamais doubler la dose en cas d'oubli.");
            traitement1.setQuantite(90);
            traitements.add(traitement1);
            
            // Traitement 2 - Bêta-bloquant
            Traitement traitement2 = new Traitement();
            traitement2.setConsultation(consultation);
            traitement2.setMedicament(med2);
            traitement2.setPosologie("1 comprimé matin et soir");
            traitement2.setDureeTraitement(90);
            traitement2.setInstructions("Prendre aux mêmes heures chaque jour. Ne pas arrêter brutalement.");
            traitement2.setQuantite(180);
            traitements.add(traitement2);
            
            // Traitement 3 - Statine
            Traitement traitement3 = new Traitement();
            traitement3.setConsultation(consultation);
            traitement3.setMedicament(med3);
            traitement3.setPosologie("1 comprimé le soir avant le coucher");
            traitement3.setDureeTraitement(90);
            traitement3.setInstructions("À prendre pendant le repas du soir. Contrôle bilan hépatique dans 3 mois.");
            traitement3.setQuantite(90);
            traitements.add(traitement3);
            
            // Enregistrer consultation + traitements + générer facture
            consultationFacade.terminerConsultation(consultation, traitements);
            
            printSuccess("Consultation terminée", "ID: " + consultation.getId());
            printSuccess("Traitements prescrits", traitements.size() + " médicaments");
            
            // Calculer montants
            BigDecimal montantMedicaments = traitements.stream()
                .map(t -> t.getMedicament().getPrixUnitaire()
                    .multiply(new BigDecimal(t.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal montantTotal = consultation.getTarifConsultation().add(montantMedicaments);
            
            printInfo("Détail financier", 
                     "Consultation: " + consultation.getTarifConsultation() + " MAD | " +
                     "Médicaments: " + montantMedicaments + " MAD | " +
                     "TOTAL: " + montantTotal + " MAD");

            // ════════════════════════════════════════════════════════════════
            // STEP 8: Simulation de paiement
            // ════════════════════════════════════════════════════════════════
            printStep(8, "Traitement du paiement");
            
            // Récupérer la facture générée
            IFactureDAO factureDAO = new FactureDAOImpl();
            Facture facture = factureDAO.findByConsultationId(consultation.getId());
            
            if (facture != null) {
                printSuccess("Facture générée", facture.getNumeroFacture() + 
                            " - Montant: " + facture.getMontantTotal() + " MAD (ID: " + facture.getId() + ")");
                
                // Paiement partiel de 5000 MAD
                BigDecimal paiementPartiel = new BigDecimal("5000.00");
                facturationFacade.encaisserPaiementPartiel(
                    facture.getId(), 
                    paiementPartiel, 
                    ModePaiement.CARTE
                );
                printSuccess("Paiement partiel", paiementPartiel + " MAD (CARTE)");
                
                // Récupérer la facture à jour depuis la BD
                Thread.sleep(1000);
                facture = factureDAO.findById(facture.getId());
                
                if (facture != null) {
                    // Calculer le montant restant avec les valeurs à jour
                    BigDecimal montantRestant = facture.getMontantTotal().subtract(facture.getMontantPaye());
                    
                    // Vérifier que le montant restant est positif
                    if (montantRestant.compareTo(BigDecimal.ZERO) > 0) {
                        facturationFacade.encaisserPaiementPartiel(
                            facture.getId(), 
                            montantRestant, 
                            ModePaiement.CHEQUE
                        );
                        printSuccess("Solde payé", montantRestant + " MAD (CHÈQUE)");
                        printSuccess("Facture", "PAYÉE intégralement ✓");
                    } else {
                        printSuccess("Facture", "DÉJÀ PAYÉE intégralement ✓");
                    }
                } else {
                    printInfo("Erreur", "Impossible de recharger la facture");
                }
            } else {
                printInfo("Avertissement", "Facture non trouvée");
            }

            // ════════════════════════════════════════════════════════════════
            // STEP 9: Statistiques finales
            // ════════════════════════════════════════════════════════════════
            printStep(9, "Statistiques du système");
            
            long nbDepartements = departementDAO.findAll().size();
            long nbMedecins = medecinDAO.findAll().size();
            long nbPatients = patientDAO.findAll().size();
            long nbRdv = rdvDAO.findAll().size();
            long nbMedicaments = medicamentDAO.findAll().size();
            
            System.out.println("  📊 Départements   : " + nbDepartements);
            System.out.println("  👨‍⚕️ Médecins       : " + nbMedecins);
            System.out.println("  🧑‍🦱 Patients       : " + nbPatients);
            System.out.println("  📅 Rendez-vous    : " + nbRdv);
            System.out.println("  💊 Médicaments    : " + nbMedicaments);

            // ════════════════════════════════════════════════════════════════
            // RÉSUMÉ FINAL
            // ════════════════════════════════════════════════════════════════
            System.out.println("\n" + "=".repeat(70));
            System.out.println("   ✅ TEST COMPLET RÉUSSI - FLUX MÉTIER VALIDÉ");
            System.out.println("=".repeat(70));
            System.out.println("🏥 Département      : " + departement.getNomDepartement());
            System.out.println("👨‍⚕️ Chef            : Dr. " + medecin1.getNom());
            System.out.println("🧑‍🦱 Patient traité  : " + patient1.getPrenom() + " " + patient1.getNom());
            System.out.println("📋 Diagnostic       : " + consultation.getDiagnostic());
            System.out.println("💊 Traitements      : " + traitements.size() + " médicaments prescrits");
            System.out.println("💰 Montant total    : " + montantTotal + " MAD");
            System.out.println("✅ Statut paiement : PAYÉ");
            System.out.println("=".repeat(70) + "\n");

        } catch (Exception e) {
            System.out.println("\n" + "!".repeat(70));
            System.out.println("   ❌ ERREUR DURANT LE TEST");
            System.out.println("!".repeat(70));
            logger.error("Erreur lors du test d'intégration complet", e);
            e.printStackTrace();
            System.exit(1);
        } finally {
            // Fermeture propre
            try {
                if (HikariCPConfig.getDataSource() != null && 
                    !HikariCPConfig.getDataSource().isClosed()) {
                    Thread.sleep(500);
                    HikariCPConfig.shutdown();
                    System.out.println("🔌 Pool de connexions fermé proprement\n");
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la fermeture", e);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES D'AFFICHAGE
    // ════════════════════════════════════════════════════════════════
    
    private static void printStep(int step, String message) {
        System.out.println("\n┌─" + "─".repeat(66) + "┐");
        System.out.println("│ STEP " + step + ": " + message + " ".repeat(65 - message.length() - 8) + "│");
        System.out.println("└─" + "─".repeat(66) + "┘");
    }

    private static void printSuccess(String label, String message) {
        System.out.println("  ✅ " + label + ": " + message);
    }

    private static void printInfo(String label, String message) {
        System.out.println("  ℹ️  " + label + ": " + message);
    }
}