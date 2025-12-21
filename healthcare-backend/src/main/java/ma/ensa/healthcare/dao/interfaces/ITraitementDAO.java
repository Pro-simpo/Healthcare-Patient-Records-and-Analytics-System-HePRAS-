package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Traitement;
import java.util.List;

public interface ITraitementDAO {
    Traitement save(Traitement traitement);
    List<Traitement> findByConsultationId(Long consultationId);
    void delete(Long id);
}