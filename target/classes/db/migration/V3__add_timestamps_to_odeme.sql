-- Migration: Ensure timestamp fields exist on odeme table
-- Version: V3
-- Description: Adds created_at and updated_at if not present (idempotent)
-- Note: These columns are already defined in V1, this migration ensures consistency

-- Add columns if they don't exist (H2 syntax)
ALTER TABLE odeme ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE odeme ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Set default values for any null records
UPDATE odeme SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE odeme SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- Note: idx_odeme_created_at already created in V2
