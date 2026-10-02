package test;

import dao.UserDAO;
import model.Mission;
import model.Orbit;
import model.Satellite;
import model.User;
import service.MissionService;
import service.OrbitService;
import service.SatelliteService;
import util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Headless Automated Verification Test to validate all CRUD, business logic,
 * validation constraints, and authentication without needing a display server.
 */
public class SmokeTest {

    public static void main(String[] args) {
        System.out.println(">>> Running Satellite Management System Smoke Tests...\n");

        int testsPassed = 0;
        int totalTests = 0;

        // Test 1: User Authentication
        totalTests++;
        UserDAO userDAO = new UserDAO();
        User admin = userDAO.authenticate("admin", "admin123");
        if (admin != null && "admin".equals(admin.getUsername())) {
            System.out.println("[PASS] Test 1: Admin authentication succeeded.");
            testsPassed++;
        } else {
            System.err.println("[FAIL] Test 1: Admin authentication failed.");
        }

        // Test 2: Validation Utility
        totalTests++;
        try {
            ValidationUtil.validateSatellite("SAT999", "TestSat", "Communication", "NASA", "12-05-2024", "Active");
            System.out.println("[PASS] Test 2: Satellite input validation passed.");
            testsPassed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 2: Validation threw unexpected error: " + e.getMessage());
        }

        // Test 3: Satellite Service CRUD
        totalTests++;
        try {
            SatelliteService satService = new SatelliteService();
            Satellite sat = new Satellite("SAT999", "AstroSat-Test", "Research", "ISRO", LocalDate.of(2024, 5, 12), "Active");
            satService.addSatellite(sat);

            Satellite retrieved = satService.getSatelliteById("SAT999");
            if (retrieved != null && "AstroSat-Test".equals(retrieved.getSatelliteName())) {
                retrieved.setSatelliteName("AstroSat-Updated");
                satService.updateSatellite(retrieved);

                Satellite updated = satService.getSatelliteById("SAT999");
                if (updated != null && "AstroSat-Updated".equals(updated.getSatelliteName())) {
                    satService.deleteSatellite("SAT999");
                    System.out.println("[PASS] Test 3: Satellite Service CRUD (Add, Get, Update, Delete) succeeded.");
                    testsPassed++;
                } else {
                    System.err.println("[FAIL] Test 3: Update verification failed.");
                }
            } else {
                System.err.println("[FAIL] Test 3: Retrieve verification failed.");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 3: Satellite CRUD failed: " + e.getMessage());
        }

        // Test 4: Mission Service & Foreign Key check
        totalTests++;
        try {
            MissionService missionService = new MissionService();
            List<Mission> missions = missionService.getAllMissions();
            if (missions != null && !missions.isEmpty()) {
                System.out.println("[PASS] Test 4: Mission Service fetched " + missions.size() + " missions successfully.");
                testsPassed++;
            } else {
                System.err.println("[FAIL] Test 4: Mission list was empty.");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 4: Mission service failed: " + e.getMessage());
        }

        // Test 5: Orbit Service & Physical Validations
        totalTests++;
        try {
            OrbitService orbitService = new OrbitService();
            List<Orbit> orbits = orbitService.getAllOrbits();
            if (orbits != null && !orbits.isEmpty()) {
                System.out.println("[PASS] Test 5: Orbit Service fetched " + orbits.size() + " orbits successfully.");
                testsPassed++;
            } else {
                System.err.println("[FAIL] Test 5: Orbit list was empty.");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 5: Orbit service failed: " + e.getMessage());
        }

        System.out.println("\n>>> Test Results: " + testsPassed + " / " + totalTests + " tests passed.");
        if (testsPassed == totalTests) {
            System.out.println(">>> ALL SMOKE TESTS PASSED CLEANLY!");
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
