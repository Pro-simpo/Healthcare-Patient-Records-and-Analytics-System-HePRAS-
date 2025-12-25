package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.RendezVousDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IRendezVousDAO;
import ma.ensa.healthcare.exception.RendezVousException;
import ma.ensa.healthcare.model.RendezVous;
import ma.ensa.healthcare.model.enums.StatutRendezVous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des rendez-vous
 */
public class RendezVousService {
    private static final Logger logger = LoggerFactory.getLogger(RendezVousService.class);
    private final IRendezVousDAO rdvDAO;

    public RendezVousService() {
        this.rdvDAO = new RendezVousDAOImpl();
    }

    /**
     * Planifie un nouveau rendez-vous avec validation
     */
    public RendezVous planifierRendezVous(RendezVous rdv) {
        validateRendezVous(rdv);
        
        // Vérifier disponibilité du médecin
        verifierDisponibiliteMedecin(rdv);
        
        // Initialiser le statut si non fourni
        if (rdv.getStatut() == null) {
            rdv.setStatut(StatutRendezVous.PLANIFIE);
        }
        
        // Initialiser date de création
        if (rdv.getDateCreation() == null) {
            rdv.setDateCreation(LocalDate.now());
        }
        
        try {
            RendezVous saved = rdvDAO.save(rdv);
            logger.info("Rendez-vous planifié : ID {} pour le {} à {}", 
                       saved.getId(), saved.getDateRdv(), saved.getHeureDebut());
            return saved;
        } catch (Exception e) {
            logger.error("Erreur lors de la planification du rendez-vous", e);
            throw new RendezVousException("Impossible de planifier le rendez-vous", e);
        }
    }

    /**
     * Récupère tous les rendez-vous
     */
    public List<RendezVous> obtenirTousLesRendezVous() {
        return rdvDAO.findAll();
    }

    public List<RendezVous> getAllRendezVous() {
        return rdvDAO.findAll();
    }

    /**
     * Récupère un rendez-vous par ID
     */
    public RendezVous getRendezVousById(Long id) {
        if (id == null) {
            throw new RendezVousException("L'ID ne peut pas être null");
        }
        
        RendezVous rdv = rdvDAO.findById(id);
        if (rdv == null) {
            throw new RendezVousException("Rendez-vous introuvable avec l'ID " + id);
        }
        return rdv;
    }

    /**
     * Récupère les rendez-vous d'une date donnée
     */
    public List<RendezVous> obtenirRendezVousParDate(LocalDate date) {
        if (date == null) {
            throw new RendezVousException("La date ne peut pas être null");
        }
        
        return rdvDAO.findAll().stream()
            .filter(rdv -> rdv.getDateRdv().equals(date))
            .collect(Collectors.toList());
    }

    /**
     * Récupère les rendez-vous d'un patient
     */
    public List<RendezVous> obtenirRendezVousPatient(Long patientId) {
        if (patientId == null) {
            throw new RendezVousException("L'ID du patient ne peut pas être null");
        }
        
        return rdvDAO.findAll().stream()
            .filter(rdv -> rdv.getPatient() != null && 
                          patientId.equals(rdv.getPatient().getId()))
            .collect(Collectors.toList());
    }

    /**
     * Récupère les rendez-vous d'un médecin
     */
    public List<RendezVous> obtenirRendezVousMedecin(Long medecinId) {
        if (medecinId == null) {
            throw new RendezVousException("L'ID du médecin ne peut pas être null");
        }
        
        return rdvDAO.findAll().stream()
            .filter(rdv -> rdv.getMedecin() != null && 
                          medecinId.equals(rdv.getMedecin().getId()))
            .collect(Collectors.toList());
    }

    /**
     * Met à jour un rendez-vous existant
     */
    public void updateRendezVous(RendezVous rdv) {
        if (rdv.getId() == null) {
            throw new RendezVousException("L'ID du rendez-vous est requis pour la mise à jour");
        }
        
        validateRendezVous(rdv);
        
        try {
            rdvDAO.update(rdv);
            logger.info("Rendez-vous mis à jour : ID {}", rdv.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du rendez-vous", e);
            throw new RendezVousException("Impossible de mettre à jour le rendez-vous", e);
        }
    }

    /**
     * Confirme un rendez-vous
     */
    public void confirmerRendezVous(Long id) {
        RendezVous rdv = getRendezVousById(id);
        
        if (rdv.getStatut() == StatutRendezVous.ANNULE) {
            throw new RendezVousException("Impossible de confirmer un rendez-vous annulé");
        }
        
        if (rdv.getStatut() == StatutRendezVous.TERMINE) {
            throw new RendezVousException("Impossible de confirmer un rendez-vous terminé");
        }
        
        rdv.setStatut(StatutRendezVous.CONFIRME);
        rdvDAO.update(rdv);
        logger.info("Rendez-vous confirmé : ID {}", id);
    }

    /**
     * Annule un rendez-vous
     */
    public void annulerRendezVous(Long id, String motifAnnulation) {
        RendezVous rdv = getRendezVousById(id);
        
        if (rdv.getStatut() == StatutRendezVous.TERMINE) {
            throw new RendezVousException("Impossible d'annuler un rendez-vous déjà terminé");
        }
        
        rdv.setStatut(StatutRendezVous.ANNULE);
        // Le motif pourrait être stocké dans un champ notes si vous l'ajoutez au modèle
        rdvDAO.update(rdv);
        logger.info("Rendez-vous annulé : ID {} - Motif: {}", id, motifAnnulation);
    }

    /**
     * Marque un rendez-vous comme terminé
     */
    public void terminerRendezVous(Long id) {
        RendezVous rdv = getRendezVousById(id);
        
        if (rdv.getStatut() == StatutRendezVous.ANNULE) {
            throw new RendezVousException("Impossible de terminer un rendez-vous annulé");
        }
        
        rdv.setStatut(StatutRendezVous.TERMINE);
        rdvDAO.update(rdv);
        logger.info("Rendez-vous terminé : ID {}", id);
    }

    /**
     * Supprime un rendez-vous
     */
    public void supprimerRendezVous(Long id) {
        if (id == null) {
            throw new RendezVousException("ID invalide");
        }
        
        try {
            rdvDAO.delete(id);
            logger.info("Rendez-vous supprimé : ID {}", id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du rendez-vous", e);
            throw new RendezVousException("Impossible de supprimer le rendez-vous", e);
        }
    }

    /**
     * Validation complète d'un rendez-vous
     */
    private void validateRendezVous(RendezVous rdv) {
        if (rdv == null) {
            throw new RendezVousException("Le rendez-vous ne peut pas être null");
        }
        
        // 1. Patient obligatoire
        if (rdv.getPatient() == null || rdv.getPatient().getId() == null) {
            throw new RendezVousException("Le patient est obligatoire");
        }
        
        // 2. Médecin obligatoire
        if (rdv.getMedecin() == null || rdv.getMedecin().getId() == null) {
            throw new RendezVousException("Le médecin est obligatoire");
        }
        
        // 3. Date obligatoire
        if (rdv.getDateRdv() == null) {
            throw new RendezVousException("La date du rendez-vous est obligatoire");
        }
        
        // 4. Heure début obligatoire
        if (rdv.getHeureDebut() == null) {
            throw new RendezVousException("L'heure de début est obligatoire");
        }
        
        // 5. Heure fin obligatoire
        if (rdv.getHeureFin() == null) {
            throw new RendezVousException("L'heure de fin est obligatoire");
        }
        
        // 6. Heure fin après heure début
        if (!rdv.getHeureFin().isAfter(rdv.getHeureDebut())) {
            throw new RendezVousException("L'heure de fin doit être après l'heure de début");
        }
        
        // 7. Date dans le futur (sauf pour update)
        if (rdv.getId() == null) { // Nouveau rendez-vous
            LocalDateTime maintenant = LocalDateTime.now();
            if (rdv.getHeureDebut().isBefore(maintenant)) {
                throw new RendezVousException("Impossible de planifier un rendez-vous dans le passé");
            }
        }
    }

    /**
     * Vérifie la disponibilité du médecin
     */
    private void verifierDisponibiliteMedecin(RendezVous nouveauRdv) {
        List<RendezVous> rdvExistants = obtenirRendezVousMedecin(nouveauRdv.getMedecin().getId());
        
        for (RendezVous existant : rdvExistants) {
            // Ignorer les rendez-vous annulés
            if (existant.getStatut() == StatutRendezVous.ANNULE) {
                continue;
            }
            
            // Ignorer le rendez-vous lui-même si c'est une mise à jour
            if (nouveauRdv.getId() != null && nouveauRdv.getId().equals(existant.getId())) {
                continue;
            }
            
            // Vérifier chevauchement
            boolean chevauchement = 
                !nouveauRdv.getHeureDebut().isAfter(existant.getHeureFin()) &&
                !nouveauRdv.getHeureFin().isBefore(existant.getHeureDebut());
            
            if (chevauchement) {
                throw new RendezVousException(
                    "Le médecin n'est pas disponible à cette heure. " +
                    "Conflit avec un rendez-vous existant."
                );
            }
        }
    }
}