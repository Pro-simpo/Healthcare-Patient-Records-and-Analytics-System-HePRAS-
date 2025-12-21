package ma.ensa.healthcare;

import ma.ensa.healthcare.config.HikariCPConfig;
import ma.ensa.healthcare.facade.ConsultationFacade;
import ma.ensa.healthcare.model.*;
import ma.ensa.healthcare.model.enums.*;
import ma.ensa.healthcare.dao.impl.*;
import ma.ensa.healthcare.dao.interfaces.*;
import ma.ensa.healthcare.service.UtilisateurService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   HEALTHCARE SYSTEM - INTEGRATION TEST A to Z    ");
        System.out.println("==================================================");

        try {
            // --- INITIALISATION DES COMPOSANTS ---
            IUtilisateurDAO userDAO = new UtilisateurDAOImpl();
            IPatientDAO patientDAO = new PatientDAOImpl();
            IMedecinDAO medecinDAO = new MedecinDAOImpl();
            IRendezVousDAO rdvDAO = new RendezVousDAOImpl();
            ConsultationFacade consultationFacade = new ConsultationFacade();

            // --- 1. TEST SÉCURITÉ : UTILISATEUR ---
            System.out.println("\n[STEP 1] Cr├®ation de l'administrateur...");
            Utilisateur admin = Utilisateur.builder()
                    .username("admin_" + System.currentTimeMillis()) // Unique
                    .password("secret123")
                    .email("admin@healthcare.ma")
                    .role(Role.ADMIN)
                    .actif(true)
                    .build();
            userDAO.save(admin);
            System.out.println("Ô£ô Utilisateur configur├® : " + admin.getUsername());

            // --- 2. TEST STRUCTURE : DÉPARTEMENT & MÉDECIN ---
            System.out.println("\n[STEP 2] Configuration du corps m├®dical...");
            // Note: Assurez-vous d'avoir un DAO pour Departement ou ins├®rez via SQL
            Medecin medecin = Medecin.builder()
                    .nom("EL FASSI")
                    .prenom("Dr. Amine")
                    .specialite("Cardiologie")
                    .build();
            medecinDAO.save(medecin);
            System.out.println("Ô£ô M├®decin enregistr├® : " + medecin.getNom());

            // --- 3. TEST PATIENT : DOSSIER COMPLET ---
            System.out.println("\n[STEP 3] Enregistrement d'un nouveau patient...");
            Patient patient = Patient.builder()
                    .nom("Berrada")
                    .prenom("Salma")
                    .cin("AB" + (int)(Math.random()*100000))
                    .email("salma@email.com")
                    .telephone("0600112233")
                    .sexe(Sexe.F) // FIX pour votre erreur pr├®c├®dente
                    .dateNaissance(LocalDate.of(1985, 10, 20))
                    .build();
            patientDAO.save(patient);
            System.out.println("Ô£ô Patient ID : " + patient.getId());

            // --- 4. TEST FLUX MÉDICAL : RDV & CONSULTATION ---
            System.out.println("\n[STEP 4] Flux Consultation...");
            RendezVous rdv = RendezVous.builder()
                    .dateHeure(LocalDateTime.now().plusHours(2))
                    .statut(StatutRendezVous.CONFIRME)
                    .motif("Douleur thoracique")
                    .patient(patient)
                    .medecin(medecin)
                    .build();
            rdvDAO.save(rdv);
            System.out.println("Ô£ô RDV Planifi├®");

            Consultation consultation = Consultation.builder()
                    .dateConsultation(LocalDate.now())
                    .diagnostic("Angine de poitrine l├®g├¿re")
                    .traitementPrescrit("Repos + Trinitrine")
                    .notesMedecin("Suivi strict requis")
                    .rendezVous(rdv)
                    .build();

            // --- 5. TEST TRANSACTIONNEL : FACADE ---
            System.out.println("\n[STEP 5] Finalisation & Facturation...");
            consultationFacade.terminerConsultation(consultation, 500.00);
            System.out.println("Ô£ô Consultation enregistr├®e");
            System.out.println("Ô£ô Facture g├®n├®r├®e : 500.00 MAD (Statut: EN_ATTENTE)");

            // --- 6. RÉCAPITULATIF FINAL ---
            System.out.println("\n==================================================");
            System.out.println("         TEST TERMIN├ë AVEC SUCC├êS !             ");
            System.out.println("==================================================");
            System.out.println("Résumé du test :");
            System.out.println("- Dossier Patient : " + patient.getNom().toUpperCase() + " " + patient.getPrenom());
            System.out.println("- Diagnostic : " + consultation.getDiagnostic());
            System.out.println("- Facture rattachée au RDV n° : " + rdv.getId());

        } catch (Exception e) {
            System.err.println("\n[!] ÉCHEC DU TEST À L'ÉTAPE :");
            e.printStackTrace();
        } finally {
            // Fermeture propre du pool pour éviter les warnings Maven
            if (HikariCPConfig.getDataSource() != null) {
                HikariCPConfig.getDataSource().close();
                System.out.println("\nPool de connexions ferm├®.");
            }
        }
    }
}