package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class providing validation for business rules,
 * ID formats, dates, and orbital mechanics parameters.
 */
public class ValidationUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidId(String id, String prefix) {
        if (isEmpty(id)) return false;
        String clean = id.trim().toUpperCase();
        return clean.matches("^[A-Z0-9_-]{2,20}$");
    }

    public static LocalDate parseDate(String dateStr) throws IllegalArgumentException {
        if (isEmpty(dateStr)) {
            throw new IllegalArgumentException("Launch date cannot be empty.");
        }
        String clean = dateStr.trim();
        // Try dd-MM-yyyy first
        try {
            return LocalDate.parse(clean, DISPLAY_FORMATTER);
        } catch (DateTimeParseException ignored) {}

        // Try yyyy-MM-dd
        try {
            return LocalDate.parse(clean, ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use dd-MM-yyyy (e.g., 14-07-2023) or yyyy-MM-dd.");
        }
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DISPLAY_FORMATTER);
    }

    public static double parseDouble(String value, String fieldName) throws IllegalArgumentException {
        if (isEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid numeric value.");
        }
    }

    public static void validateSatellite(String id, String name, String type, String country, String dateStr, String status) {
        if (isEmpty(id)) throw new IllegalArgumentException("Satellite ID is required.");
        if (isEmpty(name)) throw new IllegalArgumentException("Satellite Name is required.");
        if (isEmpty(type) || type.equalsIgnoreCase("Select Type")) throw new IllegalArgumentException("Please select a Satellite Type.");
        if (isEmpty(country)) throw new IllegalArgumentException("Country / Organization is required.");
        if (isEmpty(dateStr)) throw new IllegalArgumentException("Launch Date is required.");
        if (isEmpty(status) || status.equalsIgnoreCase("Select Status")) throw new IllegalArgumentException("Please select an Operational Status.");
        parseDate(dateStr); // validates date structure
    }

    public static void validateMission(String id, String name, String type, String objective, String dateStr, String status, String satId) {
        if (isEmpty(id)) throw new IllegalArgumentException("Mission ID is required.");
        if (isEmpty(name)) throw new IllegalArgumentException("Mission Name is required.");
        if (isEmpty(type) || type.equalsIgnoreCase("Select Type")) throw new IllegalArgumentException("Please select a Mission Type.");
        if (isEmpty(objective)) throw new IllegalArgumentException("Mission Objective is required.");
        if (isEmpty(dateStr)) throw new IllegalArgumentException("Launch Date is required.");
        if (isEmpty(status) || status.equalsIgnoreCase("Select Status")) throw new IllegalArgumentException("Please select a Mission Status.");
        if (isEmpty(satId) || satId.equalsIgnoreCase("Select Satellite")) throw new IllegalArgumentException("Please select an assigned Satellite.");
        parseDate(dateStr);
    }

    public static void validateOrbit(String orbitId, String satId, String orbitType, String altitudeStr, 
                                     String inclinationStr, String periodStr, String eccentricityStr) {
        if (isEmpty(orbitId)) throw new IllegalArgumentException("Orbit ID is required.");
        if (isEmpty(satId) || satId.equalsIgnoreCase("Select Satellite")) throw new IllegalArgumentException("Please select an assigned Satellite.");
        if (isEmpty(orbitType) || orbitType.equalsIgnoreCase("Select Type")) throw new IllegalArgumentException("Please select an Orbit Type.");

        double altitude = parseDouble(altitudeStr, "Altitude");
        if (altitude <= 0) {
            throw new IllegalArgumentException("Altitude must be greater than 0 km.");
        }

        double inclination = parseDouble(inclinationStr, "Inclination");
        if (inclination < 0 || inclination > 180) {
            throw new IllegalArgumentException("Inclination must be between 0° and 180°.");
        }

        double period = parseDouble(periodStr, "Orbital Period");
        if (period <= 0) {
            throw new IllegalArgumentException("Orbital Period must be greater than 0 minutes.");
        }

        double eccentricity = parseDouble(eccentricityStr, "Eccentricity");
        if (eccentricity < 0 || eccentricity >= 1.0) {
            throw new IllegalArgumentException("Eccentricity for a closed orbit must be between 0.0 and 0.9999.");
        }
    }
}
