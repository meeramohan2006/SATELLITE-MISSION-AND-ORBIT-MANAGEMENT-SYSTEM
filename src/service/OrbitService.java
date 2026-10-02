package service;

import dao.OrbitDAO;
import dao.SatelliteDAO;
import model.Orbit;
import model.Satellite;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer coordinating Orbit trajectory operations, physics validations,
 * and relational integrity with Satellites.
 */
public class OrbitService {

    private final OrbitDAO orbitDAO;
    private final SatelliteDAO satelliteDAO;

    public OrbitService() {
        this.orbitDAO = new OrbitDAO();
        this.satelliteDAO = new SatelliteDAO();
    }

    public OrbitService(OrbitDAO orbitDAO, SatelliteDAO satelliteDAO) {
        this.orbitDAO = orbitDAO;
        this.satelliteDAO = satelliteDAO;
    }

    public boolean addOrbit(Orbit orbit) throws Exception {
        ValidationUtil.validateOrbit(
                orbit.getOrbitId(),
                orbit.getSatelliteId(),
                orbit.getOrbitType(),
                String.valueOf(orbit.getAltitude()),
                String.valueOf(orbit.getInclination()),
                String.valueOf(orbit.getOrbitalPeriod()),
                String.valueOf(orbit.getEccentricity())
        );

        Orbit existing = orbitDAO.getOrbitById(orbit.getOrbitId());
        if (existing != null) {
            throw new IllegalArgumentException("Orbit with ID '" + orbit.getOrbitId() + "' already exists.");
        }

        Satellite sat = satelliteDAO.getSatelliteById(orbit.getSatelliteId());
        if (sat == null) {
            throw new IllegalArgumentException("Cannot create orbit. Assigned Satellite '" + orbit.getSatelliteId() + "' does not exist.");
        }

        return orbitDAO.addOrbit(orbit);
    }

    public boolean updateOrbit(Orbit orbit) throws Exception {
        ValidationUtil.validateOrbit(
                orbit.getOrbitId(),
                orbit.getSatelliteId(),
                orbit.getOrbitType(),
                String.valueOf(orbit.getAltitude()),
                String.valueOf(orbit.getInclination()),
                String.valueOf(orbit.getOrbitalPeriod()),
                String.valueOf(orbit.getEccentricity())
        );

        Orbit existing = orbitDAO.getOrbitById(orbit.getOrbitId());
        if (existing == null) {
            throw new IllegalArgumentException("Orbit with ID '" + orbit.getOrbitId() + "' not found to update.");
        }

        Satellite sat = satelliteDAO.getSatelliteById(orbit.getSatelliteId());
        if (sat == null) {
            throw new IllegalArgumentException("Assigned Satellite '" + orbit.getSatelliteId() + "' does not exist.");
        }

        return orbitDAO.updateOrbit(orbit);
    }

    public boolean deleteOrbit(String orbitId) throws Exception {
        if (ValidationUtil.isEmpty(orbitId)) {
            throw new IllegalArgumentException("Please specify an Orbit ID to delete.");
        }
        return orbitDAO.deleteOrbit(orbitId);
    }

    public Orbit getOrbitById(String orbitId) throws SQLException {
        if (ValidationUtil.isEmpty(orbitId)) return null;
        return orbitDAO.getOrbitById(orbitId);
    }

    public Orbit getOrbitBySatelliteId(String satelliteId) throws SQLException {
        if (ValidationUtil.isEmpty(satelliteId)) return null;
        return orbitDAO.getOrbitBySatelliteId(satelliteId);
    }

    public List<Orbit> getAllOrbits() throws SQLException {
        return orbitDAO.getAllOrbits();
    }

    public List<Orbit> searchOrbits(String keyword) throws SQLException {
        if (ValidationUtil.isEmpty(keyword)) {
            return getAllOrbits();
        }
        return orbitDAO.searchOrbits(keyword.trim());
    }

    public int getTotalOrbitsCount() {
        try {
            return orbitDAO.getTotalCount();
        } catch (SQLException e) {
            return 0;
        }
    }
}
