package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Facture;
import java.util.List;

public interface IFactureDAO {
    Facture save(Facture facture);
    Facture findById(Long id);
    List<Facture> findAll();
    void updateStatut(Long id, String statut);
}