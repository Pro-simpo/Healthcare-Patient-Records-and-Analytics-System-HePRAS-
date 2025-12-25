package ma.ensa.healthcare.service;

import ma.ensa.healthcare.dao.impl.ConsultationDAOImpl;
import ma.ensa.healthcare.dao.interfaces.IConsultationDAO;
import ma.ensa.healthcare.model.Consultation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service métier pour la gestion des consultations
 */
public class ConsultationService {
    private static final Logger logger = LoggerFactory.getLogger(ConsultationService.class);
    private final IConsultationDAO consultationDAO;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAOImpl();
    }

    /**
     * Enregistre une consultation avec validation métier
     */
    public Consultation enregistrerConsultation(Consultation c) {
        // Validation métier
        validerConsultation(c);
        
        // Initialiser la date si non fournie
        if (c.getDateConsultation() == null) {
            c.setDateConsultation(LocalDate.now());
        }
        
        // Sauvegarder
        Consultation saved = consultationDAO.save(c);
        logger.info("Consultation enregistrée avec succès : ID {}", saved.getId());
        
        return saved;
    }

    /**
     * Validation métier d'une consultation
     */
    private void validerConsultation(Consultation c) {
        // 1. Rendez-vous obligatoire
        if (c.getRendezVous() == null || c.getRendezVous().getId() == null) {
            throw new IllegalArgumentException("Le rendez-vous est obligatoire pour une consultation");
        }
        
        // 2. Diagnostic obligatoire
        if (c.getDiagnostic() == null || c.getDiagnostic().trim().isEmpty()) {
            throw new IllegalArgumentException("Le diagnostic est obligatoire");
        }
        
        // 3. Symptômes recommandés (warning seulement)
        if (c.getSymptomes() == null || c.getSymptomes().trim().isEmpty()) {
            logger.warn("Consultation sans symptômes - recommandé de les renseigner");
        }
        
        // 4. Tarif positif
        if (c.getTarifConsultation() != null && c.getTarifConsultation().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le tarif de consultation ne peut pas être négatif");
        }
    }

    /**
     * Récupère toutes les consultations
     */
    public List<Consultation> listerToutesConsultations() {
        return consultationDAO.findAll();
    }

    /**
     * Récupère une consultation par ID
     */
    public Consultation getConsultationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de consultation ne peut pas être null");
        }
        return consultationDAO.findById(id);
    }

    /**
     * Récupère une consultation par ID de rendez-vous
     */
    public Consultation getConsultationByRendezVous(Long rdvId) {
        if (rdvId == null) {
            throw new IllegalArgumentException("L'ID du rendez-vous ne peut pas être null");
        }
        return consultationDAO.findByRendezVousId(rdvId);
    }

    /**
     * Met à jour une consultation existante
     */
    public void modifierConsultation(Consultation c) {
        if (c.getId() == null) {
            throw new IllegalArgumentException("Impossible de modifier une consultation sans ID");
        }
        
        validerConsultation(c);
        consultationDAO.update(c);
        logger.info("Consultation modifiée : ID {}", c.getId());
    }

    /**
     * Supprime une consultation
     */
    public void supprimerConsultation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de consultation ne peut pas être null");
        }
        
        consultationDAO.delete(id);
        logger.info("Consultation supprimée : ID {}", id);
    }

    /**
     * Récupère les consultations d'un patient
     */
    public List<Consultation> getConsultationsPatient(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("L'ID du patient ne peut pas être null");
        }
        return consultationDAO.findByPatientId(patientId);
    }

    /**
     * Récupère les consultations d'un médecin
     */
    public List<Consultation> getConsultationsMedecin(Long medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin ne peut pas être null");
        }
        return consultationDAO.findByMedecinId(medecinId);
    }
}