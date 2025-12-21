package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.model.Consultation;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.service.ConsultationService;
import ma.ensa.healthcare.service.FacturationService;

public class ConsultationFacade {
    private final ConsultationService consultationService = new ConsultationService();
    private final FacturationService facturationService = new FacturationService();

    public void terminerConsultation(Consultation consultation, Double montantFacture) {
        // 1. Enregistrer la consultation
        consultationService.enregistrerConsultation(consultation);
        
        // 2. Générer automatiquement la facture liée
        facturationService.genererFacture(consultation, montantFacture);
    }
}