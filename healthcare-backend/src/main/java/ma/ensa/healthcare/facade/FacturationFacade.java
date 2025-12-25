package ma.ensa.healthcare.facade;

import ma.ensa.healthcare.dto.FactureDTO;
import ma.ensa.healthcare.model.Facture;
import ma.ensa.healthcare.model.enums.ModePaiement;
import ma.ensa.healthcare.model.enums.StatutPaiement;
import ma.ensa.healthcare.service.FacturationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade pour simplifier les opérations de facturation et paiement
 */
public class FacturationFacade {
    private final FacturationService facturationService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FacturationFacade() {
        this.facturationService = new FacturationService();
    }

    /**
     * Encaisse un paiement complet pour une facture
     * 
     * @param factureId ID de la facture
     * @param mode Mode de paiement (ESPECES, CARTE, CHEQUE, VIREMENT)
     */
    public void encaisserPaiementComplet(Long factureId, ModePaiement mode) {
        Facture facture = facturationService.getFactureById(factureId);
        
        if (facture == null) {
            throw new RuntimeException("Facture introuvable : " + factureId);
        }
        
        // Calculer le montant restant à payer
        BigDecimal montantRestant = facture.getMontantTotal().subtract(facture.getMontantPaye());
        
        // Enregistrer le paiement complet
        facturationService.encaisserPaiement(factureId, montantRestant, mode, LocalDate.now());
    }

    /**
     * Encaisse un paiement partiel pour une facture
     * 
     * @param factureId ID de la facture
     * @param montant Montant à payer
     * @param mode Mode de paiement
     */
    public void encaisserPaiementPartiel(Long factureId, BigDecimal montant, ModePaiement mode) {
        facturationService.encaisserPaiement(factureId, montant, mode, LocalDate.now());
    }

    /**
     * Encaisse un paiement avec date personnalisée
     */
    public void encaisserPaiement(Long factureId, BigDecimal montant, 
                                   ModePaiement mode, LocalDate datePaiement) {
        facturationService.encaisserPaiement(factureId, montant, mode, datePaiement);
    }

    /**
     * Récupère toutes les factures
     */
    public List<FactureDTO> getToutesLesFactures() {
        return facturationService.getToutesLesFactures().stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les factures d'un patient
     */
    public List<FactureDTO> getFacturesPatient(Long patientId) {
        return facturationService.getFacturesPatient(patientId).stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les factures impayées
     */
    public List<FactureDTO> getFacturesImpayees() {
        return facturationService.getFacturesImpayees().stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les factures par statut
     */
    public List<FactureDTO> getFacturesParStatut(StatutPaiement statut) {
        return facturationService.getFacturesParStatut(statut).stream()
            .map(this::convertirEnDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère le total des impayés
     */
    public BigDecimal getTotalImpaye() {
        return facturationService.getTotalImpaye();
    }

    /**
     * Récupère les revenus d'une période
     */
    public BigDecimal getRevenusPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return facturationService.getRevenusPeriode(dateDebut, dateFin);
    }

    /**
     * Récupère les revenus du mois en cours
     */
    public BigDecimal getRevenusMoisEnCours() {
        LocalDate maintenant = LocalDate.now();
        LocalDate debutMois = maintenant.withDayOfMonth(1);
        LocalDate finMois = maintenant.withDayOfMonth(maintenant.lengthOfMonth());
        return facturationService.getRevenusPeriode(debutMois, finMois);
    }

    /**
     * Convertit une Facture en DTO pour l'affichage
     */
    private FactureDTO convertirEnDTO(Facture facture) {
        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setNumeroFacture(facture.getNumeroFacture());
        
        // Nom complet du patient
        if (facture.getPatient() != null) {
            String nomComplet = facture.getPatient().getNom() + " " + facture.getPatient().getPrenom();
            dto.setPatientNom(nomComplet);
            dto.setPatientCin(facture.getPatient().getCin());
        }
        
        // Dates formatées
        dto.setDateFacture(facture.getDateFacture().format(DATE_FORMATTER));
        if (facture.getDatePaiement() != null) {
            dto.setDatePaiement(facture.getDatePaiement().format(DATE_FORMATTER));
        }
        
        // Montants
        dto.setMontantConsultation(facture.getMontantConsultation());
        dto.setMontantMedicaments(facture.getMontantMedicaments());
        dto.setMontantTotal(facture.getMontantTotal());
        dto.setMontantPaye(facture.getMontantPaye());
        dto.setMontantRestant(facture.getMontantRestant());
        
        // Statut et mode
        dto.setStatutPaiement(facture.getStatutPaiement().name());
        if (facture.getModePaiement() != null) {
            dto.setModePaiement(facture.getModePaiement().name());
        }
        
        return dto;
    }

    /**
     * Récupère une facture par son numéro
     */
    public FactureDTO getFactureParNumero(String numeroFacture) {
        Facture facture = facturationService.getFactureParNumero(numeroFacture);
        return facture != null ? convertirEnDTO(facture) : null;
    }

    /**
     * Annule une facture (si possible)
     */
    public void annulerFacture(Long factureId) {
        facturationService.annulerFacture(factureId);
    }
}