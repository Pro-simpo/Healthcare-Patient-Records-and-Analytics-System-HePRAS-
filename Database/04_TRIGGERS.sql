-- ============================================
-- TRIGGERS PL/SQL
-- Healthcare Patient Records System
-- ============================================

-- ============================================
-- TRIGGER 1 : TRG_VALIDATE_PATIENT
-- Description : Valider les donnees du patient avant insertion
-- ============================================

CREATE OR REPLACE TRIGGER TRG_VALIDATE_PATIENT
BEFORE INSERT OR UPDATE ON PATIENT
FOR EACH ROW
DECLARE
    v_email_valid NUMBER;
    v_phone_valid NUMBER;
BEGIN
    -- Verifier le format de l'email (simple validation)
    IF :NEW.email IS NOT NULL THEN
        IF NOT REGEXP_LIKE(:NEW.email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$') THEN
            RAISE_APPLICATION_ERROR(-20101, 'Format d''email invalide');
        END IF;
    END IF;
    
    -- Verifier le format du telephone marocain
    IF :NEW.telephone IS NOT NULL THEN
        IF NOT REGEXP_LIKE(:NEW.telephone, '^\+212[5-7][0-9]{8}$') THEN
            RAISE_APPLICATION_ERROR(-20102, 'Format de telephone invalide. Utiliser: +212XXXXXXXXX');
        END IF;
    END IF;
    
    -- Verifier que la date de naissance est dans le passé
    IF :NEW.date_naissance >= SYSDATE THEN
        RAISE_APPLICATION_ERROR(-20103, 'La date de naissance doit etre dans le passe');
    END IF;
    
    -- Verifier que l'age est raisonnable (< 150 ans)
    IF FN_CALCULER_AGE(:NEW.date_naissance) > 150 THEN
        RAISE_APPLICATION_ERROR(-20104, 'Date de naissance invalide (age > 150 ans)');
    END IF;
    
    DBMS_OUTPUT.PUT_LINE('Validation patient reussie');
END;
/

-- ============================================
-- TRIGGER 2 : TRG_AUTO_FACTURE
-- Description : Generer automatiquement une facture apres une consultation
-- ============================================

CREATE OR REPLACE TRIGGER TRG_AUTO_FACTURE
AFTER INSERT ON CONSULTATION
FOR EACH ROW
DECLARE
    v_id_facture NUMBER;
    v_id_patient NUMBER;
    v_numero_facture VARCHAR2(20);
    v_montant_medicaments NUMBER;
    v_montant_total NUMBER;
BEGIN
    -- Recuperer l'ID du patient a partir du rendez-vous
    SELECT id_patient INTO v_id_patient
    FROM RENDEZ_VOUS
    WHERE id_rdv = :NEW.id_rdv;
    
    -- Generer le numero de facture
    v_numero_facture := FN_GENERER_NUM_FACTURE();
    
    -- Generer l'ID de la facture
    SELECT seq_facture.NEXTVAL INTO v_id_facture FROM DUAL;
    
    -- Calculer le montant des medicaments (sera 0 initialement)
    v_montant_medicaments := 0;
    
    -- Calculer le montant total
    v_montant_total := :NEW.tarif_consultation + v_montant_medicaments;
    
    -- Inserer la facture
    INSERT INTO FACTURE (
        id_facture, numero_facture, id_patient, id_consultation,
        date_facture, montant_consultation, montant_medicaments,
        montant_total, montant_paye, statut_paiement
    ) VALUES (
        v_id_facture, v_numero_facture, v_id_patient, :NEW.id_consultation,
        SYSDATE, :NEW.tarif_consultation, v_montant_medicaments,
        v_montant_total, 0, 'EN_ATTENTE'
    );
    
    DBMS_OUTPUT.PUT_LINE('Facture generee automatiquement: ' || v_numero_facture);
    
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20201, 'Erreur lors de la generation de la facture: ' || SQLERRM);
END;
/

-- ============================================
-- TRIGGER 3 : TRG_UPDATE_FACTURE_MEDICAMENTS
-- Description : Mettre a jour le montant des medicaments dans la facture
-- ============================================

CREATE OR REPLACE TRIGGER TRG_UPDATE_FACTURE_MEDICAMENTS
AFTER INSERT OR UPDATE OR DELETE ON TRAITEMENT
FOR EACH ROW
DECLARE
    v_id_consultation NUMBER;
    v_montant_medicaments NUMBER;
    v_montant_consultation NUMBER;
    v_montant_total NUMBER;
BEGIN
    -- Determiner l'ID de la consultation
    IF INSERTING OR UPDATING THEN
        v_id_consultation := :NEW.id_consultation;
    ELSE -- DELETING
        v_id_consultation := :OLD.id_consultation;
    END IF;
    
    -- Calculer le nouveau montant des medicaments
    v_montant_medicaments := FN_CALCULER_MONTANT_MEDICAMENTS(v_id_consultation);
    
    -- Recuperer le montant de la consultation
    SELECT montant_consultation INTO v_montant_consultation
    FROM FACTURE
    WHERE id_consultation = v_id_consultation;
    
    -- Calculer le montant total
    v_montant_total := v_montant_consultation + v_montant_medicaments;
    
    -- Mettre a jour la facture
    UPDATE FACTURE
    SET montant_medicaments = v_montant_medicaments,
        montant_total = v_montant_total
    WHERE id_consultation = v_id_consultation;
    
    DBMS_OUTPUT.PUT_LINE('Facture mise a jour. Nouveau montant: ' || v_montant_total || ' DH');
    
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20202, 'Erreur lors de la mise a jour de la facture: ' || SQLERRM);
END;
/

-- ============================================
-- TRIGGER 4 : TRG_STOCK_ALERT
-- Description : Alerter si le stock d'un medicament est faible
-- ============================================

CREATE OR REPLACE TRIGGER TRG_STOCK_ALERT
AFTER UPDATE OF stock_disponible ON MEDICAMENT
FOR EACH ROW
WHEN (NEW.stock_disponible <= NEW.stock_alerte)
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    -- Afficher une alerte
    DBMS_OUTPUT.PUT_LINE('ALERTE STOCK FAIBLE: ' || :NEW.nom_commercial);
    DBMS_OUTPUT.PUT_LINE('Stock actuel: ' || :NEW.stock_disponible || ' - Seuil alerte: ' || :NEW.stock_alerte);
    
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
END;
/

-- ============================================
-- TRIGGER 5 : TRG_AUDIT_LOG
-- Description : Logger toutes les modifications importantes
-- ============================================

-- Creer d'abord la table de logs
CREATE TABLE AUDIT_LOG (
    id_log NUMBER PRIMARY KEY,
    table_name VARCHAR2(50),
    operation VARCHAR2(10),
    record_id NUMBER,
    old_values VARCHAR2(4000),
    new_values VARCHAR2(4000),
    modified_by VARCHAR2(100),
    modified_date DATE DEFAULT SYSDATE
);

CREATE SEQUENCE seq_audit_log START WITH 1 INCREMENT BY 1;

-- Trigger pour PATIENT
CREATE OR REPLACE TRIGGER TRG_AUDIT_PATIENT
AFTER INSERT OR UPDATE OR DELETE ON PATIENT
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
    v_operation VARCHAR2(10);
    v_old_values VARCHAR2(4000);
    v_new_values VARCHAR2(4000);
    v_user VARCHAR2(100);
BEGIN
    -- Determiner l'operation
    IF INSERTING THEN
        v_operation := 'INSERT';
        v_new_values := 'CIN:' || :NEW.cin || ', Nom:' || :NEW.nom || ' ' || :NEW.prenom;
    ELSIF UPDATING THEN
        v_operation := 'UPDATE';
        v_old_values := 'CIN:' || :OLD.cin || ', Nom:' || :OLD.nom || ' ' || :OLD.prenom;
        v_new_values := 'CIN:' || :NEW.cin || ', Nom:' || :NEW.nom || ' ' || :NEW.prenom;
    ELSE -- DELETING
        v_operation := 'DELETE';
        v_old_values := 'CIN:' || :OLD.cin || ', Nom:' || :OLD.nom || ' ' || :OLD.prenom;
    END IF;
    
    -- Recuperer l'utilisateur courant
    SELECT USER INTO v_user FROM DUAL;
    
    -- Inserer dans le log
    INSERT INTO AUDIT_LOG (
        id_log, table_name, operation, record_id,
        old_values, new_values, modified_by, modified_date
    ) VALUES (
        seq_audit_log.NEXTVAL, 'PATIENT', v_operation,
        COALESCE(:NEW.id_patient, :OLD.id_patient),
        v_old_values, v_new_values, v_user, SYSDATE
    );
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
END;
/

-- Trigger pour CONSULTATION
CREATE OR REPLACE TRIGGER TRG_AUDIT_CONSULTATION
AFTER INSERT OR UPDATE OR DELETE ON CONSULTATION
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
    v_operation VARCHAR2(10);
    v_old_values VARCHAR2(4000);
    v_new_values VARCHAR2(4000);
    v_user VARCHAR2(100);
BEGIN
    IF INSERTING THEN
        v_operation := 'INSERT';
        v_new_values := 'Consultation ID:' || :NEW.id_consultation || ', Tarif:' || :NEW.tarif_consultation || ' DH';
    ELSIF UPDATING THEN
        v_operation := 'UPDATE';
        v_old_values := 'Tarif:' || :OLD.tarif_consultation || ' DH';
        v_new_values := 'Tarif:' || :NEW.tarif_consultation || ' DH';
    ELSE
        v_operation := 'DELETE';
        v_old_values := 'Consultation ID:' || :OLD.id_consultation;
    END IF;
    
    SELECT USER INTO v_user FROM DUAL;
    
    INSERT INTO AUDIT_LOG (
        id_log, table_name, operation, record_id,
        old_values, new_values, modified_by, modified_date
    ) VALUES (
        seq_audit_log.NEXTVAL, 'CONSULTATION', v_operation,
        COALESCE(:NEW.id_consultation, :OLD.id_consultation),
        v_old_values, v_new_values, v_user, SYSDATE
    );
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
END;
/

-- ============================================
-- TRIGGER 6 : TRG_PREVENT_PAST_RDV
-- Description : Empecher la creation de RDV dans le passe
-- ============================================

CREATE OR REPLACE TRIGGER TRG_PREVENT_PAST_RDV
BEFORE INSERT OR UPDATE ON RENDEZ_VOUS
FOR EACH ROW
BEGIN
    -- Verifier que la date du RDV n'est pas dans le passe
    IF :NEW.date_rdv < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20203, 'Impossible de creer un rendez-vous dans le passe');
    END IF;
    
    -- Verifier que heure_fin > heure_debut
    IF :NEW.heure_fin <= :NEW.heure_debut THEN
        RAISE_APPLICATION_ERROR(-20204, 'L''heure de fin doit etre superieure a l''heure de debut');
    END IF;
END;
/

-- ============================================
-- TRIGGER 7 : TRG_AUTO_ID_SEQUENCES
-- Description : Generer automatiquement les IDs avec les sequences
-- ============================================

CREATE OR REPLACE TRIGGER TRG_AUTO_ID_PATIENT
BEFORE INSERT ON PATIENT
FOR EACH ROW
WHEN (NEW.id_patient IS NULL)
BEGIN
    SELECT seq_patient.NEXTVAL INTO :NEW.id_patient FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRG_AUTO_ID_MEDECIN
BEFORE INSERT ON MEDECIN
FOR EACH ROW
WHEN (NEW.id_medecin IS NULL)
BEGIN
    SELECT seq_medecin.NEXTVAL INTO :NEW.id_medecin FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRG_AUTO_ID_DEPARTEMENT
BEFORE INSERT ON DEPARTEMENT
FOR EACH ROW
WHEN (NEW.id_departement IS NULL)
BEGIN
    SELECT seq_departement.NEXTVAL INTO :NEW.id_departement FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRG_AUTO_ID_RDV
BEFORE INSERT ON RENDEZ_VOUS
FOR EACH ROW
WHEN (NEW.id_rdv IS NULL)
BEGIN
    SELECT seq_rdv.NEXTVAL INTO :NEW.id_rdv FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRG_AUTO_ID_MEDICAMENT
BEFORE INSERT ON MEDICAMENT
FOR EACH ROW
WHEN (NEW.id_medicament IS NULL)
BEGIN
    SELECT seq_medicament.NEXTVAL INTO :NEW.id_medicament FROM DUAL;
END;
/

PROMPT ============================================
PROMPT Triggers crees avec succes !
PROMPT ============================================