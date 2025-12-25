package ma.ensa.healthcare.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire de cache thread-safe avec TTL
 * Version simple mais sécurisée pour applications de taille moyenne
 * 
 * Pour production à grande échelle, utiliser Redis ou Caffeine
 */
public class CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);
    
    // Cache thread-safe
    private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    // Configuration par défaut
    private static final long DEFAULT_TTL_SECONDS = 3600; // 1 heure
    private static final int MAX_CACHE_SIZE = 1000;

    /**
     * Entrée de cache avec TTL
     */
    private static class CacheEntry {
        private final Object value;
        private final Instant expirationTime;

        public CacheEntry(Object value, long ttlSeconds) {
            this.value = value;
            this.expirationTime = Instant.now().plusSeconds(ttlSeconds);
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expirationTime);
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * Ajoute une valeur au cache avec TTL par défaut
     */
    public static void put(String key, Object value) {
        put(key, value, DEFAULT_TTL_SECONDS);
    }

    /**
     * Ajoute une valeur au cache avec TTL personnalisé
     */
    public static void put(String key, Object value, long ttlSeconds) {
        if (key == null || value == null) {
            logger.warn("Tentative d'ajout d'une entrée null au cache");
            return;
        }

        // Vérifier la taille du cache
        if (cache.size() >= MAX_CACHE_SIZE) {
            logger.warn("Cache plein ({}), nettoyage automatique", MAX_CACHE_SIZE);
            evictExpiredEntries();
            
            // Si toujours plein après nettoyage, supprimer la plus ancienne
            if (cache.size() >= MAX_CACHE_SIZE) {
                evictOldestEntry();
            }
        }

        cache.put(key, new CacheEntry(value, ttlSeconds));
        logger.debug("Cache: ajout de la clé '{}' (TTL: {}s)", key, ttlSeconds);
    }

    /**
     * Récupère une valeur du cache
     */
    public static Object get(String key) {
        if (key == null) {
            return null;
        }

        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            logger.debug("Cache MISS: clé '{}'", key);
            return null;
        }

        if (entry.isExpired()) {
            logger.debug("Cache EXPIRED: clé '{}'", key);
            cache.remove(key);
            return null;
        }

        logger.debug("Cache HIT: clé '{}'", key);
        return entry.getValue();
    }

    /**
     * Récupère une valeur avec type générique
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> get(String key, Class<T> type) {
        Object value = get(key);
        
        if (value == null) {
            return Optional.empty();
        }

        if (type.isInstance(value)) {
            return Optional.of((T) value);
        }

        logger.warn("Cache: type incorrect pour la clé '{}' (attendu: {}, trouvé: {})", 
                   key, type.getSimpleName(), value.getClass().getSimpleName());
        return Optional.empty();
    }

    /**
     * Vérifie si une clé existe dans le cache (et n'est pas expirée)
     */
    public static boolean containsKey(String key) {
        if (key == null) {
            return false;
        }

        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            return false;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            return false;
        }

        return true;
    }

    /**
     * Supprime une entrée du cache
     */
    public static void remove(String key) {
        if (key != null) {
            cache.remove(key);
            logger.debug("Cache: suppression de la clé '{}'", key);
        }
    }

    /**
     * Vide complètement le cache
     */
    public static void clear() {
        int size = cache.size();
        cache.clear();
        logger.info("Cache vidé ({} entrées supprimées)", size);
    }

    /**
     * Supprime toutes les entrées expirées
     */
    public static void evictExpiredEntries() {
        int removed = 0;
        
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().isExpired()) {
                cache.remove(entry.getKey());
                removed++;
            }
        }

        if (removed > 0) {
            logger.info("Cache: {} entrées expirées supprimées", removed);
        }
    }

    /**
     * Supprime l'entrée la plus ancienne (LRU simplifié)
     */
    private static void evictOldestEntry() {
        if (!cache.isEmpty()) {
            String oldestKey = cache.keySet().iterator().next();
            cache.remove(oldestKey);
            logger.warn("Cache: éviction forcée de la clé '{}'", oldestKey);
        }
    }

    /**
     * Obtient la taille actuelle du cache
     */
    public static int size() {
        return cache.size();
    }

    /**
     * Obtient des statistiques du cache
     */
    public static CacheStats getStats() {
        int total = cache.size();
        int expired = 0;
        
        for (CacheEntry entry : cache.values()) {
            if (entry.isExpired()) {
                expired++;
            }
        }

        return new CacheStats(total, expired, MAX_CACHE_SIZE);
    }

    /**
     * Statistiques du cache
     */
    public static class CacheStats {
        public final int totalEntries;
        public final int expiredEntries;
        public final int maxSize;

        public CacheStats(int totalEntries, int expiredEntries, int maxSize) {
            this.totalEntries = totalEntries;
            this.expiredEntries = expiredEntries;
            this.maxSize = maxSize;
        }

        public int getActiveEntries() {
            return totalEntries - expiredEntries;
        }

        public double getUsagePercent() {
            return (totalEntries * 100.0) / maxSize;
        }

        @Override
        public String toString() {
            return String.format("Cache Stats: %d/%d entries (%.1f%%), %d expired", 
                               getActiveEntries(), maxSize, getUsagePercent(), expiredEntries);
        }
    }
}