-- ============================================
-- VUES
-- Healthcare Patient Records System
-- ============================================

-- ============================================
-- VUE 1 : V_PLANNING_MEDECIN
-- Description : Vue du planning des medecins
-- ============================================

CREATE OR REPLACE VIEW V_PLANNING_MEDECIN AS
SELECT 
    r.id_rdv,
    r.date_rdv,
    r.heure_debut,
    r.heure_fin,
    r.statut,
    r.salle,
    m.id_medecin,
    m.nom || ' ' || m.prenom AS nom_medecin,
    m.specialite,
    p.id_patient,
    p.nom || ' ' || p.prenom AS nom_patient,
    p.telephone AS telephone_patient,
    r.motif,
    FN_CALCULER_AGE(p.date_naissance) AS age_patient
FROM RENDEZ_VOUS r
JOIN MEDECIN m ON r.id_medecin = m.id_medecin
JOIN PATIENT p ON r.id_patient = p.id_patient
WHERE r.statut IN ('PLANIFIE', 'CONFIRME')
ORDER BY r.date_rdv, r.heure_debut;

COMMENT ON TABLE V_PLANNING_MEDECIN IS 'Vue du planning des rendez-vous des medecins';

-- ============================================
-- VUE 2 : V_FACTURES_IMPAYEES
-- Description : Liste des factures non payees ou partiellement payees
-- ============================================

CREATE OR REPLACE VIEW V_FACTURES_IMPAYEES AS
SELECT 
    f.id_facture,
    f.numero_facture,
    f.date_facture,
    f.montant_total,
    f.montant_paye,
    f.montant_total - f.montant_paye AS reste_a_payer,
    f.statut_paiement,
    TRUNC(SYSDATE - f.date_facture) AS jours_impaye,
    p.id_patient,
    p.nom || ' ' || p.prenom AS nom_patient,
    p.telephone AS telephone_patient,
    p.email AS email_patient,
    c.date_consultation,
    m.nom || ' ' || m.prenom AS nom_medecin
FROM FACTURE f
JOIN PATIENT p ON f.id_patient = p.id_patient
JOIN CONSULTATION c ON f.id_consultation = c.id_consultation
JOIN RENDEZ_VOUS r ON c.id_rdv = r.id_rdv
JOIN MEDECIN m ON r.id_medecin = m.id_medecin
WHERE f.statut_paiement IN ('EN_ATTENTE', 'PARTIEL')
ORDER BY f.date_facture;

COMMENT ON TABLE V_FACTURES_IMPAYEES IS 'Vue des factures impayees ou partiellement payees';

-- ============================================
-- VUE 3 : V_HISTORIQUE_PATIENT
-- Description : Historique medical complet d'un patient
-- ============================================

CREATE OR REPLACE VIEW V_HISTORIQUE_PATIENT AS
SELECT 
    p.id_patient,
    p.nom || ' ' || p.prenom AS nom_patient,
    p.cin,
    FN_CALCULER_AGE(p.date_naissance) AS age,
    p.groupe_sanguin,
    p.allergies,
    c.id_consultation,
    c.date_consultation,
    r.motif,
    c.symptomes,
    c.diagnostic,
    c.observations,
    c.prescription,
    c.examens_demandes,
    c.tarif_consultation,
    m.nom || ' ' || m.prenom AS nom_medecin,
    m.specialite,
    d.nom_departement,
    f.numero_facture,
    f.montant_total AS montant_facture,
    f.statut_paiement
FROM PATIENT p
LEFT JOIN RENDEZ_VOUS r ON p.id_patient = r.id_patient
LEFT JOIN CONSULTATION c ON r.id_rdv = c.id_rdv
LEFT JOIN MEDECIN m ON r.id_medecin = m.id_medecin
LEFT JOIN DEPARTEMENT d ON m.id_departement = d.id_departement
LEFT JOIN FACTURE f ON c.id_consultation = f.id_consultation
ORDER BY p.id_patient, c.date_consultation DESC;

COMMENT ON TABLE V_HISTORIQUE_PATIENT IS 'Historique medical complet des patients';

-- ============================================
-- VUE 4 : V_TRAITEMENTS_PRESCRITS
-- Description : Vue des traitements prescrits avec details
-- ============================================

CREATE OR REPLACE VIEW V_TRAITEMENTS_PRESCRITS AS
SELECT 
    t.id_traitement,
    c.date_consultation,
    p.id_patient,
    p.nom || ' ' || p.prenom AS nom_patient,
    m.id_medecin,
    m.nom || ' ' || m.prenom AS nom_medecin,
    med.nom_commercial AS medicament,
    med.principe_actif,
    med.forme,
    t.posologie,
    t.duree_traitement,
    t.quantite,
    t.instructions,
    t.quantite * med.prix_unitaire AS montant_traitement
FROM TRAITEMENT t
JOIN CONSULTATION c ON t.id_consultation = c.id_consultation
JOIN RENDEZ_VOUS r ON c.id_rdv = r.id_rdv
JOIN PATIENT p ON r.id_patient = p.id_patient
JOIN MEDECIN m ON r.id_medecin = m.id_medecin
JOIN MEDICAMENT med ON t.id_medicament = med.id_medicament
ORDER BY c.date_consultation DESC;

COMMENT ON TABLE V_TRAITEMENTS_PRESCRITS IS 'Vue des traitements prescrits avec details complets';

-- ============================================
-- VUE 5 : V_STATISTIQUES_MEDECINS
-- Description : Statistiques par medecin
-- ============================================

CREATE OR REPLACE VIEW V_STATISTIQUES_MEDECINS AS
SELECT 
    m.id_medecin,
    m.nom || ' ' || m.prenom AS nom_medecin,
    m.specialite,
    d.nom_departement,
    COUNT(DISTINCT r.id_rdv) AS nombre_rdv_total,
    COUNT(DISTINCT CASE WHEN r.statut = 'TERMINE' THEN r.id_rdv END) AS nombre_consultations,
    COUNT(DISTINCT CASE WHEN r.statut = 'ANNULE' THEN r.id_rdv END) AS nombre_annulations,
    COUNT(DISTINCT c.id_consultation) AS nombre_consultations_realisees,
    ROUND(AVG(c.tarif_consultation), 2) AS tarif_moyen,
    SUM(f.montant_total) AS chiffre_affaires_total
FROM MEDECIN m
LEFT JOIN DEPARTEMENT d ON m.id_departement = d.id_departement
LEFT JOIN RENDEZ_VOUS r ON m.id_medecin = r.id_medecin
LEFT JOIN CONSULTATION c ON r.id_rdv = c.id_rdv
LEFT JOIN FACTURE f ON c.id_consultation = f.id_consultation
GROUP BY m.id_medecin, m.nom, m.prenom, m.specialite, d.nom_departement
ORDER BY nombre_consultations DESC;

COMMENT ON TABLE V_STATISTIQUES_MEDECINS IS 'Statistiques de performance par medecin';

-- ============================================
-- VUE 6 : V_STOCK_MEDICAMENTS
-- Description : Etat du stock des medicaments avec alertes
-- ============================================

CREATE OR REPLACE VIEW V_STOCK_MEDICAMENTS AS
SELECT 
    id_medicament,
    nom_commercial,
    principe_actif,
    forme,
    dosage,
    prix_unitaire,
    stock_disponible,
    stock_alerte,
    stock_disponible * prix_unitaire AS valeur_stock,
    CASE 
        WHEN stock_disponible = 0 THEN 'RUPTURE'
        WHEN stock_disponible <= stock_alerte THEN 'ALERTE'
        WHEN stock_disponible <= stock_alerte * 2 THEN 'ATTENTION'
        ELSE 'OK'
    END AS statut_stock,
    CASE 
        WHEN stock_disponible = 0 THEN 0
        WHEN stock_disponible <= stock_alerte THEN 1
        WHEN stock_disponible <= stock_alerte * 2 THEN 2
        ELSE 3
    END AS niveau_urgence
FROM MEDICAMENT
ORDER BY niveau_urgence, stock_disponible;

COMMENT ON TABLE V_STOCK_MEDICAMENTS IS 'Etat du stock des medicaments avec niveaux d''alerte';

-- ============================================
-- VUE 7 : V_REVENUS_JOURNALIERS
-- Description : Revenus journaliers de l'hopital
-- ============================================

CREATE OR REPLACE VIEW V_REVENUS_JOURNALIERS AS
SELECT 
    TRUNC(f.date_facture) AS date_jour,
    COUNT(DISTINCT f.id_facture) AS nombre_factures,
    COUNT(DISTINCT c.id_consultation) AS nombre_consultations,
    SUM(f.montant_consultation) AS total_consultations,
    SUM(f.montant_medicaments) AS total_medicaments,
    SUM(f.montant_total) AS total_jour,
    SUM(f.montant_paye) AS montant_encaisse,
    SUM(f.montant_total - f.montant_paye) AS montant_restant,
    ROUND(SUM(f.montant_paye) * 100.0 / NULLIF(SUM(f.montant_total), 0), 2) AS taux_encaissement
FROM FACTURE f
JOIN CONSULTATION c ON f.id_consultation = c.id_consultation
GROUP BY TRUNC(f.date_facture)
ORDER BY date_jour DESC;

COMMENT ON TABLE V_REVENUS_JOURNALIERS IS 'Statistiques de revenus par jour';

-- ============================================
-- VUE 8 : V_PATIENTS_ACTIFS
-- Description : Liste des patients avec leurs dernieres consultations
-- ============================================

CREATE OR REPLACE VIEW V_PATIENTS_ACTIFS AS
SELECT 
    p.id_patient,
    p.cin,
    p.nom || ' ' || p.prenom AS nom_complet,
    FN_CALCULER_AGE(p.date_naissance) AS age,
    p.sexe,
    p.groupe_sanguin,
    p.telephone,
    p.email,
    p.ville,
    COUNT(DISTINCT c.id_consultation) AS nombre_consultations,
    MAX(c.date_consultation) AS derniere_consultation,
    SUM(f.montant_total) AS total_depense,
    SUM(f.montant_total - f.montant_paye) AS solde_impaye,
    CASE 
        WHEN MAX(c.date_consultation) IS NULL THEN 'Nouveau patient'
        WHEN MAX(c.date_consultation) >= ADD_MONTHS(SYSDATE, -3) THEN 'Patient actif'
        WHEN MAX(c.date_consultation) >= ADD_MONTHS(SYSDATE, -12) THEN 'Patient occasionnel'
        ELSE 'Patient inactif'
    END AS categorie_patient
FROM PATIENT p
LEFT JOIN RENDEZ_VOUS r ON p.id_patient = r.id_patient
LEFT JOIN CONSULTATION c ON r.id_rdv = c.id_rdv
LEFT JOIN FACTURE f ON c.id_consultation = f.id_consultation
GROUP BY p.id_patient, p.cin, p.nom, p.prenom, 
         p.date_naissance, p.sexe, p.groupe_sanguin, p.telephone, p.email, p.ville
ORDER BY derniere_consultation DESC NULLS LAST;

COMMENT ON TABLE V_PATIENTS_ACTIFS IS 'Liste des patients avec leur activite';

-- ============================================
-- VUE 9 : V_RDV_DU_JOUR
-- Description : Liste des rendez-vous du jour
-- ============================================

CREATE OR REPLACE VIEW V_RDV_DU_JOUR AS
SELECT 
    r.id_rdv,
    r.heure_debut,
    r.heure_fin,
    r.statut,
    r.salle,
    m.nom || ' ' || m.prenom AS medecin,
    m.specialite,
    p.nom || ' ' || p.prenom AS patient,
    p.telephone AS telephone_patient,
    FN_CALCULER_AGE(p.date_naissance) AS age_patient,
    r.motif,
    d.nom_departement
FROM RENDEZ_VOUS r
JOIN MEDECIN m ON r.id_medecin = m.id_medecin
JOIN PATIENT p ON r.id_patient = p.id_patient
JOIN DEPARTEMENT d ON m.id_departement = d.id_departement
WHERE r.date_rdv = TRUNC(SYSDATE)
  AND r.statut NOT IN ('ANNULE')
ORDER BY r.heure_debut;

COMMENT ON TABLE V_RDV_DU_JOUR IS 'Rendez-vous du jour en cours';

-- ============================================
-- VUE 10 : V_DASHBOARD_GLOBAL
-- Description : Vue pour le dashboard principal
-- ============================================

CREATE OR REPLACE VIEW V_DASHBOARD_GLOBAL AS
SELECT 
    (SELECT COUNT(*) FROM PATIENT) AS total_patients,
    (SELECT COUNT(*) FROM MEDECIN) AS total_medecins,
    (SELECT COUNT(*) FROM RENDEZ_VOUS WHERE date_rdv = TRUNC(SYSDATE)) AS rdv_aujourdhui,
    (SELECT COUNT(*) FROM RENDEZ_VOUS WHERE date_rdv = TRUNC(SYSDATE) AND statut = 'TERMINE') AS consultations_aujourdhui,
    (SELECT COUNT(*) FROM FACTURE WHERE statut_paiement IN ('EN_ATTENTE', 'PARTIEL')) AS factures_impayees,
    (SELECT SUM(montant_total - montant_paye) FROM FACTURE WHERE statut_paiement IN ('EN_ATTENTE', 'PARTIEL')) AS montant_impaye_total,
    (SELECT SUM(montant_paye) FROM FACTURE WHERE TRUNC(date_facture) = TRUNC(SYSDATE)) AS revenus_aujourdhui,
    (SELECT SUM(montant_paye) FROM FACTURE WHERE TRUNC(date_facture) >= TRUNC(SYSDATE, 'MM')) AS revenus_mois_courant,
    (SELECT COUNT(*) FROM MEDICAMENT WHERE stock_disponible <= stock_alerte) AS medicaments_stock_faible
FROM DUAL;

COMMENT ON TABLE V_DASHBOARD_GLOBAL IS 'Indicateurs cles pour le dashboard';

PROMPT ============================================
PROMPT Vues creees avec succes !
PROMPT ============================================

-- Test des vues

-- Ces requetes fonctionneront une fois les donnees inserees
-- SELECT * FROM V_PLANNING_MEDECIN WHERE ROWNUM <= 5;
-- SELECT * FROM V_FACTURES_IMPAYEES WHERE ROWNUM <= 5;
-- SELECT * FROM V_DASHBOARD_GLOBAL;