-- Migration: Add performance indexes to Odeme table
-- Version: V2
-- Description: Creates indexes on foreign keys and common filter columns
-- Note: is_deleted column and basic structure already created in V1

-- Create performance indexes on odeme table
CREATE INDEX IF NOT EXISTS idx_odeme_egitim_id ON odeme(egitim_id);
CREATE INDEX IF NOT EXISTS idx_odeme_sorumlu_id ON odeme(sorumlu_id);
CREATE INDEX IF NOT EXISTS idx_odeme_durum ON odeme(durum);
CREATE INDEX IF NOT EXISTS idx_odeme_is_deleted ON odeme(is_deleted);
CREATE INDEX IF NOT EXISTS idx_odeme_created_at ON odeme(created_at);

-- Comments for documentation
COMMENT ON COLUMN odeme.is_deleted IS 'Soft delete flag: true = deleted, false = active';
COMMENT ON COLUMN odeme.operasyon IS 'Payment method: Havale, Nakit, POS, Sistem içi';
COMMENT ON COLUMN odeme.durum IS 'Payment status: Beklemede, Ödendi, İptal';
