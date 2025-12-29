package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.FactureDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IFactureDAO;
import ma.ensa.healthcare.model.Consultation;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.model.Patient;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.service.*;
import ma.ensa.healthcare.model.enums.ModePaiement;
import ma.ensa.healthcare.model.enums.StatutPaiement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service métier pour la gestion des factures et paiements
 */
public class FacturationService {
    private static final Logger logger = LoggerFactory.getLogger(FacturationService.class);
    private final IFactureDAO factureDAO;
    private final RendezVousService rendezVousService = new RendezVousService();

    public FacturationService() {
        this.factureDAO = new FactureDAOImpl();
    }

    /**
     * Génère une facture pour une consultation
     * 
     * @param consultation La consultation
     * @param montantMedicaments Le montant total des médicaments prescrits
     * @return La facture créée
     */
    public Facture genererFacture(Long idConsultation, BigDecimal montantMedicaments) {
        // Validation
        ConsultationService consultationService = new ConsultationService();
        Consultation consultation = consultationService.getConsultationById(idConsultation);
        if (consultation == null || consultation.getId() == null) {
            throw new IllegalArgumentException("Consultation invalide");
        }
        
        if (consultation.getIdRendezVous() == 0) {
            throw new IllegalArgumentException("Patient introuvable dans la consultation");
        }
        
        // Récupérer le patient
        RendezVous rdvAssocie = rendezVousService.getRendezVousById(consultation.getIdRendezVous());
        Long idPatient = rdvAssocie.getIdPatient();
        // Montants
        BigDecimal montantConsultation = consultation.getTarifConsultation() != null 
            ? consultation.getTarifConsultation() 
            : BigDecimal.ZERO;
            
        BigDecimal montantMed = montantMedicaments != null 
            ? montantMedicaments 
            : BigDecimal.ZERO;
            
        BigDecimal montantTotal = montantConsultation.add(montantMed);
        
        // Générer le numéro de facture
        String numeroFacture = genererNumeroFacture();
        
        // Créer la facture
        Facture facture = Facture.builder()
                .numeroFacture(numeroFacture)
                .idPatient(idPatient)
                .idConsultation(consultation.getId())
                .dateFacture(LocalDate.now())
                .montantConsultation(montantConsultation)
                .montantMedicaments(montantMed)
                .montantTotal(montantTotal)
                .montantPaye(BigDecimal.ZERO)
                .statutPaiement(StatutPaiement.EN_ATTENTE)
                .build();
        
        Facture saved = factureDAO.save(facture);
        logger.info("Facture générée : {} - Montant total : {} MAD", 
                   saved.getNumeroFacture(), saved.getMontantTotal());
        
        return saved;
    }

    /**
     * Enregistre un paiement complet pour une facture
     */
    public void encaisserPaiementComplet(Long factureId, ModePaiement modePaiement) {
        Facture facture = factureDAO.findById(factureId);
        
        if (facture == null) {
            throw new IllegalArgumentException("Facture introuvable : " + factureId);
        }
        
        BigDecimal montantRestant = facture.getMontantTotal().subtract(facture.getMontantPaye());
        encaisserPaiement(factureId, montantRestant, modePaiement, LocalDate.now());
    }

    /**
     * Enregistre un paiement (complet ou partiel)
     */
    public void encaisserPaiement(Long factureId, BigDecimal montant, 
                                   ModePaiement modePaiement, LocalDate datePaiement) {
        // Validation
        if (factureId == null) {
            throw new IllegalArgumentException("ID facture requis");
        }
        
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }
        
        if (modePaiement == null) {
            throw new IllegalArgumentException("Mode de paiement requis");
        }
        
        if (datePaiement == null) {
            datePaiement = LocalDate.now();
        }
        
        // Enregistrer via le DAO
        factureDAO.enregistrerPaiement(factureId, montant, modePaiement, datePaiement);
        
        logger.info("Paiement enregistré pour facture {} : {} MAD via {}", 
                   factureId, montant, modePaiement);
    }

    /**
     * Récupère une facture par ID
     */
    public Facture getFactureById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de facture ne peut pas être null");
        }
        return factureDAO.findById(id);
    }

    /**
     * Récupère toutes les factures
     */
    public List<Facture> getToutesLesFactures() {
        return factureDAO.findAll();
    }

    /**
     * Récupère les factures d'un patient
     */
    public List<Facture> getFacturesPatient(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("L'ID du patient ne peut pas être null");
        }
        return factureDAO.findByPatientId(patientId);
    }

    /**
     * Récupère une facture par son numéro
     */
    public Facture getFactureParNumero(String numeroFacture) {
        if (numeroFacture == null || numeroFacture.trim().isEmpty()) {
            throw new IllegalArgumentException("Numéro de facture requis");
        }
        return factureDAO.findByNumero(numeroFacture);
    }

    /**
     * Récupère les factures impayées
     */
    public List<Facture> getFacturesImpayees() {
        return factureDAO.findFacturesImpayees();
    }

    /**
     * Récupère les factures par statut
     */
    public List<Facture> getFacturesParStatut(StatutPaiement statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Statut requis");
        }
        return factureDAO.findByStatut(statut);
    }

    /**
     * Calcule le total des impayés
     */
    public BigDecimal getTotalImpaye() {
        return factureDAO.getTotalImpaye();
    }

    /**
     * Calcule les revenus d'une période
     */
    public BigDecimal getRevenusPeriode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Dates de début et fin requises");
        }
        
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }
        
        return factureDAO.getRevenusPeriode(dateDebut, dateFin);
    }

    /**
     * Annule une facture
     */
    public void annulerFacture(Long factureId) {
        if (factureId == null) {
            throw new IllegalArgumentException("L'ID de facture ne peut pas être null");
        }
        
        Facture facture = factureDAO.findById(factureId);
        if (facture == null) {
            throw new IllegalArgumentException("Facture introuvable");
        }
        
        if (facture.getStatutPaiement() == StatutPaiement.PAYE) {
            throw new IllegalStateException("Impossible d'annuler une facture déjà payée");
        }
        
        factureDAO.delete(factureId);
        logger.info("Facture annulée : {}", facture.getNumeroFacture());
    }

    /**
     * Génère un numéro de facture unique
     * Format : FAC-YYYY-NNNN
     */
    private String genererNumeroFacture() {
        return factureDAO.genererNumeroFacture();
    }
}