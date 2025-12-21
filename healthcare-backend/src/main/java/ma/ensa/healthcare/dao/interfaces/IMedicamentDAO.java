package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Medicament;
import java.util.List;

public interface IMedicamentDAO {
    Medicament save(Medicament medicament);
    Medicament findById(Long id);
    List<Medicament> findAll();
    void update(Medicament medicament);
    void delete(Long id);
    List<Medicament> findByNom(String nom);
}