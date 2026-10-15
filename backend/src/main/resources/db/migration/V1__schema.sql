
-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     first_name VARCHAR(100) NOT NULL,
                                     last_name VARCHAR(100) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(50) NOT NULL,
                                     active BOOLEAN NOT NULL DEFAULT TRUE,
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Apartments Table
CREATE TABLE IF NOT EXISTS apartments (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL UNIQUE,
                                          address TEXT NOT NULL,
                                          total_flats INT NOT NULL,
                                          common_area_sq_ft NUMERIC(10, 2),
                                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Households Table
CREATE TABLE IF NOT EXISTS households (
                                          id BIGSERIAL PRIMARY KEY,
                                          flat_number VARCHAR(50) NOT NULL,
                                          floor INT NOT NULL,
                                          area_sq_ft NUMERIC(10, 2) NOT NULL,
                                          occupants_count INT NOT NULL,
                                          apartment_id BIGINT NOT NULL,
                                          resident_id BIGINT UNIQUE,
                                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                          CONSTRAINT fk_household_apartment FOREIGN KEY (apartment_id) REFERENCES apartments (id) ON DELETE CASCADE,
                                          CONSTRAINT fk_household_resident FOREIGN KEY (resident_id) REFERENCES users (id) ON DELETE SET NULL,
                                          CONSTRAINT uk_apartment_flat UNIQUE (apartment_id, flat_number)
);

-- 4. Water Meters Table
CREATE TABLE IF NOT EXISTS water_meters (
                                            id BIGSERIAL PRIMARY KEY,
                                            meter_number VARCHAR(100) NOT NULL UNIQUE,
                                            installation_date DATE NOT NULL,
                                            status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                                            is_virtual BOOLEAN NOT NULL DEFAULT TRUE,
                                            last_reading_value NUMERIC(12, 3) DEFAULT 0.000,
                                            last_reading_date DATE,
                                            household_id BIGINT NOT NULL UNIQUE,
                                            CONSTRAINT fk_meter_household FOREIGN KEY (household_id) REFERENCES households (id) ON DELETE CASCADE
);

-- 5. Water Usage Logs Table
CREATE TABLE IF NOT EXISTS water_usage_logs (
                                                id BIGSERIAL PRIMARY KEY,
                                                meter_id BIGINT NOT NULL,
                                                reading_date DATE NOT NULL,
                                                current_reading NUMERIC(12, 3) NOT NULL,
                                                consumption NUMERIC(12, 3) NOT NULL,
                                                reading_source VARCHAR(50) NOT NULL,
                                                is_simulated BOOLEAN NOT NULL DEFAULT FALSE,
                                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                CONSTRAINT fk_usage_log_meter FOREIGN KEY (meter_id) REFERENCES water_meters (id) ON DELETE CASCADE,
                                                CONSTRAINT uk_meter_reading_date UNIQUE (meter_id, reading_date)
);

-- 6. Tariff Plans Table
CREATE TABLE IF NOT EXISTS tariff_plans (
                                            id BIGSERIAL PRIMARY KEY,
                                            apartment_id BIGINT NOT NULL,
                                            plan_name VARCHAR(100) NOT NULL,
                                            base_rate_per_kl NUMERIC(10, 2) NOT NULL,
                                            tier1_threshold_kl NUMERIC(10, 2) NOT NULL,
                                            tier2_rate_per_kl NUMERIC(10, 2) NOT NULL,
                                            tier2_threshold_kl NUMERIC(10, 2),
                                            tier3_rate_per_kl NUMERIC(10, 2),
                                            effective_from DATE NOT NULL,
                                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                            CONSTRAINT fk_tariff_apartment FOREIGN KEY (apartment_id) REFERENCES apartments (id) ON DELETE CASCADE
);

-- 7. Billing Cycles Table
CREATE TABLE IF NOT EXISTS billing_cycles (
                                              id BIGSERIAL PRIMARY KEY,
                                              apartment_id BIGINT NOT NULL,
                                              cycle_name VARCHAR(100) NOT NULL,
                                              start_date DATE NOT NULL,
                                              end_date DATE NOT NULL,
                                              status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
                                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                              CONSTRAINT fk_cycle_apartment FOREIGN KEY (apartment_id) REFERENCES apartments (id) ON DELETE CASCADE,
                                              CONSTRAINT uk_apartment_cycle UNIQUE (apartment_id, cycle_name)
);

-- Indexes for Query Performance
CREATE INDEX IF NOT EXISTS idx_usage_meter_date ON water_usage_logs (meter_id, reading_date);
CREATE INDEX IF NOT EXISTS idx_household_apt ON households (apartment_id);