BEGIN;

ALTER TABLE products
ADD COLUMN IF NOT EXISTS farm_id BIGINT;

INSERT INTO farms (
    producer_profile_id,
    name,
    location_text,
    gps,
    description,
    active,
    created_at,
    updated_at
)
SELECT
    pp.id,
    COALESCE(NULLIF(pp.farm_name, ''), 'Finca principal'),
    pp.location_text,
    pp.gps,
    'Finca creada automaticamente durante la migracion a multiples fincas.',
    TRUE,
    NOW(),
    NOW()
FROM producer_profiles pp
WHERE NOT EXISTS (
    SELECT 1
    FROM farms f
    WHERE f.producer_profile_id = pp.id
);

WITH first_farm AS (
    SELECT DISTINCT ON (f.producer_profile_id)
        f.producer_profile_id,
        f.id
    FROM farms f
    ORDER BY f.producer_profile_id, f.created_at ASC, f.id ASC
)
UPDATE products p
SET farm_id = ff.id
FROM first_farm ff
WHERE p.producer_profile_id = ff.producer_profile_id
  AND p.farm_id IS NULL;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM products
        WHERE farm_id IS NULL
    ) THEN
        RAISE EXCEPTION 'Quedaron productos sin farm_id asignado. Revisa los datos antes de continuar.';
    END IF;
END $$;

ALTER TABLE products
ALTER COLUMN farm_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_products_farm'
    ) THEN
        ALTER TABLE products
        ADD CONSTRAINT fk_products_farm
        FOREIGN KEY (farm_id) REFERENCES farms(id);
    END IF;
END $$;

COMMIT;
