
-- 1. Apartments
CREATE TABLE apartments (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(150) NOT NULL,
                            code VARCHAR(50) NOT NULL UNIQUE,
                            address VARCHAR(255) NOT NULL,
                            city VARCHAR(100) NOT NULL,
                            total_units INT NOT NULL DEFAULT 0,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       apartment_id BIGINT REFERENCES apartments(id) ON DELETE SET NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(120) NOT NULL,
                       phone_number VARCHAR(30),
                       role VARCHAR(30) NOT NULL CHECK (role IN ('ROLE_ADMIN', 'ROLE_RESIDENT')),
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Households
CREATE TABLE households (
                            id BIGSERIAL PRIMARY KEY,
                            apartment_id BIGINT NOT NULL REFERENCES apartments(id) ON DELETE CASCADE,
                            unit_number VARCHAR(50) NOT NULL,
                            floor_area_sqft NUMERIC(10,2) NOT NULL CHECK (floor_area_sqft > 0),
                            occupant_count INT NOT NULL DEFAULT 1 CHECK (occupant_count > 0),
                            primary_resident_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                            is_metered BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT uk_apartment_unit UNIQUE (apartment_id, unit_number)
);

-- 4. Water Meters
CREATE TABLE water_meters (
                              id BIGSERIAL PRIMARY KEY,
                              apartment_id BIGINT NOT NULL REFERENCES apartments(id) ON DELETE CASCADE,
                              household_id BIGINT REFERENCES households(id) ON DELETE SET NULL,
                              meter_serial_number VARCHAR(100) NOT NULL UNIQUE,
                              status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'FAULTY')),
                              installation_date DATE NOT NULL,
                              last_reading_timestamp TIMESTAMP WITH TIME ZONE,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Water Usage Logs
CREATE TABLE water_usage_logs (
                                  id BIGSERIAL PRIMARY KEY,
                                  meter_id BIGINT NOT NULL REFERENCES water_meters(id) ON DELETE CASCADE,
                                  household_id BIGINT NOT NULL REFERENCES households(id) ON DELETE CASCADE,
                                  reading_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
                                  cumulative_reading_liters NUMERIC(14,2) NOT NULL CHECK (cumulative_reading_liters >= 0),
                                  consumption_liters NUMERIC(12,2) NOT NULL CHECK (consumption_liters >= 0),
                                  reading_source VARCHAR(30) NOT NULL DEFAULT 'MANUAL' CHECK (reading_source IN ('MANUAL', 'CSV_IMPORT')),
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT uk_meter_timestamp UNIQUE (meter_id, reading_timestamp)
);

-- 6. Billing Cycles
CREATE TABLE billing_cycles (
                                id BIGSERIAL PRIMARY KEY,
                                apartment_id BIGINT NOT NULL REFERENCES apartments(id) ON DELETE CASCADE,
                                cycle_name VARCHAR(100) NOT NULL,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                due_date DATE NOT NULL,
                                status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'CALCULATING', 'FINALIZED', 'CLOSED')),
                                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 7. Tariff Plans
CREATE TABLE tariff_plans (
                              id BIGSERIAL PRIMARY KEY,
                              apartment_id BIGINT NOT NULL REFERENCES apartments(id) ON DELETE CASCADE,
                              plan_name VARCHAR(120) NOT NULL,
                              effective_from DATE NOT NULL,
                              is_active BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. Tariff Tiers
CREATE TABLE tariff_tiers (
                              id BIGSERIAL PRIMARY KEY,
                              tariff_plan_id BIGINT NOT NULL REFERENCES tariff_plans(id) ON DELETE CASCADE,
                              tier_order INT NOT NULL,
                              tier_name VARCHAR(80) NOT NULL,
                              min_volume_kl NUMERIC(10,2) NOT NULL DEFAULT 0.00,
                              max_volume_kl NUMERIC(10,2),
                              rate_per_kl NUMERIC(10,2) NOT NULL CHECK (rate_per_kl >= 0),
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. Bulk Water Purchases
CREATE TABLE bulk_water_purchases (
                                      id BIGSERIAL PRIMARY KEY,
                                      apartment_id BIGINT NOT NULL REFERENCES apartments(id) ON DELETE CASCADE,
                                      billing_cycle_id BIGINT NOT NULL REFERENCES billing_cycles(id) ON DELETE CASCADE,
                                      recorded_by_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                      vendor_name VARCHAR(150) NOT NULL,
                                      purchase_date DATE NOT NULL,
                                      volume_kl NUMERIC(12,2) NOT NULL CHECK (volume_kl > 0),
                                      total_cost NUMERIC(12,2) NOT NULL CHECK (total_cost >= 0),
                                      invoice_reference VARCHAR(100),
                                      created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 10. Bills
CREATE TABLE bills (
                       id BIGSERIAL PRIMARY KEY,
                       bill_number VARCHAR(60) NOT NULL UNIQUE,
                       apartment_id BIGINT NOT NULL REFERENCES apartments(id) ON DELETE CASCADE,
                       billing_cycle_id BIGINT NOT NULL REFERENCES billing_cycles(id) ON DELETE CASCADE,
                       household_id BIGINT NOT NULL REFERENCES households(id) ON DELETE CASCADE,
                       total_consumption_kl NUMERIC(12,2) NOT NULL DEFAULT 0.00,
                       consumption_charge NUMERIC(12,2) NOT NULL DEFAULT 0.00,
                       shared_cost_allocation NUMERIC(12,2) NOT NULL DEFAULT 0.00,
                       flat_area_charge NUMERIC(12,2) NOT NULL DEFAULT 0.00,
                       adjustments NUMERIC(12,2) NOT NULL DEFAULT 0.00,
                       total_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
                       payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID' CHECK (payment_status IN ('UNPAID', 'PAID', 'OVERDUE')),
                       pdf_file_path VARCHAR(255),
                       issued_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 11. Invoice Line Items
CREATE TABLE invoice_line_items (
                                    id BIGSERIAL PRIMARY KEY,
                                    bill_id BIGINT NOT NULL REFERENCES bills(id) ON DELETE CASCADE,
                                    category VARCHAR(40) NOT NULL,
                                    description VARCHAR(255) NOT NULL,
                                    quantity NUMERIC(10,2) NOT NULL DEFAULT 1.00,
                                    unit_rate NUMERIC(10,2) NOT NULL DEFAULT 0.00,
                                    amount NUMERIC(12,2) NOT NULL
);

-- 12. Alerts
CREATE TABLE alerts (
                        id BIGSERIAL PRIMARY KEY,
                        apartment_id BIGINT NOT NULL REFERENCES apartments(id) ON DELETE CASCADE,
                        household_id BIGINT NOT NULL REFERENCES households(id) ON DELETE CASCADE,
                        meter_id BIGINT REFERENCES water_meters(id) ON DELETE SET NULL,
                        alert_type VARCHAR(50) NOT NULL,
                        severity VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
                        observed_value NUMERIC(12,2) NOT NULL,
                        baseline_mean NUMERIC(12,2),
                        baseline_std_dev NUMERIC(12,2),
                        message TEXT NOT NULL,
                        is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
                        created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 13. Notifications
CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               alert_id BIGINT REFERENCES alerts(id) ON DELETE SET NULL,
                               bill_id BIGINT REFERENCES bills(id) ON DELETE SET NULL,
                               channel VARCHAR(30) NOT NULL DEFAULT 'IN_APP',
                               recipient VARCHAR(150) NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               message TEXT NOT NULL,
                               status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);