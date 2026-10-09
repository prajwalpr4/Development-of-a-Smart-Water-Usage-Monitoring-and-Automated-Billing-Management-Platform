package com.codecrew.smartwater.controller;

import com.codecrew.smartwater.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health & System Diagnostics", description = "Endpoints for verifying system health and database connectivity")
public class HealthController {

    private final DataSource dataSource;

    @Autowired
    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    @Operation(summary = "Application & Database Health Check", description = "Reports application status and verifies database connectivity without exposing sensitive configuration")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("application", "Development of a Smart Water Usage Monitoring and Automated Billing Management Platform");
        health.put("team", "Code_Crew");
        health.put("framework", "Spring Boot 3.5.16");
        health.put("stage", "Foundation Setup");
        health.put("timestamp", Instant.now());

        Map<String, Object> dbHealth = new HashMap<>();
        boolean dbConnected = false;
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                dbConnected = true;
                DatabaseMetaData metaData = connection.getMetaData();
                dbHealth.put("status", "CONNECTED");
                dbHealth.put("databaseProduct", metaData.getDatabaseProductName());
            } else {
                dbHealth.put("status", "DISCONNECTED");
                dbHealth.put("details", "Database connection validation timed out");
            }
        } catch (Exception ex) {
            dbHealth.put("status", "DISCONNECTED");
            dbHealth.put("details", "Database is currently unreachable");
        }

        health.put("status", dbConnected ? "UP" : "DEGRADED");
        health.put("database", dbHealth);

        return ResponseEntity.ok(ApiResponse.success("Health check completed", health));
    }

    @GetMapping("/db")
    @Operation(summary = "Database connectivity check", description = "Verifies active database connection and metadata")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkDatabase() {
        Map<String, Object> dbStatus = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            dbStatus.put("status", "CONNECTED");
            dbStatus.put("databaseProductName", metaData.getDatabaseProductName());
            dbStatus.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            dbStatus.put("driverName", metaData.getDriverName());
            dbStatus.put("driverVersion", metaData.getDriverVersion());
            return ResponseEntity.ok(ApiResponse.success("Database connection successful", dbStatus));
        } catch (Exception ex) {
            dbStatus.put("status", "DISCONNECTED");
            dbStatus.put("error", ex.getMessage());
            return ResponseEntity.status(503).body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Database connectivity check failed: " + ex.getMessage())
                    .data(dbStatus)
                    .timestamp(Instant.now())
                    .build());
        }
    }
}
