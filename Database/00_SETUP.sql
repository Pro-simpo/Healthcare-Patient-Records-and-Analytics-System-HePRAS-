-- ============================================
-- SCRIPT DE SETUP INITIAL
-- Healthcare Patient Records System
-- Oracle 21c
-- ============================================



-- Activer le mode script 
ALTER SESSION SET "_ORACLE_SCRIPT"=true;


-- ============================================
-- 1. TABLESPACE
-- ============================================

CREATE TABLESPACE healthcare_data
    DATAFILE 'healthcare_data01.dbf' SIZE 100M
    AUTOEXTEND ON NEXT 10M MAXSIZE 500M
    SEGMENT SPACE MANAGEMENT AUTO;

PROMPT Tablespace healthcare_data cree

-- ============================================
-- 2. UTILISATEUR
-- ============================================

CREATE USER healthy IDENTIFIED BY Healthy2024
    DEFAULT TABLESPACE healthcare_data
    TEMPORARY TABLESPACE temp
    QUOTA UNLIMITED ON healthcare_data;

PROMPT Utilisateur healthy cree

-- ============================================
-- 3. ATTRIBUER LES PRIVILÈGES
-- ============================================

-- Privilèges de base
GRANT CONNECT TO healthy;
GRANT RESOURCE TO healthy;
GRANT DBA TO healthy;  

-- Privilèges explicites supplémentaires
GRANT CREATE SESSION TO healthy;
GRANT CREATE TABLE TO healthy;
GRANT CREATE VIEW TO healthy;
GRANT CREATE SEQUENCE TO healthy;
GRANT CREATE PROCEDURE TO healthy;
GRANT CREATE TRIGGER TO healthy;
GRANT CREATE SYNONYM TO healthy;
GRANT CREATE ROLE TO healthy;
GRANT CREATE USER TO healthy;
GRANT ALTER USER TO healthy;

-- Privilèges DBMS packages
GRANT EXECUTE ON DBMS_LOCK TO healthy;
GRANT EXECUTE ON DBMS_OUTPUT TO healthy;
GRANT EXECUTE ON DBMS_RLS TO healthy;
GRANT EXECUTE ON DBMS_CRYPTO TO healthy;
GRANT EXECUTE ON DBMS_RANDOM TO healthy;

-- Privilèges d'audit
GRANT AUDIT ANY TO healthy;

PROMPT Privileges attribues a healthy

-- ============================================
-- 5. VÉRIFICATION
-- ============================================

SET LINESIZE 120
COLUMN username FORMAT A20
COLUMN default_tablespace FORMAT A20
COLUMN temporary_tablespace FORMAT A20
COLUMN account_status FORMAT A20

PROMPT
PROMPT === VERIFICATION UTILISATEUR ===
SELECT username, default_tablespace, temporary_tablespace, account_status
FROM dba_users
WHERE username = 'HEALTHY';

PROMPT
PROMPT === VERIFICATION PRIVILEGES ===
SELECT privilege
FROM dba_sys_privs
WHERE grantee = 'HEALTHY'
ORDER BY privilege;

PROMPT
PROMPT === VERIFICATION ROLES ===
SELECT granted_role
FROM dba_role_privs
WHERE grantee = 'HEALTHY'
ORDER BY granted_role;

PROMPT
PROMPT ============================================
PROMPT SETUP TERMINE AVEC SUCCES !
PROMPT ============================================
PROMPT
PROMPT Informations de connexion :
PROMPT ---------------------------
PROMPT Utilisateur : healthy
PROMPT Mot de passe : Healthy2024
PROMPT Tablespace : healthcare_data
PROMPT
PROMPT Commande de connexion :
PROMPT sqlplus healthy/Healthy2024@localhost:1521/XE