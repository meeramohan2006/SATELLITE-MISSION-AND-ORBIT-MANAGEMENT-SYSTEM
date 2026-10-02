package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model representing a Satellite entity in the system.
 * Demonstrates OOP: Encapsulation with private fields and public accessors.
 */
public class Satellite {
    private String satelliteId;
    private String satelliteName;
    private String satelliteType;
    private String countryOrg;
    private LocalDate launchDate;
    private String operationalStatus;

    public Satellite() {
    }

    public Satellite(String satelliteId, String satelliteName, String satelliteType, 
                     String countryOrg, LocalDate launchDate, String operationalStatus) {
        this.satelliteId = satelliteId;
        this.satelliteName = satelliteName;
        this.satelliteType = satelliteType;
        this.countryOrg = countryOrg;
        this.launchDate = launchDate;
        this.operationalStatus = operationalStatus;
    }

    public String getSatelliteId() {
        return satelliteId;
    }

    public void setSatelliteId(String satelliteId) {
        this.satelliteId = satelliteId;
    }

    public String getSatelliteName() {
        return satelliteName;
    }

    public void setSatelliteName(String satelliteName) {
        this.satelliteName = satelliteName;
    }

    public String getSatelliteType() {
        return satelliteType;
    }

    public void setSatelliteType(String satelliteType) {
        this.satelliteType = satelliteType;
    }

    public String getCountryOrg() {
        return countryOrg;
    }

    public void setCountryOrg(String countryOrg) {
        this.countryOrg = countryOrg;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(operationalStatus);
    }

    @Override
    public String toString() {
        return satelliteId + " - " + satelliteName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Satellite satellite = (Satellite) o;
        return Objects.equals(satelliteId, satellite.satelliteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(satelliteId);
    }
}
