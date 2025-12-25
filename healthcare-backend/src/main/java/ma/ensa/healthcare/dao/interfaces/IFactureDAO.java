package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Facture;
import java.util.List;

import ma.ensa.healthcare.model.enums.ModePaiement;
import ma.ensa.healthcare.model.enums.StatutPaiement;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface DAO pour l'entité Facture
 */
public interface IFactureDAO {
    /**
     * Enregistre une nouvelle facture
     */
    Facture save(Facture facture);
    
    /**
     * Recherche une facture par son ID
     */
    Facture findById(Long id);
    
    /**
     * Récupère toutes les factures
     */
    List<Facture> findAll();
    
    /**
     * Met à jour une facture complète
     */
    void update(Facture facture);
    
    /**
     * Supprime une facture
     */
    void delete(Long id);
    
    /**
     * Met à jour uniquement le statut d'une facture
     */
    void updateStatut(Long id, String statut);

    void enregistrerPaiement(Long factureId, BigDecimal montant, ModePaiement modePaiement, LocalDate datePaiement);
    List<Facture> findByPatientId(Long patientId);
    Facture findByNumero(String numeroFacture);
    List<Facture> findFacturesImpayees();
    List<Facture> findByStatut(StatutPaiement statut);
    Facture findByConsultationId(Long consultationId);
    BigDecimal getTotalImpaye();
    BigDecimal getRevenusPeriode(LocalDate dateDebut, LocalDate dateFin);
    String genererNumeroFacture();
}