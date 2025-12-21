package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.MedecinDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IMedecinDAO;
import ma.ensa.healthcare.exception.MedecinException;
import ma.ensa.healthcare.model.Medecin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MedecinService {

    private static final Logger logger = LoggerFactory.getLogger(MedecinService.class);
    private final IMedecinDAO medecinDAO;

    public MedecinService() {
        this.medecinDAO = new MedecinDAOImpl();
    }

    public Medecin createMedecin(Medecin medecin) {
        validateMedecin(medecin);
        try {
            return medecinDAO.save(medecin);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du médecin", e);
            throw new MedecinException("Impossible de créer le médecin", e);
        }
    }

    public Medecin getMedecinById(Long id) {
        Medecin m = medecinDAO.findById(id);
        if (m == null) {
            throw new MedecinException("Médecin introuvable avec l'ID " + id);
        }
        return m;
    }

    public List<Medecin> getAllMedecins() {
        return medecinDAO.findAll();
    }

    public void updateMedecin(Medecin medecin) {
        validateMedecin(medecin);
        if (medecin.getId() == null) {
            throw new MedecinException("L'ID du médecin est requis pour la mise à jour");
        }
        medecinDAO.update(medecin);
    }

    public void deleteMedecin(Long id) {
        if (id == null) {
            throw new MedecinException("ID invalide");
        }
        medecinDAO.delete(id);
    }

    // Validation simple interne (pourrait être dans une classe Validator séparée)
    private void validateMedecin(Medecin medecin) {
        if (medecin == null) {
            throw new MedecinException("Le médecin ne peut pas être null");
        }
        if (medecin.getNom() == null || medecin.getNom().trim().isEmpty()) {
            throw new MedecinException("Le nom du médecin est obligatoire");
        }
        if (medecin.getSpecialite() == null || medecin.getSpecialite().trim().isEmpty()) {
            throw new MedecinException("La spécialité est obligatoire");
        }
    }
}