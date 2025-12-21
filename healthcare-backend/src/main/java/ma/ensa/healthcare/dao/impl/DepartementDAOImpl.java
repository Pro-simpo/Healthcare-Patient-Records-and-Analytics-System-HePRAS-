package ma.ensa.healthcare.dao.impl;

import ma.ensa.healthcare.config.DatabaseConfig;
import ma.ensa.healthcare.dao.interfaces.IDepartementDAO;
import ma.ensa.healthcare.model.Departement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartementDAOImpl implements IDepartementDAO {
    private static final Logger logger = LoggerFactory.getLogger(DepartementDAOImpl.class);

    @Override
    public Departement save(Departement dept) {
        String sql = "INSERT INTO DEPARTEMENT (NOM, DESCRIPTION) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            
            ps.setString(1, dept.getNom());
            ps.setString(2, dept.getDescription());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) dept.setId(rs.getLong(1));
            }
            logger.info("Département enregistré : {}", dept.getNom());
        } catch (SQLException e) {
            logger.error("Erreur save Departement", e);
        }
        return dept;
    }

    @Override
    public Departement findById(Long id) {
        String sql = "SELECT * FROM DEPARTEMENT WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToDept(rs);
            }
        } catch (SQLException e) {
            logger.error("Erreur findById Departement", e);
        }
        return null;
    }

    @Override
    public List<Departement> findAll() {
        List<Departement> list = new ArrayList<>();
        String sql = "SELECT * FROM DEPARTEMENT";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToDept(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur findAll Departement", e);
        }
        return list;
    }

    @Override
    public void update(Departement dept) {
        String sql = "UPDATE DEPARTEMENT SET NOM = ?, DESCRIPTION = ? WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getNom());
            ps.setString(2, dept.getDescription());
            ps.setLong(3, dept.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur update Departement", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM DEPARTEMENT WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erreur delete Departement", e);
        }
    }

    private Departement mapResultSetToDept(ResultSet rs) throws SQLException {
        return Departement.builder()
                .id(rs.getLong("ID"))
                .nom(rs.getString("NOM"))
                .description(rs.getString("DESCRIPTION"))
                .build();
    }
}