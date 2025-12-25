package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.TraitementDAOImpl;
import ma.ensa.healthcare.dao.interfaces.ITraitementDAO;
import ma.ensa.healthcare.model.Traitement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service métier pour la gestion des traitements
 */
public class TraitementService {
    private static final Logger logger = LoggerFactory.getLogger(TraitementService.class);
    private final ITraitementDAO traitementDAO;

    public TraitementService() {
        this.traitementDAO = new TraitementDAOImpl();
    }

    /**
     * Enregistre un traitement
     */
    public Traitement save(Traitement traitement) {
        if (traitement == null) {
            throw new IllegalArgumentException("Le traitement ne peut pas être null");
        }
        
        Traitement saved = traitementDAO.save(traitement);
        logger.info("Traitement enregistré : ID {}", saved.getId());
        return saved;
    }

    /**
     * Récupère un traitement par ID
     */
    public Traitement getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        return traitementDAO.findById(id);
    }

    /**
     * Récupère tous les traitements
     */
    public List<Traitement> getAll() {
        return traitementDAO.findAll();
    }

    /**
     * Met à jour un traitement
     */
    public void update(Traitement traitement) {
        if (traitement == null || traitement.getId() == null) {
            throw new IllegalArgumentException("Le traitement et son ID sont requis");
        }
        traitementDAO.update(traitement);
        logger.info("Traitement mis à jour : ID {}", traitement.getId());
    }

    /**
     * Supprime un traitement
     */
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        traitementDAO.delete(id);
        logger.info("Traitement supprimé : ID {}", id);
    }
}