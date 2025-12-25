-- ============================================
-- DONNEES DE TEST - Contexte Marocain
-- Healthcare Patient Records System
-- ============================================

SET SERVEROUTPUT ON;

PROMPT ============================================
PROMPT Insertion des donnees de test
PROMPT ============================================

-- ============================================
-- 1. DEPARTEMENTS
-- ============================================

INSERT INTO DEPARTEMENT (id_departement, nom_departement, nombre_lits, telephone)
VALUES (seq_departement.NEXTVAL, 'Cardiologie', 25, '+212520123456');

INSERT INTO DEPARTEMENT (id_departement, nom_departement, nombre_lits, telephone)
VALUES (seq_departement.NEXTVAL, 'Pediatrie', 30, '+212520123457');

INSERT INTO DEPARTEMENT (id_departement, nom_departement, nombre_lits, telephone)
VALUES (seq_departement.NEXTVAL, 'Chirurgie Generale', 20, '+212520123458');

INSERT INTO DEPARTEMENT (id_departement, nom_departement, nombre_lits, telephone)
VALUES (seq_departement.NEXTVAL, 'Medecine Interne', 35, '+212520123459');

INSERT INTO DEPARTEMENT (id_departement, nom_departement, nombre_lits, telephone)
VALUES (seq_departement.NEXTVAL, 'Gynecologie-Obstetrique', 28, '+212520123460');

INSERT INTO DEPARTEMENT (id_departement, nom_departement, nombre_lits, telephone)
VALUES (seq_departement.NEXTVAL, 'Ophtalmologie', 15, '+212520123461');

INSERT INTO DEPARTEMENT (id_departement, nom_departement, nombre_lits, telephone)
VALUES (seq_departement.NEXTVAL, 'Dermatologie', 12, '+212520123462');

COMMIT;
PROMPT 7 departements inseres

-- ============================================
-- 2. MEDECINS
-- ============================================

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001234', 'ALAMI', 'Youssef', 'Cardiologie', '+212661234567', 'y.alami@hospital.ma', TO_DATE('2015-03-15', 'YYYY-MM-DD'), 1);

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001235', 'BENANI', 'Fatima', 'Pediatrie', '+212662345678', 'f.benani@hospital.ma', TO_DATE('2018-09-01', 'YYYY-MM-DD'), 2);

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001236', 'TAZI', 'Mohammed', 'Chirurgie Generale', '+212663456789', 'm.tazi@hospital.ma', TO_DATE('2012-01-10', 'YYYY-MM-DD'), 3);

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001237', 'IDRISSI', 'Amina', 'Medecine Interne', '+212664567890', 'a.idrissi@hospital.ma', TO_DATE('2016-06-20', 'YYYY-MM-DD'), 4);

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001238', 'FILALI', 'Hassan', 'Gynecologie-Obstetrique', '+212665678901', 'h.filali@hospital.ma', TO_DATE('2014-11-05', 'YYYY-MM-DD'), 5);

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001239', 'SLAOUI', 'Khadija', 'Ophtalmologie', '+212666789012', 'k.slaoui@hospital.ma', TO_DATE('2019-02-14', 'YYYY-MM-DD'), 6);

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001240', 'BENNANI', 'Omar', 'Dermatologie', '+212667890123', 'o.bennani@hospital.ma', TO_DATE('2017-08-30', 'YYYY-MM-DD'), 7);

INSERT INTO MEDECIN (id_medecin, numero_ordre, nom, prenom, specialite, telephone, email, date_embauche, id_departement)
VALUES (seq_medecin.NEXTVAL, 'M001241', 'CHAKIR', 'Salma', 'Cardiologie', '+212668901234', 's.chakir@hospital.ma', TO_DATE('2020-01-15', 'YYYY-MM-DD'), 1);

COMMIT;
PROMPT 8 medecins inseres

-- Definir les chefs de departement
UPDATE DEPARTEMENT SET chef_departement_id = 1 WHERE id_departement = 1; -- Dr. Alami chef Cardiologie
UPDATE DEPARTEMENT SET chef_departement_id = 2 WHERE id_departement = 2; -- Dr. Benani chef Pediatrie
UPDATE DEPARTEMENT SET chef_departement_id = 3 WHERE id_departement = 3; -- Dr. Tazi chef Chirurgie
COMMIT;
PROMPT Chefs de departement definis

-- ============================================
-- 3. PATIENTS
-- ============================================

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'M567234', 'HASSANI', 'Ahmed', TO_DATE('1985-06-15', 'YYYY-MM-DD'), 'M', '25 Rue Hassan II', 'Casablanca', '20000', '+212661111111', 'ahmed.hassani@gmail.com', 'O+', NULL);

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'F892341', 'MANSOURI', 'Fatima', TO_DATE('1990-03-22', 'YYYY-MM-DD'), 'F', '12 Avenue Mohammed V', 'Rabat', '10000', '+212662222222', 'f.mansouri@gmail.com', 'A+', 'Penicilline');

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'K345678', 'BENSGHIR', 'Karim', TO_DATE('1978-11-08', 'YYYY-MM-DD'), 'M', '45 Bd Zerktouni', 'Marrakech', '40000', '+212663333333', 'k.BENSGHIR@yahoo.fr', 'B+', NULL);

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'T456789', 'MOIZ', 'Salma', TO_DATE('1995-07-30', 'YYYY-MM-DD'), 'F', '78 Rue de Fes', 'Tanger', '90000', '+212664444444', 's.MOIZ@hotmail.com', 'AB+', 'Aspirine');

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'CB567890', 'TAHIRI', 'Mehdi', TO_DATE('2010-04-12', 'YYYY-MM-DD'), 'M', '34 Quartier Palmiers', 'Casablanca', '20100', '+212665555555', 'tahiri.family@gmail.com', 'O-', NULL);

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'A678901', 'ZIANI', 'Zineb', TO_DATE('1988-12-25', 'YYYY-MM-DD'), 'F', '56 Avenue des FAR', 'Agadir', '80000', '+212666666666', 'z.ziani@outlook.com', 'A-', 'Iode');

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'F789012', 'KADIRI', 'Yassine', TO_DATE('1982-09-18', 'YYYY-MM-DD'), 'M', '23 Rue Ibn Batouta', 'Fes', '30000', '+212667777777', 'y.kadiri@gmail.com', 'B-', NULL);

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'M890123', 'BERRADA', 'Nadia', TO_DATE('1975-02-14', 'YYYY-MM-DD'), 'F', '89 Boulevard Moulay Youssef', 'Meknes', '50000', '+212668888888', 'n.berrada@yahoo.fr', 'O+', 'Pollen');

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'K901234', 'RAMI', 'Samir', TO_DATE('1992-05-05', 'YYYY-MM-DD'), 'M', '67 Quartier Industriel', 'Kenitra', '14000', '+212669999999', 's.rami@gmail.com', 'AB-', NULL);

INSERT INTO PATIENT (id_patient, cin, nom, prenom, date_naissance, sexe, adresse, ville, code_postal, telephone, email, groupe_sanguin, allergies)
VALUES (seq_patient.NEXTVAL, 'O012345', 'LAHLOU', 'Meriem', TO_DATE('2015-08-20', 'YYYY-MM-DD'), 'F', '15 Residence Al Amal', 'Oujda', '60000', '+212660000000', 'lahlou.family@gmail.com', 'A+', NULL);

COMMIT;
PROMPT 10 patients inseres

-- ============================================
-- 4. MEDICAMENTS
-- ============================================

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Doliprane', 'Paracetamol', 'COMPRIME', '1000mg', 15.50, 500, 50);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Amoxil', 'Amoxicilline', 'COMPRIME', '500mg', 45.00, 300, 40);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Ventoline', 'Salbutamol', 'SIROP', '2mg/5ml', 65.00, 150, 30);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Augmentin', 'Amoxicilline + Acide Clavulanique', 'COMPRIME', '1g', 85.50, 200, 35);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Aspirine Protect', 'Acide Acetylsalicylique', 'COMPRIME', '100mg', 25.00, 400, 60);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Brufen', 'Ibuprofene', 'COMPRIME', '400mg', 32.00, 350, 50);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Solupred', 'Prednisolone', 'COMPRIME', '20mg', 55.00, 180, 30);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Zyrtec', 'Cetirizine', 'COMPRIME', '10mg', 48.00, 250, 40);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Birodogyl', 'Spiramycine + Metronidazole', 'COMPRIME', '1.5MUI', 72.00, 160, 25);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Mopral', 'Omeprazole', 'GELULE', '20mg', 95.00, 220, 35);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Zithromax', 'Azithromycine', 'COMPRIME', '250mg', 120.00, 130, 20);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Voltarene', 'Diclofenac', 'POMMADE', '1%', 42.00, 180, 30);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Bactrim', 'Sulfamethoxazole + Trimethoprime', 'COMPRIME', '800mg+160mg', 38.00, 280, 45);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Celestene', 'Betamethasone', 'COMPRIME', '0.5mg', 28.00, 200, 30);

INSERT INTO MEDICAMENT (id_medicament, nom_commercial, principe_actif, forme, dosage, prix_unitaire, stock_disponible, stock_alerte)
VALUES (seq_medicament.NEXTVAL, 'Flagyl', 'Metronidazole', 'COMPRIME', '500mg', 35.00, 310, 50);

COMMIT;
PROMPT 15 medicaments inseres

-- ============================================
-- 5. RENDEZ-VOUS
-- ============================================

-- RDV aujourd'hui
INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 1, 1, TRUNC(SYSDATE), 
        TO_TIMESTAMP(TO_CHAR(SYSDATE, 'YYYY-MM-DD') || ' 09:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE, 'YYYY-MM-DD') || ' 09:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Douleurs thoraciques', 'TERMINE', 'Salle 101');

INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 2, 2, TRUNC(SYSDATE),
        TO_TIMESTAMP(TO_CHAR(SYSDATE, 'YYYY-MM-DD') || ' 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE, 'YYYY-MM-DD') || ' 10:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Vaccination enfant', 'TERMINE', 'Salle 201');

INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 3, 3, TRUNC(SYSDATE),
        TO_TIMESTAMP(TO_CHAR(SYSDATE, 'YYYY-MM-DD') || ' 14:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE, 'YYYY-MM-DD') || ' 14:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Consultation pre-operatoire', 'CONFIRME', 'Salle 301');

-- RDV demain
INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 4, 4, TRUNC(SYSDATE) + 1,
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 1, 'YYYY-MM-DD') || ' 09:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 1, 'YYYY-MM-DD') || ' 09:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Suivi diabete', 'PLANIFIE', 'Salle 401');

INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 5, 2, TRUNC(SYSDATE) + 1,
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 1, 'YYYY-MM-DD') || ' 11:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE + 1, 'YYYY-MM-DD') || ' 11:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Controle pediatrique', 'PLANIFIE', 'Salle 202');

-- RDV de la semaine derniere (pour consultation)
INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 6, 7, TRUNC(SYSDATE) - 3,
        TO_TIMESTAMP(TO_CHAR(SYSDATE - 3, 'YYYY-MM-DD') || ' 15:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE - 3, 'YYYY-MM-DD') || ' 15:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Probleme de peau', 'TERMINE', 'Salle 701');

INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 7, 1, TRUNC(SYSDATE) - 5,
        TO_TIMESTAMP(TO_CHAR(SYSDATE - 5, 'YYYY-MM-DD') || ' 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE - 5, 'YYYY-MM-DD') || ' 10:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Hypertension arterielle', 'TERMINE', 'Salle 102');

INSERT INTO RENDEZ_VOUS (id_rdv, id_patient, id_medecin, date_rdv, heure_debut, heure_fin, motif, statut, salle)
VALUES (seq_rdv.NEXTVAL, 8, 5, TRUNC(SYSDATE) - 2,
        TO_TIMESTAMP(TO_CHAR(SYSDATE - 2, 'YYYY-MM-DD') || ' 16:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP(TO_CHAR(SYSDATE - 2, 'YYYY-MM-DD') || ' 16:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'Consultation gynecologique', 'TERMINE', 'Salle 501');

COMMIT;
PROMPT 8 rendez-vous inseres

-- ============================================
-- 6. CONSULTATIONS
-- ============================================

-- Consultation 1 (Patient 1 - Dr. Alami)
INSERT INTO CONSULTATION (id_consultation, id_rdv, date_consultation, symptomes, diagnostic, observations, prescription, tarif_consultation)
VALUES (seq_consultation.NEXTVAL, 1, SYSDATE,
        'Douleurs thoraciques, essoufflement',
        'Angine de poitrine',
        'Patient presente des facteurs de risque cardiovasculaire. ECG montre des anomalies ST-T.',
        'Repos, regime alimentaire, medicaments prescrits',
        250.00);

-- Consultation 2 (Patient 2 - Dr. Benani)
INSERT INTO CONSULTATION (id_consultation, id_rdv, date_consultation, symptomes, diagnostic, observations, prescription, tarif_consultation)
VALUES (seq_consultation.NEXTVAL, 2, SYSDATE,
        'Vaccination de routine',
        'Enfant en bonne sante',
        'Croissance normale. Poids et taille dans les normes.',
        'Vaccin ROR administre',
        150.00);

-- Consultation 3 (Patient 6 - Dr. Bennani - Dermatologie)
INSERT INTO CONSULTATION (id_consultation, id_rdv, date_consultation, symptomes, diagnostic, observations, prescription, tarif_consultation)
VALUES (seq_consultation.NEXTVAL, 6, SYSDATE - 3,
        'Eruptions cutanees, demangeaisons',
        'Eczema atopique',
        'Lesions erythemateuses sur les bras et le cou.',
        'Creme corticoide, antihistaminiques',
        200.00);

-- Consultation 4 (Patient 7 - Dr. Alami)
INSERT INTO CONSULTATION (id_consultation, id_rdv, date_consultation, symptomes, diagnostic, observations, prescription, tarif_consultation)
VALUES (seq_consultation.NEXTVAL, 7, SYSDATE - 5,
        'Tension arterielle elevee, maux de tete',
        'Hypertension arterielle grade 2',
        'TA: 160/100 mmHg. Patient non traite auparavant.',
        'Antihypertenseurs, regime hyposode',
        250.00);

-- Consultation 5 (Patient 8 - Dr. Filali)
INSERT INTO CONSULTATION (id_consultation, id_rdv, date_consultation, symptomes, diagnostic, observations, prescription, tarif_consultation)
VALUES (seq_consultation.NEXTVAL, 8, SYSDATE - 2,
        'Retard de regles, test de grossesse positif',
        'Grossesse confirmee (8 semaines)',
        'Echographie transvaginale normale. Embryon viable.',
        'Acide folique, vitamines prenatales',
        300.00);

COMMIT;
PROMPT 5 consultations inserees

-- ============================================
-- 7. TRAITEMENTS
-- ============================================

-- Traitements pour Consultation 1 (Angine de poitrine)
INSERT INTO TRAITEMENT (id_traitement, id_consultation, id_medicament, posologie, duree_traitement, instructions, quantite)
VALUES (seq_traitement.NEXTVAL, 1, 5, '1 comprime par jour le matin', 90, 'A prendre a jeun', 90);

-- Traitements pour Consultation 3 (Eczema)
INSERT INTO TRAITEMENT (id_traitement, id_consultation, id_medicament, posologie, duree_traitement, instructions, quantite)
VALUES (seq_traitement.NEXTVAL, 3, 12, 'Appliquer 2 fois par jour', 21, 'Sur les zones affectees', 2);

INSERT INTO TRAITEMENT (id_traitement, id_consultation, id_medicament, posologie, duree_traitement, instructions, quantite)
VALUES (seq_traitement.NEXTVAL, 3, 8, '1 comprime le soir', 14, 'En cas de demangeaisons', 14);

-- Traitements pour Consultation 4 (Hypertension)
INSERT INTO TRAITEMENT (id_traitement, id_consultation, id_medicament, posologie, duree_traitement, instructions, quantite)
VALUES (seq_traitement.NEXTVAL, 4, 5, '1 comprime par jour', 30, 'Prendre le matin', 30);

-- Traitements pour Consultation 5 (Grossesse)
-- Les medicaments pour grossesse ne sont pas dans notre liste, on utilise des generiques
INSERT INTO TRAITEMENT (id_traitement, id_consultation, id_medicament, posologie, duree_traitement, instructions, quantite)
VALUES (seq_traitement.NEXTVAL, 5, 1, '1 comprime si douleur', 90, 'Maximum 3g par jour', 30);

COMMIT;
PROMPT 5 traitements prescrits

-- Note: Les factures sont generees automatiquement par le trigger TRG_AUTO_FACTURE
PROMPT Factures generees automatiquement par trigger

-- ============================================
-- 8. UTILISATEURS
-- ============================================

-- Utilisateur pour Dr. Alami
INSERT INTO UTILISATEUR (id_utilisateur, username, password_hash, email, role, statut, id_medecin, date_creation)
VALUES (seq_utilisateur.NEXTVAL, 'dr.alami', 
        'c70b5dd9ebfb6f51d09d4132b7170c9d20750a7852f00680f65658f0310e810', -- Hash de 'Medecin2024#'
        'y.alami@hospital.ma', 'MEDECIN', 'ACTIF', 1, SYSDATE);

-- Utilisateur pour receptionniste
INSERT INTO UTILISATEUR (id_utilisateur, username, password_hash, email, role, statut, date_creation)
VALUES (seq_utilisateur.NEXTVAL, 'receptionniste', 
        'f6a6263167c92de8644ac998b3c4e4d1bd6c192618e5b1e5dbf0e1b5d4b3e6d', -- Hash de 'Reception2024#'
        'reception@hospital.ma', 'RECEPTIONNISTE', 'ACTIF', SYSDATE);

-- Utilisateur pour admin
INSERT INTO UTILISATEUR (id_utilisateur, username, password_hash, email, role, statut, date_creation)
VALUES (seq_utilisateur.NEXTVAL, 'admin', 
        'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', -- Hash de 'Admin2024#'
        'admin@hospital.ma', 'ADMIN', 'ACTIF', SYSDATE); 

-- Utilisateur patient (Patient Hassani)
INSERT INTO UTILISATEUR (id_utilisateur, username, password_hash, email, role, statut, id_patient, date_creation)
VALUES (seq_utilisateur.NEXTVAL, 'ahmed.hassani', 
        '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', -- Hash de 'Patient2024#'
        'ahmed.hassani@gmail.com', 'PATIENT', 'ACTIF', 1, SYSDATE);

COMMIT;
PROMPT 4 utilisateurs inseres

-- ============================================
-- VERIFICATION DES DONNEES
-- ============================================

PROMPT
PROMPT ============================================
PROMPT VERIFICATION DES DONNEES INSEREES
PROMPT ============================================

SELECT 'DEPARTEMENTS' AS Table_Name, COUNT(*) AS Nombre_Lignes FROM DEPARTEMENT
UNION ALL
SELECT 'MEDECINS', COUNT(*) FROM MEDECIN
UNION ALL
SELECT 'PATIENTS', COUNT(*) FROM PATIENT
UNION ALL
SELECT 'RENDEZ_VOUS', COUNT(*) FROM RENDEZ_VOUS
UNION ALL
SELECT 'CONSULTATIONS', COUNT(*) FROM CONSULTATION
UNION ALL
SELECT 'TRAITEMENTS', COUNT(*) FROM TRAITEMENT
UNION ALL
SELECT 'MEDICAMENTS', COUNT(*) FROM MEDICAMENT
UNION ALL
SELECT 'FACTURES', COUNT(*) FROM FACTURE
UNION ALL
SELECT 'UTILISATEURS', COUNT(*) FROM UTILISATEUR;

PROMPT
PROMPT ============================================
PROMPT DONNEES DE TEST INSEREES AVEC SUCCES !
PROMPT ============================================
PROMPT
PROMPT Contexte: Systeme hospitalier marocain
PROMPT Villes: Casablanca, Rabat, Marrakech, Tanger, Fes, etc.
PROMPT Monnaie: Dirham marocain (DH)
PROMPT Telephones: Format +212XXXXXXXXX
PROMPT CIN: Carte d'Identite Nationale (format: X123456 ou 12345678)
PROMPT ============================================