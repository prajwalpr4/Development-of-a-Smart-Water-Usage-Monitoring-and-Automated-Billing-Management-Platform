CREATE INDEX idx_usage_household_time ON water_usage_logs (household_id, reading_timestamp DESC);
CREATE INDEX idx_usage_meter_time ON water_usage_logs (meter_id, reading_timestamp DESC);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_households_apartment ON households (apartment_id);
CREATE INDEX idx_meters_household ON water_meters (household_id);