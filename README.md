# HePRAS : Healthcare Patient Records and Analytics System

Application de bureau pour un établissement de santé : dossiers patients, médecins, rendez-vous, consultations, ordonnances et facturation, avec un tableau de bord de statistiques.
L'interface est en JavaFX, la logique en Java et les données dans une base Oracle avec procédures, fonctions, déclencheurs et vues PL/SQL.

Projet réalisé en équipe à l'ENSA de Tétouan (décembre 2025 à février 2026).

Présentation du projet : https://pro-simpo.github.io/#pd-hepras

## Fonctionnalités

- **Connexion par compte** avec droits selon le rôle : administrateur, médecin, réceptionniste.
- **Patients** : enregistrement, recherche, modification.
- **Médecins et départements** : fiches, spécialités, rattachement à un département.
- **Rendez-vous** : planification avec contrôle de la disponibilité du médecin, suivi du statut.
- **Consultations et traitements** : compte rendu, prescription de médicaments, contrôle du stock.
- **Facturation** : création des factures, enregistrement des paiements, export en PDF.
- **Tableau de bord** : statistiques globales, par période, par médecin et par patient.
- **Journal d'audit** : les modifications des patients et des consultations sont tracées dans la base par des déclencheurs.

## Architecture

```
Database/              Scripts Oracle, à exécuter dans l'ordre
healthcare-backend/    Logique métier et accès aux données (JDBC)
healthcare-frontend/   Interface JavaFX (FXML et CSS)
```

**Backend** (`healthcare-backend`), organisé en couches :

| Dossier | Rôle |
| :--- | :--- |
| `model` | Entités et énumérations |
| `dao` | Accès aux données en JDBC (interfaces et implémentations) |
| `service` | Règles métier |
| `facade` | Points d'entrée regroupés par domaine |
| `validation` | Contrôle des données saisies |
| `transaction` | Gestion des transactions |
| `config` | Lecture de la configuration, pool de connexions HikariCP |
| `util` | Export PDF, pagination, cache, dates |

**Frontend** (`healthcare-frontend`) : un contrôleur et une vue FXML par écran (connexion, accueil, tableau de bord, patients, médecins, rendez-vous, consultations, factures, paramètres).

## Base de données

| Script | Contenu |
| :--- | :--- |
| `00_fix_setup.sql` | Création de l'utilisateur Oracle `healthcare_admin` |
| `01_CREATE_TABLES.sql` | 9 tables, 9 séquences, 22 index |
| `02_PROCEDURES.sql` | 7 procédures stockées |
| `03_FONCTIONS.sql` | 9 fonctions |
| `04_TRIGGERS.sql` | 12 déclencheurs et la table d'audit |
| `05_VIEWS.sql` | 10 vues |
| `06_SECURITY.sql` | 4 rôles et une politique d'accès aux données des patients |
| `07_DATA.sql` | Données de test |
| `08_sync_sequences.sql` | Remise à niveau des séquences après l'insertion des données |

## Technologies

| Rôle | Outil |
| :--- | :--- |
| Langage | Java (JDK 23) |
| Interface | JavaFX 23, FXML, ControlsFX |
| Base de données | Oracle Database XE 21c, PL/SQL |
| Accès aux données | JDBC (`ojdbc11`), pool de connexions HikariCP |
| Export PDF | iText 7 |
| Journalisation | SLF4J et Logback |
| Construction | Maven |

## Installation

### 1. Base de données

Il faut Oracle Database XE. Exécutez `00_fix_setup.sql` avec le compte `SYS`, puis les scripts `01` à `08` dans l'ordre avec le compte `healthcare_admin` :

```bash
sqlplus sys as sysdba @Database/00_fix_setup.sql
sqlplus healthcare_admin/admin123@localhost:1521/XE
```

```sql
@Database/01_CREATE_TABLES.sql
@Database/02_PROCEDURES.sql
@Database/03_FONCTIONS.sql
@Database/04_TRIGGERS.sql
@Database/05_VIEWS.sql
@Database/06_SECURITY.sql
@Database/07_DATA.sql
@Database/08_sync_sequences.sql
```

Les paramètres de connexion sont dans `healthcare-backend/src/main/resources/application-dev.properties`.

### 2. Application

Il faut un JDK 23 et Maven. Le frontend dépend du backend : installez d'abord le backend.

```bash
cd healthcare-backend
mvn clean install

cd ../healthcare-frontend
mvn clean javafx:run
```

### Comptes de test

Les comptes ci-dessous sont créés par `07_DATA.sql` pour une base locale de démonstration.

| Rôle | Nom d'utilisateur | Mot de passe |
| :--- | :--- | :--- |
| Administrateur | `admin` | `123456` |
| Médecin | `dr.alami` | `123456` |
| Réceptionniste | `receptionniste` | `123456` |

## Équipe

Taobata Simporé, Abdoul-Moumouni Diallo, Daniel Kunaka.
