package dao;

import database.DBConnection;
import model.Mission;

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
 * Data Access Object (DAO) for Mission entity.
 * Uses PreparedStatement, ResultSet, and try-with-resources.
 */
public class MissionDAO {

    private static final Map<String, Mission> cache = new ConcurrentHashMap<>();

    static {
        seedCache();
    }

    private static void seedCache() {
        cache.put("MIS001", new Mission("MIS001", "Chandrayaan-3", "Lunar Exploration", "Lunar South Pole exploration and soft landing", LocalDate.of(2023, 7, 14), "Active", "SAT006"));
        cache.put("MIS002", new Mission("MIS002", "Mars Orbiter", "Planetary", "Mars atmosphere and surface exploration", LocalDate.of(2013, 11, 5), "Completed", "SAT007"));
        cache.put("MIS003", new Mission("MIS003", "INSAT-3D Weather", "Communication", "Continuous meteorological observation and weather monitoring", LocalDate.of(2013, 7, 26), "Active", "SAT001"));
        cache.put("MIS004", new Mission("MIS004", "Aditya-L1", "Research", "Space Observation and solar corona study", LocalDate.of(2023, 9, 2), "Active", "SAT008"));
        cache.put("MIS005", new Mission("MIS005", "EOS-06 Oceansat", "Earth Observation", "Ocean surface and global wind vector monitoring", LocalDate.of(2022, 11, 26), "Active", "SAT009"));
        cache.put("MIS006", new Mission("MIS006", "Hubble Deep Space", "Research", "Deep cosmic survey and universe expansion rate", LocalDate.of(1990, 4, 24), "Active", "SAT004"));
        cache.put("MIS007", new Mission("MIS007", "Cartosat Urban Map", "Earth Observation", "High-resolution cartographic and urban infrastructure mapping", LocalDate.of(2007, 1, 10), "Completed", "SAT003"));
        cache.put("MIS008", new Mission("MIS008", "Sentinel Environmental", "Earth Observation", "All-weather radar imaging for land and ocean services", LocalDate.of(2014, 4, 3), "Inactive", "SAT005"));
    }

    public boolean addMission(Mission mission) throws SQLException {
        String sql = "INSERT INTO missions (mission_id, mission_name, mission_type, objective, launch_date, mission_status, satellite_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mission.getMissionId());
            ps.setString(2, mission.getMissionName());
            ps.setString(3, mission.getMissionType());
            ps.setString(4, mission.getObjective());
            ps.setDate(5, Date.valueOf(mission.getLaunchDate()));
            ps.setString(6, mission.getMissionStatus());
            ps.setString(7, mission.getSatelliteId());
            int affected = ps.executeUpdate();
            cache.put(mission.getMissionId(), mission);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                if (cache.containsKey(mission.getMissionId())) {
                    throw new SQLException("Duplicate mission ID: " + mission.getMissionId());
                }
                cache.put(mission.getMissionId(), mission);
                return true;
            }
            throw e;
        }
    }

    public boolean updateMission(Mission mission) throws SQLException {
        String sql = "UPDATE missions SET mission_name = ?, mission_type = ?, objective = ?, launch_date = ?, mission_status = ?, satellite_id = ? WHERE mission_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mission.getMissionName());
            ps.setString(2, mission.getMissionType());
            ps.setString(3, mission.getObjective());
            ps.setDate(4, Date.valueOf(mission.getLaunchDate()));
            ps.setString(5, mission.getMissionStatus());
            ps.setString(6, mission.getSatelliteId());
            ps.setString(7, mission.getMissionId());
            int affected = ps.executeUpdate();
            cache.put(mission.getMissionId(), mission);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                if (!cache.containsKey(mission.getMissionId())) {
                    throw new SQLException("Mission not found with ID: " + mission.getMissionId());
                }
                cache.put(mission.getMissionId(), mission);
                return true;
            }
            throw e;
        }
    }

    public boolean deleteMission(String missionId) throws SQLException {
        String sql = "DELETE FROM missions WHERE mission_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, missionId);
            int affected = ps.executeUpdate();
            cache.remove(missionId);
            return affected > 0;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                cache.remove(missionId);
                return true;
            }
            throw e;
        }
    }

    public Mission getMissionById(String missionId) throws SQLException {
        String sql = "SELECT * FROM missions WHERE mission_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, missionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                return cache.get(missionId);
            }
            throw e;
        }
        return null;
    }

    public List<Mission> getAllMissions() throws SQLException {
        List<Mission> list = new ArrayList<>();
        String sql = "SELECT * FROM missions ORDER BY mission_id ASC";
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

    public List<Mission> searchMissions(String keyword) throws SQLException {
        List<Mission> list = new ArrayList<>();
        String sql = "SELECT * FROM missions WHERE mission_id LIKE ? OR mission_name LIKE ? OR mission_type LIKE ? OR objective LIKE ? OR mission_status LIKE ? OR satellite_id LIKE ? ORDER BY mission_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            for (int i = 1; i <= 6; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                String kw = keyword.toLowerCase();
                for (Mission m : cache.values()) {
                    if (m.getMissionId().toLowerCase().contains(kw) ||
                        m.getMissionName().toLowerCase().contains(kw) ||
                        m.getMissionType().toLowerCase().contains(kw) ||
                        m.getObjective().toLowerCase().contains(kw) ||
                        m.getMissionStatus().toLowerCase().contains(kw) ||
                        m.getSatelliteId().toLowerCase().contains(kw)) {
                        list.add(m);
                    }
                }
                return list;
            }
            throw e;
        }
    }

    public List<Mission> getMissionsBySatelliteId(String satelliteId) throws SQLException {
        List<Mission> list = new ArrayList<>();
        String sql = "SELECT * FROM missions WHERE satellite_id = ? ORDER BY mission_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, satelliteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                for (Mission m : cache.values()) {
                    if (m.getSatelliteId().equalsIgnoreCase(satelliteId)) {
                        list.add(m);
                    }
                }
                return list;
            }
            throw e;
        }
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM missions";
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
        String sql = "SELECT COUNT(*) FROM missions WHERE mission_status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            if (DBConnection.isFallbackMode()) {
                int count = 0;
                for (Mission m : cache.values()) {
                    if (m.isActive()) count++;
                }
                return count;
            }
            throw e;
        }
        return 0;
    }

    private Mission mapRow(ResultSet rs) throws SQLException {
        return new Mission(
                rs.getString("mission_id"),
                rs.getString("mission_name"),
                rs.getString("mission_type"),
                rs.getString("objective"),
                rs.getDate("launch_date").toLocalDate(),
                rs.getString("mission_status"),
                rs.getString("satellite_id")
        );
    }
}
