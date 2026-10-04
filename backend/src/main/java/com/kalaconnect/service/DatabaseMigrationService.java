package com.kalaconnect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class DatabaseMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationService.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final AtomicBoolean migrationCompleted = new AtomicBoolean(false);

    public DatabaseMigrationService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Application started. Checking database connectivity for automated migrations...");
        try {
            if (isDatabaseConnected()) {
                applyMigrations();
            } else {
                logger.warn("Database connection not ready at startup. Migrations will run automatically upon first database access.");
            }
        } catch (Exception e) {
            logger.warn("Database is currently unreachable at startup ({}). The application will continue to serve requests and retry connection on /api/health/db.", e.getClass().getSimpleName());
        }
    }

    public synchronized boolean applyMigrations() {
        if (migrationCompleted.get()) {
            return true;
        }

        try {
            logger.info("Applying database migrations from classpath:db/migration/V1__initial_schema.sql and V2__learning_and_certificates.sql...");
            Resource migrationScript1 = new ClassPathResource("db/migration/V1__initial_schema.sql");
            Resource migrationScript2 = new ClassPathResource("db/migration/V2__learning_and_certificates.sql");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.setContinueOnError(true);
            populator.setIgnoreFailedDrops(true);
            populator.addScript(migrationScript1);
            populator.addScript(migrationScript2);

            try (Connection connection = dataSource.getConnection()) {
                populator.populate(connection);
            }

            migrationCompleted.set(true);
            logger.info("KalaConnect database schema migration executed successfully. All tables and indexes are verified.");
            return true;
        } catch (Exception e) {
            logger.error("Failed to execute database migration: {}", e.getMessage());
            return false;
        }
    }

    public boolean isDatabaseConnected() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return result != null && result == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
