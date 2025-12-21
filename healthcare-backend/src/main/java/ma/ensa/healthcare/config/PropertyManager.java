package ma.ensa.healthcare.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire centralisé des propriétés de configuration.
 * Implémente le pattern Singleton.
 * [cite: 120, 122, 124]
 */
public class PropertyManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyManager.class);
    private static PropertyManager instance;
    private final Properties properties;
    private final String environment;

    // Constructeur privé (Singleton)
    private PropertyManager() {
        properties = new Properties();
        environment = determineEnvironment();
        loadProperties();
    }

    /**
     * Point d'accès unique au Singleton.
     * [cite: 131]
     */
    public static synchronized PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    /**
     * Détermine l'environnement actuel (dev, test, prod).
     * Ordre : System Property > Env Variable > Default (dev)
     * [cite: 137, 283]
     */
    private String determineEnvironment() {
        String env = System.getProperty("app.env");
        if (env == null) {
            env = System.getenv("APP_ENV");
        }
        if (env == null) {
            env = "dev"; // Par défaut [cite: 140]
        }
        logger.info("Environment détecté : {}", env);
        return env;
    }

    /**
     * Charge le fichier properties correspondant à l'environnement.
     * [cite: 125, 283]
     */
    private void loadProperties() {
        String fileName = "application-" + environment + ".properties";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                String errorMsg = "Impossible de trouver le fichier de configuration : " + fileName;
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            properties.load(input);
            logger.info("Configuration chargée avec succès depuis : {}", fileName);
        } catch (IOException ex) {
            logger.error("Erreur lors du chargement de la configuration", ex);
            throw new RuntimeException("Erreur de configuration critique", ex);
        }
    }

    // --- Méthodes d'accès aux propriétés [cite: 133-136, 283] ---

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Valeur invalide pour la clé {} (attendu: int). Utilisation de la valeur par défaut: {}", key, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    // --- Méthodes utilitaires d'environnement [cite: 136] ---
    
    public boolean isDevelopment() {
        return "dev".equalsIgnoreCase(environment);
    }
    
    public boolean isTest() {
        return "test".equalsIgnoreCase(environment);
    }
    
    public boolean isProduction() {
        return "prod".equalsIgnoreCase(environment);
    }
}