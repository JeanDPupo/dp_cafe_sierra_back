BEGIN;

ALTER TABLE payments
DROP CONSTRAINT IF EXISTS payments_provider_check;

ALTER TABLE payments
ADD CONSTRAINT payments_provider_check
CHECK ((provider)::text IN ('MERCADO_PAGO', 'NEQUI'));

COMMIT;
