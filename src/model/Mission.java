package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model representing a Space Mission associated with a Satellite.
 * Demonstrates OOP: Encapsulation and Domain Relationship (Mission -> Satellite).
 */
public class Mission {
    private String missionId;
    private String missionName;
    private String missionType;
    private String objective;
    private LocalDate launchDate;
    private String missionStatus;
    private String satelliteId;

    public Mission() {
    }

    public Mission(String missionId, String missionName, String missionType, 
                   String objective, LocalDate launchDate, String missionStatus, String satelliteId) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.missionType = missionType;
        this.objective = objective;
        this.launchDate = launchDate;
        this.missionStatus = missionStatus;
        this.satelliteId = satelliteId;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public String getMissionStatus() {
        return missionStatus;
    }

    public void setMissionStatus(String missionStatus) {
        this.missionStatus = missionStatus;
    }

    public String getSatelliteId() {
        return satelliteId;
    }

    public void setSatelliteId(String satelliteId) {
        this.satelliteId = satelliteId;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(missionStatus);
    }

    @Override
    public String toString() {
        return missionId + " - " + missionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(missionId, mission.missionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId);
    }
}
