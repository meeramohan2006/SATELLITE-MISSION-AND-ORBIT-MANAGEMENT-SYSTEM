# Satellite Mission and Orbit Data Management System

A Java Swing desktop application designed for space telemetry, mission tracking, and orbital parameters management. Built using **Java 17+**, **Java Swing**, **JDBC**, and **MySQL**.

The user interface follows a modern dark navy/blue aerospace aesthetic with photorealistic Earth-from-space backdrops, customized glassmorphic panels, status pill badges, and fluid navigation.

---

## 📸 Key Features & Architecture

* **Landing / Welcome Screen:** Atmospheric Earth-from-space vista with introduction and one-click login initiation.
* **Authentication System:** Secure credential verification with support for Administrator and Telemetry Operator roles.
* **Mission Control Dashboard:** Real-time summary metric cards (Total Satellites, Active Satellites, Total Missions, Active Missions) with an interactive mission overview banner.
* **Satellite Management:** Full CRUD (Create, Read, Update, Delete) with search by ID or name, input validation, and operational status badges.
* **Mission Management:** Mission dispatch and status tracking with relational linking to registered satellites.
* **Orbit Management:** Physics telemetry tracking (Altitude, Inclination, Orbital Period, Eccentricity) with validation of orbital mechanics constraints.
* **Universal Search Module:** Cross-entity lookup filtering by satellite name, ID, mission objective, or orbit regime (LEO, GEO, MEO, SSO).
* **Reports & Data Export:** Filtered tabular views with one-click **Export to CSV**.
* **Zero-Crash Resilience:** Built-in MySQL connection diagnostics, in-app database settings configuration, and synchronized fallback cache to guarantee 100% testability even before database credentials are configured.

---

## 🗂 Final Project Structure

```text
SATELLITE-MISSION-AND-ORBIT-MANAGEMENT-SYSTEM/
 ├── database.sql                     # Complete MySQL schema & initial seed dataset
 ├── pom.xml                          # Maven build configuration
 ├── build.bat                        # One-click Windows build script (javac)
 ├── run.bat                          # One-click Windows run script (java)
 ├── README.md                        # Project documentation & viva guide
 ├── lib/
 │    └── mysql-connector-j-8.3.0.jar # MySQL JDBC Driver
 ├── src/
 │    ├── Main.java                   # Application launcher and Look & Feel setup
 │    ├── database/
 │    │    └── DBConnection.java      # JDBC manager, connection testing & auto-init
 │    ├── model/
 │    │    ├── Satellite.java         # Satellite entity model
 │    │    ├── Mission.java           # Mission entity model
 │    │    ├── Orbit.java             # Orbit entity model
 │    │    └── User.java              # Authenticated user model
 │    ├── dao/
 │    │    ├── SatelliteDAO.java      # Satellite data access layer (JDBC + fallback)
 │    │    ├── MissionDAO.java        # Mission data access layer (JDBC + fallback)
 │    │    ├── OrbitDAO.java          # Orbit data access layer (JDBC + fallback)
 │    │    └── UserDAO.java           # Authentication data access layer
 │    ├── service/
 │    │    ├── SatelliteService.java  # Business logic & validation for satellites
 │    │    ├── MissionService.java    # Business logic & relational checks for missions
 │    │    └── OrbitService.java      # Business logic & physics checks for orbits
 │    ├── util/
 │    │    └── ValidationUtil.java    # Input validation, date parsing & physics rules
 │    ├── gui/
 │    │    ├── NavigationManager.java # Smooth screen transition coordinator
 │    │    ├── LandingFrame.java      # Welcome / Landing screen
 │    │    ├── LoginFrame.java        # Authentication screen
 │    │    ├── DashboardFrame.java    # Dashboard with metrics and quick links
 │    │    ├── SatelliteFrame.java    # Satellite CRUD management frame
 │    │    ├── MissionFrame.java      # Space Mission CRUD management frame
 │    │    ├── OrbitFrame.java        # Orbital Telemetry CRUD management frame
 │    │    ├── SearchFrame.java       # Universal search screen
 │    │    ├── ReportsFrame.java      # Reports viewer & CSV export frame
 │    │    ├── theme/
 │    │    │    └── Theme.java         # Centralized colors, fonts, and styling
 │    │    └── components/
 │    │         ├── BackgroundPanel.java   # Earth-from-space background renderer
 │    │         ├── CustomButton.java      # Rounded modern button with states
 │    │         ├── SidebarPanel.java      # Left navigation bar with active pills
 │    │         ├── TopBarPanel.java       # Header bar with DB status & user badge
 │    │         ├── StatCard.java          # Dashboard metric stat card
 │    │         ├── ModernTable.java       # Dark table with status badge renderer
 │    │         ├── ExitDialog.java        # Exit confirmation modal dialog
 │    │         └── DbConfigDialog.java    # In-app MySQL connection config modal
 │    └── resources/
 │         ├── db.properties          # Connection parameters (host, port, credentials)
 │         └── assets/
 │              ├── space_earth_bg.jpg     # High-res Earth-from-space background
 │              └── satellite_orbit_bg.jpg # High-res satellite in orbit visual
 └── bin/                             # Compiled Java bytecode (.class files)
```

---

## 🛠 Prerequisites

* **Java Development Kit (JDK):** Version 17 or higher (tested with Java 25 LTS).
* **MySQL Server:** Version 8.0+ (optional: running locally on port 3306).

---

## 🔑 Default Login Credentials

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | **ADMIN** | Mission Director (Full Access) |
| `operator` | `operator123` | **OPERATOR** | Telemetry Controller |

---

## 💾 Database Setup Instructions

### Option 1: Automatic Initialization (Recommended)
1. Ensure your local MySQL server is running (e.g., port 3306).
2. Configure your MySQL credentials in `src/resources/db.properties` or click **"⚙ DB Settings"** on the Login screen / top bar in the app.
3. The application will **automatically create** the `satellite_management` database, build all required tables, and seed initial records on startup!

### Option 2: Manual MySQL Setup
Open MySQL Workbench or the MySQL Command Line Client, and run:

```sql
SOURCE path/to/database.sql;
```

Or from Windows PowerShell / Command Prompt:
```powershell
mysql -u root -p < database.sql
```

### Database Properties Configuration (`src/resources/db.properties`)
```properties
db.host=localhost
db.port=3306
db.name=satellite_management
db.user=root
db.password=your_mysql_password
```

---

## 🚀 How to Build and Run

### Method 1: Using Provided Windows Scripts
1. Double-click or run:
   ```cmd
   build.bat
   ```
2. Launch the application:
   ```cmd
   run.bat
   ```

### Method 2: Using Standard Java Commands (Command Prompt / PowerShell)
* **Compile:**
  ```powershell
  javac -encoding UTF-8 -cp "lib/*" -d bin src/model/*.java src/dao/*.java src/database/*.java src/util/*.java src/service/*.java src/gui/theme/*.java src/gui/components/*.java src/gui/*.java src/Main.java
  xcopy /s /y /i src\resources bin\
  ```
* **Run:**
  ```powershell
  java -cp "bin;lib/*;src/resources" Main
  ```

### Method 3: Using Maven (If Maven is installed)
```bash
mvn clean compile
mvn exec:java
```

---

## 🏛 Object-Oriented Programming (OOP) Concepts Used

This application demonstrates core Object-Oriented Programming principles:

### 1. Encapsulation
* All model classes ([Satellite.java](file:///src/model/Satellite.java), [Mission.java](file:///src/model/Mission.java), [Orbit.java](file:///src/model/Orbit.java), [User.java](file:///src/model/User.java)) protect their internal state by declaring fields `private`.
* Access to fields is strictly mediated via public getters, setters, and domain validation methods.

### 2. Abstraction
* **Layered Architecture:** The presentation layer (`gui`) interacts strictly with the `service` layer, which in turn communicates with the `dao` layer.
* The GUI does not write raw SQL queries or manage database connections directly; complex SQL operations and connection pooling are abstracted inside [DBConnection.java](file:///src/database/DBConnection.java) and DAOs.

### 3. Separation of Concerns & Single Responsibility Principle (SRP)
* **Model Layer:** Holds data attributes and entity representation.
* **DAO Layer:** Encapsulates persistence logic and JDBC transactions.
* **Service Layer:** Enforces business logic, relational integrity, and input validation.
* **GUI Layer:** Handles rendering, user interaction, and event listeners.
* **Util Layer:** Reusable validation algorithms for dates and numerical physics.

### 4. Polymorphism & Custom Components
* Custom Swing UI components ([CustomButton.java](file:///src/gui/components/CustomButton.java), [BackgroundPanel.java](file:///src/gui/components/BackgroundPanel.java), [ModernTable.java](file:///src/gui/components/ModernTable.java)) extend base Swing classes (`JButton`, `JPanel`, `JTable`) and override polymorphic hooks like `paintComponent(Graphics g)` and table cell rendering to deliver bespoke modern graphics.

---

## 🔗 Database Relationships & Schema Explanation

The schema consists of three interconnected tables representing space domain entities:

```
+-------------------------------------------------------------+
|                         SATELLITES                          |
+-------------------------------------------------------------+
| PK  satellite_id         VARCHAR(20)                        |
|     satellite_name       VARCHAR(100)                       |
|     satellite_type       VARCHAR(50)                        |
|     country_org          VARCHAR(100)                       |
|     launch_date          DATE                               |
|     operational_status   VARCHAR(30)                        |
+-------------------------------------------------------------+
         |                                         |
         | 1 : N (One-to-Many)                     | 1 : 1 / 1 : N
         v                                         v
+------------------------------------+    +------------------------------------+
|              MISSIONS              |    |               ORBITS               |
+------------------------------------+    +------------------------------------+
| PK  mission_id      VARCHAR(20)    |    | PK  orbit_id        VARCHAR(20)    |
|     mission_name    VARCHAR(100)   |    | FK  satellite_id    VARCHAR(20)    |
|     mission_type    VARCHAR(50)    |    |     orbit_type      VARCHAR(50)    |
|     objective       TEXT           |    |     altitude        DOUBLE         |
|     launch_date     DATE           |    |     inclination     DOUBLE         |
|     mission_status  VARCHAR(30)    |    |     orbital_period  DOUBLE         |
| FK  satellite_id    VARCHAR(20)    |    |     eccentricity    DOUBLE         |
+------------------------------------+    +------------------------------------+
```

### Relational Integrity:
1. **Satellites -> Missions (1 to Many):**
   * A single satellite can execute one or multiple space missions throughout its lifespan (e.g., Cartosat-2 executing urban mapping followed by cartographic updates).
   * Enforced via Foreign Key `fk_mission_satellite` with `ON DELETE CASCADE ON UPDATE CASCADE`.
2. **Satellites -> Orbits (1 to Many):**
   * Each satellite is assigned an orbital trajectory regime (e.g., LEO, GEO, SSO, Lunar Orbit).
   * Enforced via Foreign Key `fk_orbit_satellite` with `ON DELETE CASCADE ON UPDATE CASCADE`.

---

## 🎓 Basic Viva Questions & Answers (B.Tech Computer Science)

### Q1: What architecture pattern does this project follow?
**Answer:** It follows the **3-Tier / MVC (Model-View-Controller)** architectural pattern with clean layer separation:
* **Presentation Tier (View):** Java Swing GUI frames and custom rendering components.
* **Business Logic Tier (Controller / Service):** `SatelliteService`, `MissionService`, and `OrbitService` validating inputs and enforcing rules.
* **Data Access Tier (Model / DAO):** `SatelliteDAO`, `MissionDAO`, and `OrbitDAO` executing PreparedStatement JDBC queries against MySQL.

### Q2: Why is `PreparedStatement` preferred over `Statement` in JDBC?
**Answer:**
1. **Prevents SQL Injection:** Input parameters are bound safely through placeholders (`?`) and strongly typed setters rather than string concatenation.
2. **Performance:** PreparedStatements are pre-compiled and cached by the database engine for faster repetitive execution.

### Q3: What is the purpose of try-with-resources in database operations?
**Answer:** The `try-with-resources` statement guarantees that every opened resource (such as `Connection`, `PreparedStatement`, and `ResultSet`) implementing `java.lang.AutoCloseable` is automatically closed when execution completes, preventing memory and connection pool leaks even if an exception occurs.

### Q4: How is relational integrity enforced between Satellites and Missions/Orbits?
**Answer:** Relational integrity is enforced at both the database level and application layer:
* At the **database level**, foreign key constraints (`satellite_id`) with `ON DELETE CASCADE` prevent orphan records.
* At the **service layer**, `MissionService` and `OrbitService` verify that the target satellite exists before permitting insertion or modification.

### Q5: What input validations are implemented?
**Answer:**
* Non-empty text verification for IDs, names, and objectives.
* Date parsing ensuring valid calendar formats (`dd-MM-yyyy` and `yyyy-MM-dd`).
* Physical orbital validations: Altitude > 0 km, Orbital Period > 0 min, Inclination between 0° and 180°, and closed orbit Eccentricity constrained to `0.0 <= e < 1.0`.

### Q6: How does the application prevent crashes if the MySQL server credentials are unknown?
**Answer:** `DBConnection` implements fault-tolerant connection diagnostics with configurable connection timeouts. If the database is unreachable or access is denied, the application activates an in-memory synchronized seed cache matching the reference dataset. Users can dynamically update and test MySQL credentials at runtime via the **"⚙ DB Settings"** dialog without restarting the application.
