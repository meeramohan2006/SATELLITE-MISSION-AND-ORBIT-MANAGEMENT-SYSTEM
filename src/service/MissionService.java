package service;

import dao.MissionDAO;
import dao.SatelliteDAO;
import model.Mission;
import model.Satellite;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer coordinating Space Mission operations and relational checks.
 */
public class MissionService {

    private final MissionDAO missionDAO;
    private final SatelliteDAO satelliteDAO;

    public MissionService() {
        this.missionDAO = new MissionDAO();
        this.satelliteDAO = new SatelliteDAO();
    }

    public MissionService(MissionDAO missionDAO, SatelliteDAO satelliteDAO) {
        this.missionDAO = missionDAO;
        this.satelliteDAO = satelliteDAO;
    }

    public boolean addMission(Mission mission) throws Exception {
        ValidationUtil.validateMission(
                mission.getMissionId(),
                mission.getMissionName(),
                mission.getMissionType(),
                mission.getObjective(),
                mission.getLaunchDate() != null ? mission.getLaunchDate().toString() : null,
                mission.getMissionStatus(),
                mission.getSatelliteId()
        );

        // Check if mission ID already exists
        Mission existing = missionDAO.getMissionById(mission.getMissionId());
        if (existing != null) {
            throw new IllegalArgumentException("Mission with ID '" + mission.getMissionId() + "' already exists.");
        }

        // Verify foreign key: referenced satellite must exist
        Satellite sat = satelliteDAO.getSatelliteById(mission.getSatelliteId());
        if (sat == null) {
            throw new IllegalArgumentException("Cannot create mission. Satellite with ID '" + mission.getSatelliteId() + "' does not exist.");
        }

        return missionDAO.addMission(mission);
    }

    public boolean updateMission(Mission mission) throws Exception {
        ValidationUtil.validateMission(
                mission.getMissionId(),
                mission.getMissionName(),
                mission.getMissionType(),
                mission.getObjective(),
                mission.getLaunchDate() != null ? mission.getLaunchDate().toString() : null,
                mission.getMissionStatus(),
                mission.getSatelliteId()
        );

        Mission existing = missionDAO.getMissionById(mission.getMissionId());
        if (existing == null) {
            throw new IllegalArgumentException("Mission with ID '" + mission.getMissionId() + "' not found to update.");
        }

        Satellite sat = satelliteDAO.getSatelliteById(mission.getSatelliteId());
        if (sat == null) {
            throw new IllegalArgumentException("Satellite with ID '" + mission.getSatelliteId() + "' does not exist.");
        }

        return missionDAO.updateMission(mission);
    }

    public boolean deleteMission(String missionId) throws Exception {
        if (ValidationUtil.isEmpty(missionId)) {
            throw new IllegalArgumentException("Please specify a Mission ID to delete.");
        }
        return missionDAO.deleteMission(missionId);
    }

    public Mission getMissionById(String missionId) throws SQLException {
        if (ValidationUtil.isEmpty(missionId)) return null;
        return missionDAO.getMissionById(missionId);
    }

    public List<Mission> getAllMissions() throws SQLException {
        return missionDAO.getAllMissions();
    }

    public List<Mission> searchMissions(String keyword) throws SQLException {
        if (ValidationUtil.isEmpty(keyword)) {
            return getAllMissions();
        }
        return missionDAO.searchMissions(keyword.trim());
    }

    public List<Mission> getMissionsBySatelliteId(String satelliteId) throws SQLException {
        return missionDAO.getMissionsBySatelliteId(satelliteId);
    }

    public int getTotalMissionsCount() {
        try {
            return missionDAO.getTotalCount();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int getActiveMissionsCount() {
        try {
            return missionDAO.getActiveCount();
        } catch (SQLException e) {
            return 0;
        }
    }
}
