package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Medecin;
import java.util.List;

public interface IMedecinDAO {
    Medecin save(Medecin medecin);
    Medecin findById(Long id);
    List<Medecin> findBySpecialite(String specialite);
    List<Medecin> findAll();
    void update(Medecin medecin);
    void delete(Long id);
}