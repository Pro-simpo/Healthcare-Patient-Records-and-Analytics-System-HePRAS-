package ma.ensa.healthcare.util;

import ma.ensa.healthcare.service.*;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import ma.ensa.healthcare.model.Facture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service d'export PDF pour les factures
 * Utilise iText 7 pour générer des PDF professionnels
 */
public class PdfExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfExportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final PatientService patientService = new PatientService();
    
    // Couleurs personnalisées
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(33, 150, 243); // Bleu
    private static final DeviceRgb SUCCESS_COLOR = new DeviceRgb(76, 175, 80);  // Vert
    private static final DeviceRgb DANGER_COLOR = new DeviceRgb(244, 67, 54);   // Rouge
    private static final DeviceRgb WARNING_COLOR = new DeviceRgb(255, 152, 0);  // Orange

    /**
     * Exporte une seule facture en PDF
     * 
     * @param facture La facture à exporter
     * @param outputPath Chemin du fichier de sortie (ex: "facture_FAC-2024-0001.pdf")
     * @return Le chemin complet du fichier créé
     */
    public static String exportFactureToPdf(Facture facture, String outputPath) {
        try {
            // Créer le fichier PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Marges du document
            document.setMargins(50, 50, 50, 50);

            // === EN-TÊTE ===
            addHeader(document);
            
            // === INFORMATIONS FACTURE ===
            addFactureInfo(document, facture);
            
            // === INFORMATIONS PATIENT ===
            addPatientInfo(document, facture);
            
            // === DÉTAILS DE LA FACTURE ===
            addFactureDetails(document, facture);
            
            // === PIED DE PAGE ===
            addFooter(document);
            
            // Fermer le document
            document.close();
            
            logger.info("PDF généré avec succès : {}", outputPath);
            return outputPath;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du PDF", e);
            throw new RuntimeException("Impossible de générer le PDF : " + e.getMessage(), e);
        }
    }

    /**
     * Exporte une liste de factures en PDF (rapport)
     * 
     * @param factures Liste des factures à exporter
     * @param outputPath Chemin du fichier de sortie
     * @return Le chemin complet du fichier créé
     */
    public static String exportFacturesListToPdf(List<Facture> factures, String outputPath) {
        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(50, 50, 50, 50);

            // === EN-TÊTE ===
            Paragraph title = new Paragraph("RAPPORT DES FACTURES")
                .setFontSize(24)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Généré le " + LocalDateTime.now().format(DATETIME_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
            document.add(subtitle);

            // === STATISTIQUES GLOBALES ===
            addGlobalStats(document, factures);

            // === TABLE DES FACTURES ===
            Table table = new Table(new float[]{1, 2, 2, 2, 2, 2, 1.5f});
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginTop(20);

            // En-têtes
            String[] headers = {"N° Facture", "Date", "Patient", "Total", "Payé", "Reste", "Statut"};
            for (String header : headers) {
                Cell cell = new Cell()
                    .add(new Paragraph(header).setBold().setFontSize(10))
                    .setBackgroundColor(PRIMARY_COLOR)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
                table.addHeaderCell(cell);
            }

            // Données
            for (Facture f : factures) {
                table.addCell(createCell(f.getNumeroFacture()));
                table.addCell(createCell(f.getDateFacture().format(DATE_FORMATTER)));

                
                String patientNom = f.getIdPatient() != 0 ? 
                    patientService.getPatientById(f.getIdPatient()).getNom() + " " + patientService.getPatientById(f.getIdPatient()).getPrenom() : "N/A";
                table.addCell(createCell(patientNom));
                
                table.addCell(createCell(formatMontant(f.getMontantTotal())));
                table.addCell(createCell(formatMontant(f.getMontantPaye())));
                table.addCell(createCell(formatMontant(f.getMontantRestant())));
                
                Cell statutCell = createCell(f.getStatutPaiement().name());
                switch (f.getStatutPaiement().name()) {
                    case "PAYE" -> statutCell.setFontColor(SUCCESS_COLOR).setBold();
                    case "PARTIEL" -> statutCell.setFontColor(WARNING_COLOR).setBold();
                    case "EN_ATTENTE" -> statutCell.setFontColor(DANGER_COLOR).setBold();
                }
                table.addCell(statutCell);
            }

            document.add(table);
            
            // Pied de page
            addFooter(document);
            
            document.close();
            logger.info("Rapport PDF généré : {} factures", factures.size());
            return outputPath;
            
        } catch (Exception e) {
            logger.error("Erreur génération rapport PDF", e);
            throw new RuntimeException("Impossible de générer le rapport : " + e.getMessage(), e);
        }
    }

    // === MÉTHODES PRIVÉES ===

    private static void addHeader(Document document) {
        try {
            // Logo + Nom de l'établissement
            Paragraph hospitalName = new Paragraph("HEALTHCARE SYSTEM")
                .setFontSize(26)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(hospitalName);
            
            Paragraph address = new Paragraph("Avenue Hassan II, Casablanca, Maroc\nTél: +212 5XX-XXXXXX")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
            document.add(address);
            
        } catch (Exception e) {
            logger.warn("Erreur ajout en-tête", e);
        }
    }

    private static void addFactureInfo(Document document, Facture facture) {
        Table infoTable = new Table(new float[]{1, 1});
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.setMarginBottom(20);

        // Colonne gauche - Infos facture
        Cell leftCell = new Cell()
            .setBorder(Border.NO_BORDER)
            .add(new Paragraph("FACTURE N°").setBold().setFontSize(14).setFontColor(PRIMARY_COLOR))
            .add(new Paragraph(facture.getNumeroFacture()).setFontSize(16).setBold())
            .add(new Paragraph("\nDate d'émission:").setBold().setFontSize(10).setMarginTop(10))
            .add(new Paragraph(facture.getDateFacture().format(DATE_FORMATTER)).setFontSize(12));
        
        // Colonne droite - Statut
        Cell rightCell = new Cell()
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT);
        
        String statutText = "STATUT: " + facture.getStatutPaiement().name();
        Paragraph statutPara = new Paragraph(statutText)
            .setBold()
            .setFontSize(14);
        
        switch (facture.getStatutPaiement().name()) {
            case "PAYE" -> statutPara.setFontColor(SUCCESS_COLOR);
            case "PARTIEL" -> statutPara.setFontColor(WARNING_COLOR);
            case "EN_ATTENTE" -> statutPara.setFontColor(DANGER_COLOR);
        }
        
        rightCell.add(statutPara);
        
        if (facture.getDatePaiement() != null) {
            rightCell.add(new Paragraph("Payée le: " + facture.getDatePaiement().format(DATE_FORMATTER))
                .setFontSize(10));
        }

        infoTable.addCell(leftCell);
        infoTable.addCell(rightCell);
        document.add(infoTable);
    }

    private static void addPatientInfo(Document document, Facture facture) {
        if (facture.getIdPatient() == 0) {
            return;
        }

        Table patientTable = new Table(1);
        patientTable.setWidth(UnitValue.createPercentValue(100));
        patientTable.setMarginBottom(20);

        Cell cell = new Cell()
            .setBackgroundColor(new DeviceRgb(245, 245, 245))
            .setPadding(15)
            .setBorder(new SolidBorder(PRIMARY_COLOR, 1))
            .add(new Paragraph("INFORMATIONS PATIENT").setBold().setFontColor(PRIMARY_COLOR))
            .add(new Paragraph("Nom: " + patientService.getPatientById(facture.getIdPatient()).getNom() + " " + 
                              patientService.getPatientById(facture.getIdPatient()).getPrenom()).setMarginTop(5))
            .add(new Paragraph("CIN: " + patientService.getPatientById(facture.getIdPatient()).getCin()))
            .add(new Paragraph("Téléphone: " + 
                (patientService.getPatientById(facture.getIdPatient()).getTelephone() != null ? patientService.getPatientById(facture.getIdPatient()).getTelephone() : "N/A")));

        patientTable.addCell(cell);
        document.add(patientTable);
    }

    private static void addFactureDetails(Document document, Facture facture) {
        // Titre
        Paragraph detailsTitle = new Paragraph("DÉTAILS DE LA FACTURE")
            .setBold()
            .setFontSize(14)
            .setFontColor(PRIMARY_COLOR)
            .setMarginBottom(10);
        document.add(detailsTitle);

        // Table des montants
        Table detailsTable = new Table(new float[]{3, 1});
        detailsTable.setWidth(UnitValue.createPercentValue(100));

        // Consultation
        detailsTable.addCell(createDetailCell("Consultation médicale", false));
        detailsTable.addCell(createAmountCell(formatMontant(facture.getMontantConsultation())));

        // Médicaments
        detailsTable.addCell(createDetailCell("Médicaments prescrits", false));
        detailsTable.addCell(createAmountCell(formatMontant(facture.getMontantMedicaments())));

        // Ligne de séparation
        Cell separatorCell = new Cell(1, 2)
            .setBorder(Border.NO_BORDER)
            .setBorderTop(new SolidBorder(ColorConstants.GRAY, 1))
            .setPadding(5);
        detailsTable.addCell(separatorCell);

        // Total
        detailsTable.addCell(createDetailCell("TOTAL", true));
        detailsTable.addCell(createAmountCell(formatMontant(facture.getMontantTotal()), true));

        // Montant payé
        detailsTable.addCell(createDetailCell("Montant payé", false)
            .setFontColor(SUCCESS_COLOR));
        detailsTable.addCell(createAmountCell(formatMontant(facture.getMontantPaye()))
            .setFontColor(SUCCESS_COLOR));

        // Reste à payer
        BigDecimal reste = facture.getMontantRestant();
        DeviceRgb resteColor = reste.compareTo(BigDecimal.ZERO) > 0 ? DANGER_COLOR : SUCCESS_COLOR;
        
        detailsTable.addCell(createDetailCell("Reste à payer", true)
            .setFontColor(resteColor));
        detailsTable.addCell(createAmountCell(formatMontant(reste), true)
            .setFontColor(resteColor));

        document.add(detailsTable);

        // Note de paiement
        if (facture.getModePaiement() != null) {
            Paragraph paymentNote = new Paragraph(
                "Mode de paiement: " + facture.getModePaiement().name())
                .setFontSize(10)
                .setItalic()
                .setMarginTop(10);
            document.add(paymentNote);
        }
    }

    private static void addGlobalStats(Document document, List<Facture> factures) {
        BigDecimal totalFacture = factures.stream()
            .map(Facture::getMontantTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPaye = factures.stream()
            .map(Facture::getMontantPaye)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalImpaye = factures.stream()
            .map(Facture::getMontantRestant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Table statsTable = new Table(4);
        statsTable.setWidth(UnitValue.createPercentValue(100));
        statsTable.setMarginBottom(20);

        statsTable.addCell(createStatCell("Nombre de factures", String.valueOf(factures.size()), PRIMARY_COLOR));
        statsTable.addCell(createStatCell("Total facturé", formatMontant(totalFacture), PRIMARY_COLOR));
        statsTable.addCell(createStatCell("Total encaissé", formatMontant(totalPaye), SUCCESS_COLOR));
        statsTable.addCell(createStatCell("Total impayé", formatMontant(totalImpaye), DANGER_COLOR));

        document.add(statsTable);
    }

    private static void addFooter(Document document) {
        Paragraph footer = new Paragraph(
            "\n\n_______________________________________________\n" +
            "Ce document a été généré automatiquement le " + 
            LocalDateTime.now().format(DATETIME_FORMATTER) + "\n" +
            "Healthcare System - Tous droits réservés © 2024")
            .setFontSize(9)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
            .setMarginTop(30);
        document.add(footer);
    }

    // === HELPERS ===

    private static Cell createCell(String text) {
        return new Cell()
            .add(new Paragraph(text).setFontSize(9))
            .setPadding(6)
            .setTextAlignment(TextAlignment.CENTER);
    }

    private static Cell createDetailCell(String text, boolean bold) {
        Cell cell = new Cell()
            .add(new Paragraph(text).setFontSize(11))
            .setBorder(Border.NO_BORDER)
            .setPaddingTop(8)
            .setPaddingBottom(8);
        
        if (bold) {
            cell.add(new Paragraph(text).setBold().setFontSize(12));
        }
        
        return cell;
    }

    private static Cell createAmountCell(String amount) {
        return createAmountCell(amount, false);
    }

    private static Cell createAmountCell(String amount, boolean bold) {
        Cell cell = new Cell()
            .add(new Paragraph(amount).setFontSize(11))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT)
            .setPaddingTop(8)
            .setPaddingBottom(8);
        
        if (bold) {
            cell.add(new Paragraph(amount).setBold().setFontSize(12));
        }
        
        return cell;
    }

    private static Cell createStatCell(String label, String value, DeviceRgb color) {
        return new Cell()
            .setBackgroundColor(new DeviceRgb(245, 245, 245))
            .setPadding(15)
            .setTextAlignment(TextAlignment.CENTER)
            .add(new Paragraph(label).setFontSize(10).setFontColor(ColorConstants.GRAY))
            .add(new Paragraph(value).setBold().setFontSize(16).setFontColor(color));
    }

    private static String formatMontant(BigDecimal montant) {
        return montant != null ? String.format("%.2f MAD", montant) : "0.00 MAD";
    }
}