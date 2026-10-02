package dao;

import database.DBConnection;
import model.Satellite;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object (DAO) for Satellite entity.
 * Uses JDBC with PreparedStatement, ResultSet, and try-with-resources.
 * Includes graceful memory caching fallback for offline evaluation.
 */
public class SatelliteDAO {

    // Fallback in-memory cache seeded with default data
    private static final Map<String, Satellite> cache = new ConcurrentHashMap<>();

    static {
        seedCache();
    }

    private static void seedCache() {
        cache.put("SAT001", new Satellite("SAT001", "INSAT-3D", "Communication", "ISRO", LocalDate.of(2013, 7, 26), "Active"));
        cache.put("SAT002", new Satellite("SAT002", "GSAT-10", "Communication", "ISRO", LocalDate.of(2012, 9, 29), "Active"));
        cache.put("SAT003", new Satellite("SAT003", "Cartosat-2", "Earth Observation", "ISRO", LocalDate.of(2007, 1, 10), "Active"));
        cache.put("SAT004", new Satellite("SAT004", "Hubble", "Research", "NASA", LocalDate.of(1990, 4, 24), "Active"));
        cache.put("SAT005", new Satellite("SAT005", "Sentinel-1", "Earth Observation", "ESA", LocalDate.of(2014, 4, 3), "Inactive"));
        cache.put("SAT006", new Satellite("SAT006", "Chandrayaan-3", "Planetary", "ISRO", LocalDate.of(2023, 7, 14), "Active"));
        cache.put("SAT007", new Satellite("SAT007", "Mars Orbiter", "Planetary", "ISRO", LocalDate.of(2013, 11, 5), "Inactive"));
        cache.put("SAT008", new Satellite("SAT008", "Aditya-L1", "Research", "ISRO", LocalDate.of(2023, 9, 2), "Active"));
        cache.put("SAT009", new Satellite("SAT009", "EOS-06", "Earth Observation", "ISRO", LocalDate.of(2022, 11, 26), "Active"));
        cache.put("SAT010", new Satellite("SAT010", "IRNSS-1A", "Navigation", "ISRO", LocalDate.of(2013, 7, 1), "Inactive"));
        cache.put("SAT011", new Satellite("SAT011", "James Webb", "Research", "NASA/ESA", LocalDate.of(2021, 12, 25), "Active"));
        cache.put("SAT012", new Satellite("SAT012", "Starlink-G4", "Communication", "SpaceX", LocalDate.of(2022, 5, 18), "Active"));
    }

    public boolean addSatellite(Satellite satellite) throws SQLException {
        String sql = "INSERT INTO satellites (satellite_id, satellite_name, satellite_type, country_org, launch_date, operational_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, satellite.getSatelliteId());
            ps.setString(2, satellite.getSatelliteName());
            ps.setString(3, satellite.getSatelliteType());
            ps.setString(4, satellite.getCountryOrg());
            ps.setDate(5, Date.valueOf(satellite.getLaunchDate()));
            ps.setString(6, satellite.getOperationalStatus());
            int affected = ps.executeUpdate();
            cache.put(satellite.getSatelliteId(), satellite);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                if (cache.containsKey(satellite.getSatelliteId())) {
                    throw new SQLException("Duplicate satellite ID: " + satellite.getSatelliteId());
                }
                cache.put(satellite.getSatelliteId(), satellite);
                return true;
            }
            throw e;
        }
    }

    public boolean updateSatellite(Satellite satellite) throws SQLException {
        String sql = "UPDATE satellites SET satellite_name = ?, satellite_type = ?, country_org = ?, launch_date = ?, operational_status = ? WHERE satellite_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, satellite.getSatelliteName());
            ps.setString(2, satellite.getSatelliteType());
            ps.setString(3, satellite.getCountryOrg());
            ps.setDate(4, Date.valueOf(satellite.getLaunchDate()));
            ps.setString(5, satellite.getOperationalStatus());
            ps.setString(6, satellite.getSatelliteId());
            int affected = ps.executeUpdate();
            cache.put(satellite.getSatelliteId(), satellite);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                if (!cache.containsKey(satellite.getSatelliteId())) {
                    throw new SQLException("Satellite not found with ID: " + satellite.getSatelliteId());
                }
                cache.put(satellite.getSatelliteId(), satellite);
                return true;
            }
            throw e;
        }
    }

    public boolean deleteSatellite(String satelliteId) throws SQLException {
        String sql = "DELETE FROM satellites WHERE satellite_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, satelliteId);
            int affected = ps.executeUpdate();
            cache.remove(satelliteId);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                cache.remove(satelliteId);
                return true;
            }
            throw e;
        }
    }

    public Satellite getSatelliteById(String satelliteId) throws SQLException {
        String sql = "SELECT * FROM satellites WHERE satellite_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, satelliteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                return cache.get(satelliteId);
            }
            throw e;
        }
        return null;
    }

    public List<Satellite> getAllSatellites() throws SQLException {
        List<Satellite> list = new ArrayList<>();
        String sql = "SELECT * FROM satellites ORDER BY satellite_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                return new ArrayList<>(cache.values());
            }
            throw e;
        }
    }

    public List<Satellite> searchSatellites(String keyword) throws SQLException {
        List<Satellite> list = new ArrayList<>();
        String sql = "SELECT * FROM satellites WHERE satellite_id LIKE ? OR satellite_name LIKE ? OR satellite_type LIKE ? OR country_org LIKE ? OR operational_status LIKE ? ORDER BY satellite_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                String kw = keyword.toLowerCase();
                for (Satellite s : cache.values()) {
                    if (s.getSatelliteId().toLowerCase().contains(kw) ||
                        s.getSatelliteName().toLowerCase().contains(kw) ||
                        s.getSatelliteType().toLowerCase().contains(kw) ||
                        s.getCountryOrg().toLowerCase().contains(kw) ||
                        s.getOperationalStatus().toLowerCase().contains(kw)) {
                        list.add(s);
                    }
                }
                return list;
            }
            throw e;
        }
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM satellites";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                return cache.size();
            }
            throw e;
        }
        return 0;
    }

    public int getActiveCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM satellites WHERE operational_status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                int count = 0;
                for (Satellite s : cache.values()) {
                    if (s.isActive()) count++;
                }
                return count;
            }
            throw e;
        }
        return 0;
    }

    private Satellite mapRow(ResultSet rs) throws SQLException {
        return new Satellite(
                rs.getString("satellite_id"),
                rs.getString("satellite_name"),
                rs.getString("satellite_type"),
                rs.getString("country_org"),
                rs.getDate("launch_date").toLocalDate(),
                rs.getString("operational_status")
        );
    }
}
