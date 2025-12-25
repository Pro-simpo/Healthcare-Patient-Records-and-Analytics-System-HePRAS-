package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.model.Consultation;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.model.Traitement;
import ma.ensa.healthcare.service.ConsultationService;
import ma.ensa.healthcare.service.FacturationService;
import ma.ensa.healthcare.service.TraitementService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Facade pour orchestrer le processus de consultation
 * Coordination entre ConsultationService, TraitementService et FacturationService
 */
public class ConsultationFacade {
    private final ConsultationService consultationService;
    private final FacturationService facturationService;
    private final TraitementService traitementService;

    public ConsultationFacade() {
        this.consultationService = new ConsultationService();
        this.facturationService = new FacturationService();
        this.traitementService = new TraitementService();
    }

    /**
     * Termine une consultation complète avec traitements et facturation
     * 
     * @param consultation La consultation à enregistrer
     * @param traitements Liste des traitements prescrits
     * @return La facture générée
     */
    public Facture terminerConsultation(Consultation consultation, List<Traitement> traitements) {
        // 1. Enregistrer la consultation
        Consultation consultationSauvegardee = consultationService.enregistrerConsultation(consultation);
        
        // 2. Enregistrer les traitements prescrits
        BigDecimal montantMedicaments = BigDecimal.ZERO;
        if (traitements != null && !traitements.isEmpty()) {
            for (Traitement traitement : traitements) {
                traitement.setConsultation(consultationSauvegardee);
                
                // Calculer le montant des médicaments
                BigDecimal prixUnitaire = traitement.getMedicament().getPrixUnitaire();
                int quantite = traitement.getQuantite();
                montantMedicaments = montantMedicaments.add(
                    prixUnitaire.multiply(BigDecimal.valueOf(quantite))
                );
            }
        }
        
        // 3. Générer automatiquement la facture
        Facture facture = facturationService.genererFacture(
            consultationSauvegardee, 
            montantMedicaments
        );
        
        return facture;
    }

    /**
     * Enregistre une consultation simple sans traitements
     */
    public Consultation enregistrerConsultationSimple(Consultation consultation) {
        // Enregistrer la consultation
        Consultation consultationSauvegardee = consultationService.enregistrerConsultation(consultation);
        
        // Générer la facture (sans médicaments)
        facturationService.genererFacture(consultationSauvegardee, BigDecimal.ZERO);
        
        return consultationSauvegardee;
    }

    /**
     * Récupère une consultation avec ses traitements
     */
    public Consultation getConsultationComplete(Long consultationId) {
        Consultation consultation = consultationService.getConsultationById(consultationId);
        return consultation;
    }

    /**
     * Récupère toutes les consultations d'un patient
     */
    public List<Consultation> getConsultationsPatient(Long patientId) {
        return consultationService.getConsultationsPatient(patientId);
    }

    /**
     * Récupère toutes les consultations d'un médecin
     */
    public List<Consultation> getConsultationsMedecin(Long medecinId) {
        return consultationService.getConsultationsMedecin(medecinId);
    }

    /**
     * Met à jour une consultation existante
     */
    public void modifierConsultation(Consultation consultation) {
        consultationService.modifierConsultation(consultation);
    }
}