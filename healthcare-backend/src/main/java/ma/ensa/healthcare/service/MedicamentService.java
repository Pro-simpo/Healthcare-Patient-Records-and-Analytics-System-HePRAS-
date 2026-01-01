package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.MedicamentDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IMedicamentDAO;
import ma.ensa.healthcare.model.Medicament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des médicaments
 * VERSION MINIMALE - Seulement pour les alertes de stock
 */
public class MedicamentService {
    
    private static final Logger logger = LoggerFactory.getLogger(MedicamentService.class);
    private final IMedicamentDAO medicamentDAO;

    public MedicamentService() {
        this.medicamentDAO = new MedicamentDAOImpl();
    }

    /**
     * Retourne tous les médicaments
     */
    public List<Medicament> getAllMedicaments() {
        try {
            return medicamentDAO.findAll();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des médicaments", e);
            throw new RuntimeException("Impossible de récupérer les médicaments", e);
        }
    }

    /**
     * Retourne les médicaments en alerte (stock <= seuil d'alerte)
     * 
     * @return Liste des médicaments dont le stock est critique
     */
    public List<Medicament> getMedicamentsEnAlerte() {
        try {
            List<Medicament> tous = medicamentDAO.findAll();
            
            // Filtrer les médicaments où stock_disponible <= stock_alerte
            List<Medicament> enAlerte = tous.stream()
                .filter(m -> m.getStockDisponible() != null && 
                            m.getStockAlerte() != null &&
                            m.getStockDisponible() <= m.getStockAlerte())
                .sorted((m1, m2) -> {
                    // Trier par niveau de criticité (ratio stock/alerte)
                    double ratio1 = m1.getStockDisponible().doubleValue() / m1.getStockAlerte().doubleValue();
                    double ratio2 = m2.getStockDisponible().doubleValue() / m2.getStockAlerte().doubleValue();
                    return Double.compare(ratio1, ratio2);
                })
                .collect(Collectors.toList());
            
            logger.info("{} médicament(s) en alerte détecté(s)", enAlerte.size());
            return enAlerte;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des alertes médicaments", e);
            return List.of(); // Retourner une liste vide en cas d'erreur
        }
    }

    /**
     * Compte le nombre de médicaments en alerte
     */
    public int countMedicamentsEnAlerte() {
        return getMedicamentsEnAlerte().size();
    }

    /**
     * Recherche un médicament par ID
     */
    public Medicament getMedicamentById(Long id) {
        try {
            return medicamentDAO.findById(id);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du médicament {}", id, e);
            throw new RuntimeException("Impossible de récupérer le médicament", e);
        }
    }

    /**
     * Recherche des médicaments par nom
     */
    public List<Medicament> searchByNom(String nom) {
        try {
            return medicamentDAO.findByNom(nom);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de médicaments", e);
            throw new RuntimeException("Impossible de rechercher les médicaments", e);
        }
    }

    /**
     * Crée un nouveau médicament
     */
    public Medicament createMedicament(Medicament medicament) {
        try {
            // Validation basique
            if (medicament.getNomCommercial() == null || medicament.getNomCommercial().trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom commercial est obligatoire");
            }
            if (medicament.getPrincipeActif() == null || medicament.getPrincipeActif().trim().isEmpty()) {
                throw new IllegalArgumentException("Le principe actif est obligatoire");
            }
            
            Medicament saved = medicamentDAO.save(medicament);
            logger.info("Médicament créé avec succès : {}", saved.getNomCommercial());
            return saved;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création du médicament", e);
            throw new RuntimeException("Impossible de créer le médicament", e);
        }
    }

    /**
     * Met à jour un médicament existant
     */
    public void updateMedicament(Medicament medicament) {
        try {
            if (medicament.getId() == null) {
                throw new IllegalArgumentException("L'ID du médicament est obligatoire pour la mise à jour");
            }
            
            medicamentDAO.update(medicament);
            logger.info("Médicament mis à jour : {}", medicament.getId());
            
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du médicament", e);
            throw new RuntimeException("Impossible de mettre à jour le médicament", e);
        }
    }

    /**
     * Supprime un médicament
     */
    public void deleteMedicament(Long id) {
        try {
            medicamentDAO.delete(id);
            logger.info("Médicament supprimé : {}", id);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du médicament {}", id, e);
            throw new RuntimeException("Impossible de supprimer le médicament", e);
        }
    }

    /**
     * Vérifie si un médicament est en alerte
     */
    public boolean isEnAlerte(Medicament medicament) {
        return medicament.getStockDisponible() != null && 
               medicament.getStockAlerte() != null &&
               medicament.getStockDisponible() <= medicament.getStockAlerte();
    }

    /**
     * Retourne le niveau de criticité d'un médicament (en pourcentage)
     * 100% = stock complet, 0% = rupture de stock
     */
    public double getNiveauStock(Medicament medicament) {
        if (medicament.getStockDisponible() == null || 
            medicament.getStockAlerte() == null || 
            medicament.getStockAlerte() == 0) {
            return 0.0;
        }
        
        double ratio = (double) medicament.getStockDisponible() / medicament.getStockAlerte();
        return Math.min(ratio * 100, 100.0);
    }
}