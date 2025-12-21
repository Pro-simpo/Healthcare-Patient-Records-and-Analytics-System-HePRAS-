package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IUtilisateurDAO;
import ma.ensa.healthcare.model.Utilisateur;
import ma.ensa.healthcare.model.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UtilisateurDAOImpl implements IUtilisateurDAO {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurDAOImpl.class);

    @Override
    public Utilisateur save(Utilisateur u) {
        String sql = "INSERT INTO UTILISATEUR (USERNAME, PASSWORD, EMAIL, ROLE, ACTIF) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRole().name());
            ps.setBoolean(5, u.isActif());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) u.setId(rs.getLong(1));
        } catch (SQLException e) { logger.error("Erreur save Utilisateur", e); }
        return u;
    }

    @Override
    public Utilisateur findByUsername(String username) {
        String sql = "SELECT * FROM UTILISATEUR WHERE USERNAME = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) { logger.error("Erreur findByUsername", e); }
        return null;
    }

    @Override public Utilisateur findById(Long id) { return null; }
    @Override public void updatePassword(Long id, String newPassword) { /* SQL UPDATE */ }
    @Override public void delete(Long id) { /* SQL DELETE */ }

    private Utilisateur mapResultSetToUser(ResultSet rs) throws SQLException {
        return Utilisateur.builder()
                .id(rs.getLong("ID"))
                .username(rs.getString("USERNAME"))
                .password(rs.getString("PASSWORD"))
                .email(rs.getString("EMAIL"))
                .role(Role.valueOf(rs.getString("ROLE")))
                .actif(rs.getBoolean("ACTIF"))
                .build();
    }
}