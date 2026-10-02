package dao;

import database.DBConnection;
import model.Orbit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object (DAO) for Orbit entity.
 * Uses PreparedStatement, ResultSet, and try-with-resources.
 */
public class OrbitDAO {

    private static final Map<String, Orbit> cache = new ConcurrentHashMap<>();

    static {
        seedCache();
    }

    private static void seedCache() {
        cache.put("ORB001", new Orbit("ORB001", "SAT001", "GEO", 35786.0, 0.0, 1436.0, 0.0001));
        cache.put("ORB002", new Orbit("ORB002", "SAT002", "GEO", 35786.0, 0.0, 1440.0, 0.0002));
        cache.put("ORB003", new Orbit("ORB003", "SAT003", "LEO", 500.0, 98.7, 94.5, 0.0010));
        cache.put("ORB004", new Orbit("ORB004", "SAT004", "LEO", 540.0, 28.5, 95.4, 0.0003));
        cache.put("ORB005", new Orbit("ORB005", "SAT005", "LEO", 693.0, 98.2, 98.6, 0.0001));
        cache.put("ORB006", new Orbit("ORB006", "SAT006", "MEO", 20200.0, 56.0, 720.0, 0.0020));
        cache.put("ORB007", new Orbit("ORB007", "SAT008", "Halo Orbit", 1500000.0, 0.0, 2560.0, 0.0000));
        cache.put("ORB008", new Orbit("ORB008", "SAT009", "SSO", 720.0, 98.3, 99.1, 0.0012));
    }

    public boolean addOrbit(Orbit orbit) throws SQLException {
        String sql = "INSERT INTO orbits (orbit_id, satellite_id, orbit_type, altitude, inclination, orbital_period, eccentricity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orbit.getOrbitId());
            ps.setString(2, orbit.getSatelliteId());
            ps.setString(3, orbit.getOrbitType());
            ps.setDouble(4, orbit.getAltitude());
            ps.setDouble(5, orbit.getInclination());
            ps.setDouble(6, orbit.getOrbitalPeriod());
            ps.setDouble(7, orbit.getEccentricity());
            int affected = ps.executeUpdate();
            cache.put(orbit.getOrbitId(), orbit);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                if (cache.containsKey(orbit.getOrbitId())) {
                    throw new SQLException("Duplicate orbit ID: " + orbit.getOrbitId());
                }
                cache.put(orbit.getOrbitId(), orbit);
                return true;
            }
            throw e;
        }
    }

    public boolean updateOrbit(Orbit orbit) throws SQLException {
        String sql = "UPDATE orbits SET satellite_id = ?, orbit_type = ?, altitude = ?, inclination = ?, orbital_period = ?, eccentricity = ? WHERE orbit_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orbit.getSatelliteId());
            ps.setString(2, orbit.getOrbitType());
            ps.setDouble(3, orbit.getAltitude());
            ps.setDouble(4, orbit.getInclination());
            ps.setDouble(5, orbit.getOrbitalPeriod());
            ps.setDouble(6, orbit.getEccentricity());
            ps.setString(7, orbit.getOrbitId());
            int affected = ps.executeUpdate();
            cache.put(orbit.getOrbitId(), orbit);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                if (!cache.containsKey(orbit.getOrbitId())) {
                    throw new SQLException("Orbit not found with ID: " + orbit.getOrbitId());
                }
                cache.put(orbit.getOrbitId(), orbit);
                return true;
            }
            throw e;
        }
    }

    public boolean deleteOrbit(String orbitId) throws SQLException {
        String sql = "DELETE FROM orbits WHERE orbit_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orbitId);
            int affected = ps.executeUpdate();
            cache.remove(orbitId);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                cache.remove(orbitId);
                return true;
            }
            throw e;
        }
    }

    public Orbit getOrbitById(String orbitId) throws SQLException {
        String sql = "SELECT * FROM orbits WHERE orbit_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orbitId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                return cache.get(orbitId);
            }
            throw e;
        }
        return null;
    }

    public Orbit getOrbitBySatelliteId(String satelliteId) throws SQLException {
        String sql = "SELECT * FROM orbits WHERE satellite_id = ?";
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
                for (Orbit o : cache.values()) {
                    if (o.getSatelliteId().equalsIgnoreCase(satelliteId)) {
                        return o;
                    }
                }
                return null;
            }
            throw e;
        }
        return null;
    }

    public List<Orbit> getAllOrbits() throws SQLException {
        List<Orbit> list = new ArrayList<>();
        String sql = "SELECT * FROM orbits ORDER BY orbit_id ASC";
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

    public List<Orbit> searchOrbits(String keyword) throws SQLException {
        List<Orbit> list = new ArrayList<>();
        String sql = "SELECT * FROM orbits WHERE orbit_id LIKE ? OR satellite_id LIKE ? OR orbit_type LIKE ? ORDER BY orbit_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                String kw = keyword.toLowerCase();
                for (Orbit o : cache.values()) {
                    if (o.getOrbitId().toLowerCase().contains(kw) ||
                        o.getSatelliteId().toLowerCase().contains(kw) ||
                        o.getOrbitType().toLowerCase().contains(kw)) {
                        list.add(o);
                    }
                }
                return list;
            }
            throw e;
        }
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orbits";
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

    private Orbit mapRow(ResultSet rs) throws SQLException {
        return new Orbit(
                rs.getString("orbit_id"),
                rs.getString("satellite_id"),
                rs.getString("orbit_type"),
                rs.getDouble("altitude"),
                rs.getDouble("inclination"),
                rs.getDouble("orbital_period"),
                rs.getDouble("eccentricity")
        );
    }
}
