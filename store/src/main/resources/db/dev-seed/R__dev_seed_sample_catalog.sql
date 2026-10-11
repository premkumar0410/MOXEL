INSERT INTO category (name, slug) VALUES
    ('Round Neck', 'round-neck'),
    ('Oversized',  'oversized'),
    ('Polo',       'polo')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO product (category_id, name, slug, description, fabric, gsm)
SELECT c.id, 'Classic Round Neck Tee', 'classic-round-neck-tee',
       'Everyday tee, soft and breathable for Chennai weather.', '100% combed cotton', 180
FROM category c WHERE c.slug = 'round-neck'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO product (category_id, name, slug, description, fabric, gsm)
SELECT c.id, 'Drop Shoulder Oversized Tee', 'drop-shoulder-oversized-tee',
       'Heavy, boxy fit. Great base for bold prints.', '100% cotton, bio-washed', 240
FROM category c WHERE c.slug = 'oversized'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO product (category_id, name, slug, description, fabric, gsm)
SELECT c.id, 'Team Polo', 'team-polo',
       'Collared polo for teams, offices and events.', 'Cotton-poly pique', 200
FROM category c WHERE c.slug = 'polo'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO product_variant (product_id, size, color, sku, price, stock)
SELECT p.id, v.size, v.color, v.sku, v.price, v.stock
FROM product p
CROSS JOIN (VALUES
    ('S',  'Black', 'RN-BLK-S',  399.00, 25),
    ('M',  'Black', 'RN-BLK-M',  399.00, 40),
    ('L',  'Black', 'RN-BLK-L',  399.00, 30),
    ('XL', 'Black', 'RN-BLK-XL', 429.00, 15),
    ('M',  'White', 'RN-WHT-M',  399.00, 35),
    ('L',  'White', 'RN-WHT-L',  399.00, 20)
) AS v(size, color, sku, price, stock)
WHERE p.slug = 'classic-round-neck-tee'
ON CONFLICT (sku) DO NOTHING;

INSERT INTO product_variant (product_id, size, color, sku, price, stock)
SELECT p.id, v.size, v.color, v.sku, v.price, v.stock
FROM product p
CROSS JOIN (VALUES
    ('M',  'Black',     'OS-BLK-M',  549.00, 20),
    ('L',  'Black',     'OS-BLK-L',  549.00, 20),
    ('XL', 'Black',     'OS-BLK-XL', 579.00, 10),
    ('M',  'Off White', 'OS-OFW-M',  549.00, 15),
    ('L',  'Off White', 'OS-OFW-L',  549.00, 0)
) AS v(size, color, sku, price, stock)
WHERE p.slug = 'drop-shoulder-oversized-tee'
ON CONFLICT (sku) DO NOTHING;

INSERT INTO product_variant (product_id, size, color, sku, price, stock)
SELECT p.id, v.size, v.color, v.sku, v.price, v.stock
FROM product p
CROSS JOIN (VALUES
    ('M',  'Navy', 'PL-NVY-M',  499.00, 18),
    ('L',  'Navy', 'PL-NVY-L',  499.00, 18),
    ('XL', 'Navy', 'PL-NVY-XL', 529.00, 8)
) AS v(size, color, sku, price, stock)
WHERE p.slug = 'team-polo'
ON CONFLICT (sku) DO NOTHING;