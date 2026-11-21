package ma.ensa.healthcare.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Hachage et vérification des mots de passe.
 *
 * Chaque mot de passe est haché en SHA-256 avec un sel aléatoire propre
 * à l'utilisateur. La valeur enregistrée dans UTILISATEUR.password_hash
 * a la forme "sel:empreinte", les deux parties étant écrites en hexadécimal.
 *
 * Le mot de passe en clair n'est jamais enregistré.
 */
public final class PasswordUtils {

    private static final String ALGORITHME = "SHA-256";
    private static final int TAILLE_SEL = 16;
    private static final char SEPARATEUR = ':';
    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtils() {
        // Classe utilitaire : pas d'instance
    }

    /**
     * Hache un mot de passe avec un nouveau sel aléatoire.
     *
     * @param password mot de passe en clair
     * @return la valeur à enregistrer, sous la forme "sel:empreinte"
     */
    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être null");
        }
        byte[] sel = new byte[TAILLE_SEL];
        RANDOM.nextBytes(sel);
        return toHex(sel) + SEPARATEUR + toHex(sha256(sel, password));
    }

    /**
     * Vérifie un mot de passe saisi contre la valeur enregistrée.
     *
     * @param password mot de passe en clair saisi par l'utilisateur
     * @param valeurEnregistree contenu de password_hash ("sel:empreinte")
     * @return true si le mot de passe correspond
     */
    public static boolean verify(String password, String valeurEnregistree) {
        if (password == null || valeurEnregistree == null) {
            return false;
        }
        int position = valeurEnregistree.indexOf(SEPARATEUR);
        if (position <= 0 || position == valeurEnregistree.length() - 1) {
            return false;
        }
        try {
            byte[] sel = fromHex(valeurEnregistree.substring(0, position));
            byte[] attendue = fromHex(valeurEnregistree.substring(position + 1));
            byte[] calculee = sha256(sel, password);
            // Comparaison en temps constant
            return MessageDigest.isEqual(attendue, calculee);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] sha256(byte[] sel, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHME);
            digest.update(sel);
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithme " + ALGORITHME + " indisponible", e);
        }
    }

    private static String toHex(byte[] octets) {
        char[] resultat = new char[octets.length * 2];
        for (int i = 0; i < octets.length; i++) {
            int valeur = octets[i] & 0xFF;
            resultat[i * 2] = HEX[valeur >>> 4];
            resultat[i * 2 + 1] = HEX[valeur & 0x0F];
        }
        return new String(resultat);
    }

    private static byte[] fromHex(String texte) {
        if (texte.length() % 2 != 0) {
            throw new IllegalArgumentException("Longueur hexadécimale invalide");
        }
        byte[] resultat = new byte[texte.length() / 2];
        for (int i = 0; i < resultat.length; i++) {
            int fort = Character.digit(texte.charAt(i * 2), 16);
            int faible = Character.digit(texte.charAt(i * 2 + 1), 16);
            if (fort < 0 || faible < 0) {
                throw new IllegalArgumentException("Caractère hexadécimal invalide");
            }
            resultat[i] = (byte) ((fort << 4) | faible);
        }
        return resultat;
    }
}
