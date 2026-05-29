-- Cafedirecto Sacramento - Schema para Supabase PostgreSQL
-- Ejecutar en el SQL Editor de Supabase antes de desplegar el backend
-- Alternativa: el backend con DDL_AUTO=update crea las tablas automaticamente

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    whatsapp_number VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS producer_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    active_seller BOOLEAN NOT NULL DEFAULT false,
    brand_name VARCHAR(120) NOT NULL,
    bio VARCHAR(500),
    story VARCHAR(1500),
    location_text VARCHAR(180) NOT NULL,
    gps VARCHAR(80),
    years_experience VARCHAR(80),
    cover_image_url VARCHAR(500),
    payment_details VARCHAR(500),
    mercadopago_access_token VARCHAR(300),
    mercadopago_public_key VARCHAR(300),
    nequi_phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS farms (
    id BIGSERIAL PRIMARY KEY,
    producer_profile_id BIGINT NOT NULL REFERENCES producer_profiles(id),
    name VARCHAR(120) NOT NULL,
    location_text VARCHAR(180) NOT NULL,
    gps VARCHAR(80),
    description VARCHAR(800),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    producer_profile_id BIGINT NOT NULL REFERENCES producer_profiles(id),
    farm_id BIGINT NOT NULL REFERENCES farms(id),
    name VARCHAR(140) NOT NULL,
    variety VARCHAR(80) NOT NULL,
    price_per_kg DECIMAL(12,2) NOT NULL,
    available_kg DECIMAL(12,2) NOT NULL,
    description VARCHAR(1500) NOT NULL,
    main_image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_processes (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    stage VARCHAR(30) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    result_type VARCHAR(280) NOT NULL,
    order_index INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS process_media (
    id BIGSERIAL PRIMARY KEY,
    process_id BIGINT NOT NULL REFERENCES product_processes(id),
    media_type VARCHAR(20) NOT NULL,
    url VARCHAR(500) NOT NULL,
    caption VARCHAR(280),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content VARCHAR(1000) NOT NULL,
    visible BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity_kg DECIMAL(10,2) NOT NULL,
    unit_price_snapshot DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    buyer_user_id BIGINT NOT NULL REFERENCES users(id),
    seller_profile_id BIGINT NOT NULL REFERENCES producer_profiles(id),
    total_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    product_name_snapshot VARCHAR(140) NOT NULL,
    quantity_kg DECIMAL(10,2) NOT NULL,
    unit_price_snapshot DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    provider VARCHAR(20) NOT NULL DEFAULT 'MERCADO_PAGO',
    external_reference VARCHAR(255) NOT NULL,
    provider_payment_id VARCHAR(255),
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment_events (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id),
    event_type VARCHAR(80) NOT NULL,
    raw_payload TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
