/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_ConnectionPool.java                              ║
 * ║  MODULE : 03-jdbc / 01-jdbc-fundamentals                        ║
 * ║  GRADLE : ./gradlew :03-jdbc:run                                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE LEVEL : 1 (Guided — target: under 20 minutes)         ║
 * ║  PURPOSE        : Configure HikariCP and acquire a connection   ║
 * ║  COVERS         : HikariConfig, pool sizing, DataSource pattern ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  INSTRUCTIONS                                                    ║
 * ║  Complete the 3 TODO items to configure HikariCP correctly.     ║
 * ║  Read 07-connection-pooling.md and 08-hikaricp.md first.        ║
 * ║                                                                  ║
 * ║  BEFORE RUNNING: start PostgreSQL                               ║
 * ║    docker run -d --name springdb                                 ║
 * ║      -e POSTGRES_DB=springdb                                    ║
 * ║      -e POSTGRES_USER=spring                                    ║
 * ║      -e POSTGRES_PASSWORD=spring                                ║
 * ║      -p 5432:5432 postgres:16                                   ║
 * ║                                                                  ║
 * ║  SUCCESS: Console shows:                                         ║
 * ║    Pool initialized with 10 connections                         ║
 * ║    Connected to: PostgreSQL                                      ║
 * ║    Pool closed.                                                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.exercises;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Exercise 01 — Configure HikariCP Connection Pool.
 *
 * <p>HikariCP is the default connection pool used by Spring Boot.
 * Understanding how to configure it manually makes its Spring Boot
 * auto-configuration (via application.properties) much clearer.</p>
 *
 * <p><b>Python SQLAlchemy equivalent:</b>
 * <pre>
 *   engine = create_engine(
 *       "postgresql://spring:spring@localhost:5432/springdb",
 *       pool_size=10,
 *       max_overflow=0
 *   )
 *   with engine.connect() as conn:
 *       print(conn.dialect.name)
 * </pre>
 */
public class Ex01_ConnectionPool {

    public static void main(String[] args) {
        HikariConfig config = new HikariConfig();

        // TODO 1: Set the JDBC connection URL for PostgreSQL.
        // Format: jdbc:postgresql://host:port/database
        // Docker container details: host=localhost, port=5432, database=springdb
        // Hint: see 01-jdbc-architecture.md — "Connection URL Format" section
        config.setJdbcUrl("YOUR_URL_HERE");

        // TODO 2: Set the username and password.
        // Username: spring   Password: spring
        config.setUsername("YOUR_USERNAME");
        config.setPassword("YOUR_PASSWORD");

        // TODO 3: Set the maximum pool size to 10.
        // This caps the number of simultaneous database connections this pool will hold.
        // Hint: check HikariConfig javadoc or 08-hikaricp.md for the method name
        // config.set????(10);

        // --- Do not modify below this line ---
        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            System.out.println("Pool initialized with " + config.getMaximumPoolSize() + " connections");

            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Connected to: " + meta.getDatabaseProductName()
                        + " " + meta.getDatabaseProductVersion());
            }

            System.out.println("Pool closed.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.err.println("Is Docker running? Try: docker ps | grep springdb");
        }
    }
}
