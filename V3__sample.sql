
INSERT INTO apartments (id, name, code, address, city, total_units)
VALUES (1, 'Green Valley Heights', 'GVH-01', 'Plot 42, Outer Ring Road', 'Bengaluru', 4);

INSERT INTO users (id, apartment_id, email, password_hash, full_name, role)
VALUES
    (1, 1, 'admin@codecrew.org', '$2a$10$1Y8c.mQ0Jj9v6k8W3s8f..vO3e2g6x4i5u7w8y9z0a1b2c3d4e5f6', 'Rajesh Sharma', 'ROLE_ADMIN'),
    (2, 1, 'john.doe@gmail.com', '$2a$10$1Y8c.mQ0Jj9v6k8W3s8f..vO3e2g6x4i5u7w8y9z0a1b2c3d4e5f6', 'John Doe', 'ROLE_RESIDENT'),
    (3, 1, 'jane.smith@gmail.com', '$2a$10$1Y8c.mQ0Jj9v6k8W3s8f..vO3e2g6x4i5u7w8y9z0a1b2c3d4e5f6', 'Jane Smith', 'ROLE_RESIDENT');

INSERT INTO households (id, apartment_id, unit_number, floor_area_sqft, occupant_count, primary_resident_id, is_metered)
VALUES
    (1, 1, 'A-101', 1450.00, 4, 2, TRUE),
    (2, 1, 'A-102', 1200.00, 2, 3, TRUE);

INSERT INTO water_meters (id, apartment_id, household_id, meter_serial_number, status, installation_date)
VALUES
    (1, 1, 1, 'WM-GVH-A101', 'ACTIVE', '2026-01-01'),
    (2, 1, 2, 'WM-GVH-A102', 'ACTIVE', '2026-01-01');

INSERT INTO water_usage_logs (meter_id, household_id, reading_timestamp, cumulative_reading_liters, consumption_liters, reading_source)
VALUES
    (1, 1, '2026-02-01 08:00:00+00', 10450.00, 450.00, 'MANUAL'),
    (1, 1, '2026-02-02 08:00:00+00', 10910.00, 460.00, 'MANUAL');

SELECT setval('apartments_id_seq', (SELECT MAX(id) FROM apartments));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('households_id_seq', (SELECT MAX(id) FROM households));
SELECT setval('water_meters_id_seq', (SELECT MAX(id) FROM water_meters));
