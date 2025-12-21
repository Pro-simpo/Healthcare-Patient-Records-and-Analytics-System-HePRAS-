package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Departement;
import java.util.List;

public interface IDepartementDAO {
    Departement save(Departement departement);
    Departement findById(Long id);
    List<Departement> findAll();
    void update(Departement departement);
    void delete(Long id);
}