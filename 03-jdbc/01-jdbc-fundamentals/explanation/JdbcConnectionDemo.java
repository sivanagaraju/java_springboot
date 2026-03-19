/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  JDBC CONNECTION DEMO — java.sql.Connection Lifecycle          ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  JDBC Connection Lifecycle:                                    ║
 * ║                                                                ║
 * ║  DriverManager.getConnection(url, user, pass)                  ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  ┌──────────────────┐                                          ║
 * ║  │ TCP Handshake     │  ~5ms                                   ║
 * ║  │ SSL Negotiation   │  ~10ms                                  ║
 * ║  │ Authentication    │  ~5ms                                   ║
 * ║  │ Session Setup     │  ~5ms                                   ║
 * ║  └──────┬───────────┘                                          ║
 * ║         ▼                                                      ║
 * ║  Connection OPEN ──► Execute SQL ──► close()                   ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    conn = psycopg2.connect(dsn)                                ║
 * ║    cursor = conn.cursor()                                      ║
 * ║    cursor.execute(sql)                                         ║
 * ║    conn.close()                                                ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.demo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Demonstrates JDBC connection management patterns.
 *
 * <p>Architecture:
 * <pre>
 *   ┌─────────────┐    ┌──────────────┐    ┌──────────┐
 *   │ Application  │───►│DriverManager │───►│ Database │
 *   │ (this class) │    │(selects drv) │    │(Postgres)│
 *   └─────────────┘    └──────────────┘    └──────────┘
 * </pre>
 *
 * <p>Python equivalent:
 * <pre>
 *   # Python psycopg2
 *   import psycopg2
 *   conn = psycopg2.connect("postgresql://localhost:5432/mydb")
 *   print(conn.info.server_version)
 *   conn.close()
 * </pre>
 */
public class JdbcConnectionDemo {

    // Connection constants (in production, use application.yml)
    private static final String URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    /**
     * Entry point — demonstrates three connection patterns.
     *
     * <pre>
     *   main()
     *    ├──► basicConnection()         — simplest approach
     *    ├──► propertiesConnection()     — configurable properties
     *    └──► connectionMetadata()       — inspect DB info
     * </pre>
     */
    public static void main(String[] args) {
        System.out.println("=== JDBC Connection Demo ===\n");

        basicConnection();
        propertiesConnection();
        connectionMetadata();
    }

    /**
     * Pattern 1: Basic connection with URL + user + password.
     *
     * <pre>
     *   try (Connection conn = DriverManager.getConnection(url, user, pass))
     *        │                     │
     *        │                     └──► ServiceLoader finds PostgreSQL driver
     *        │                          Driver.acceptsURL("jdbc:postgresql://...")
     *        │                          Driver.connect(url, props) → Connection
     *        │
     *        └──► conn.close() called automatically (AutoCloseable)
     * </pre>
     *
     * Python equivalent: {@code with psycopg2.connect(dsn) as conn:}
     */
    private static void basicConnection() {
        System.out.println("--- Pattern 1: Basic Connection ---");

        // try-with-resources guarantees close() even if exception occurs
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected successfully!");
            System.out.println("Auto-commit: " + conn.getAutoCommit());  // true (JDBC default)
            System.out.println("Transaction isolation: " + conn.getTransactionIsolation());
            System.out.println("Catalog (database): " + conn.getCatalog());
            System.out.println("Schema: " + conn.getSchema());
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
        }
        // Connection is automatically closed here
        System.out.println();
    }

    /**
     * Pattern 2: Connection with Properties object.
     *
     * <pre>
     *   Properties props = new Properties()
     *    ├── user = "postgres"
     *    ├── password = "postgres"
     *    ├── ssl = "true"
     *    ├── connectTimeout = "5"        (seconds)
     *    ├── socketTimeout = "30"        (seconds)
     *    └── ApplicationName = "jdbc-demo"  (visible in pg_stat_activity)
     * </pre>
     *
     * Python equivalent:
     * {@code conn = psycopg2.connect(host="localhost", port=5432, ..., connect_timeout=5)}
     */
    private static void propertiesConnection() {
        System.out.println("--- Pattern 2: Properties Connection ---");

        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("connectTimeout", "5");        // 5-second connect timeout
        props.setProperty("socketTimeout", "30");        // 30-second query timeout
        props.setProperty("ApplicationName", "jdbc-demo"); // Visible in pg_stat_activity

        try (Connection conn = DriverManager.getConnection(URL, props)) {
            System.out.println("Connected with properties!");
            System.out.println("Client info: " + conn.getClientInfo("ApplicationName"));
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Pattern 3: Reading database metadata.
     *
     * <pre>
     *   Connection
     *     └──► getMetaData() → DatabaseMetaData
     *           ├── getDatabaseProductName()     → "PostgreSQL"
     *           ├── getDatabaseProductVersion()  → "15.4"
     *           ├── getDriverName()              → "PostgreSQL JDBC Driver"
     *           ├── getURL()                     → "jdbc:postgresql://..."
     *           └── supportsTransactions()       → true
     * </pre>
     */
    private static void connectionMetadata() {
        System.out.println("--- Pattern 3: Connection Metadata ---");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            DatabaseMetaData meta = conn.getMetaData();

            System.out.println("Database: " + meta.getDatabaseProductName()
                    + " " + meta.getDatabaseProductVersion());
            System.out.println("Driver: " + meta.getDriverName()
                    + " " + meta.getDriverVersion());
            System.out.println("JDBC Version: " + meta.getJDBCMajorVersion()
                    + "." + meta.getJDBCMinorVersion());
            System.out.println("URL: " + meta.getURL());
            System.out.println("Supports transactions: " + meta.supportsTransactions());
            System.out.println("Supports savepoints: " + meta.supportsSavepoints());
            System.out.println("Max connections: " + meta.getMaxConnections());
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        System.out.println();
    }
}
