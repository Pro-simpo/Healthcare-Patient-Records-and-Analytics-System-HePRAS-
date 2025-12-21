-- ============================================
-- FONCTIONS PL/SQL
-- Healthcare Patient Records System
-- ============================================

-- ============================================
-- FONCTION 1 : FN_CALCULER_AGE
-- Description : Calculer l'age d'un patient
-- ============================================

CREATE OR REPLACE FUNCTION FN_CALCULER_AGE (
    p_date_naissance IN DATE
) RETURN NUMBER
AS
    v_age NUMBER;
BEGIN
    v_age := TRUNC(MONTHS_BETWEEN(SYSDATE, p_date_naissance) / 12);
    RETURN v_age;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END FN_CALCULER_AGE;
/

-- ============================================
-- FONCTION 2 : FN_VERIFIER_DISPO_MEDECIN
-- Description : Verifier si un medecin est disponible
-- Retourne : 1 si disponible, 0 sinon
-- ============================================

CREATE OR REPLACE FUNCTION FN_VERIFIER_DISPO_MEDECIN (
    p_id_medecin IN NUMBER,
    p_date_rdv IN DATE,
    p_heure_debut IN TIMESTAMP,
    p_heure_fin IN TIMESTAMP
) RETURN NUMBER
AS
    v_conflict_count NUMBER;
BEGIN
    -- Compter les rendez-vous qui se chevauchent
    SELECT COUNT(*) INTO v_conflict_count
    FROM RENDEZ_VOUS
    WHERE id_medecin = p_id_medecin
      AND date_rdv = p_date_rdv
      AND statut NOT IN ('ANNULE')
      AND (
          (p_heure_debut >= heure_debut AND p_heure_debut < heure_fin) OR
          (p_heure_fin > heure_debut AND p_heure_fin <= heure_fin) OR
          (p_heure_debut <= heure_debut AND p_heure_fin >= heure_fin)
      );
    
    -- Retourner 1 si disponible, 0 sinon
    IF v_conflict_count = 0 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END FN_VERIFIER_DISPO_MEDECIN;
/

-- ============================================
-- FONCTION 3 : FN_GENERER_NUM_FACTURE
-- Description : Generer un numero de facture unique
-- Format : FAC-YYYY-NNNN
-- ============================================

CREATE OR REPLACE FUNCTION FN_GENERER_NUM_FACTURE
RETURN VARCHAR2
AS
    v_annee VARCHAR2(4);
    v_numero NUMBER;
    v_num_facture VARCHAR2(20);
BEGIN
    -- Recuperer l'annee courante
    v_annee := TO_CHAR(SYSDATE, 'YYYY');
    
    -- Compter le nombre de factures de l'annee
    SELECT COUNT(*) + 1 INTO v_numero
    FROM FACTURE
    WHERE TO_CHAR(date_facture, 'YYYY') = v_annee;
    
    -- Generer le numero de facture
    v_num_facture := 'FAC-' || v_annee || '-' || LPAD(v_numero, 4, '0');
    
    RETURN v_num_facture;
    
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'FAC-' || v_annee || '-' || LPAD(seq_facture.NEXTVAL, 4, '0');
END FN_GENERER_NUM_FACTURE;
/

-- ============================================
-- FONCTION 4 : FN_CALCULER_MONTANT_MEDICAMENTS
-- Description : Calculer le montant total des medicaments d'une consultation
-- ============================================

CREATE OR REPLACE FUNCTION FN_CALCULER_MONTANT_MEDICAMENTS (
    p_id_consultation IN NUMBER
) RETURN NUMBER
AS
    v_montant_total NUMBER := 0;
BEGIN
    SELECT NVL(SUM(t.quantite * m.prix_unitaire), 0)
    INTO v_montant_total
    FROM TRAITEMENT t
    JOIN MEDICAMENT m ON t.id_medicament = m.id_medicament
    WHERE t.id_consultation = p_id_consultation;
    
    RETURN v_montant_total;
    
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END FN_CALCULER_MONTANT_MEDICAMENTS;
/

-- ============================================
-- FONCTION 5 : FN_OBTENIR_NOM_COMPLET_PATIENT
-- Description : Retourner le nom complet d'un patient
-- ============================================

CREATE OR REPLACE FUNCTION FN_OBTENIR_NOM_COMPLET_PATIENT (
    p_id_patient IN NUMBER
) RETURN VARCHAR2
AS
    v_nom_complet VARCHAR2(200);
BEGIN
    SELECT nom || ' ' || prenom
    INTO v_nom_complet
    FROM PATIENT
    WHERE id_patient = p_id_patient;
    
    RETURN v_nom_complet;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'Patient inconnu';
    WHEN OTHERS THEN
        RETURN 'Erreur';
END FN_OBTENIR_NOM_COMPLET_PATIENT;
/

-- ============================================
-- FONCTION 6 : FN_OBTENIR_NOM_COMPLET_MEDECIN
-- Description : Retourner le nom complet d'un medecin avec titre
-- ============================================

CREATE OR REPLACE FUNCTION FN_OBTENIR_NOM_COMPLET_MEDECIN (
    p_id_medecin IN NUMBER
) RETURN VARCHAR2
AS
    v_nom_complet VARCHAR2(200);
    v_specialite VARCHAR2(50);
BEGIN
    SELECT 'Dr. ' || nom || ' ' || prenom, specialite
    INTO v_nom_complet, v_specialite
    FROM MEDECIN
    WHERE id_medecin = p_id_medecin;
    
    RETURN v_nom_complet || ' (' || v_specialite || ')';
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'Medecin inconnu';
    WHEN OTHERS THEN
        RETURN 'Erreur';
END FN_OBTENIR_NOM_COMPLET_MEDECIN;
/

-- ============================================
-- FONCTION 7 : FN_COMPTER_RDV_JOUR
-- Description : Compter le nombre de RDV d'un medecin pour un jour
-- ============================================

CREATE OR REPLACE FUNCTION FN_COMPTER_RDV_JOUR (
    p_id_medecin IN NUMBER,
    p_date IN DATE
) RETURN NUMBER
AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM RENDEZ_VOUS
    WHERE id_medecin = p_id_medecin
      AND date_rdv = p_date
      AND statut NOT IN ('ANNULE');
    
    RETURN v_count;
    
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END FN_COMPTER_RDV_JOUR;
/

-- ============================================
-- FONCTION 8 : FN_CALCULER_MONTANT_TOTAL_FACTURE
-- Description : Calculer le montant total d'une facture
-- ============================================

CREATE OR REPLACE FUNCTION FN_CALCULER_MONTANT_TOTAL_FACTURE (
    p_montant_consultation IN NUMBER,
    p_montant_medicaments IN NUMBER
) RETURN NUMBER
AS
BEGIN
    RETURN NVL(p_montant_consultation, 0) + NVL(p_montant_medicaments, 0);
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END FN_CALCULER_MONTANT_TOTAL_FACTURE;
/

-- ============================================
-- FONCTION 9 : FN_VERIFIER_STOCK_MEDICAMENT
-- Description : Verifier si le stock est suffisant
-- Retourne : 1 si suffisant, 0 sinon
-- ============================================

CREATE OR REPLACE FUNCTION FN_VERIFIER_STOCK_MEDICAMENT (
    p_id_medicament IN NUMBER,
    p_quantite_demandee IN NUMBER
) RETURN NUMBER
AS
    v_stock_dispo NUMBER;
BEGIN
    SELECT stock_disponible
    INTO v_stock_dispo
    FROM MEDICAMENT
    WHERE id_medicament = p_id_medicament;
    
    IF v_stock_dispo >= p_quantite_demandee THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        RETURN 0;
END FN_VERIFIER_STOCK_MEDICAMENT;
/

PROMPT ============================================
PROMPT Fonctions creees avec succes !
PROMPT ============================================