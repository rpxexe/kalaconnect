-- =============================================================================
-- KalaConnect PostgreSQL Initial Schema
-- Designed for Aiven PostgreSQL Cloud Deployment via JDBC
-- =============================================================================

-- 1. Table: users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ARTISAN', 'NGO_ADMIN', 'CUSTOMER')),
    profile_image VARCHAR(500),
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for users
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- 2. Table: artisan_profiles
CREATE TABLE IF NOT EXISTS artisan_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    shg_name VARCHAR(255),
    artisan_name VARCHAR(150) NOT NULL,
    bio TEXT,
    location VARCHAR(255),
    district VARCHAR(100),
    state VARCHAR(100),
    experience INTEGER DEFAULT 0,
    contact_preference VARCHAR(50) DEFAULT 'PHONE',
    profile_image VARCHAR(500),
    approval_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED', 'SUSPENDED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Schema upgrades for existing installations
ALTER TABLE artisan_profiles ADD COLUMN IF NOT EXISTS contact_preference VARCHAR(50) DEFAULT 'PHONE';
ALTER TABLE artisan_profiles ADD COLUMN IF NOT EXISTS profile_image VARCHAR(500);

-- Indexes for artisan_profiles
CREATE INDEX IF NOT EXISTS idx_artisan_profiles_user_id ON artisan_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_artisan_profiles_district ON artisan_profiles(district);
CREATE INDEX IF NOT EXISTS idx_artisan_profiles_state ON artisan_profiles(state);
CREATE INDEX IF NOT EXISTS idx_artisan_profiles_created_at ON artisan_profiles(created_at);

-- 3. Table: skills
CREATE TABLE IF NOT EXISTS skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_skills_name ON skills(name);

-- Pre-seed core handicraft skills
INSERT INTO skills (name, category, description) VALUES
('Pottery', 'Clay & Ceramic', 'Handcrafted earthenware, terracotta and glazed pottery'),
('Embroidery', 'Textile & Needlework', 'Intricate thread work, Zardozi, Kantha, and Chikankari'),
('Weaving', 'Textile & Handloom', 'Traditional handloom weaving and jacquard fabric work'),
('Painting', 'Traditional Art', 'Madhubani, Warli, Pattachitra and miniature painting'),
('Jewellery', 'Ornamental & Beadwork', 'Terracotta, Dokra brass, and handmade beaded jewellery'),
('Woodcraft', 'Carpentry & Carving', 'Hand-carved wooden sculptures, utility and decor items'),
('Bamboo craft', 'Cane & Bamboo', 'Eco-friendly bamboo basketry, furniture and homeware'),
('Textile craft', 'Fabrics & Dyeing', 'Block printing, Bandhani, Batik and natural fabric dyes')
ON CONFLICT (name) DO NOTHING;

-- 4. Table: artisan_skills
CREATE TABLE IF NOT EXISTS artisan_skills (
    id BIGSERIAL PRIMARY KEY,
    artisan_profile_id BIGINT NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
    artisan_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    proficiency_level VARCHAR(50) DEFAULT 'INTERMEDIATE',
    years_of_experience INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_artisan_skill UNIQUE (artisan_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_artisan_skills_artisan_id ON artisan_skills(artisan_id);
CREATE INDEX IF NOT EXISTS idx_artisan_skills_skill_id ON artisan_skills(skill_id);

-- 5. Table: products
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    artisan_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    material VARCHAR(150),
    craft_type VARCHAR(150),
    description TEXT NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity >= 0),
    location VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    views_count INTEGER NOT NULL DEFAULT 0,
    ai_description TEXT,
    ai_caption TEXT,
    ai_hashtags TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Schema upgrades for existing installations
ALTER TABLE products ADD COLUMN IF NOT EXISTS views_count INTEGER NOT NULL DEFAULT 0;

-- Indexes for products
CREATE INDEX IF NOT EXISTS idx_products_artisan_id ON products(artisan_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_products_created_at ON products(created_at);

-- 6. Table: product_images
CREATE TABLE IF NOT EXISTS product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id);

-- 7. Table: enquiries
CREATE TABLE IF NOT EXISTS enquiries (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT REFERENCES products(id) ON DELETE SET NULL,
    customer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    artisan_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(50),
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONTACTED', 'RESPONDED', 'RESOLVED', 'CLOSED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Upgrade columns for existing installations
ALTER TABLE enquiries ADD COLUMN IF NOT EXISTS customer_name VARCHAR(255);
ALTER TABLE enquiries ADD COLUMN IF NOT EXISTS customer_email VARCHAR(255);
ALTER TABLE enquiries ADD COLUMN IF NOT EXISTS customer_phone VARCHAR(50);

-- Indexes for enquiries
CREATE INDEX IF NOT EXISTS idx_enquiries_customer_id ON enquiries(customer_id);
CREATE INDEX IF NOT EXISTS idx_enquiries_artisan_id ON enquiries(artisan_id);
CREATE INDEX IF NOT EXISTS idx_enquiries_product_id ON enquiries(product_id);
CREATE INDEX IF NOT EXISTS idx_enquiries_status ON enquiries(status);
CREATE INDEX IF NOT EXISTS idx_enquiries_created_at ON enquiries(created_at);

-- 8. Table: notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for notifications
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

-- 9. Table: admin_actions
CREATE TABLE IF NOT EXISTS admin_actions (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    action_type VARCHAR(100) NOT NULL,
    target_type VARCHAR(100) NOT NULL,
    target_id BIGINT NOT NULL,
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_admin_actions_admin_id ON admin_actions(admin_id);
CREATE INDEX IF NOT EXISTS idx_admin_actions_target ON admin_actions(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_admin_actions_created_at ON admin_actions(created_at);

-- 10. Table: analytics_events
CREATE TABLE IF NOT EXISTS analytics_events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    event_type VARCHAR(100) NOT NULL,
    event_data TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_analytics_events_user_id ON analytics_events(user_id);
CREATE INDEX IF NOT EXISTS idx_analytics_events_type ON analytics_events(event_type);
CREATE INDEX IF NOT EXISTS idx_analytics_events_created_at ON analytics_events(created_at);

-- 11. Table: uploaded_images (Persistent cloud database storage for uploaded product & artisan images)
CREATE TABLE IF NOT EXISTS uploaded_images (
    id VARCHAR(100) PRIMARY KEY,
    content_type VARCHAR(100) NOT NULL,
    data BYTEA NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_uploaded_images_created_at ON uploaded_images(created_at);
