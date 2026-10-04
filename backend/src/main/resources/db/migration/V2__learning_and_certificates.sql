-- Learning Modules Table
CREATE TABLE IF NOT EXISTS learning_modules (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(500),
    category VARCHAR(100) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    read_time VARCHAR(50),
    badge_text VARCHAR(50),
    summary TEXT,
    key_points TEXT,
    detailed_content TEXT,
    practical_tip TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Artisan Certificate Requests / Approvals Table
CREATE TABLE IF NOT EXISTS artisan_certificates (
    id BIGSERIAL PRIMARY KEY,
    artisan_id BIGINT,
    artisan_name VARCHAR(255) NOT NULL,
    shg_name VARCHAR(255),
    score INTEGER NOT NULL,
    total_questions INTEGER NOT NULL,
    percentage INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL',
    admin_notes TEXT,
    approved_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_artisan_certificates_status ON artisan_certificates(status);
CREATE INDEX IF NOT EXISTS idx_artisan_certificates_artisan_id ON artisan_certificates(artisan_id);

-- Clean up any hardcoded render URLs in product_images table to relative /api/images/
UPDATE product_images 
SET image_url = REPLACE(image_url, 'https://kalaconnect-eptc.onrender.com/api/images/', '/api/images/')
WHERE image_url LIKE '%kalaconnect-eptc.onrender.com/api/images/%';
