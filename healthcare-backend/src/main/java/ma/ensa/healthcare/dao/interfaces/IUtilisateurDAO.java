package ma.ensa.healthcare.dao.interfaces;

import ma.ensa.healthcare.model.Utilisateur;
import java.util.Optional;

public interface IUtilisateurDAO {
    Utilisateur save(Utilisateur utilisateur);
    Utilisateur findByUsername(String username);
    Utilisateur findById(Long id);
    void updatePassword(Long id, String newPassword);
    void delete(Long id);
}