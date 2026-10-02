package service;

import dao.SatelliteDAO;
import model.Satellite;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer coordinating Satellite operations, business validation, and DAO calls.
 * Demonstrates OOP: Separation of Concerns and Abstraction.
 */
public class SatelliteService {

    private final SatelliteDAO satelliteDAO;

    public SatelliteService() {
        this.satelliteDAO = new SatelliteDAO();
    }

    public SatelliteService(SatelliteDAO satelliteDAO) {
        this.satelliteDAO = satelliteDAO;
    }

    public boolean addSatellite(Satellite satellite) throws Exception {
        ValidationUtil.validateSatellite(
                satellite.getSatelliteId(),
                satellite.getSatelliteName(),
                satellite.getSatelliteType(),
                satellite.getCountryOrg(),
                satellite.getLaunchDate() != null ? satellite.getLaunchDate().toString() : null,
                satellite.getOperationalStatus()
        );

        Satellite existing = satelliteDAO.getSatelliteById(satellite.getSatelliteId());
        if (existing != null) {
            throw new IllegalArgumentException("Satellite with ID '" + satellite.getSatelliteId() + "' already exists.");
        }

        return satelliteDAO.addSatellite(satellite);
    }

    public boolean updateSatellite(Satellite satellite) throws Exception {
        ValidationUtil.validateSatellite(
                satellite.getSatelliteId(),
                satellite.getSatelliteName(),
                satellite.getSatelliteType(),
                satellite.getCountryOrg(),
                satellite.getLaunchDate() != null ? satellite.getLaunchDate().toString() : null,
                satellite.getOperationalStatus()
        );

        Satellite existing = satelliteDAO.getSatelliteById(satellite.getSatelliteId());
        if (existing == null) {
            throw new IllegalArgumentException("Satellite with ID '" + satellite.getSatelliteId() + "' not found to update.");
        }

        return satelliteDAO.updateSatellite(satellite);
    }

    public boolean deleteSatellite(String satelliteId) throws Exception {
        if (ValidationUtil.isEmpty(satelliteId)) {
            throw new IllegalArgumentException("Please specify a Satellite ID to delete.");
        }
        return satelliteDAO.deleteSatellite(satelliteId);
    }

    public Satellite getSatelliteById(String satelliteId) throws SQLException {
        if (ValidationUtil.isEmpty(satelliteId)) return null;
        return satelliteDAO.getSatelliteById(satelliteId);
    }

    public List<Satellite> getAllSatellites() throws SQLException {
        return satelliteDAO.getAllSatellites();
    }

    public List<Satellite> searchSatellites(String keyword) throws SQLException {
        if (ValidationUtil.isEmpty(keyword)) {
            return getAllSatellites();
        }
        return satelliteDAO.searchSatellites(keyword.trim());
    }

    public int getTotalSatellitesCount() {
        try {
            return satelliteDAO.getTotalCount();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int getActiveSatellitesCount() {
        try {
            return satelliteDAO.getActiveCount();
        } catch (SQLException e) {
            return 0;
        }
    }
}
