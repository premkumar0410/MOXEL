-- keep a valid number if one is hiding in the old format
UPDATE customer
SET phone = right(regexp_replace(phone, '[^0-9]', '', 'g'), 10)
WHERE phone IS NOT NULL
  AND regexp_replace(phone, '[^0-9]', '', 'g') ~ '^(91)?[6-9][0-9]{9}$';

-- anything still not exactly 10 valid digits is cleared
UPDATE customer SET phone = NULL
WHERE phone IS NOT NULL AND phone !~ '^[6-9][0-9]{9}$';

ALTER TABLE customer ALTER COLUMN phone TYPE VARCHAR(10);

ALTER TABLE customer ADD CONSTRAINT chk_customer_phone
    CHECK (phone IS NULL OR phone ~ '^[6-9][0-9]{9}$');