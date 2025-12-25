package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Traitement;
import java.util.List;

/**
 * Interface DAO pour l'entité Traitement
 */
public interface ITraitementDAO {
    /**
     * Enregistre un nouveau traitement
     */
    Traitement save(Traitement traitement);
    
    /**
     * Recherche un traitement par son ID
     */
    Traitement findById(Long id);
    
    /**
     * Récupère tous les traitements
     */
    List<Traitement> findAll();
    
    /**
     * Met à jour un traitement
     */
    void update(Traitement traitement);
    
    /**
     * Supprime un traitement
     */
    void delete(Long id);
    
    /**
     * Récupère tous les traitements d'une consultation
     */
    List<Traitement> findByConsultationId(Long consultationId);
}