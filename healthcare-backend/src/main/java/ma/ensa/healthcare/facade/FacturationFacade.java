package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.service.FacturationService;
import ma.ensa.healthcare.dto.FactureDTO;

public class FacturationFacade {
    private final FacturationService facturationService = new FacturationService();

    public void encaisser(Long factureId) {
        facturationService.encaisserPaiement(factureId);
        // Ici on pourrait ajouter l'envoi d'un email de confirmation par un EmailService
    }
}