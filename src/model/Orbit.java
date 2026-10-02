package model;

import java.util.Objects;

/**
 * Model representing an Orbit configuration for a Satellite.
 * Demonstrates OOP: Encapsulation and Domain Relationship (Orbit -> Satellite).
 */
public class Orbit {
    private String orbitId;
    private String satelliteId;
    private String orbitType;
    private double altitude;       // in kilometers
    private double inclination;    // in degrees (0 to 180)
    private double orbitalPeriod;  // in minutes
    private double eccentricity;   // 0 <= e < 1 for closed orbits

    public Orbit() {
    }

    public Orbit(String orbitId, String satelliteId, String orbitType, 
                 double altitude, double inclination, double orbitalPeriod, double eccentricity) {
        this.orbitId = orbitId;
        this.satelliteId = satelliteId;
        this.orbitType = orbitType;
        this.altitude = altitude;
        this.inclination = inclination;
        this.orbitalPeriod = orbitalPeriod;
        this.eccentricity = eccentricity;
    }

    public String getOrbitId() {
        return orbitId;
    }

    public void setOrbitId(String orbitId) {
        this.orbitId = orbitId;
    }

    public String getSatelliteId() {
        return satelliteId;
    }

    public void setSatelliteId(String satelliteId) {
        this.satelliteId = satelliteId;
    }

    public String getOrbitType() {
        return orbitType;
    }

    public void setOrbitType(String orbitType) {
        this.orbitType = orbitType;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getInclination() {
        return inclination;
    }

    public void setInclination(double inclination) {
        this.inclination = inclination;
    }

    public double getOrbitalPeriod() {
        return orbitalPeriod;
    }

    public void setOrbitalPeriod(double orbitalPeriod) {
        this.orbitalPeriod = orbitalPeriod;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    @Override
    public String toString() {
        return orbitId + " (" + orbitType + ", Alt: " + altitude + " km)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orbit orbit = (Orbit) o;
        return Objects.equals(orbitId, orbit.orbitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orbitId);
    }
}
