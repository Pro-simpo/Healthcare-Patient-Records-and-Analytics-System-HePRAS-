-- ============================================
-- PROCEDURES STOCKEES PL/SQL
-- Healthcare Patient Records System
-- ============================================

-- ============================================
-- PROCEDURE 1 : SP_INSERT_PATIENT
-- Description : Inserer un nouveau patient
-- ============================================

CREATE OR REPLACE PROCEDURE SP_INSERT_PATIENT (
    p_cin IN VARCHAR2,
    p_nom IN VARCHAR2,
    p_prenom IN VARCHAR2,
    p_date_naissance IN DATE,
    p_sexe IN CHAR,
    p_adresse IN VARCHAR2 DEFAULT NULL,
    p_ville IN VARCHAR2 DEFAULT NULL,
    p_code_postal IN VARCHAR2 DEFAULT NULL,
    p_telephone IN VARCHAR2 DEFAULT NULL,
    p_email IN VARCHAR2 DEFAULT NULL,
    p_groupe_sanguin IN VARCHAR2 DEFAULT NULL,
    p_allergies IN VARCHAR2 DEFAULT NULL,
    p_id_patient OUT NUMBER
)
AS
BEGIN
    -- Generer le nouvel ID
    SELECT seq_patient.NEXTVAL INTO p_id_patient FROM DUAL;
    
    -- Inserer le patient
    INSERT INTO PATIENT (
        id_patient, cin, nom, prenom, date_naissance,
        sexe, adresse, ville, code_postal, telephone, email,
        groupe_sanguin, allergies, date_inscription
    ) VALUES (
        p_id_patient, p_cin, p_nom, p_prenom, p_date_naissance,
        p_sexe, p_adresse, p_ville, p_code_postal, p_telephone, p_email,
        p_groupe_sanguin, p_allergies, SYSDATE
    );
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Patient insere avec succes. ID: ' || p_id_patient);
    
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Numero CIN deja existant');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20002, 'Erreur lors de l''insertion du patient: ' || SQLERRM);
END SP_INSERT_PATIENT;
/

-- ============================================
-- PROCEDURE 2 : SP_INSERT_MEDECIN
-- Description : Inserer un nouveau medecin
-- ============================================

CREATE OR REPLACE PROCEDURE SP_INSERT_MEDECIN (
    p_numero_ordre IN VARCHAR2,
    p_nom IN VARCHAR2,
    p_prenom IN VARCHAR2,
    p_specialite IN VARCHAR2,
    p_telephone IN VARCHAR2,
    p_email IN VARCHAR2,
    p_date_embauche IN DATE,
    p_id_departement IN NUMBER,
    p_id_medecin OUT NUMBER
)
AS
    v_dept_count NUMBER;
BEGIN
    -- Verifier que le departement existe
    SELECT COUNT(*) INTO v_dept_count 
    FROM DEPARTEMENT 
    WHERE id_departement = p_id_departement;
    
    IF v_dept_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Departement inexistant');
    END IF;
    
    -- Generer le nouvel ID
    SELECT seq_medecin.NEXTVAL INTO p_id_medecin FROM DUAL;
    
    -- Inserer le medecin
    INSERT INTO MEDECIN (
        id_medecin, numero_ordre, nom, prenom, specialite,
        telephone, email, date_embauche, id_departement
    ) VALUES (
        p_id_medecin, p_numero_ordre, p_nom, p_prenom, p_specialite,
        p_telephone, p_email, p_date_embauche, p_id_departement
    );
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Medecin insere avec succes. ID: ' || p_id_medecin);
    
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20004, 'Numero d''ordre deja existant');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20005, 'Erreur lors de l''insertion du medecin: ' || SQLERRM);
END SP_INSERT_MEDECIN;
/

-- ============================================
-- PROCEDURE 3 : SP_CREER_RDV
-- Description : Creer un rendez-vous avec verification de disponibilite
-- ============================================

CREATE OR REPLACE PROCEDURE SP_CREER_RDV (
    p_id_patient IN NUMBER,
    p_id_medecin IN NUMBER,
    p_date_rdv IN DATE,
    p_heure_debut IN TIMESTAMP,
    p_heure_fin IN TIMESTAMP,
    p_motif IN VARCHAR2 DEFAULT NULL,
    p_salle IN VARCHAR2 DEFAULT NULL,
    p_id_rdv OUT NUMBER
)
AS
    v_conflict_count NUMBER;
    v_patient_exists NUMBER;
    v_medecin_exists NUMBER;
BEGIN
    -- Verifier que le patient existe
    SELECT COUNT(*) INTO v_patient_exists 
    FROM PATIENT 
    WHERE id_patient = p_id_patient;
    
    IF v_patient_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Patient inexistant');
    END IF;
    
    -- Verifier que le medecin existe
    SELECT COUNT(*) INTO v_medecin_exists 
    FROM MEDECIN 
    WHERE id_medecin = p_id_medecin;
    
    IF v_medecin_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'Medecin inexistant');
    END IF;
    
    -- Verifier que heure_fin > heure_debut
    IF p_heure_fin <= p_heure_debut THEN
        RAISE_APPLICATION_ERROR(-20008, 'L''heure de fin doit etre superieure a l''heure de debut');
    END IF;
    
    -- Verifier la disponibilite du medecin (pas de chevauchement)
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
    
    IF v_conflict_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20009, 'Le medecin n''est pas disponible a cet horaire');
    END IF;
    
    -- Generer le nouvel ID
    SELECT seq_rdv.NEXTVAL INTO p_id_rdv FROM DUAL;
    
    -- Inserer le rendez-vous
    INSERT INTO RENDEZ_VOUS (
        id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin,
        motif, statut, salle, date_creation
    ) VALUES (
        p_id_rdv, p_id_patient, p_id_medecin, p_date_rdv, p_heure_debut, p_heure_fin,
        p_motif, 'PLANIFIE', p_salle, SYSDATE
    );
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Rendez-vous cree avec succes. ID: ' || p_id_rdv);
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20010, 'Erreur lors de la creation du rendez-vous: ' || SQLERRM);
END SP_CREER_RDV;
/

-- ============================================
-- PROCEDURE 4 : SP_MODIFIER_STATUT_RDV
-- Description : Modifier le statut d'un rendez-vous
-- ============================================

CREATE OR REPLACE PROCEDURE SP_MODIFIER_STATUT_RDV (
    p_id_rdv IN NUMBER,
    p_nouveau_statut IN VARCHAR2
)
AS
    v_rdv_exists NUMBER;
    v_ancien_statut VARCHAR2(20);
BEGIN
    -- Verifier que le RDV existe
    SELECT COUNT(*), MAX(statut) 
    INTO v_rdv_exists, v_ancien_statut
    FROM RENDEZ_VOUS 
    WHERE id_rdv = p_id_rdv;
    
    IF v_rdv_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'Rendez-vous inexistant');
    END IF;
    
    -- Verifier que le nouveau statut est valide
    IF p_nouveau_statut NOT IN ('PLANIFIE', 'CONFIRME', 'TERMINE', 'ANNULE') THEN
        RAISE_APPLICATION_ERROR(-20012, 'Statut invalide. Valeurs possibles: PLANIFIE, CONFIRME, TERMINE, ANNULE');
    END IF;
    
    -- Mettre a jour le statut
    UPDATE RENDEZ_VOUS
    SET statut = p_nouveau_statut
    WHERE id_rdv = p_id_rdv;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Statut modifie: ' || v_ancien_statut || ' -> ' || p_nouveau_statut);
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20013, 'Erreur lors de la modification du statut: ' || SQLERRM);
END SP_MODIFIER_STATUT_RDV;
/

-- ============================================
-- PROCEDURE 5 : SP_CREER_CONSULTATION
-- Description : Creer une consultation a partir d'un rendez-vous
-- ============================================

CREATE OR REPLACE PROCEDURE SP_CREER_CONSULTATION (
    p_id_rdv IN NUMBER,
    p_symptomes IN VARCHAR2,
    p_diagnostic IN VARCHAR2,
    p_observations IN VARCHAR2 DEFAULT NULL,
    p_prescription IN VARCHAR2 DEFAULT NULL,
    p_examens_demandes IN VARCHAR2 DEFAULT NULL,
    p_tarif_consultation IN NUMBER,
    p_id_consultation OUT NUMBER
)
AS
    v_rdv_statut VARCHAR2(20);
    v_consult_exists NUMBER;
BEGIN
    -- Verifier que le RDV existe et son statut
    BEGIN
        SELECT statut INTO v_rdv_statut
        FROM RENDEZ_VOUS
        WHERE id_rdv = p_id_rdv;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20014, 'Rendez-vous inexistant');
    END;
    
    -- Verifier qu'il n'y a pas deja une consultation pour ce RDV
    SELECT COUNT(*) INTO v_consult_exists
    FROM CONSULTATION
    WHERE id_rdv = p_id_rdv;
    
    IF v_consult_exists > 0 THEN
        RAISE_APPLICATION_ERROR(-20015, 'Une consultation existe deja pour ce rendez-vous');
    END IF;
    
    -- Generer le nouvel ID
    SELECT seq_consultation.NEXTVAL INTO p_id_consultation FROM DUAL;
    
    -- Inserer la consultation
    INSERT INTO CONSULTATION (
        id_consultation, id_rdv, date_consultation, symptomes, diagnostic,
        observations, prescription, examens_demandes, tarif_consultation
    ) VALUES (
        p_id_consultation, p_id_rdv, SYSDATE, p_symptomes, p_diagnostic,
        p_observations, p_prescription, p_examens_demandes, p_tarif_consultation
    );
    
    -- Mettre a jour le statut du RDV a TERMINE
    UPDATE RENDEZ_VOUS
    SET statut = 'TERMINE'
    WHERE id_rdv = p_id_rdv;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Consultation creee avec succes. ID: ' || p_id_consultation);
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20016, 'Erreur lors de la creation de la consultation: ' || SQLERRM);
END SP_CREER_CONSULTATION;
/

-- ============================================
-- PROCEDURE 6 : SP_PRESCRIRE_MEDICAMENT
-- Description : Ajouter un medicament a une consultation
-- ============================================

CREATE OR REPLACE PROCEDURE SP_PRESCRIRE_MEDICAMENT (
    p_id_consultation IN NUMBER,
    p_id_medicament IN NUMBER,
    p_posologie IN VARCHAR2,
    p_duree_traitement IN NUMBER,
    p_instructions IN VARCHAR2 DEFAULT NULL,
    p_quantite IN NUMBER,
    p_id_traitement OUT NUMBER
)
AS
    v_consult_exists NUMBER;
    v_medic_exists NUMBER;
    v_stock_dispo NUMBER;
BEGIN
    -- Verifier que la consultation existe
    SELECT COUNT(*) INTO v_consult_exists
    FROM CONSULTATION
    WHERE id_consultation = p_id_consultation;
    
    IF v_consult_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20017, 'Consultation inexistante');
    END IF;
    
    -- Verifier que le medicament existe et le stock
    SELECT COUNT(*), NVL(MAX(stock_disponible), 0)
    INTO v_medic_exists, v_stock_dispo
    FROM MEDICAMENT
    WHERE id_medicament = p_id_medicament;
    
    IF v_medic_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20018, 'Medicament inexistant');
    END IF;
    
    IF v_stock_dispo < p_quantite THEN
        RAISE_APPLICATION_ERROR(-20019, 'Stock insuffisant. Stock disponible: ' || v_stock_dispo);
    END IF;
    
    -- Generer le nouvel ID
    SELECT seq_traitement.NEXTVAL INTO p_id_traitement FROM DUAL;
    
    -- Inserer le traitement
    INSERT INTO TRAITEMENT (
        id_traitement, id_consultation, id_medicament, posologie,
        duree_traitement, instructions, quantite
    ) VALUES (
        p_id_traitement, p_id_consultation, p_id_medicament, p_posologie,
        p_duree_traitement, p_instructions, p_quantite
    );
    
    -- Mettre a jour le stock
    UPDATE MEDICAMENT
    SET stock_disponible = stock_disponible - p_quantite
    WHERE id_medicament = p_id_medicament;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Traitement prescrit avec succes. ID: ' || p_id_traitement);
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20020, 'Erreur lors de la prescription: ' || SQLERRM);
END SP_PRESCRIRE_MEDICAMENT;
/

-- ============================================
-- PROCEDURE 7 : SP_ENREGISTRER_PAIEMENT
-- Description : Enregistrer un paiement pour une facture
-- ============================================

CREATE OR REPLACE PROCEDURE SP_ENREGISTRER_PAIEMENT (
    p_id_facture IN NUMBER,
    p_montant_paye IN NUMBER,
    p_mode_paiement IN VARCHAR2
)
AS
    v_facture_exists NUMBER;
    v_montant_total NUMBER;
    v_montant_deja_paye NUMBER;
    v_nouveau_montant_paye NUMBER;
    v_nouveau_statut VARCHAR2(20);
BEGIN
    -- Verifier que la facture existe et recuperer les montants
    BEGIN
        SELECT COUNT(*), MAX(montant_total), MAX(montant_paye)
        INTO v_facture_exists, v_montant_total, v_montant_deja_paye
        FROM FACTURE
        WHERE id_facture = p_id_facture
        GROUP BY id_facture;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20021, 'Facture inexistante');
    END;
    
    -- Calculer le nouveau montant paye
    v_nouveau_montant_paye := v_montant_deja_paye + p_montant_paye;
    
    -- Verifier que le paiement ne depasse pas le montant total
    IF v_nouveau_montant_paye > v_montant_total THEN
        RAISE_APPLICATION_ERROR(-20022, 'Le montant paye depasse le montant total de la facture');
    END IF;
    
    -- Determiner le nouveau statut
    IF v_nouveau_montant_paye >= v_montant_total THEN
        v_nouveau_statut := 'PAYE';
    ELSIF v_nouveau_montant_paye > 0 THEN
        v_nouveau_statut := 'PARTIEL';
    ELSE
        v_nouveau_statut := 'EN_ATTENTE';
    END IF;
    
    -- Mettre a jour la facture
    UPDATE FACTURE
    SET montant_paye = v_nouveau_montant_paye,
        statut_paiement = v_nouveau_statut,
        mode_paiement = p_mode_paiement,
        date_paiement = CASE WHEN v_nouveau_statut = 'PAYE' THEN SYSDATE ELSE date_paiement END
    WHERE id_facture = p_id_facture;
    
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Paiement enregistre. Nouveau statut: ' || v_nouveau_statut);
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20023, 'Erreur lors de l''enregistrement du paiement: ' || SQLERRM);
END SP_ENREGISTRER_PAIEMENT;
/

PROMPT ============================================
PROMPT Procedures stockees creees avec succes !
PROMPT ============================================