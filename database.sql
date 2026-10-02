-- ====================================================================
-- Satellite Mission and Orbit Data Management System
-- Database Setup Script (MySQL)
-- Database Name: satellite_management
-- ====================================================================

CREATE DATABASE IF NOT EXISTS satellite_management;
USE satellite_management;

-- Drop existing tables to ensure clean initialization
DROP TABLE IF EXISTS orbits;
DROP TABLE IF EXISTS missions;
DROP TABLE IF EXISTS satellites;
DROP TABLE IF EXISTS users;

-- --------------------------------------------------------------------
-- Table: users (Application Authentication)
-- --------------------------------------------------------------------
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'OPERATOR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default Demo Accounts
INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'Mission Director', 'ADMIN'),
('operator', 'operator123', 'Telemetry Controller', 'OPERATOR');

-- --------------------------------------------------------------------
-- Table: satellites
-- Stores primary satellite registry, telemetry identity, and state
-- --------------------------------------------------------------------
CREATE TABLE satellites (
    satellite_id VARCHAR(20) PRIMARY KEY,
    satellite_name VARCHAR(100) NOT NULL,
    satellite_type VARCHAR(50) NOT NULL,
    country_org VARCHAR(100) NOT NULL,
    launch_date DATE NOT NULL,
    operational_status VARCHAR(30) NOT NULL
);

-- Seed Satellites (Matches visual reference UI)
INSERT INTO satellites (satellite_id, satellite_name, satellite_type, country_org, launch_date, operational_status) VALUES
('SAT001', 'INSAT-3D', 'Communication', 'ISRO', '2013-07-26', 'Active'),
('SAT002', 'GSAT-10', 'Communication', 'ISRO', '2012-09-29', 'Active'),
('SAT003', 'Cartosat-2', 'Earth Observation', 'ISRO', '2007-01-10', 'Active'),
('SAT004', 'Hubble', 'Research', 'NASA', '1990-04-24', 'Active'),
('SAT005', 'Sentinel-1', 'Earth Observation', 'ESA', '2014-04-03', 'Inactive'),
('SAT006', 'Chandrayaan-3', 'Planetary', 'ISRO', '2023-07-14', 'Active'),
('SAT007', 'Mars Orbiter', 'Planetary', 'ISRO', '2013-11-05', 'Inactive'),
('SAT008', 'Aditya-L1', 'Research', 'ISRO', '2023-09-02', 'Active'),
('SAT009', 'EOS-06', 'Earth Observation', 'ISRO', '2022-11-26', 'Active'),
('SAT010', 'IRNSS-1A', 'Navigation', 'ISRO', '2013-07-01', 'Inactive'),
('SAT011', 'James Webb', 'Research', 'NASA/ESA', '2021-12-25', 'Active'),
('SAT012', 'Starlink-G4', 'Communication', 'SpaceX', '2022-05-18', 'Active');

-- --------------------------------------------------------------------
-- Table: missions
-- Stores missions assigned to satellites
-- Foreign Key: satellite_id references satellites(satellite_id)
-- --------------------------------------------------------------------
CREATE TABLE missions (
    mission_id VARCHAR(20) PRIMARY KEY,
    mission_name VARCHAR(100) NOT NULL,
    mission_type VARCHAR(50) NOT NULL,
    objective TEXT NOT NULL,
    launch_date DATE NOT NULL,
    mission_status VARCHAR(30) NOT NULL,
    satellite_id VARCHAR(20) NOT NULL,
    CONSTRAINT fk_mission_satellite 
        FOREIGN KEY (satellite_id) REFERENCES satellites(satellite_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Seed Missions (Matches visual reference UI)
INSERT INTO missions (mission_id, mission_name, mission_type, objective, launch_date, mission_status, satellite_id) VALUES
('MIS001', 'Chandrayaan-3', 'Lunar Exploration', 'Lunar South Pole exploration and soft landing', '2023-07-14', 'Active', 'SAT006'),
('MIS002', 'Mars Orbiter', 'Planetary', 'Mars atmosphere and surface exploration', '2013-11-05', 'Completed', 'SAT007'),
('MIS003', 'INSAT-3D Weather', 'Communication', 'Continuous meteorological observation and weather monitoring', '2013-07-26', 'Active', 'SAT001'),
('MIS004', 'Aditya-L1', 'Research', 'Space Observation and solar corona study', '2023-09-02', 'Active', 'SAT008'),
('MIS005', 'EOS-06 Oceansat', 'Earth Observation', 'Ocean surface and global wind vector monitoring', '2022-11-26', 'Active', 'SAT009'),
('MIS006', 'Hubble Deep Space', 'Research', 'Deep cosmic survey and universe expansion rate', '1990-04-24', 'Active', 'SAT004'),
('MIS007', 'Cartosat Urban Map', 'Earth Observation', 'High-resolution cartographic and urban infrastructure mapping', '2007-01-10', 'Completed', 'SAT003'),
('MIS008', 'Sentinel Environmental', 'Earth Observation', 'All-weather radar imaging for land and ocean services', '2014-04-03', 'Inactive', 'SAT005');

-- --------------------------------------------------------------------
-- Table: orbits
-- Stores orbital mechanics and trajectory telemetry
-- Foreign Key: satellite_id references satellites(satellite_id)
-- --------------------------------------------------------------------
CREATE TABLE orbits (
    orbit_id VARCHAR(20) PRIMARY KEY,
    satellite_id VARCHAR(20) NOT NULL,
    orbit_type VARCHAR(50) NOT NULL,
    altitude DOUBLE NOT NULL,
    inclination DOUBLE NOT NULL,
    orbital_period DOUBLE NOT NULL,
    eccentricity DOUBLE NOT NULL,
    CONSTRAINT fk_orbit_satellite 
        FOREIGN KEY (satellite_id) REFERENCES satellites(satellite_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Seed Orbits (Matches visual reference UI)
INSERT INTO orbits (orbit_id, satellite_id, orbit_type, altitude, inclination, orbital_period, eccentricity) VALUES
('ORB001', 'SAT001', 'GEO', 35786.0, 0.0, 1436.0, 0.0001),
('ORB002', 'SAT002', 'GEO', 35786.0, 0.0, 1440.0, 0.0002),
('ORB003', 'SAT003', 'LEO', 500.0, 98.7, 94.5, 0.0010),
('ORB004', 'SAT004', 'LEO', 540.0, 28.5, 95.4, 0.0003),
('ORB005', 'SAT005', 'LEO', 693.0, 98.2, 98.6, 0.0001),
('ORB006', 'SAT006', 'MEO', 20200.0, 56.0, 720.0, 0.0020),
('ORB007', 'SAT008', 'Halo Orbit (L1)', 1500000.0, 0.0, 2560.0, 0.0000),
('ORB008', 'SAT009', 'SSO', 720.0, 98.3, 99.1, 0.0012);

-- Verification Query
SELECT 'Database setup completed successfully!' AS Status;
SELECT COUNT(*) AS total_satellites FROM satellites;
SELECT COUNT(*) AS total_missions FROM missions;
SELECT COUNT(*) AS total_orbits FROM orbits;
