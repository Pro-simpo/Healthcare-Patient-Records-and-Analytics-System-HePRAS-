package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.RendezVousDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IRendezVousDAO;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.validation.RendezVousValidator;
import java.util.List;

public class RendezVousService {
    private final IRendezVousDAO rdvDAO = new RendezVousDAOImpl();

    public RendezVous planifierRendezVous(RendezVous rdv) {
        RendezVousValidator.validate(rdv);
        return rdvDAO.save(rdv);
    }

    public List<RendezVous> obtenirToutLesRendezVous() {
        return rdvDAO.findAll();
    }

    public void annulerRendezVous(Long id) {
        RendezVous rdv = rdvDAO.findById(id);
        if (rdv != null) {
            rdv.setStatut(ma.ensa.healthcare.model.enums.StatutRendezVous.ANNULE);
            rdvDAO.update(rdv);
        }
    }
}