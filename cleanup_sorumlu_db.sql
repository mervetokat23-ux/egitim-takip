-- Sorumlu tablosundan eski 'unvan' kolonunu kaldırma
-- H2 Database için SQL scripti

-- Eski unvan kolonunu kaldır (eğer varsa)
ALTER TABLE sorumlu DROP COLUMN IF EXISTS unvan;

-- Kontrol: Güncel tablo yapısını göster
SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'SORUMLU';

-- Kontrol: Yeni unvanlar tablosunu göster
SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'SORUMLU_UNVANLAR';


