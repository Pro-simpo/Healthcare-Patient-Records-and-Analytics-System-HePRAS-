-- ============================================
-- HEALTHCARE PATIENT RECORDS SYSTEM
-- Script de création des tables - Oracle 21c
-- ============================================

-- Nettoyage 
/*
DROP TABLE TRAITEMENT CASCADE CONSTRAINTS;
DROP TABLE FACTURE CASCADE CONSTRAINTS;
DROP TABLE CONSULTATION CASCADE CONSTRAINTS;
DROP TABLE RENDEZ_VOUS CASCADE CONSTRAINTS;
DROP TABLE UTILISATEUR CASCADE CONSTRAINTS;
DROP TABLE MEDICAMENT CASCADE CONSTRAINTS;
DROP TABLE MEDECIN CASCADE CONSTRAINTS;
DROP TABLE PATIENT CASCADE CONSTRAINTS;
DROP TABLE DEPARTEMENT CASCADE CONSTRAINTS;

DROP SEQUENCE seq_patient;
DROP SEQUENCE seq_medecin;
DROP SEQUENCE seq_departement;
DROP SEQUENCE seq_rdv;
DROP SEQUENCE seq_consultation;
DROP SEQUENCE seq_traitement;
DROP SEQUENCE seq_medicament;
DROP SEQUENCE seq_facture;
DROP SEQUENCE seq_utilisateur;
*/

-- ============================================
-- CRÉATION DES SEQUENCES
-- ============================================

CREATE SEQUENCE seq_patient START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_medecin START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_departement START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_rdv START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_consultation START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_traitement START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_medicament START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_facture START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_utilisateur START WITH 1 INCREMENT BY 1 NOCACHE;

PROMPT Sequences creees avec succes

-- ============================================
-- TABLE DEPARTEMENT 
-- ============================================

CREATE TABLE DEPARTEMENT (
    id_departement NUMBER(10) PRIMARY KEY,
    nom_departement VARCHAR2(100) UNIQUE NOT NULL,
    chef_departement_id NUMBER(10), -- FK vers MEDECIN
    nombre_lits NUMBER(3) DEFAULT 0 CHECK (nombre_lits >= 0),
    telephone VARCHAR2(20)
);

COMMENT ON TABLE DEPARTEMENT IS 'Table des departements de l''hopital';
COMMENT ON COLUMN DEPARTEMENT.chef_departement_id IS 'Chef du departement (FK vers MEDECIN)';

PROMPT Table DEPARTEMENT creee

-- ============================================
-- TABLE MEDECIN
-- ============================================

CREATE TABLE MEDECIN (
    id_medecin NUMBER(10) PRIMARY KEY,
    numero_ordre VARCHAR2(20) UNIQUE NOT NULL,
    nom VARCHAR2(50) NOT NULL,
    prenom VARCHAR2(50) NOT NULL,
    specialite VARCHAR2(50) NOT NULL,
    telephone VARCHAR2(20),
    email VARCHAR2(100),
    date_embauche DATE NOT NULL,
    id_departement NUMBER(10) NOT NULL,
    CONSTRAINT fk_medecin_departement FOREIGN KEY (id_departement)
        REFERENCES DEPARTEMENT(id_departement)
);

CREATE INDEX idx_medecin_nom ON MEDECIN(nom, prenom);
CREATE INDEX idx_medecin_specialite ON MEDECIN(specialite);
CREATE INDEX idx_medecin_departement ON MEDECIN(id_departement);

COMMENT ON TABLE MEDECIN IS 'Table des medecins';
COMMENT ON COLUMN MEDECIN.numero_ordre IS 'Numero d''ordre du medecin';

PROMPT Table MEDECIN creee

-- Ajouter la FK de DEPARTEMENT vers MEDECIN maintenant que MEDECIN existe
ALTER TABLE DEPARTEMENT
ADD CONSTRAINT fk_departement_chef FOREIGN KEY (chef_departement_id)
    REFERENCES MEDECIN(id_medecin) ON DELETE SET NULL;

PROMPT Contrainte FK DEPARTEMENT->MEDECIN ajoutee

-- ============================================
-- TABLE PATIENT
-- ============================================

CREATE TABLE PATIENT (
    id_patient NUMBER(10) PRIMARY KEY,
    cin VARCHAR2(8) UNIQUE NOT NULL,
    nom VARCHAR2(50) NOT NULL,
    prenom VARCHAR2(50) NOT NULL,
    date_naissance DATE NOT NULL,
    sexe CHAR(1) NOT NULL CHECK (sexe IN ('M', 'F')),
    adresse VARCHAR2(200),
    ville VARCHAR2(50),
    code_postal VARCHAR2(10),
    telephone VARCHAR2(20),
    email VARCHAR2(100),
    groupe_sanguin VARCHAR2(5) CHECK (groupe_sanguin IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    allergies VARCHAR2(500),
    date_inscription DATE DEFAULT SYSDATE NOT NULL
    -- Note: La validation date_naissance < SYSDATE est geree par trigger
);

CREATE INDEX idx_patient_nom ON PATIENT(nom, prenom);
CREATE INDEX idx_patient_cin ON PATIENT(cin);
CREATE INDEX idx_patient_email ON PATIENT(email);

COMMENT ON TABLE PATIENT IS 'Table des patients';
COMMENT ON COLUMN PATIENT.cin IS 'Numero de Carte d''Identite Nationale marocaine (format: X123456 ou 12345678)';
COMMENT ON COLUMN PATIENT.groupe_sanguin IS 'Groupe sanguin : A+, A-, B+, B-, AB+, AB-, O+, O-';

PROMPT Table PATIENT creee

-- ============================================
-- TABLE RENDEZ_VOUS
-- ============================================

CREATE TABLE RENDEZ_VOUS (
    id_rdv NUMBER(10) PRIMARY KEY,
    id_patient NUMBER(10) NOT NULL,
    id_medecin NUMBER(10) NOT NULL,
    date_rdv DATE NOT NULL,
    heure_debut TIMESTAMP NOT NULL,
    heure_fin TIMESTAMP NOT NULL,
    motif VARCHAR2(500),
    statut VARCHAR2(20) DEFAULT 'PLANIFIE' CHECK (statut IN ('PLANIFIE', 'CONFIRME', 'TERMINE', 'ANNULE')),
    salle VARCHAR2(20),
    date_creation DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_rdv_patient FOREIGN KEY (id_patient)
        REFERENCES PATIENT(id_patient),
    CONSTRAINT fk_rdv_medecin FOREIGN KEY (id_medecin)
        REFERENCES MEDECIN(id_medecin),
    CONSTRAINT uq_rdv_medecin_horaire UNIQUE (id_medecin, date_rdv, heure_debut)
);

CREATE INDEX idx_rdv_patient ON RENDEZ_VOUS(id_patient);
CREATE INDEX idx_rdv_medecin ON RENDEZ_VOUS(id_medecin);
CREATE INDEX idx_rdv_date ON RENDEZ_VOUS(date_rdv);
CREATE INDEX idx_rdv_statut ON RENDEZ_VOUS(statut);

COMMENT ON TABLE RENDEZ_VOUS IS 'Table des rendez-vous';
COMMENT ON COLUMN RENDEZ_VOUS.statut IS 'PLANIFIE, CONFIRME, TERMINE, ANNULE';

PROMPT Table RENDEZ_VOUS creee

-- ============================================
-- TABLE CONSULTATION
-- ============================================

CREATE TABLE CONSULTATION (
    id_consultation NUMBER(10) PRIMARY KEY,
    id_rdv NUMBER(10) UNIQUE NOT NULL,
    date_consultation DATE DEFAULT SYSDATE NOT NULL,
    symptomes VARCHAR2(1000),
    diagnostic VARCHAR2(1000),
    observations VARCHAR2(2000),
    prescription VARCHAR2(2000),
    examens_demandes VARCHAR2(500),
    tarif_consultation NUMBER(8,2) CHECK (tarif_consultation >= 0),
    CONSTRAINT fk_consultation_rdv FOREIGN KEY (id_rdv)
        REFERENCES RENDEZ_VOUS(id_rdv) ON DELETE CASCADE
);

CREATE INDEX idx_consultation_rdv ON CONSULTATION(id_rdv);
CREATE INDEX idx_consultation_date ON CONSULTATION(date_consultation);

COMMENT ON TABLE CONSULTATION IS 'Table des consultations medicales';
COMMENT ON COLUMN CONSULTATION.id_rdv IS 'Un rendez-vous ne peut avoir qu''une seule consultation';

PROMPT Table CONSULTATION creee

-- ============================================
-- TABLE MEDICAMENT
-- ============================================

CREATE TABLE MEDICAMENT (
    id_medicament NUMBER(10) PRIMARY KEY,
    nom_commercial VARCHAR2(100) UNIQUE NOT NULL,
    principe_actif VARCHAR2(100) NOT NULL,
    forme VARCHAR2(50) CHECK (forme IN ('COMPRIME', 'SIROP', 'INJECTION', 'GELULE', 'POMMADE')),
    dosage VARCHAR2(50),
    prix_unitaire NUMBER(8,2) CHECK (prix_unitaire >= 0),
    stock_disponible NUMBER(10) DEFAULT 0 CHECK (stock_disponible >= 0),
    stock_alerte NUMBER(10) DEFAULT 0 CHECK (stock_alerte >= 0)
);

CREATE INDEX idx_medicament_stock ON MEDICAMENT(stock_disponible);

COMMENT ON TABLE MEDICAMENT IS 'Table des medicaments';
COMMENT ON COLUMN MEDICAMENT.stock_alerte IS 'Seuil d''alerte pour le stock';

PROMPT Table MEDICAMENT creee

-- ============================================
-- TABLE TRAITEMENT
-- ============================================

CREATE TABLE TRAITEMENT (
    id_traitement NUMBER(10) PRIMARY KEY,
    id_consultation NUMBER(10) NOT NULL,
    id_medicament NUMBER(10) NOT NULL,
    posologie VARCHAR2(200) NOT NULL,
    duree_traitement NUMBER(3) CHECK (duree_traitement > 0),
    instructions VARCHAR2(500),
    quantite NUMBER(5) CHECK (quantite > 0),
    CONSTRAINT fk_traitement_consultation FOREIGN KEY (id_consultation)
        REFERENCES CONSULTATION(id_consultation) ON DELETE CASCADE,
    CONSTRAINT fk_traitement_medicament FOREIGN KEY (id_medicament)
        REFERENCES MEDICAMENT(id_medicament)
);

CREATE INDEX idx_traitement_consultation ON TRAITEMENT(id_consultation);
CREATE INDEX idx_traitement_medicament ON TRAITEMENT(id_medicament);

COMMENT ON TABLE TRAITEMENT IS 'Table des traitements prescrits';
COMMENT ON COLUMN TRAITEMENT.duree_traitement IS 'Duree en jours';

PROMPT Table TRAITEMENT creee

-- ============================================
-- TABLE FACTURE
-- ============================================

CREATE TABLE FACTURE (
    id_facture NUMBER(10) PRIMARY KEY,
    numero_facture VARCHAR2(20) UNIQUE NOT NULL,
    id_patient NUMBER(10) NOT NULL,
    id_consultation NUMBER(10) UNIQUE NOT NULL,
    date_facture DATE DEFAULT SYSDATE NOT NULL,
    montant_consultation NUMBER(10,2) DEFAULT 0 CHECK (montant_consultation >= 0),
    montant_medicaments NUMBER(10,2) DEFAULT 0 CHECK (montant_medicaments >= 0),
    montant_total NUMBER(10,2) DEFAULT 0 CHECK (montant_total >= 0),
    montant_paye NUMBER(10,2) DEFAULT 0 CHECK (montant_paye >= 0),
    statut_paiement VARCHAR2(20) DEFAULT 'EN_ATTENTE' CHECK (statut_paiement IN ('EN_ATTENTE', 'PAYE', 'PARTIEL')),
    mode_paiement VARCHAR2(20) CHECK (mode_paiement IN ('ESPECES', 'CARTE', 'CHEQUE', 'VIREMENT')),
    date_paiement DATE,
    CONSTRAINT fk_facture_patient FOREIGN KEY (id_patient)
        REFERENCES PATIENT(id_patient),
    CONSTRAINT fk_facture_consultation FOREIGN KEY (id_consultation)
        REFERENCES CONSULTATION(id_consultation)
);

CREATE INDEX idx_facture_patient ON FACTURE(id_patient);
CREATE INDEX idx_facture_consultation ON FACTURE(id_consultation);
CREATE INDEX idx_facture_statut ON FACTURE(statut_paiement);
CREATE INDEX idx_facture_date ON FACTURE(date_facture);

COMMENT ON TABLE FACTURE IS 'Table des factures';
COMMENT ON COLUMN FACTURE.numero_facture IS 'Format: FAC-YYYY-NNNN';

PROMPT Table FACTURE creee

-- ============================================
-- TABLE UTILISATEUR
-- ============================================

CREATE TABLE UTILISATEUR (
    id_utilisateur NUMBER(10) PRIMARY KEY,
    username VARCHAR2(50) UNIQUE NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    role VARCHAR2(20) NOT NULL CHECK (role IN ('ADMIN', 'MEDECIN', 'RECEPTIONNISTE', 'PATIENT')),
    statut VARCHAR2(20) DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'INACTIF', 'SUSPENDU')),
    id_medecin NUMBER(10) UNIQUE,
    id_patient NUMBER(10) UNIQUE,
    date_creation DATE DEFAULT SYSDATE NOT NULL,
    derniere_connexion DATE,
    tentatives_echec NUMBER(2) DEFAULT 0 CHECK (tentatives_echec >= 0),
    CONSTRAINT fk_utilisateur_medecin FOREIGN KEY (id_medecin)
        REFERENCES MEDECIN(id_medecin) ON DELETE SET NULL,
    CONSTRAINT fk_utilisateur_patient FOREIGN KEY (id_patient)
        REFERENCES PATIENT(id_patient) ON DELETE SET NULL,
    CONSTRAINT chk_utilisateur_entite CHECK (
        (id_medecin IS NULL AND id_patient IS NULL) OR
        (id_medecin IS NOT NULL AND id_patient IS NULL) OR
        (id_medecin IS NULL AND id_patient IS NOT NULL)
    )
);

CREATE INDEX idx_utilisateur_username ON UTILISATEUR(username);
CREATE INDEX idx_utilisateur_email ON UTILISATEUR(email);
CREATE INDEX idx_utilisateur_role ON UTILISATEUR(role);

COMMENT ON TABLE UTILISATEUR IS 'Table des comptes utilisateurs';
COMMENT ON COLUMN UTILISATEUR.password_hash IS 'Hash SHA-256 du mot de passe';
COMMENT ON COLUMN UTILISATEUR.tentatives_echec IS 'Compte des tentatives de connexion echouees';

PROMPT Table UTILISATEUR creee

-- ============================================
-- RESUME
-- ============================================

PROMPT
PROMPT ============================================
PROMPT VERIFICATION DES TABLES CREEES
PROMPT ============================================

SELECT table_name, num_rows, tablespace_name
FROM user_tables
WHERE table_name IN ('PATIENT', 'MEDECIN', 'DEPARTEMENT', 'RENDEZ_VOUS', 
                     'CONSULTATION', 'MEDICAMENT', 'TRAITEMENT', 'FACTURE', 'UTILISATEUR')
ORDER BY table_name;

PROMPT
PROMPT ============================================
PROMPT VERIFICATION DES CONTRAINTES
PROMPT ============================================

SELECT constraint_name, constraint_type, table_name, status
FROM user_constraints
WHERE table_name IN ('PATIENT', 'MEDECIN', 'DEPARTEMENT', 'RENDEZ_VOUS', 
                     'CONSULTATION', 'MEDICAMENT', 'TRAITEMENT', 'FACTURE', 'UTILISATEUR')
ORDER BY table_name, constraint_type;

