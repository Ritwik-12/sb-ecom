ALTER TABLE payments ADD COLUMN pg_order_id VARCHAR(255);

-- one gateway payment can only ever back one order (NULLs allowed, e.g. COD)
CREATE UNIQUE INDEX ux_payments_pg_payment_id
    ON payments (pg_payment_id) WHERE pg_payment_id IS NOT NULL;