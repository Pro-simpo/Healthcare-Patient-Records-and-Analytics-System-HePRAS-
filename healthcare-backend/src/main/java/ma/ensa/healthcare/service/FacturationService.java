package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.FactureDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IFactureDAO;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.model.enums.StatutPaiement;
import java.time.LocalDate;

public class FacturationService {
    private final IFactureDAO factureDAO = new FactureDAOImpl();

    public Facture genererFacture(ma.ensa.healthcare.model.Consultation c, Double montant) {
        Facture f = Facture.builder()
                .consultation(c)
                .montant(montant)
                .dateFacture(LocalDate.now())
                .statut(StatutPaiement.EN_ATTENTE)
                .build();
        return factureDAO.save(f);
    }

    public void encaisserPaiement(Long factureId) {
        factureDAO.updateStatut(factureId, StatutPaiement.PAYE.name());
    }
}