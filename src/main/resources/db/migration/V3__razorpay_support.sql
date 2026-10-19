-- ============================================
-- Payment integrity
-- ============================================
-- COD orders store an empty payment reference. Normalise those to NULL so they
-- never collide with each other under the unique index below.
UPDATE payments
SET pg_payment_id = NULL
WHERE pg_payment_id = '';

-- A gateway payment reference (e.g. a Stripe PaymentIntent id) may back at most
-- one payment row. If this statement fails on existing data, find the duplicates
-- with: SELECT pg_payment_id, COUNT(*) FROM payments GROUP BY pg_payment_id HAVING COUNT(*) > 1;
CREATE UNIQUE INDEX IF NOT EXISTS uq_payments_pg_payment_id
    ON payments (pg_payment_id)
    WHERE pg_payment_id IS NOT NULL;