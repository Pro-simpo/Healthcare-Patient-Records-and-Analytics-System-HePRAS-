package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.RendezVous;
import java.util.List;

public interface IRendezVousDAO {
    RendezVous save(RendezVous rendezVous);
    RendezVous findById(Long id);
    List<RendezVous> findAll();
    void update(RendezVous rendezVous);
    void delete(Long id);
}