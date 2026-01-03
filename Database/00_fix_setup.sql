-- ============================================
-- SCRIPT DE CONFIGURATION - Nouvelle Base Locale
-- Healthcare Patient Records System
-- ============================================

-- Se connecter en tant que SYS dans XE
-- sqlplus sys as sysdba

ALTER SESSION SET "_ORACLE_SCRIPT"=true;

-- ============================================
-- 0. UTILISER LA BASE XE
-- ============================================
-- Pas besoin de créer une PDB, Oracle XE utilise directement le SID XE

PROMPT Base XE sélectionnée

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

-- Créer l'utilisateur dans XE
CREATE USER healthcare_admin IDENTIFIED BY admin123
    DEFAULT TABLESPACE USERS
    TEMPORARY TABLESPACE TEMP
    QUOTA UNLIMITED ON USERS;

PROMPT Utilisateur healthcare_admin créé dans XE

-- ============================================
-- 2. ATTRIBUER LES PRIVILÈGES
-- ============================================

GRANT CONNECT, RESOURCE TO healthcare_admin;
GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE SEQUENCE,
      CREATE PROCEDURE, CREATE TRIGGER, CREATE SYNONYM TO healthcare_admin;

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
PROMPT Base utilisée : XE
PROMPT Utilisateur : healthcare_admin
PROMPT Mot de passe : admin123
PROMPT ============================================
PROMPT
PROMPT Prochaines étapes :
PROMPT 1. Se connecter : sqlplus healthcare_admin/admin123@localhost:1521/XE
PROMPT 2. Exécuter les scripts applicatifs : 01_CREATE_TABLES.sql à 07_DATA.sql
PROMPT ============================================