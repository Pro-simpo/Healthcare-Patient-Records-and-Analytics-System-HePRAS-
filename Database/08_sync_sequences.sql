-- ============================================
-- SCRIPT DE RESYNCHRONISATION DES SEQUENCES
-- Healthcare Patient Records System
-- ============================================
-- À exécuter APRÈS le script 07_DATA.sql
-- ============================================

SET SERVEROUTPUT ON;

DECLARE
    v_max_id NUMBER;
    v_sql VARCHAR2(500);
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== DEBUT RESYNCHRONISATION DES SEQUENCES ===');
    DBMS_OUTPUT.PUT_LINE('');
    
    -- ============================================
    -- 1. SEQUENCE PATIENT
    -- ============================================
    SELECT NVL(MAX(id_patient), 0) INTO v_max_id FROM PATIENT;
    v_sql := 'DROP SEQUENCE seq_patient';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_patient START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_patient resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 2. SEQUENCE MEDECIN
    -- ============================================
    SELECT NVL(MAX(id_medecin), 0) INTO v_max_id FROM MEDECIN;
    v_sql := 'DROP SEQUENCE seq_medecin';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_medecin START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_medecin resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 3. SEQUENCE DEPARTEMENT
    -- ============================================
    SELECT NVL(MAX(id_departement), 0) INTO v_max_id FROM DEPARTEMENT;
    v_sql := 'DROP SEQUENCE seq_departement';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_departement START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_departement resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 4. SEQUENCE RENDEZ_VOUS
    -- ============================================
    SELECT NVL(MAX(id_rdv), 0) INTO v_max_id FROM RENDEZ_VOUS;
    v_sql := 'DROP SEQUENCE seq_rdv';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_rdv START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_rdv resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 5. SEQUENCE CONSULTATION
    -- ============================================
    SELECT NVL(MAX(id_consultation), 0) INTO v_max_id FROM CONSULTATION;
    v_sql := 'DROP SEQUENCE seq_consultation';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_consultation START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_consultation resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 6. SEQUENCE TRAITEMENT
    -- ============================================
    SELECT NVL(MAX(id_traitement), 0) INTO v_max_id FROM TRAITEMENT;
    v_sql := 'DROP SEQUENCE seq_traitement';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_traitement START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_traitement resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 7. SEQUENCE MEDICAMENT
    -- ============================================
    SELECT NVL(MAX(id_medicament), 0) INTO v_max_id FROM MEDICAMENT;
    v_sql := 'DROP SEQUENCE seq_medicament';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_medicament START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_medicament resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 8. SEQUENCE FACTURE
    -- ============================================
    SELECT NVL(MAX(id_facture), 0) INTO v_max_id FROM FACTURE;
    v_sql := 'DROP SEQUENCE seq_facture';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_facture START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_facture resynchronisée : START WITH ' || (v_max_id + 1));
    
    -- ============================================
    -- 9. SEQUENCE UTILISATEUR
    -- ============================================
    SELECT NVL(MAX(id_utilisateur), 0) INTO v_max_id FROM UTILISATEUR;
    v_sql := 'DROP SEQUENCE seq_utilisateur';
    EXECUTE IMMEDIATE v_sql;
    v_sql := 'CREATE SEQUENCE seq_utilisateur START WITH ' || (v_max_id + 1) || ' INCREMENT BY 1 NOCACHE';
    EXECUTE IMMEDIATE v_sql;
    DBMS_OUTPUT.PUT_LINE('seq_utilisateur resynchronisée : START WITH ' || (v_max_id + 1));
    
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('=== RESYNCHRONISATION TERMINEE ===');
    
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERREUR : ' || SQLERRM);
        RAISE;
END;
/

-- ============================================
-- VERIFICATION DES SEQUENCES
-- ============================================
PROMPT
PROMPT === VERIFICATION DES SEQUENCES ===

SELECT 
    sequence_name,
    last_number AS "Prochaine valeur"
FROM user_sequences
WHERE sequence_name LIKE 'SEQ_%'
ORDER BY sequence_name;

PROMPT
PROMPT === VERIFICATION DES DONNEES ===

SELECT 'PATIENT' AS Table_Name, COUNT(*) AS Nb_Lignes, NVL(MAX(id_patient), 0) AS Max_ID FROM PATIENT
UNION ALL
SELECT 'MEDECIN', COUNT(*), NVL(MAX(id_medecin), 0) FROM MEDECIN
UNION ALL
SELECT 'DEPARTEMENT', COUNT(*), NVL(MAX(id_departement), 0) FROM DEPARTEMENT
UNION ALL
SELECT 'RENDEZ_VOUS', COUNT(*), NVL(MAX(id_rdv), 0) FROM RENDEZ_VOUS
UNION ALL
SELECT 'CONSULTATION', COUNT(*), NVL(MAX(id_consultation), 0) FROM CONSULTATION
UNION ALL
SELECT 'TRAITEMENT', COUNT(*), NVL(MAX(id_traitement), 0) FROM TRAITEMENT
UNION ALL
SELECT 'MEDICAMENT', COUNT(*), NVL(MAX(id_medicament), 0) FROM MEDICAMENT
UNION ALL
SELECT 'FACTURE', COUNT(*), NVL(MAX(id_facture), 0) FROM FACTURE
UNION ALL
SELECT 'UTILISATEUR', COUNT(*), NVL(MAX(id_utilisateur), 0) FROM UTILISATEUR
ORDER BY Table_Name;