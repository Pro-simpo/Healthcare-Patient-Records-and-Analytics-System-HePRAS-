-- ============================================
-- SCRIPT DE CORRECTION - Connexion Backend
-- Healthcare Patient Records System
-- ============================================

-- Se connecter en tant que SYS
-- sqlplus sys as sysdba

ALTER SESSION SET "_ORACLE_SCRIPT"=true;

-- ============================================
-- 1. CRÉER L'UTILISATEUR healthcare_admin
-- ============================================

-- Supprimer si existe déjà
BEGIN
   EXECUTE IMMEDIATE 'DROP USER healthcare_admin CASCADE';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- Créer l'utilisateur
CREATE USER healthcare_admin IDENTIFIED BY admin123
    DEFAULT TABLESPACE USERS
    TEMPORARY TABLESPACE temp
    QUOTA UNLIMITED ON USERS;

PROMPT Utilisateur healthcare_admin créé

-- ============================================
-- 2. ATTRIBUER LES PRIVILÈGES
-- ============================================

GRANT CONNECT TO healthcare_admin;
GRANT RESOURCE TO healthcare_admin;
GRANT CREATE SESSION TO healthcare_admin;
GRANT CREATE TABLE TO healthcare_admin;
GRANT CREATE VIEW TO healthcare_admin;
GRANT CREATE SEQUENCE TO healthcare_admin;
GRANT CREATE PROCEDURE TO healthcare_admin;
GRANT CREATE TRIGGER TO healthcare_admin;
GRANT CREATE SYNONYM TO healthcare_admin;

-- Privilèges DBMS packages
GRANT EXECUTE ON DBMS_LOCK TO healthcare_admin;
GRANT EXECUTE ON DBMS_OUTPUT TO healthcare_admin;
GRANT EXECUTE ON DBMS_CRYPTO TO healthcare_admin;
GRANT EXECUTE ON DBMS_RANDOM TO healthcare_admin;

PROMPT Privilèges attribués

-- ============================================
-- 3. VÉRIFICATION
-- ============================================

SELECT username, default_tablespace, account_status
FROM dba_users
WHERE username = 'HEALTHCARE_ADMIN';

PROMPT ============================================
PROMPT Configuration corrigée !
PROMPT Utilisateur : healthcare_admin
PROMPT Mot de passe : admin123
PROMPT ============================================
PROMPT
PROMPT Prochaines étapes :
PROMPT 1. Se connecter : sqlplus healthcare_admin/admin123@localhost:1521/XEPDB1
PROMPT 2. Exécuter les scripts : 01_CREATE_TABLES.sql à 07_DATA.sql
PROMPT ============================================