-- ================================================================
-- HEALTHCARE SYSTEM - DONNÉES DE TEST
-- ================================================================
-- Fichier : 07_DATA.sql
-- Version corrigée avec dates valides
-- ================================================================

SET DEFINE OFF;
SET SERVEROUTPUT ON;

-- ============================================
-- 1. DEPARTEMENTS
-- ============================================

INSERT INTO DEPARTEMENT (ID_DEPARTEMENT, NOM_DEPARTEMENT, CHEF_DEPARTEMENT_ID, NOMBRE_LITS, TELEPHONE) 
VALUES (1, 'Cardiologie', NULL, 25, '+212520123456');

INSERT INTO DEPARTEMENT (ID_DEPARTEMENT, NOM_DEPARTEMENT, CHEF_DEPARTEMENT_ID, NOMBRE_LITS, TELEPHONE) 
VALUES (2, 'Pediatrie', NULL, 30, '+212520123457');

INSERT INTO DEPARTEMENT (ID_DEPARTEMENT, NOM_DEPARTEMENT, CHEF_DEPARTEMENT_ID, NOMBRE_LITS, TELEPHONE) 
VALUES (3, 'Chirurgie Generale', NULL, 20, '+212520123458');

INSERT INTO DEPARTEMENT (ID_DEPARTEMENT, NOM_DEPARTEMENT, CHEF_DEPARTEMENT_ID, NOMBRE_LITS, TELEPHONE) 
VALUES (4, 'Medecine Interne', NULL, 35, '+212520123459');

INSERT INTO DEPARTEMENT (ID_DEPARTEMENT, NOM_DEPARTEMENT, CHEF_DEPARTEMENT_ID, NOMBRE_LITS, TELEPHONE) 
VALUES (5, 'Gynecologie-Obstetrique', NULL, 28, '+212520123460');

INSERT INTO DEPARTEMENT (ID_DEPARTEMENT, NOM_DEPARTEMENT, CHEF_DEPARTEMENT_ID, NOMBRE_LITS, TELEPHONE) 
VALUES (6, 'Ophtalmologie', NULL, 15, '+212520123461');

INSERT INTO DEPARTEMENT (ID_DEPARTEMENT, NOM_DEPARTEMENT, CHEF_DEPARTEMENT_ID, NOMBRE_LITS, TELEPHONE) 
VALUES (7, 'Dermatologie', NULL, 12, '+212520123462');

PROMPT 7 departements inseres

-- ============================================
-- 2. MEDECINS
-- ============================================

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (1, 'M001234', 'ALAMI', 'Youssef', 'Cardiologie', '+212661234567', 'y.alami@hospital.ma', 
        TO_DATE('2015-03-15', 'YYYY-MM-DD'), 1);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (2, 'M001235', 'BENANI', 'Fatima', 'Pediatrie', '+212662345678', 'f.benani@hospital.ma', 
        TO_DATE('2018-09-01', 'YYYY-MM-DD'), 2);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (3, 'M001236', 'TAZI', 'Mohammed', 'Chirurgie Generale', '+212663456789', 'm.tazi@hospital.ma', 
        TO_DATE('2012-01-10', 'YYYY-MM-DD'), 3);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (4, 'M001237', 'IDRISSI', 'Amina', 'Medecine Interne', '+212664567890', 'a.idrissi@hospital.ma', 
        TO_DATE('2016-06-20', 'YYYY-MM-DD'), 4);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (5, 'M001238', 'FILALI', 'Hassan', 'Gynecologie-Obstetrique', '+212665678901', 'h.filali@hospital.ma', 
        TO_DATE('2014-11-05', 'YYYY-MM-DD'), 5);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (6, 'M001239', 'SLAOUI', 'Khadija', 'Ophtalmologie', '+212666789012', 'k.slaoui@hospital.ma', 
        TO_DATE('2019-02-14', 'YYYY-MM-DD'), 6);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (7, 'M001240', 'BENNANI', 'Omar', 'Dermatologie', '+212667890123', 'o.bennani@hospital.ma', 
        TO_DATE('2017-08-30', 'YYYY-MM-DD'), 7);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (8, 'M001241', 'CHAKIR', 'Salma', 'Cardiologie', '+212668901234', 's.chakir@hospital.ma', 
        TO_DATE('2020-01-15', 'YYYY-MM-DD'), 1);

INSERT INTO MEDECIN (ID_MEDECIN, NUMERO_ORDRE, NOM, PRENOM, SPECIALITE, TELEPHONE, EMAIL, DATE_EMBAUCHE, ID_DEPARTEMENT) 
VALUES (9, 'M001242', 'AMRANI', 'Rachid', 'Pediatrie', '+212669012345', 'r.amrani@hospital.ma', 
        TO_DATE('2021-03-10', 'YYYY-MM-DD'), 2);

PROMPT 9 medecins inseres

-- ============================================
-- 3. PATIENTS
-- ============================================

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (1, 'M567234', 'HASSANI', 'Ahmed', TO_DATE('1985-06-15', 'YYYY-MM-DD'), 'M', 
        '25 Rue Hassan II', 'Casablanca', '20000', '+212661111111', 'ahmed.hassani@gmail.com', 
        'O+', NULL, SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (2, 'F892341', 'MANSOURI', 'Fatima', TO_DATE('1990-03-22', 'YYYY-MM-DD'), 'F', 
        '12 Avenue Mohammed V', 'Rabat', '10000', '+212662222222', 'f.mansouri@gmail.com', 
        'A+', 'Penicilline', SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (3, 'K345678', 'BENSGHIR', 'Karim', TO_DATE('1978-11-08', 'YYYY-MM-DD'), 'M', 
        '45 Bd Zerktouni', 'Marrakech', '40000', '+212663333333', 'k.bensghir@yahoo.fr', 
        'B+', NULL, SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (4, 'T456789', 'MOIZ', 'Salma', TO_DATE('1995-07-30', 'YYYY-MM-DD'), 'F', 
        '78 Rue de Fes', 'Tanger', '90000', '+212664444444', 's.moiz@hotmail.com', 
        'AB+', 'Aspirine', SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (5, 'CB567890', 'TAHIRI', 'Mehdi', TO_DATE('2010-04-12', 'YYYY-MM-DD'), 'M', 
        '34 Quartier Palmiers', 'Casablanca', '20100', '+212665555555', 'tahiri.family@gmail.com', 
        'O-', NULL, SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (6, 'A678901', 'ZIANI', 'Zineb', TO_DATE('1988-12-25', 'YYYY-MM-DD'), 'F', 
        '56 Avenue des FAR', 'Agadir', '80000', '+212666666666', 'z.ziani@outlook.com', 
        'A-', 'Iode', SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (7, 'F789012', 'KADIRI', 'Yassine', TO_DATE('1982-09-18', 'YYYY-MM-DD'), 'M', 
        '23 Rue Ibn Batouta', 'Fes', '30000', '+212667777777', 'y.kadiri@gmail.com', 
        'B-', NULL, SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (8, 'K901234', 'RAMI', 'Samir', TO_DATE('1992-05-05', 'YYYY-MM-DD'), 'M', 
        '67 Quartier Industriel', 'Kenitra', '14000', '+212669999999', 's.rami@gmail.com', 
        'AB-', NULL, SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (9, 'O012345', 'LAHLOU', 'Meriem', TO_DATE('2015-08-20', 'YYYY-MM-DD'), 'F', 
        '15 Residence Al Amal', 'Oujda', '60000', '+212660000000', 'lahlou.family@gmail.com', 
        'A+', NULL, SYSDATE);

INSERT INTO PATIENT (ID_PATIENT, CIN, NOM, PRENOM, DATE_NAISSANCE, SEXE, ADRESSE, VILLE, CODE_POSTAL, 
                     TELEPHONE, EMAIL, GROUPE_SANGUIN, ALLERGIES, DATE_INSCRIPTION) 
VALUES (10, 'X432356', 'SABADOU', 'Ali', TO_DATE('2008-12-12', 'YYYY-MM-DD'), 'M', 
        'Quartier Al Houda', 'Tetouan', '93000', '+212668888888', 'sabadou@gmail.com', 
        'O+', NULL, SYSDATE);

PROMPT 10 patients inseres

-- ============================================
-- 4. UTILISATEURS
-- ============================================

-- Mot de passe de tous les comptes de test : 123456
-- La colonne PASSWORD_HASH contient "sel:empreinte" (SHA-256 avec sel, voir PasswordUtils.java)

-- Admin
INSERT INTO UTILISATEUR (ID_UTILISATEUR, USERNAME, PASSWORD_HASH, EMAIL, ROLE, STATUT, 
                        ID_MEDECIN, ID_PATIENT, TENTATIVES_ECHEC) 
VALUES (1, 'admin', 'b4ff7124abbdbfff8ad8df55730c11ba:0699037891243c44a40dac75c8903bbf49ec96c6f78ec0b8bf85cedf70bfc75c', 'admin@hospital.ma', 'ADMIN', 'ACTIF', 
        NULL, NULL, 0);

-- Medecin Dr. Alami
INSERT INTO UTILISATEUR (ID_UTILISATEUR, USERNAME, PASSWORD_HASH, EMAIL, ROLE, STATUT, 
                        ID_MEDECIN, ID_PATIENT, TENTATIVES_ECHEC) 
VALUES (2, 'dr.alami', '11e2d6e022b3de6c7fec420ed90db8f9:62f1966831b420456731208e81747778a1af8da0ad8f7854d2dae3ac3a473278', 'y.alami@hospital.ma', 'MEDECIN', 'ACTIF', 
        1, NULL, 0);

-- Receptionniste
INSERT INTO UTILISATEUR (ID_UTILISATEUR, USERNAME, PASSWORD_HASH, EMAIL, ROLE, STATUT, 
                        ID_MEDECIN, ID_PATIENT, TENTATIVES_ECHEC) 
VALUES (3, 'receptionniste', 'db040ff9074b822c2c898ae60d54209c:b771bb574f4cce374ac16c9c5fa87acef28fcefc867609443072388c0c034ca5', 'reception@hospital.ma', 'RECEPTIONNISTE', 'ACTIF', 
        NULL, NULL, 0);

-- Patient Ahmed Hassani
INSERT INTO UTILISATEUR (ID_UTILISATEUR, USERNAME, PASSWORD_HASH, EMAIL, ROLE, STATUT, 
                        ID_MEDECIN, ID_PATIENT, TENTATIVES_ECHEC) 
VALUES (4, 'ahmed.hassani', '2e0bd258396164ab704e916e8343ee53:a18663d2c5f02c48728a510abf582189e20ad31f0ab9c71490817f9dec8b7f2d', 'ahmed.hassani@gmail.com', 'PATIENT', 'ACTIF', 
        NULL, 1, 0);

-- Patient Fatima Mansouri
INSERT INTO UTILISATEUR (ID_UTILISATEUR, USERNAME, PASSWORD_HASH, EMAIL, ROLE, STATUT, 
                        ID_MEDECIN, ID_PATIENT, TENTATIVES_ECHEC) 
VALUES (5, 'fatima.mansouri', '860026007ba7ab4eeba26adfb8a408de:1327fc02da13692efbc3ab3df571a4c133a16faa23c33aaeeb798b4c372a8737', 'f.mansouri@gmail.com', 'PATIENT', 'ACTIF', 
        NULL, 2, 0);

PROMPT 5 utilisateurs inseres

-- ============================================
-- 5. MEDICAMENTS
-- ============================================

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (1, 'Doliprane', 'Paracetamol', 'COMPRIME', '1000mg', 15.5, 500, 50);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (2, 'Amoxil', 'Amoxicilline', 'COMPRIME', '500mg', 45, 300, 40);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (3, 'Ventoline', 'Salbutamol', 'SIROP', '2mg/5ml', 65, 150, 30);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (4, 'Augmentin', 'Amoxicilline + Acide Clavulanique', 'COMPRIME', '1g', 85.5, 200, 35);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (5, 'Aspirine Protect', 'Acide Acetylsalicylique', 'COMPRIME', '100mg', 25, 400, 60);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (6, 'Brufen', 'Ibuprofene', 'COMPRIME', '400mg', 32, 350, 50);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (7, 'Solupred', 'Prednisolone', 'COMPRIME', '20mg', 55, 180, 30);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (8, 'Zyrtec', 'Cetirizine', 'COMPRIME', '10mg', 48, 250, 40);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (9, 'Birodogyl', 'Spiramycine + Metronidazole', 'COMPRIME', '1.5MUI', 72, 160, 25);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (10, 'Mopral', 'Omeprazole', 'GELULE', '20mg', 95, 220, 35);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (11, 'Zithromax', 'Azithromycine', 'COMPRIME', '250mg', 120, 130, 20);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (12, 'Voltarene', 'Diclofenac', 'POMMADE', '1%', 42, 180, 30);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (13, 'Bactrim', 'Sulfamethoxazole + Trimethoprime', 'COMPRIME', '800mg+160mg', 38, 280, 45);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (14, 'Celestene', 'Betamethasone', 'COMPRIME', '0.5mg', 28, 200, 30);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (15, 'Flagyl', 'Metronidazole', 'COMPRIME', '500mg', 35, 310, 50);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (16, 'Insuline Rapide', 'Insuline', 'INJECTION', '100UI/ml', 120, 50, 10);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (17, 'Spasfon', 'Phloroglucinol', 'COMPRIME', '80mg', 12, 95, 50);

INSERT INTO MEDICAMENT (ID_MEDICAMENT, NOM_COMMERCIAL, PRINCIPE_ACTIF, FORME, DOSAGE, 
                       PRIX_UNITAIRE, STOCK_DISPONIBLE, STOCK_ALERTE) 
VALUES (18, 'Advil', 'Ibuprofene', 'COMPRIME', '400mg', 6.5, 180, 60);

PROMPT 18 medicaments inseres

-- ============================================
-- 6. RENDEZ-VOUS (dates futures à partir d'aujourd'hui)
-- ============================================

-- RDV dans 2 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (1, 1, 1, SYSDATE + 2, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 2, 'YYYY-MM-DD') || ' 09:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 2, 'YYYY-MM-DD') || ' 09:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Douleurs thoraciques', 'PLANIFIE', 'Salle 101', SYSDATE);

-- RDV dans 3 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (2, 2, 2, SYSDATE + 3, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 3, 'YYYY-MM-DD') || ' 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 3, 'YYYY-MM-DD') || ' 10:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Vaccination de routine', 'CONFIRME', 'Salle 201', SYSDATE);

-- RDV dans 5 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (3, 3, 3, SYSDATE + 5, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 5, 'YYYY-MM-DD') || ' 14:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 5, 'YYYY-MM-DD') || ' 14:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Consultation pre-operatoire', 'CONFIRME', 'Salle 301', SYSDATE);

-- RDV dans 7 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (4, 4, 4, SYSDATE + 7, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 7, 'YYYY-MM-DD') || ' 09:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 7, 'YYYY-MM-DD') || ' 09:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Suivi diabete', 'PLANIFIE', 'Salle 401', SYSDATE);

-- RDV dans 10 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (5, 5, 2, SYSDATE + 10, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 10, 'YYYY-MM-DD') || ' 11:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 10, 'YYYY-MM-DD') || ' 11:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Controle pediatrique', 'PLANIFIE', 'Salle 202', SYSDATE);

-- RDV dans 14 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (6, 6, 6, SYSDATE + 14, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 14, 'YYYY-MM-DD') || ' 15:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 14, 'YYYY-MM-DD') || ' 15:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Controle de la vue', 'CONFIRME', 'Salle 601', SYSDATE);

-- RDV dans 21 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (7, 7, 7, SYSDATE + 21, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 21, 'YYYY-MM-DD') || ' 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 21, 'YYYY-MM-DD') || ' 10:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Probleme de peau', 'PLANIFIE', 'Salle 701', SYSDATE);

-- RDV dans 30 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (8, 8, 8, SYSDATE + 30, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 30, 'YYYY-MM-DD') || ' 14:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 30, 'YYYY-MM-DD') || ' 14:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Bilan cardiaque', 'CONFIRME', 'Salle 102', SYSDATE);

-- RDV dans 45 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (9, 9, 9, SYSDATE + 45, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 45, 'YYYY-MM-DD') || ' 16:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 45, 'YYYY-MM-DD') || ' 16:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Suivi pediatrique', 'PLANIFIE', 'Salle 203', SYSDATE);

-- RDV dans 60 jours
INSERT INTO RENDEZ_VOUS (ID_RDV, ID_PATIENT, ID_MEDECIN, DATE_RDV, HEURE_DEBUT, HEURE_FIN, 
                        MOTIF, STATUT, SALLE, DATE_CREATION) 
VALUES (10, 10, 5, SYSDATE + 60, 
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 60, 'YYYY-MM-DD') || ' 09:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 60, 'YYYY-MM-DD') || ' 09:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Consultation de routine', 'PLANIFIE', 'Salle 501', SYSDATE);

PROMPT 10 rendez-vous inseres

-- ============================================
-- 7. CONSULTATIONS (pour les RDV terminés - simulés comme passés récemment)
-- ============================================

-- Consultation hier
INSERT INTO CONSULTATION (ID_CONSULTATION, ID_RDV, DATE_CONSULTATION, SYMPTOMES, DIAGNOSTIC, 
                         OBSERVATIONS, PRESCRIPTION, EXAMENS_DEMANDES, TARIF_CONSULTATION) 
VALUES (1, 1, SYSDATE - 1, 
        'Douleurs thoraciques, essoufflement', 
        'Angine de poitrine probable', 
        'Patient presente des facteurs de risque cardiovasculaire. ECG a realiser.', 
        'Repos, regime alimentaire, medicaments prescrits', 
        'ECG, Bilan sanguin complet', 
        250);

-- Mettre le RDV 1 en statut TERMINE
UPDATE RENDEZ_VOUS SET STATUT = 'TERMINE' WHERE ID_RDV = 1;

-- Consultation il y a 3 jours
INSERT INTO CONSULTATION (ID_CONSULTATION, ID_RDV, DATE_CONSULTATION, SYMPTOMES, DIAGNOSTIC, 
                         OBSERVATIONS, PRESCRIPTION, EXAMENS_DEMANDES, TARIF_CONSULTATION) 
VALUES (2, 2, SYSDATE - 3, 
        'Vaccination de routine', 
        'Enfant en bonne sante', 
        'Croissance normale. Poids et taille dans les normes.', 
        'Vaccin ROR administre', 
        NULL, 
        150);

-- Mettre le RDV 2 en statut TERMINE
UPDATE RENDEZ_VOUS SET STATUT = 'TERMINE' WHERE ID_RDV = 2;

PROMPT 2 consultations inserees

-- ============================================
-- 8. TRAITEMENTS
-- ============================================

-- Traitement pour consultation 1 (Angine de poitrine)
INSERT INTO TRAITEMENT (ID_TRAITEMENT, ID_CONSULTATION, ID_MEDICAMENT, POSOLOGIE, 
                       DUREE_TRAITEMENT, INSTRUCTIONS, QUANTITE)
VALUES (seq_traitement.NEXTVAL, 1, 5, '1 comprime par jour le matin', 30, 
        'A prendre a jeun avec un grand verre d eau', 30);

INSERT INTO TRAITEMENT (ID_TRAITEMENT, ID_CONSULTATION, ID_MEDICAMENT, POSOLOGIE, 
                       DUREE_TRAITEMENT, INSTRUCTIONS, QUANTITE)
VALUES (seq_traitement.NEXTVAL, 1, 10, '1 gelule le soir', 30, 
        'A prendre 30 minutes avant le repas', 30);

-- Traitement pour consultation 2 (Vaccination - pas de medicament prescrit)

PROMPT 2 traitements prescrits

-- ============================================
-- 9. FACTURES (générées automatiquement par trigger pour consultations)
-- ============================================

PROMPT Factures generees automatiquement par le trigger TRG_AUTO_FACTURE

-- ============================================
-- VERIFICATION
-- ============================================

PROMPT
PROMPT ============================================
PROMPT RESUME DES DONNEES INSEREES
PROMPT ============================================

SELECT 'DEPARTEMENTS' AS TABLE_NAME, COUNT(*) AS NOMBRE FROM DEPARTEMENT
UNION ALL
SELECT 'MEDECINS', COUNT(*) FROM MEDECIN
UNION ALL
SELECT 'PATIENTS', COUNT(*) FROM PATIENT
UNION ALL
SELECT 'UTILISATEURS', COUNT(*) FROM UTILISATEUR
UNION ALL
SELECT 'MEDICAMENTS', COUNT(*) FROM MEDICAMENT
UNION ALL
SELECT 'RENDEZ-VOUS', COUNT(*) FROM RENDEZ_VOUS
UNION ALL
SELECT 'CONSULTATIONS', COUNT(*) FROM CONSULTATION
UNION ALL
SELECT 'TRAITEMENTS', COUNT(*) FROM TRAITEMENT
UNION ALL
SELECT 'FACTURES', COUNT(*) FROM FACTURE;

PROMPT
PROMPT ============================================
PROMPT Insertion des donnees terminee avec succes
PROMPT ============================================

COMMIT;