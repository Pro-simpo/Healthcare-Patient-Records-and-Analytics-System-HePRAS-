package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.*;
import ma.ensa.healthcare.dao.interfaces.*;
import ma.ensa.healthcare.dto.StatisticsDTO;
import ma.ensa.healthcare.model.enums.StatutRendezVous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Service pour les statistiques et analyses
 */
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    
    private final IPatientDAO patientDAO;
    private final IMedecinDAO medecinDAO;
    private final IRendezVousDAO rendezVousDAO;
    private final IConsultationDAO consultationDAO;
    private final IFactureDAO factureDAO;

    public AnalyticsService() {
        this.patientDAO = new PatientDAOImpl();
        this.medecinDAO = new MedecinDAOImpl();
        this.rendezVousDAO = new RendezVousDAOImpl();
        this.consultationDAO = new ConsultationDAOImpl();
        this.factureDAO = new FactureDAOImpl();
    }

    /**
     * Récupère les statistiques globales du système
     */
    public StatisticsDTO getGlobalStats() {
        logger.info("Calcul des statistiques globales...");
        
        StatisticsDTO stats = new StatisticsDTO();
        
        try {
            // 1. Nombre total de patients
            long totalPatients = patientDAO.findAll().size();
            stats.setTotalPatients(totalPatients);
            
            // 2. Nombre total de médecins
            long totalMedecins = medecinDAO.findAll().size();
            stats.setTotalMedecins(totalMedecins);
            
            // 3. Rendez-vous aujourd'hui
            long rdvAujourdhui = compterRendezVousAujourdhui();
            stats.setTotalRendezVousAujourdhui(rdvAujourdhui);
            
            // 4. Consultations aujourd'hui
            long consultationsAujourdhui = compterConsultationsAujourdhui();
            stats.setConsultationsAujourdhui(consultationsAujourdhui);
            
            // 5. Chiffre d'affaires du mois - ✅ CORRIGÉ
            BigDecimal caMois = getChiffreAffaireMois();
            stats.setChiffreAffaireMois(caMois);
            
            // 6. Montant total impayé
            BigDecimal montantImpaye = factureDAO.getTotalImpaye();
            stats.setMontantImpayeTotal(montantImpaye);
            
            // 7. Nombre de factures impayées
            long facturesImpayees = factureDAO.findFacturesImpayees().size();
            stats.setFacturesImpayees(facturesImpayees);
            
            // 8. Consultations par spécialité
            Map<String, Integer> consultationsParSpecialite = getConsultationsParSpecialite();
            stats.setConsultationsParSpecialite(consultationsParSpecialite);
            
            // 9. Rendez-vous par statut
            Map<String, Integer> rdvParStatut = getRendezVousParStatut();
            stats.setRdvParStatut(rdvParStatut);
            
            logger.info("Statistiques calculées : {} patients, {} médecins", 
                       totalPatients, totalMedecins);
            
        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques", e);
            throw new RuntimeException("Erreur lors du calcul des statistiques", e);
        }
        
        return stats;
    }

    /**
     * Compte les rendez-vous du jour
     */
    private long compterRendezVousAujourdhui() {
        LocalDate aujourdhui = LocalDate.now();
        return rendezVousDAO.findAll().stream()
            .filter(rdv -> rdv.getDateRdv().equals(aujourdhui))
            .count();
    }

    /**
     * Compte les consultations du jour
     */
    private long compterConsultationsAujourdhui() {
        LocalDate aujourdhui = LocalDate.now();
        return consultationDAO.findAll().stream()
            .filter(c -> c.getDateConsultation().equals(aujourdhui))
            .count();
    }

    /**
     * Calcule le chiffre d'affaires du mois en cours
     */
    private BigDecimal getChiffreAffaireMois() {
        LocalDate maintenant = LocalDate.now();
        LocalDate debutMois = maintenant.withDayOfMonth(1);
        LocalDate finMois = maintenant.withDayOfMonth(maintenant.lengthOfMonth());
        
        return factureDAO.getRevenusPeriode(debutMois, finMois);
    }

    /**
     * Calcule le nombre de consultations par spécialité
     */
    private Map<String, Integer> getConsultationsParSpecialite() {
        Map<String, Integer> stats = new HashMap<>();
        
        consultationDAO.findAll().forEach(consultation -> {
            if (consultation.getRendezVous() != null && 
                consultation.getRendezVous().getMedecin() != null) {
                
                String specialite = consultation.getRendezVous()
                    .getMedecin()
                    .getSpecialite();
                
                if (specialite != null && !specialite.isEmpty()) {
                    stats.put(specialite, stats.getOrDefault(specialite, 0) + 1);
                }
            }
        });
        
        return stats;
    }

    /**
     * Calcule le nombre de rendez-vous par statut
     */
    private Map<String, Integer> getRendezVousParStatut() {
        Map<String, Integer> stats = new HashMap<>();
        
        rendezVousDAO.findAll().forEach(rdv -> {
            if (rdv.getStatut() != null) {
                String statut = rdv.getStatut().name();
                stats.put(statut, stats.getOrDefault(statut, 0) + 1);
            }
        });
        
        return stats;
    }

    /**
     * Statistiques pour une période donnée
     */
    public StatisticsDTO getStatsPeriode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Dates de début et fin requises");
        }
        
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }
        
        StatisticsDTO stats = new StatisticsDTO();
        
        // Revenus de la période
        BigDecimal revenus = factureDAO.getRevenusPeriode(dateDebut, dateFin);
        stats.setChiffreAffaireMois(revenus);
        
        // Consultations de la période
        long consultations = consultationDAO.findAll().stream()
            .filter(c -> !c.getDateConsultation().isBefore(dateDebut) && 
                        !c.getDateConsultation().isAfter(dateFin))
            .count();
        stats.setConsultationsAujourdhui(consultations);
        
        return stats;
    }

    /**
     * Statistiques par médecin
     */
    public Map<String, Object> getStatsMedecin(Long medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("ID médecin requis");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // Nombre de consultations
        long nbConsultations = consultationDAO.findByMedecinId(medecinId).size();
        stats.put("nombreConsultations", nbConsultations);
        
        // Nombre de rendez-vous
        long nbRendezVous = rendezVousDAO.findAll().stream()
            .filter(rdv -> rdv.getMedecin() != null && 
                          medecinId.equals(rdv.getMedecin().getId()))
            .count();
        stats.put("nombreRendezVous", nbRendezVous);
        
        return stats;
    }

    /**
     * Statistiques par patient
     */
    public Map<String, Object> getStatsPatient(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("ID patient requis");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // Nombre de consultations
        long nbConsultations = consultationDAO.findByPatientId(patientId).size();
        stats.put("nombreConsultations", nbConsultations);
        
        // Nombre de factures
        long nbFactures = factureDAO.findByPatientId(patientId).size();
        stats.put("nombreFactures", nbFactures);
        
        // Total dépensé
        BigDecimal totalDepense = factureDAO.findByPatientId(patientId).stream()
            .map(f -> f.getMontantPaye() != null ? f.getMontantPaye() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalDepense", totalDepense);
        
        // Montant restant à payer
        BigDecimal totalImpaye = factureDAO.findByPatientId(patientId).stream()
            .map(f -> f.getMontantRestant())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("montantRestant", totalImpaye);
        
        return stats;
    }
}